package com.facilio.bmsconsole.jobs;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DataFetcherJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DataFetcherJob.class.getName());
    private static final String URL = "http://wateruos.living-planit.com/api/GetSamplesByFilter?";


    private static JSONObject getData(String urL,int probeId, String from, String to) throws IOException, ParseException {

        String uRL=urL+"probeId="+probeId+"&startDate="+from+"&endDate="+to;
        URL url = new URL(urL+"probeId="+probeId+"&startDate="+from+"&endDate="+to);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer 5ErFG8xJf82ctX2JfIGSwfWQQJczfcilB2pbGMHc4=");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output;
        output = in.readLine();
        LOGGER.info("Data from URL"+uRL);
        LOGGER.info("Bearer "+"5ErFG8xJf82ctX2JfIGSwfWQQJczfcilB2pbGMHc4=");
        LOGGER.info(output);
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(output);
        long maxTimeStamp=0L;
        JSONObject selectedJSON= null;
        for (Object o:jsonArray){
            JSONObject jsonObject=(JSONObject) o;
            String timestamp = jsonObject.get("Timestamp").toString();
            if(timestamp!=null){
            if(toMillis(timestamp)>maxTimeStamp){
                maxTimeStamp=toMillis(jsonObject.get("Timestamp").toString());
                selectedJSON=jsonObject;
            }
            }else{
                LOGGER.error("Key Timestamp not found");
            }

        }
        LOGGER.info("selected json"+selectedJSON);
        return selectedJSON;
    }
    private static long toMillis(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date dt = sdf.parse(timestamp);
            return (dt.getTime());
        } catch (java.text.ParseException e) {
            LOGGER.error("Error while converting timestamp from string to long");
            return -1;
        }
    }

    private static String getToTime(ZonedDateTime now,int interval) {
        int nearestPastQuarter=(now.getMinute()/interval)*interval;
        int nearestPastHour = now.getHour();
        return(now.getYear()+"-"+
                new DecimalFormat("00").format(now.getMonthValue())+"-"+
                new DecimalFormat("00").format(now.getDayOfMonth())+"T"+
                new DecimalFormat("00").format(nearestPastHour) +":"+
                new DecimalFormat("00").format(nearestPastQuarter)+":"+
                "00");
    }

    private static String getFromTime(ZonedDateTime now,int interval) {
        now=now.minusMinutes(interval);
        int nearestPastQuarter=(now.getMinute()/interval)*interval;

        int nearestPastHour = now.getHour();
        return(now.getYear()+"-"+
                new DecimalFormat("00").format(now.getMonthValue())+"-"+
                new DecimalFormat("00").format(now.getDayOfMonth())+"T"+
                new DecimalFormat("00").format(nearestPastHour) +":"+
                new DecimalFormat("00").format(nearestPastQuarter)+":"+
                "01");

    }


    @Override
    public void execute(JobContext jc) throws Exception {


           ZonedDateTime now = ZonedDateTime.now(ZoneId.of( "UTC" ));
           JSONObject data;
           try {
               LOGGER.info("execute called");
               data = getData(getUrl(),getProbeId(), getFromTime(now,15), getToTime(now,15));
               LOGGER.info(data);
               System.out.println(data);
               JSONObject timeSeriesData= toTimeSeriesData(data);
               LOGGER.info(timeSeriesData);
               System.out.println(timeSeriesData);
               TimeSeriesAPI.processPayLoad(0,timeSeriesData,null);

           }catch(Exception ex){
               LOGGER.error("Error while getting/Processing Data from "+getUrl());
               LOGGER.error(ex.getMessage());
           }



    }

    private JSONObject toTimeSeriesData(JSONObject data) {
        JSONObject timeSeriesData= new JSONObject();
        timeSeriesData.put("agent", getAgent());
        timeSeriesData.put("PUBLISH_TYPE","timeseries");
        timeSeriesData.put("timestamp",""+toMillis(data.get("Timestamp").toString()));
        timeSeriesData.put(getController(),data);
        return timeSeriesData;
    }

    private String getUrl(){
        return "http://wateruos.living-planit.com/api/GetSamplesByFilter?";
    }

    private int getProbeId() {
        return 22;
    }

    private String getController() {
        return "Probe22";
    }

    private String getAgent() {
        return AccountUtil.getCurrentOrg().getDomain();
    }
}
