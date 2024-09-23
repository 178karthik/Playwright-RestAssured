package com.karthik178.playwritemanager.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.utils.LogHandler;
import com.karthik178.configservice.common.BasePlaywriteTest;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvUtils extends BasePlaywriteTest {


    private static final Logger logger = LogManager.getLogger(CsvUtils.class);


    public String convertJSONToCSV(String jsonFilePath, Map<String, Object> replaceKeys) throws IOException {
        String jsonString = PayloadBuilder.readClassPathResource(jsonFilePath);
        String fileName = Paths.get(jsonFilePath).getFileName().toString().replace(".json", "").toLowerCase();
        System.out.println(jsonString);
        String resolvedString = PayloadBuilder.getResolvedString(jsonString, replaceKeys);
        System.out.println(resolvedString);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(resolvedString);
        System.out.println(actualObj.toString());

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = actualObj.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();

        String csvFileName = String.format("%s/%s%s.csv", Paths.get("target"),fileName, getCurrentDateAndTime("ddMMMYYHHmm"));
        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(new File(csvFileName), actualObj);
        LogHandler.logInfo(logger, "CSV File generated :: " + csvFileName);
        return csvFileName;
    }

    public void convertJsonToCsv(String json, String csv ,String[] columnNames)
    {
        String jsonString;
        JSONObject jsonObject;
        try {
            jsonString = new String(
                    Files.readAllBytes(Paths.get(json)));
            jsonObject = new JSONObject(jsonString);
            JSONArray docs
                    = jsonObject.getJSONArray("data");
            File file = new File(csv);
            JSONArray keys = new JSONArray(columnNames);
            StringBuilder csvData = new StringBuilder();
            csvData.append(CDL.rowToString(keys));
            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i);
                JSONArray values = new JSONArray();
                for (String key : columnNames) {
                    values.put(doc.get(key));
                }
                csvData.append(CDL.rowToString(values));
            }
            FileUtils.writeStringToFile(file, csvData.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Step("Convert JsonFile to CSV")
    public String convertJsonFileToCSV(String uniqueFilename) {

        String jsonFilePath = System.getProperty("user.dir") + "//src//test//resources//sms//uitests//citiustech//preselectedSpecializationCitiusTech.json";
        String csvFilePath = System.getProperty("user.dir") + "//src//test//resources//uploadFiles//"+uniqueFilename;
        String[] columnNames = {"Employee Id", "Specialization Ids"};
        CsvUtils csv = new CsvUtils();
        csv.convertJsonToCsv(jsonFilePath, csvFilePath, columnNames);
        return csvFilePath;
    }

    @Step("Modify the JsonFile with RandomspecializationId")
    public void modifyTheJsonFileWithRandomSpecializationId(String specilizationId) throws IOException {

        String path = System.getProperty("user.dir") + "//src//test//resources//sms//uitests//citiustech//preselectedSpecializationCitiusTech.json";
        File jsonFile = new File(path);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonFile);
        JsonNode dataArray = rootNode.get("data");
        if (dataArray.isArray() && dataArray.size() > 0) {
            ObjectNode firstObject = (ObjectNode) dataArray.get(0);
            firstObject.put("Specialization Ids", specilizationId);
        }
        ObjectWriter objectWriter = objectMapper.writer().with(SerializationFeature.INDENT_OUTPUT);
        objectWriter.writeValue(jsonFile, rootNode);


    }


}









