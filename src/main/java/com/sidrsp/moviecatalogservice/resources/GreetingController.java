package com.sidrsp.moviecatalogservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import com.sidrsp.moviecatalogservice.configs.DbSettings;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    @Autowired
    private DbSettings dbSettings;

    @Value("${greeting.message: default Hi}")
    private String greetingMsg;

    @Value("Static Message")
    private String staticMessage;

    @Value("${my.list.values}")
    private List<String> listValues;

    @Value("#{${dbValues}}")
    private Map<String, String> dbValueMap;

    @Value("${app.name}")
    private String appName;

    @Value("${app.desc}")
    private String appDesc;

    @Value("${profile}")
    private String activeProfile;

    @RequestMapping("/sayhello")
    public String greeting() {
        return appName + appDesc + greetingMsg + staticMessage + listValues + dbValueMap.get("username");
    }

    @RequestMapping("/withConfigProperties")
    public String withConfigProperties() {
        return greetingMsg + dbSettings.getConnection() + dbSettings.getHost() + dbSettings.getPort();
    }

    @RequestMapping("/profile")
    public String getProfile() {
        return appName + appDesc + activeProfile + dbSettings.getConnection() + dbSettings.getHost() + dbSettings.getPort();
    }

}
