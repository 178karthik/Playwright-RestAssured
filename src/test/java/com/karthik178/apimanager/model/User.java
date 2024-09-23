package com.karthik178.apimanager.model;

import com.fasterxml.jackson.databind.JsonNode;
/**
 * Represents an User Model
 * @author Karthik T
 */
public class User {

    private String status;
    private String token;

    private JsonNode user;
    private JsonNode client;
    private JsonNode features;
    private String redirect;
    private String devMode;

    public User(String status, String token, JsonNode user, JsonNode client, JsonNode features, String redirect, String devMode) {
        this.status = status;
        this.token = token;
        this.user = user;
        this.client = client;
        this.features = features;
        this.redirect = redirect;
        this.devMode = devMode;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getDevMode() {
        return devMode;
    }

    public void setDevMode(String devMode) {
        this.devMode = devMode;
    }

    public User() {
    }

    public User(String status, String token, JsonNode user, JsonNode client, JsonNode features) {
        this.status = status;
        this.token = token;
        this.user = user;
        this.client = client;
        this.features = features;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JsonNode getUser() {
        return user;
    }

    public void setUser(JsonNode user) {
        this.user = user;
    }

    public JsonNode getClient() {
        return client;
    }

    public void setClient(JsonNode client) {
        this.client = client;
    }

    public JsonNode getFeatures() {
        return features;
    }

    public void setFeatures(JsonNode features) {
        this.features = features;
    }
}
