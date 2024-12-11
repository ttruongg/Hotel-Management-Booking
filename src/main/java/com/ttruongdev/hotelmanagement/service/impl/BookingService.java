package com.ttruongdev.hotelmanagement.service.impl;

import com.ttruongdev.hotelmanagement.dto.BookingDTO;
import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.entity.Booking;
import com.ttruongdev.hotelmanagement.entity.Room;
import com.ttruongdev.hotelmanagement.entity.User;
import com.ttruongdev.hotelmanagement.exception.AppException;
import com.ttruongdev.hotelmanagement.repository.BookingRepository;
import com.ttruongdev.hotelmanagement.repository.RoomRepository;
import com.ttruongdev.hotelmanagement.repository.UserRepository;
import com.ttruongdev.hotelmanagement.service.interfac.IBookingService;
import com.ttruongdev.hotelmanagement.service.interfac.IRoomService;
import com.ttruongdev.hotelmanagement.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BookingService implements IBookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new AppException("Room not available for date");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error saving a booking " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new AppException("Booking not found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(booking);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting booking " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBooking() {
        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll();
            List<BookingDTO> bookingDTOS = Utils.mapBookingListEntityToBookingListDTO(bookingList);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOS);


        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting list of bookings " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new AppException("Booking not found"));
            bookingRepository.deleteById(bookingId);

            response.setStatusCode(200);
            response.setMessage("Successful");

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error canceling booking " + ex.getMessage());
        }


        return response;
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))


                );

    }
}
