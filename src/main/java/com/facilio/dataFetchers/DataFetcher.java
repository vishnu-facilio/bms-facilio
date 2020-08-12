package com.facilio.dataFetchers;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;

public abstract class DataFetcher {
    private static final Logger LOGGER = LogManager.getLogger(DataFetcher.class.getName());
    protected FacilioAgent agent;

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public FacilioAgent getAgent() {
        return this.agent;
    }

    abstract Object getData() throws Exception;

    abstract List<JSONObject> preProcess(Object o);

    public void process() {
        try {
            Object data = getData();
            LOGGER.info("Data : " + data.toString());
            List<JSONObject> timeSeriesData = preProcess(data);
            LOGGER.info("TimeSeriesData :" + timeSeriesData);
            for (JSONObject item : timeSeriesData) {
                TimeSeriesAPI.processPayLoad(0, item, null);
            }
        } catch (Exception ex) {
            LOGGER.info("Error while getting/Processing Data from endpoint");
            LOGGER.info(ex.getMessage());
            LOGGER.info(ex.getStackTrace());
            ex.printStackTrace();
        }
    }
}
