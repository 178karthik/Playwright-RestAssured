package com.karthik178.apimanager.model;

import com.karthik178.apimanager.rest.RestRequestDefinition;
import io.restassured.response.Response;

/**
 * Represents an ResponseEntity Model
 * @author Karthik T
 */
public class ResponseEntity {

    Response response;
    RestRequestDefinition definition;

    UserContext userContext;

    public ResponseEntity(Response response, RestRequestDefinition definition, UserContext userContext) {
        this.response = response;
        this.definition = definition;
        this.userContext = userContext;
    }

    public ResponseEntity(Response response, RestRequestDefinition definition) {
        this.response = response;
        this.definition = definition;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }


    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public RestRequestDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(RestRequestDefinition definition) {
        this.definition = definition;
    }
}
