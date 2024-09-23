package com.karthik178.apimanager.model;

/**
 * Represents an Secret Model
 * @author Karthik T
 */
public class Secret {

    String key;
    String url;
    String email;
    String password;



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Secret() {
    }

    public Secret(String url, String email, String password) {
        this.url = url;
        this.email = email;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Secret{" +
                "key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", email='" + email + '\'' +
                ", passwod='" + password + '\'' +
                '}';
    }

    public String prettyPrint() {
        return "Environment information \n" +
                "key=" + key + '\n' +
                "url=" + url + '\n' +
                "email=" + email + '\n' +
                "passwod=" + password + '\n';
    }
}
