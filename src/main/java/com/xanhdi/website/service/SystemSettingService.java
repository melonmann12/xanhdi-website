package com.xanhdi.website.service;

import com.xanhdi.website.model.SystemSetting;
import com.xanhdi.website.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Autowired
    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Cacheable(value = "systemSettings")
    public Map<String, String> getSettingsMap() {
        return systemSettingRepository.findAll().stream()
                .collect(Collectors.toMap(
                        SystemSetting::getSettingKey,
                        s -> s.getSettingValue() != null ? s.getSettingValue() : ""
                ));
    }
}
