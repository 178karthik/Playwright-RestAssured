package com.karthik178.apimanager.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik178.apimanager.enums.HttpRequestMethod;

import java.util.Map;
import java.util.Objects;

/**
 * Represents RestRequestDefinition
 * @author Karthik T
 */
public class RestRequestDefinition {

    private String description;
    private String uriPath;
    private HttpRequestMethod method;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private JsonNode body;
    private Map<String, String> cookie;
    private Map<String, String> cleanup;

    public RestRequestDefinition() {

    }

    public RestRequestDefinition(String uriPath, HttpRequestMethod method, Map<String, String> parameters, Map<String, String> headers, JsonNode body, Map<String, String> cookie, Map<String, String> cleanup) {
        this.uriPath = uriPath;
        this.method = method;
        this.parameters = parameters;
        this.headers = headers;
        this.body = body;
        this.cookie = cookie;
        this.cleanup = cleanup;
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public RestRequestDefinition setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public String getUriPath() {
        return uriPath;
    }

    public RestRequestDefinition setUriPath(String uriPath) {
        this.uriPath = uriPath;
        return this;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public RestRequestDefinition setMethod(HttpRequestMethod method) {
        this.method = method;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RestRequestDefinition setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public JsonNode getBody() {
        return body;
    }

    public RestRequestDefinition setBody(JsonNode body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getCleanup() {
        return cleanup;
    }

    public void setCleanup(Map<String, String> cleanup) {
        this.cleanup = cleanup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestRequestDefinition that)) return false;
        return Objects.equals(getUriPath(), that.getUriPath()) && getMethod() == that.getMethod() && Objects.equals(getParameters(), that.getParameters()) && Objects.equals(getHeaders(), that.getHeaders()) && Objects.equals(getBody(), that.getBody()) && Objects.equals(getCookie(), that.getCookie()) && Objects.equals(getCleanup(), that.getCleanup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUriPath(), getMethod(), getParameters(), getHeaders(), getBody(), getCookie(), getCleanup());
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String prettyString = "";
        try {
            prettyString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            prettyString = String.valueOf(body);
        }
        return "\nAPI REQUEST INFO" +
                "\nendPoint='" + uriPath + '\'' +
                ",\nmethod=" + method +
                ",\nparameters=" + parameters +
                ",\nheaders=" + headers +
                ",\nbody : \n" + prettyString  ;
    }
}
