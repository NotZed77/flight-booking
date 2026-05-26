package com.springai.demo.service;

import com.springai.demo.advisor.TokenUsageAdvisor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    @Value("classpath:AI_Overview_Report.pdf")
    Resource pdfFile;

    public void ingestVectorStore(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
        List<Document> pages = reader.get();

        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();
        List<Document> chunks = tokenTextSplitter.apply(pages);

        vectorStore.add(chunks);
    }

    public String askAIWithAdvisors(String prompt, String userId){
        return chatClient.prompt()
                .system("""
                        You are an AI assistant called Cody.
                        Greet users with your Name (Cody) and the user name if you know their name.
                        Answer in a friendly, conversational tone.
                        """)
                .user(prompt)
                .advisors(

//                        new SafeGuardAdvisor(List.of("Politics", "Gaming")),

                        MessageChatMemoryAdvisor.builder(chatMemory)
                                        .conversationId(userId)
                                                .build(),

                        VectorStoreChatMemoryAdvisor.builder(vectorStore)
                                .conversationId(userId)
                            .defaultTopK(4)
                            .build(),

                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .filterExpression("file_name == 'AI_Overview_Report.pdf'")
                                        .topK(4)
                                        .build())
                                .build(),

                        new TokenUsageAdvisor()
                )
                .call()
                .content();
    }


    public String askAI(String prompt) {

        String template = """
                You are an AI assistant helping a developer.
                
                Rules:
                - Use ONLY the information provided in the context
                - You MAY rephrase, summarize, and explain in the natural language
                - Do NOT introduce new concepts or facts
                - If multiple context sections are relevant, combine them into a single explanation.
                - If the answer is not present, say "I don't know"
                
                Context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .similarityThreshold(0.4)
                .filterExpression("file_name == 'AI_Overview_Report.pdf'")
                .build());
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context", context));

        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors()
                .call()
                .content();
    }

    public static List<Document> springAiDocs(){
        return List.of(
                new Document("AI platform known for conversational models, coding assistance, and generative AI tools. ",
                        Map.of("title", "OpenAI")),

                new Document("Multimodal AI system developed by Google for reasoning, search, and productivity tasks.",
                        Map.of("title", "Google Gemini")),

                new Document("AI assistant focused on safe, reliable, and long-context conversations.",
                        Map.of("title", "Anthropic Claude"))
        );
    }

}
