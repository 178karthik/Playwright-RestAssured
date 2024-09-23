package com.karthik178.apimanager.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class JsonSchemaToJsonObject {

    public static List<String> properitiesNotResolved = new ArrayList<>();


    public static JsonNode generateJsonObjectFromSchema(JsonNode schemaNode, Map<String, String> demandFormInfo, Map<String, Object> replacers) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        JsonNode propertiesNode = schemaNode.get("properties");
        if (propertiesNode != null && propertiesNode.isObject()) {
            propertiesNode.fields().forEachRemaining(entry -> {
                String propertyName = entry.getKey();
                JsonNode propertySchema = entry.getValue();
                JsonNode propertyValue = generateJsonNode(propertyName, propertySchema, demandFormInfo, replacers);
                if (Objects.nonNull(propertyValue) && propertyValue.toString().contains("Yet to implement function")) {
                    properitiesNotResolved.add(propertyName);
                }
                rootNode.set(propertyName, propertyValue);
            });
        }

        return rootNode;
    }

    private static JsonNode generateJsonNode(String propertyName, JsonNode propertySchema, Map<String, String> demandFormInfo, Map<String, Object> replacers) {
        ObjectMapper mapper = new ObjectMapper();
        String type = propertySchema.get("type").asText();

        switch (type.toLowerCase()) {
            case "string":
                if (replacers.containsKey(propertyName)) {
                    return JsonNodeFactory.instance.textNode(replacers.get(propertyName).toString());
                }
                if (demandFormInfo.containsKey(propertyName)) {
                    return JsonNodeFactory.instance.textNode(demandFormInfo.get(propertyName));
                }
                if (Objects.nonNull(propertySchema.get("allow"))) {
                    return JsonNodeFactory.instance.textNode("");
                }
                return JsonNodeFactory.instance.textNode("Yet to implement function");
            case "integer":
                if (replacers.containsKey(propertyName)) {
                    return JsonNodeFactory.instance.numberNode(Integer.valueOf(replacers.get(propertyName).toString()));
                }
                return JsonNodeFactory.instance.textNode("Yet to implement function");
            case "object":
                if (replacers.containsKey(propertyName)) {
                    return JsonNodeFactory.instance.pojoNode(replacers.get(propertyName));
                }
                return generateJsonObjectFromSchema(propertySchema, demandFormInfo, replacers);
            case "array":
                if (replacers.containsKey(propertyName)) {
                    if (propertyName.equalsIgnoreCase("skill")) {
                        return JsonNodeFactory.instance.pojoNode(replacers.get(propertyName));
                    }
                    return JsonNodeFactory.instance.textNode(replacers.get(propertyName).toString());
                }
                ObjectNode rootNode = mapper.createObjectNode();
                Iterator<JsonNode> arrayElements = propertySchema.elements();
                List<JsonNode> result = new ArrayList<>();
                arrayElements.forEachRemaining(entry -> {
                    JsonNode exampleValue = generateJsonObjectFromSchema(entry, demandFormInfo, replacers);
                    if (exampleValue.size() != 0) {
                        result.add(exampleValue);
                    }

                });
                return  JsonNodeFactory.instance.arrayNode(result.size()).addAll(result);
            default:
                return JsonNodeFactory.instance.nullNode();
        }
    }
}
