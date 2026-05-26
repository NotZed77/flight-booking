package com.springai.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTests {

    @Autowired
    private AIService aiService;

    @Test
    public void testGetJoke(){
        var joke = aiService.getJoke("Friends");
        System.out.println(joke);
    }

    @Test
    public void testEmbedText(){
        var embed = aiService.getEmbedding("This is a big text here");
        System.out.println(embed.length);
        for(float e: embed){
            System.out.print(e+" ");
        }
    }

    @Test
    public void testStoreData(){
        aiService.ingestDataToVectorStore();
    }

    @Test
    public void testSimilaritySearch(){
        var res = aiService.similaritySearch("space movie");
        for(var doc: res){
            System.out.println(doc);
        }
    }

}
