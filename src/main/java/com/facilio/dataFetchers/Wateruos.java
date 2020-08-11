package com.facilio.dataFetchers;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.ConnectionApiContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Wateruos extends DataFetcher {
    private static final Logger LOGGER = LogManager.getLogger(Wateruos.class.getName());

    JSONObject getData() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of( "UTC" ));
        ConnectionApiContext connectionApiContext = ConnectionUtil.getConnectionApi("Wateruos");
        Map<String,String> params = new HashMap<>();
        params.put("startDate",getFromTime(now,15));
        params.put("endDate",getToTime(now,15));
        String res;
        if (connectionApiContext!=null){
            res = (String)connectionApiContext.execute(params);
            LOGGER.info("Logging Response from "+connectionApiContext.getName()+"\n"+res);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(res);
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
        }else{
            LOGGER.error("connectionApiContext is null");
            return null;
        }
    }

    List<JSONObject> preProcess(Object o) {
        List<JSONObject> list = new ArrayList();
        JSONObject data = (JSONObject) o;
        String probeId = data.get("ProbeID").toString();
        String timestamp = ""+toMillis(data.get("Timestamp").toString());
        data.remove("Timestamp");
        data.remove("UTC_Offset");
        data.remove("ProbeID");
        data.remove("Sample_Id");
        data.remove("Group");
        JSONObject timeSeriesData= new JSONObject();
        timeSeriesData.put("agent", getAgent());
        timeSeriesData.put("PUBLISH_TYPE","timeseries");
        timeSeriesData.put("timestamp",timestamp);
        timeSeriesData.put("ProbeID-"+probeId,data);
        list.add(timeSeriesData);
        return list;
    }


    private static long toMillis(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date dt = sdf.parse(timestamp);
            return (dt.getTime());
        } catch (java.text.ParseException e) {
            LOGGER.error("Error while converting timestamp from string to long");
            System.out.println(e.getMessage());
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
}
