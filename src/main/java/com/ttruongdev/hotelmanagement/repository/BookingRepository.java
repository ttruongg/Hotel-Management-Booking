package com.ttruongdev.hotelmanagement.repository;


import com.ttruongdev.hotelmanagement.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long roomId);

    List<Booking> findBybookingConfirmationCode(String bookingConfirmationCode);

    List<Booking> findByUserId(Long userId);
}
