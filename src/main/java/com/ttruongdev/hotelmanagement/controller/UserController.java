package com.ttruongdev.hotelmanagement.controller;

import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.service.interfac.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUser() {
        Response response = userService.getAllUser();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{userid}")
    public ResponseEntity<Response> getUserByUd(@PathVariable("userid") String userid) {
        Response response = userService.getUserById(userid);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-user-booking/{userid}")
    public ResponseEntity<Response> getUserBookingHistory(@PathVariable("userid") String userid) {
        Response response = userService.getUserBookingHistory(userid);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<Response> deleteUser(@PathVariable("userid") String userid) {
        Response response = userService.deleteUser(userid);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
