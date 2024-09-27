package com.karthik178.apimanager.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.karthik178.apimanager.model.ResponseEntity;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.utils.AllureLogger;
import com.karthik178.apimanager.utils.LogHandler;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ISuite;
import org.testng.ITestContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


/**
 * Represents APIExecutor
 * @author Karthik T
 */
public class APIExecutor {

    private  static RestAssuredConfig timeoutConfig;
    public static Path sPath ;
    public APIExecutor() {

        timeoutConfig = RestAssured.config().
                httpClient(HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 20000));
    }

    private static final Logger logger = LogManager.getLogger(APIExecutor.class);

    public static String curl;

    private static Headers createHeaders(Map<String, String> headers) {
        headers.put("x-auto-test", "true");
        List<Header> headerList = headers.entrySet().stream().map(entry -> new Header(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        return new Headers(headerList);
    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static Response execute(String uri, RestRequestDefinition definition) {
        RequestSpecification specification = given().config(timeoutConfig);
        Map<String, String> headers = new HashMap<>();
        if (definition.getHeaders() != null) {
            headers.putAll(definition.getHeaders());
        }
        specification = specification.headers(createHeaders(headers));

        if (definition.getParameters() != null) {
            specification = specification.queryParams(definition.getParameters());
        }
//        if (definition.getCookie() != null) {
//            Cookie myCookie = new Cookie.Builder("x-prism-token", definition.getCookie().get("x-prism-token"))
//                    .setSecured(true)
//                    .setComment("session id cookie")
//                    .build();
//            specification = specification.cookie(myCookie);
//        }
        if (definition.getBody() != null) {
            specification = specification.body(definition.getBody());
        }
        String baseURI = uri;;
        Response response;
        logger.info(specification.log().all());
        Instant start = Instant.now();
        System.out.println("=============================================================");
        String allureReqestDescription = String.format("Request Info :: %s", definition.getDescription());
        AllureLogger.info(allureReqestDescription);
        String curCurl = String.format("Client :: %s", baseURI + "   \n" +definition.toString());
        AllureLogger.info(curCurl);
        switch (definition.getMethod()) {
            case GET:
                System.out.println("GET " + baseURI + definition.getUriPath() );
                response = specification.get(baseURI + definition.getUriPath());
                break;
            case PUT:
                System.out.println("PUT " + baseURI + definition.getUriPath() );
                response = specification.put(baseURI + definition.getUriPath());
                break;
            case POST:
                System.out.println("POST " + baseURI + definition.getUriPath());
                if (definition.getHeaders().get("Content-Type").contains("multipart/form-data")) {
                    JSONObject jsonBody = new JSONObject(definition.getHeaders().get("filePath"));
                    specification.multiPart(new File(jsonBody.getString("filePath")));
                }
                response = specification.post(baseURI + definition.getUriPath());
                break;
            case DELETE:
                System.out.println("DELETE " + baseURI + definition.getUriPath()) ;
                response = specification.delete(baseURI + definition.getUriPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported http method " + definition.getMethod());
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        AllureLogger.info(String.format("Status code : %d", response.getStatusCode()));
        return response;
    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static Response execute(String uri, RestRequestDefinition definition, boolean lessLog) {
        RequestSpecification specification = given().config(timeoutConfig);
        Map<String, String> headers = new HashMap<>();
        if (definition.getHeaders() != null) {
            headers.putAll(definition.getHeaders());
        }
        specification = specification.headers(createHeaders(headers));

        if (definition.getParameters() != null) {
            specification = specification.queryParams(definition.getParameters());
        }
        if (definition.getCookie() != null) {
            Cookie myCookie = new Cookie.Builder("x-prism-token", definition.getCookie().get("x-prism-token"))
                    .setSecured(true)
                    .setComment("session id cookie")
                    .build();
            specification = specification.cookie(myCookie);
        }
        if (definition.getBody() != null) {
            specification = specification.body(definition.getBody());
        }
        String baseURI = uri;;
        Response response;
        Instant start = Instant.now();
        String curCurl = String.format("Client :: %s", baseURI + "   \n" +definition.toString());
        switch (definition.getMethod()) {
            case GET:
                System.out.println("GET " + baseURI + definition.getUriPath() );
                response = specification.get(baseURI + definition.getUriPath());
                break;
            case PUT:
                System.out.println("PUT " + baseURI + definition.getUriPath() );
                response = specification.put(baseURI + definition.getUriPath());
                break;
            case POST:
                System.out.println("POST " + baseURI + definition.getUriPath());
                if (definition.getHeaders().get("Content-Type").contains("multipart/form-data")) {
                    JSONObject jsonBody = new JSONObject(definition.getHeaders().get("filePath"));
                    specification.multiPart(new File(jsonBody.getString("filePath")));
                }
                response = specification.post(baseURI + definition.getUriPath());
                break;
            case DELETE:
                System.out.println("DELETE " + baseURI + definition.getUriPath()) ;
                response = specification.delete(baseURI + definition.getUriPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported http method " + definition.getMethod());
        }
        if (Objects.nonNull(System.getProperty("printresponse"))){
            System.out.println(response.body().prettyPrint());
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        return response;
    }

    public static void saveResponse(RestRequestDefinition definition, Response response, String treatementType) {
        String env = System.getProperty("env");
        String directoryName = "apiresponses-" + env + "-";
        try {
            if (Objects.isNull(sPath)) {
                sPath = Files.createTempDirectory(Paths.get("target"),directoryName);
            }
            JsonElement je = JsonParser.parseString(response.body().prettyPrint());
            String apiPath = definition.getUriPath().replaceAll("/", "-").substring(1);
            apiPath = String.format("%s-%s", treatementType, apiPath);
            FileWriter fw = new FileWriter(new File( sPath.toString() + "/" + apiPath + ".json"));
            fw.write(je.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static void saveResponse(String fileName, Response response, String treatementType) {
        String env = System.getProperty("env");
        String directoryName = "apiresponses-" + env + "-";
        try {
            if (Objects.isNull(sPath)) {
                sPath = Files.createTempDirectory(Paths.get("target"),directoryName);
            }
            JsonElement je = JsonParser.parseString(response.jsonPath().prettify());
            String apiPath = fileName.replaceAll("/", "-").replaceAll(".json", "").replaceAll("\\.", "-");
            apiPath = String.format("%s-%s", treatementType, apiPath);
            FileWriter fw = new FileWriter(new File( sPath.toString() + "/" + apiPath + ".json"));
            fw.write(je.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static Response execute(UserContext context, RestRequestDefinition definition) {
        RequestSpecification specification = given().config(timeoutConfig);
        Map<String, String> headers = new HashMap<>();
        if (definition.getHeaders() != null) {
            headers.putAll(definition.getHeaders());
        }
        specification = specification.headers(createHeaders(headers));

        if (definition.getParameters() != null) {
            specification = specification.queryParams(definition.getParameters());
        }
        Cookie myCookie = new Cookie.Builder("x-prism-token", context.getUser().getToken())
                .setSecured(true)
                .setComment("session id cookie")
                .build();
        specification = specification.cookies(context.getAuthCookies());
        if (definition.getBody() != null) {
            specification = specification.body(definition.getBody());
        }
        String baseURI = context.getSecret().getUrl();
        Response response;
//        System.out.println("=============================================================");
        String allureReqestDescription = String.format("Request Info :: %s", definition.getDescription());
//        LogHandler.logInfo(allureReqestDescription);
//        specification.log().all();
        //setCurl(definition);
        String curCurl = String.format("\n====Payload Info======\nBase URL =%s\n%s\n", baseURI,definition.toString());
//        LogHandler.logInfo(curCurl);
        Instant start = Instant.now();
        switch (definition.getMethod()) {
            case GET:
                System.out.println("GET " + baseURI + definition.getUriPath() );
                response = specification.get(baseURI + definition.getUriPath());
                break;
            case PUT:
                System.out.println("PUT " + baseURI + definition.getUriPath() );
                response = specification.put(baseURI + definition.getUriPath());
                break;
            case POST:
                System.out.println("POST " + baseURI + definition.getUriPath());
                if (definition.getHeaders().get("Content-Type").contains("multipart/form-data")) {
                    JSONObject jsonBody = new JSONObject(definition.getHeaders().get("filePath"));
                    specification.multiPart(new File(jsonBody.getString("filePath")));
                }
                response = specification.post(baseURI + definition.getUriPath());
                break;
            case DELETE:
                System.out.println("DELETE " + baseURI + definition.getUriPath()) ;
                response = specification.delete(baseURI + definition.getUriPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported http method " + definition.getMethod());
        }
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        String log = String.format("Reponse Status code :: %d \n", response.getStatusCode());
//        if (Objects.nonNull(System.getProperty("printresponse"))){
//            System.out.println(response.body().prettyPrint());
//        }
//        if (response.getStatusCode() >= 300) {
//            Assert.assertTrue(response.getStatusCode() < 300,response.body().prettyPrint());
//        }
//        LogHandler.logInfo(log);
//        if (Objects.nonNull( System.getProperty("treatmentType"))) {
//            saveResponse(definition, response, System.getProperty("treatmentType"));
//        }
        return response;

    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static Response execute(UserContext context, RestRequestDefinition definition, ITestContext iTestContext) {
        RequestSpecification specification = given().config(timeoutConfig);
        Map<String, String> headers = new HashMap<>();
        if (definition.getHeaders() != null) {
            headers.putAll(definition.getHeaders());
        }
        specification = specification.headers(createHeaders(headers));

        if (definition.getParameters() != null) {
            specification = specification.queryParams(definition.getParameters());
        }
        Cookie myCookie = new Cookie.Builder("x-prism-token", context.getUser().getToken())
                .setSecured(true)
                .setComment("session id cookie")
                .build();
        specification = specification.cookie(myCookie);
        if (definition.getBody() != null) {
            specification = specification.body(definition.getBody());
        }
        String baseURI = context.getSecret().getUrl();
        Response response;
        System.out.println("=============================================================");
        String allureReqestDescription = String.format("Request Info :: %s", definition.getDescription());
        LogHandler.logInfo(allureReqestDescription);
        specification.log().all();
        //setCurl(definition);
        String curCurl = String.format("Client :: %s", baseURI + "   \n" +definition.toString());
        LogHandler.logInfo(curCurl);

        Instant start = Instant.now();
        switch (definition.getMethod()) {
            case GET:
                System.out.println("GET " + baseURI + definition.getUriPath() );
                response = specification.get(baseURI + definition.getUriPath());
                break;
            case PUT:
                System.out.println("PUT " + baseURI + definition.getUriPath() );
                response = specification.put(baseURI + definition.getUriPath());
                break;
            case POST:
                System.out.println("POST " + baseURI + definition.getUriPath());
                if (definition.getHeaders().get("Content-Type").contains("multipart/form-data")) {
                    JSONObject jsonBody = new JSONObject(definition.getHeaders().get("filePath"));
                    specification.multiPart(new File(jsonBody.getString("filePath")));
                }
                response = specification.post(baseURI + definition.getUriPath());
                break;
            case DELETE:
                System.out.println("DELETE " + baseURI + definition.getUriPath()) ;
                response = specification.delete(baseURI + definition.getUriPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported http method " + definition.getMethod());
        }
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        String log = String.format("Status code :: %d", response.getStatusCode());
        LogHandler.logInfo(log);
        if (Objects.nonNull(System.getProperty("printresponse"))){
            AllureLogger.info(response.getBody().prettyPrint());
        }
        if (response.getStatusCode() >= 300) {
            Assert.assertTrue(response.getStatusCode() < 300, String.format("Status code: %s, \n %s", response.getStatusCode(), response.getBody().prettyPrint()));
        }
        if (Objects.nonNull(definition.getCleanup()) && !response.jsonPath().get("status").toString().toLowerCase().contentEquals("fail")){
            if (definition.getCleanup().containsKey("entityType")){
                iTestContext.setAttribute(definition.getCleanup().get("entityType"), new ResponseEntity(response, definition, context));
            }

        }
        return response;
    }

    @Step("Request info : {definition.uriPath} :: {definition.description}")
    public static Response execute(UserContext context, RestRequestDefinition definition, ITestContext iTestContext, ISuite iSuite) {
        RequestSpecification specification = given().config(timeoutConfig);
        Map<String, String> headers = new HashMap<>();
        if (definition.getHeaders() != null) {
            headers.putAll(definition.getHeaders());
        }
        specification = specification.headers(createHeaders(headers));

        if (definition.getParameters() != null) {
            specification = specification.queryParams(definition.getParameters());
        }
        Cookie myCookie = new Cookie.Builder("x-prism-token", context.getUser().getToken())
                .setSecured(true)
                .setComment("session id cookie")
                .build();
        specification = specification.cookie(myCookie);
        if (definition.getBody() != null) {
            specification = specification.body(definition.getBody());
        }
        String baseURI = context.getSecret().getUrl();
        Response response;
        System.out.println("=============================================================");
        String allureReqestDescription = String.format("Request Info :: %s", definition.getDescription());
        AllureLogger.info(allureReqestDescription);
        specification.log().all();
        //setCurl(definition);
        String curCurl = String.format("Client :: %s", baseURI + "   \n" +definition.toString());
        AllureLogger.info(curCurl);

        Instant start = Instant.now();
        switch (definition.getMethod()) {
            case GET:
                System.out.println("GET " + baseURI + definition.getUriPath() );
                response = specification.get(baseURI + definition.getUriPath());
                break;
            case PUT:
                System.out.println("PUT " + baseURI + definition.getUriPath() );
                response = specification.put(baseURI + definition.getUriPath());
                break;
            case POST:
                System.out.println("POST " + baseURI + definition.getUriPath());
                if (definition.getHeaders().get("Content-Type").contains("multipart/form-data")) {
                    JSONObject jsonBody = new JSONObject(definition.getHeaders().get("filePath"));
                    specification.multiPart(new File(jsonBody.getString("filePath")));
                }
                response = specification.post(baseURI + definition.getUriPath());
                break;
            case DELETE:
                System.out.println("DELETE " + baseURI + definition.getUriPath()) ;
                response = specification.delete(baseURI + definition.getUriPath());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported http method " + definition.getMethod());
        }
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        String log = String.format("Status code :: %d", response.getStatusCode());
        AllureLogger.info(log);
        System.out.println();
        if (Objects.nonNull(definition.getCleanup()) && !response.jsonPath().get("status").toString().toLowerCase().contentEquals("fail")){
            if (definition.getCleanup().containsKey("entityType")){
                iTestContext.setAttribute(definition.getCleanup().get("entityType"), new ResponseEntity(response, definition, context));
                iSuite.setAttribute(definition.getCleanup().get("entityType"), new ResponseEntity(response, definition, context));
            }

        }
        return response;
    }

    public static void setCurl(RestRequestDefinition definition) {

        try {
            // add uri
            final String[] build_curl = {"curl -X " + definition.getMethod() + " '" + definition.getUriPath()};

            // add query params
            if (definition.getParameters() != null && !definition.getParameters().isEmpty()) {
                build_curl[0] = build_curl[0] + "?";
                definition.getParameters().forEach((k, v) -> build_curl[0] = build_curl[0] + k + "=" + v + "&");
            }
            build_curl[0] = build_curl[0] + "'";

            // add headers
            definition.getHeaders().forEach((k, v) -> build_curl[0] = build_curl[0] + " -H '" + k + ": " + v + "'");
            build_curl[0] = build_curl[0] + " -H '";

            // add body
            if (definition.getBody() != null && !definition.getBody().toString().equalsIgnoreCase("")) {
                build_curl[0] = build_curl[0] + (" -d '" + definition.getBody() + "'");
            }

            System.out.println("curl : " + build_curl[0]);
            curl = "\n{code}\n" + build_curl[0] + "\n{code}" + "\n";
        } catch (Exception e) {
            curl = "No valid curl formed !!";
        }
    }
}
