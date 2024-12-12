package com.ecom.security_service.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ADDRESS")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "STREET", nullable = false)
    private String street;
    @Column(name = "STREET_NUMBER", nullable = false)
    private Integer streetNumber;
    @Column(name = "HOUSE_NUMBER", nullable = false)
    private Integer houseNumber;
    @Column(name = "CITY", nullable = false)
    private String city;
    @Column(name = "POSTAL_CODE", nullable = false)
    private String postalCode;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "isDefault", nullable = false)
    private boolean isDefault;
    @Column(name = "billing_address", nullable = false)
    private boolean billingAddress;
    @Column(name = "shipping_address", nullable = false)
    private boolean shippingAddress;

}