package com.ttruongdev.hotelmanagement.service.impl;

import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.dto.RoomDTO;
import com.ttruongdev.hotelmanagement.entity.Room;
import com.ttruongdev.hotelmanagement.exception.AppException;
import com.ttruongdev.hotelmanagement.repository.BookingRepository;
import com.ttruongdev.hotelmanagement.repository.RoomRepository;
import com.ttruongdev.hotelmanagement.service.AwsS3Service;
import com.ttruongdev.hotelmanagement.service.interfac.IRoomService;
import com.ttruongdev.hotelmanagement.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();

        try {
            String imgUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imgUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);

            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + ex.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();

        try {
            List<Room> rooms = roomRepository.findAll();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(rooms);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try {
            roomRepository.findById(roomId).orElseThrow(() -> new AppException("Room Not Found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error deleting room: " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();

        try {
            String imgUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imgUrl = awsS3Service.saveImageToS3(photo);
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
            if (imgUrl != null) room.setRoomPhotoUrl(imgUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error updating room " + ex.getMessage());
        }

        return response;
    }


    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(room);

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting user " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndTypes(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.getAvailableRoomByTypesAndDate(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);

        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting room: " + ex.getMessage());
        }


        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);


        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting room " + ex.getMessage());
        }
        return response;
    }
}
