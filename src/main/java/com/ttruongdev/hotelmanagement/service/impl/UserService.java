package com.ttruongdev.hotelmanagement.service.impl;

import com.ttruongdev.hotelmanagement.dto.LoginRequest;
import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.dto.UserDTO;
import com.ttruongdev.hotelmanagement.entity.User;
import com.ttruongdev.hotelmanagement.exception.AppException;
import com.ttruongdev.hotelmanagement.repository.UserRepository;
import com.ttruongdev.hotelmanagement.service.interfac.IUserService;
import com.ttruongdev.hotelmanagement.utils.JWT;
import com.ttruongdev.hotelmanagement.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JWT jwt;
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new AppException(user.getEmail() + "already exits");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User saveUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(saveUser);
            response.setStatusCode(200);
            response.setUser(userDTO);

        } catch (AppException ex) {
            response.setStatusCode(400);
            response.setMessage(ex.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred during User registration " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new AppException("user not found"));

            var token = jwt.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("1 Day");
            response.setMessage("successful");


        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());

        } catch (Exception ex) {

            response.setStatusCode(500);
            response.setMessage("Error occurred during user login " + ex.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUser() {
        return null;
    }

    @Override
    public Response getUserById(String id) {
        return null;
    }

    @Override
    public Response getUserBookingHistory(String id) {
        return null;
    }

    @Override
    public Response deleteUser(String id) {
        return null;
    }
}
