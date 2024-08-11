package com.ecom.security_service.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table(name = "VERIFICATION_TOKEN")
@Entity
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "expiration_date")
    private Date expirationDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
