package com.springai.demo.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;

@Slf4j
public class TokenUsageAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        long startTime = System.currentTimeMillis();

        ChatClientResponse advisedResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse = advisedResponse.chatResponse();

        if(chatResponse != null && chatResponse.getMetadata().getUsage() != null){
            var usage = chatResponse.getMetadata().getUsage();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Token Usage: Input = {} | Output = {} | Total = {} | Time = {}ms",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens(),
                    duration);
        }

        return advisedResponse;
    }

    @Override
    public String getName() {
        return "ChatClientResponse";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
