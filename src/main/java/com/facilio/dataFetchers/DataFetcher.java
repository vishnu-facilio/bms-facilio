package com.facilio.dataFetchers;

import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public abstract class DataFetcher {
 private static final Logger LOGGER = LogManager.getLogger(Wateruos.class.getName());

 abstract Object getData() throws Exception;
 abstract JSONObject preProcess(Object o);
 public void process(){
  JSONObject data;
  try {
   LOGGER.info("process called");
   data = (JSONObject) getData();
   LOGGER.info("Data: "+data);
   JSONObject timeSeriesData= preProcess(data);
   LOGGER.info("TimeSeriesData" + timeSeriesData);
   //TimeSeriesAPI.processPayLoad(0,timeSeriesData,null);
    LOGGER.info("anand.h@facilio.com "+ timeSeriesData);
  }catch(Exception ex){
   LOGGER.error("Error while getting/Processing Data from endpoint");
   LOGGER.error(ex.getMessage());
  }
 }
}
