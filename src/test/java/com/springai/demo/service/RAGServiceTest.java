package com.springai.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServiceTest {

    @Autowired
    private RAGService ragService;

    @Test
    public void testIngest(){
        ragService.ingestVectorStore();
    }

    @Test
    public void testAskAI(){
        String response = ragService.askAI("What is AI?");
        System.out.println(response);
    }

    @Test
    public void testAskAIWithAdvisors(){
        String response = ragService.askAIWithAdvisors("WHAT DO YOU KNOW ABOUT ME? ", "zed");
        System.out.println(response);
    }

}
