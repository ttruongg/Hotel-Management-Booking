package com.ttruongdev.hotelmanagement.service.interfac;

import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBooking();

    Response cancelBooking(Long bookingId);
}
