package com.karthik178.apimanager.model;

import io.restassured.path.json.JsonPath;
/**
 * Represents an UserContextWithSettings Model
 * @author Karthik T
 */
public class UserContextWithSettings extends UserContext{

    JsonPath smsSettings;

    public UserContextWithSettings(JsonPath smsSettings) {
        this.smsSettings = smsSettings;
    }

    public UserContextWithSettings(User user, Secret secret, JsonPath smsSettings) {
        super(user, secret);
        this.smsSettings = smsSettings;
    }

    public JsonPath getSmsSettings() {
        return smsSettings;
    }

    public void setSmsSettings(JsonPath smsSettings) {
        this.smsSettings = smsSettings;
    }
}
