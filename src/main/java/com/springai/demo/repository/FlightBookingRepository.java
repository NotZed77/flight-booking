package com.springai.demo.repository;

import com.springai.demo.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long> {

    List<FlightBooking> findByUserIdOrderByDepartureTime(String userId);

    boolean existsByUserIdAndDestinationAndDepartureTime(
            String userId, String destination, Instant departureTime);
}
