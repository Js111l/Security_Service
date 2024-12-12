package com.ecom.security_service.controller;

import com.ecom.security_service.model.AddressModel;
import com.ecom.security_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/addresses")
    public List<AddressModel> getAddresses(HttpServletRequest request) {
        return this.userService.getCurrentUserAddresses(request);
    }

    @GetMapping("/default-addresses")
    public Pair<AddressModel, AddressModel> getDefaultAddresses(HttpServletRequest request) {
        return this.userService.getCurrentUserDefaultAddresses(request);
    }
}
