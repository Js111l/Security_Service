package com.ecom.security_service.dao.entity;

import jakarta.persistence.*;
import lombok.Data;


@Table(name = "SHOP_USER")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "email_confirmed")
    private Boolean emailConfirmed;
}
