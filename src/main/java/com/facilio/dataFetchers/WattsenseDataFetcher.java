package com.facilio.dataFetchers;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.integration.wattsense.WattsenseClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WattsenseDataFetcher extends DataFetcher {
    private static final Logger LOGGER = LogManager.getLogger(WattsenseDataFetcher.class.getName());
    private WattsenseClient client;

    public WattsenseDataFetcher(WattsenseClient client) {
        this.client = client;
    }

    @Override
    Object getData() throws Exception {
        if (client != null) {
            return client.getData();
        } else {
            LOGGER.info("client is null");
            return null;
        }
    }

    @Override
    List<JSONObject> preProcess(Object o) {
        List<JSONObject> list = new ArrayList<>();
        try {
            Map<String, JSONArray> controllerVsProperties = (Map<String, JSONArray>) o;
            if (controllerVsProperties.size() > 0) {
                controllerVsProperties.forEach((k, v) -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("agent", getAgent().getName());
                    jsonObject.put("publishType", 6);
                    JSONObject controllerObject = new JSONObject();
                    controllerObject.put("name", k);
                    jsonObject.put("controller", controllerObject);
                    jsonObject.put("controllerType", FacilioControllerType.MISC.asInt());
                    JSONArray data = new JSONArray();
                    JSONObject pointVsValues = new JSONObject();
                    for (Object item : v) {
                        JSONObject property = (JSONObject) item;
                        String pointName = property.get("property").toString();
                        String value = property.get("payload").toString();
                        pointVsValues.put(pointName, value);
                    }
                    data.add(pointVsValues);
                    jsonObject.put("data", data);
                    list.add(jsonObject);
                });
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(e.getStackTrace());
        }
        return list;
    }
}
