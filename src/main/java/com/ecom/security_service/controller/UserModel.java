package com.ecom.security_service.controller;

import com.ecom.security_service.enums.UserRole;

public record UserModel(Long id, String email, UserRole role, Boolean emailConfirmed) {
}
