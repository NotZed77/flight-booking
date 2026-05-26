package com.springai.demo.dto;

public record Joke(
    String text,
    String category,
    Double laughScore,
    Boolean isNSFW
){
}
