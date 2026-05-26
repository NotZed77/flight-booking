package com.springai.demo.dto;

import com.springai.demo.entity.BookingStatus;

import java.time.Instant;

public record BookingResponse(Long id, String destination, Instant departureTime, BookingStatus status){

}
