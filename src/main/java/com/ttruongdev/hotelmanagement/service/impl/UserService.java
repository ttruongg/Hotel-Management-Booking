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

import java.util.List;

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
        Response response = new Response();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);

        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + ex.getMessage());
        }

        return response;
    }

    @Override
    public Response getUserById(String id) {
        Response response = new Response();
        try {
            User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new AppException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);
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
    public Response getUserBookingHistory(String id) {
        Response response = new Response();
        try {
            User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new AppException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);

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
    public Response deleteUser(String id) {
        Response response = new Response();
        try {
            userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new AppException("User not found"));
            userRepository.deleteById(Long.valueOf(id));
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (AppException ex) {
            response.setStatusCode(404);
            response.setMessage(ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(500);
            response.setMessage("Error deleting user " + ex.getMessage());
        }
        return response;
    }
}
