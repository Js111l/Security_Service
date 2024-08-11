package com.ecom.security_service.dao;

import com.ecom.security_service.dao.entity.ApplicationSetting;
import com.ecom.security_service.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationSettingsRepository extends JpaRepository<ApplicationSetting, Long> {
    @Query(value = " select s from ApplicationSetting s where s.settingType in :types ")
    List<ApplicationSetting> getMailSettings(@Param("types") List<SettingType> settingTypeList);
}
