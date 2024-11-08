package com.ecom.security_service.model;

public record UserDataModel(Long id,
                            String lastName,
                            String firstName,
                            String phoneNumber,
                            String email
) {
}
