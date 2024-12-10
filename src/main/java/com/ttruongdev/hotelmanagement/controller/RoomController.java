package com.ttruongdev.hotelmanagement.controller;

import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.service.interfac.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addNewRoom(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
    ) {
        if (photo == null || photo.isEmpty() || roomType == null || roomType.isBlank() || roomPrice == null) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide values for all fields(photo, roomType, roomPrice");
            return ResponseEntity.status(response.getStatusCode()).body(response);

        }

        Response response = roomService.addNewRoom(photo, roomType, roomPrice, roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping
    public ResponseEntity<Response> getAllRooms() {
        Response response = new Response();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Response> getRoomById(@PathVariable Long roomId) {
        Response response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<Response> getAvailableRooms() {
        Response response = new Response();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available-rooms-date-types")
    public ResponseEntity<Response> getAvailableRoomsByDateAndTypes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String roomType
    ) {
        if (checkInDate == null || roomType == null || roomType.isBlank() || checkOutDate == null) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide values for checkInDate, checkOutDate, roomType");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        Response response = roomService.getAvailableRoomsByDateAndTypes(checkInDate, checkOutDate, roomType);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN)")
    public ResponseEntity<Response> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
    ) {
        Response response = roomService.updateRoom(roomId, roomDescription, roomType, roomPrice, photo);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN)")
    public ResponseEntity<Response> deleteRoom(@PathVariable Long roomId) {
        Response response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
