package com.facilio.agent.integration.queue.preprocessor;

import com.facilio.agent.integration.queue.AgentMessageIntegrationProcessorType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for pre processing the data from AltairSmartEdge
 */
public class AltairSmartEdge implements AgentMessagePreProcessor {
    private static final Logger LOGGER = LogManager.getLogger(AltairSmartEdge.class.getName());

    @Override
    public List<JSONObject> preProcess(Object o)  {
        JSONObject timeSeriesData = new JSONObject();
        timeSeriesData.put("agent", AgentMessageIntegrationProcessorType.ALTAIR.getLabel());
        timeSeriesData.put("PUBLISH_TYPE","timeseries");
        timeSeriesData.put("timestamp",System.currentTimeMillis());

        JSONArray array = (JSONArray) ((JSONObject) o).get("events");
        JSONObject data = new JSONObject();
        for (int i=0;i<array.size();i++){
            data.put(((JSONObject)array.get(i)).get("label"),((JSONObject)array.get(i)).get("value"));
        }
        timeSeriesData.put(((JSONObject)array.get(0)).get("deviceCd"),data);
        LOGGER.info("Timeseries Data :"+timeSeriesData);

        List<JSONObject> messages = new ArrayList<>();
        messages.add(timeSeriesData);
        return messages;
    }
}
