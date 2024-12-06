package com.ttruongdev.hotelmanagement.service.interfac;

import com.ttruongdev.hotelmanagement.dto.LoginRequest;
import com.ttruongdev.hotelmanagement.dto.Response;
import com.ttruongdev.hotelmanagement.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUser();

    Response getUserById(String id);

    Response getUserBookingHistory(String id);

    Response deleteUser(String id);

}
