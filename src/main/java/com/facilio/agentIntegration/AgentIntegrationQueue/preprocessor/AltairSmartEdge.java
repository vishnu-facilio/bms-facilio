package com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor;

import com.facilio.agentIntegration.AgentIntegrationQueue.AgentMessageIntegrationProcessorType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Implementation for pre processing the data from AltairSmartEdge
 */
public class AltairSmartEdge implements AgentIntegrationPreprocessor {
    private static final Logger LOGGER = LogManager.getLogger(AltairSmartEdge.class.getName());

    @Override
    public JSONObject preProcess(Object o)  {
        LOGGER.info("anand.h 2002");
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
        LOGGER.info("anand.h 2003 "+timeSeriesData);
        return timeSeriesData;
    }


}
