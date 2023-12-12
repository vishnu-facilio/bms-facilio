package com.facilio.bmsconsole.templates;

import com.facilio.bmsconsole.util.FreeMarkerAPI;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseMailTemplate extends Template{
    protected static final Logger LOGGER = LogManager.getLogger(BaseMailTemplate.class.getName());

    @Override
    protected JSONObject getParsedJson(JSONObject json, Map<String, Object> params, Map<String, Object> parameters) throws Exception {
        JSONObject parsedJson = new JSONObject();
        if (MapUtils.isNotEmpty(params)) {
            if (isFtl()) {
                // StrSubstitutor.replace(jsonStr, params);
                for (Object key : json.keySet()) {
                    Object value = json.get(key);
                    if (value != null) {
                        if (value instanceof JSONArray) {
                            JSONArray newArray = new JSONArray();
                            for (Object arrayVal : (JSONArray) value) {
                                newArray.add(FreeMarkerAPI.processTemplate(arrayVal.toString(), params));
                            }
                            parsedJson.put(key, newArray);
                        } else {
                            parsedJson.put(key, FreeMarkerAPI.processTemplate(value.toString(), params));
                        }
                    }
                }
                parameters.put("mailType", "html");
            } else {
                String emailMessage = (String) json.remove("message");
                String jsonStr = null;
                try {
                    String messageStr = StringSubstitutor.replace(emailMessage, encodeHtmlValueForEmailMessage(params));
                    json.put("message",messageStr);
                    for (String key : params.keySet()) {
                        Object value = params.get(key);
                        if (value != null) {
                            value = StringEscapeUtils.escapeJava(value.toString());
                            params.put(key, value);
                        }
                    }

                    jsonStr = json.toJSONString();

                    jsonStr = StringSubstitutor.replace(jsonStr, params);
                    JSONParser parser = new JSONParser();
                    parsedJson = (JSONObject) parser.parse(jsonStr);
                } catch (Exception e) {
                    LOGGER.error(new StringBuilder("Error occurred during replacing of place holders \n")
                            .append("JSON : ")
                            .append(jsonStr)
                            .append("\nParams : ")
                            .append(params)
                            .append("\nParameters : ")
                            .append(parameters)
                            .toString(), e);
                    throw e;
                }
            }
        } else {
            parsedJson.putAll(json);
        }
        return parsedJson;
    }
    protected Map encodeHtmlValueForEmailMessage(Map<String, Object> params){
        Map<String, Object>  cloneParams = new HashMap();
        for(String key:params.keySet()){
            Object value=params.get(key);
            if(value instanceof String) {
                cloneParams.put(key,StringEscapeUtils.escapeHtml4(value.toString()));
            }
        }
        return cloneParams;
    }
}
