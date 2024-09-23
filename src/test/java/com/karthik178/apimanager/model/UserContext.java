package com.karthik178.apimanager.model;

import java.util.Map;

/**
 * Represents an UserContext Model
 * @author Karthik T
 */
public class UserContext {

    private User user;
    private Secret secret;
    private Map<String, String> authCookies;

    public UserContext() {
    }

    public UserContext(User user, Secret secret) {
        this.user = user;
        this.secret = secret;
    }

    public UserContext(User user, Secret secret, Map<String, String> authCookies) {
        this.user = user;
        this.secret = secret;
        this.authCookies = authCookies;
    }

    public UserContext(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public Map<String, String> getAuthCookies() {
        return authCookies;
    }

    public void setAuthCookies(Map<String, String> authCookies) {
        this.authCookies = authCookies;
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "user=" + user +
                ", secret=" + secret +
                '}';
    }
}
