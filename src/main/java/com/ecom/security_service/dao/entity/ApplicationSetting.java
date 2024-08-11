package com.ecom.security_service.dao.entity;

import com.ecom.security_service.enums.SettingType;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "APPLICATION_SETTINGS")
@Entity
@Data
public class ApplicationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "setting_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingType settingType;
    @Column(name = "setting_value", nullable = false)
    private String settingValue;
}
