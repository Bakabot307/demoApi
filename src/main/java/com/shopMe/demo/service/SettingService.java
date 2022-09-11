package com.shopMe.demo.service;

import com.shopMe.demo.Settings.EmailSettingBag;
import com.shopMe.demo.model.Settings.Setting;
import com.shopMe.demo.model.Settings.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopMe.demo.repository.*;

import java.util.List;

@Service
public class SettingService {
    @Autowired
    private SettingRepository repo;


    public List<Setting> getGeneralSettings() {
        return repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = repo.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));

        return new EmailSettingBag(settings);
    }
}