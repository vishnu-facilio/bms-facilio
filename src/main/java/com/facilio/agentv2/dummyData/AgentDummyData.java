package com.facilio.agentv2.dummyData;

import com.facilio.agent.PublishType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AgentDummyData
{
    public static JSONObject getControllerJSON(String name){
        JSONObject controllerJSON = new JSONObject();
        controllerJSON.put(AgentConstants.IP_ADDRESS,"ip");
        controllerJSON.put(AgentConstants.NAME,name);
        controllerJSON.put(AgentConstants.ACTIVE,true);
        controllerJSON.put(AgentConstants.AVAILABLE_POINTS,11);
        controllerJSON.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());
        controllerJSON.put(AgentConstants.LAST_MODIFIED_TIME,System.currentTimeMillis());
        controllerJSON.put(AgentConstants.LAST_DATA_SENT_TIME,System.currentTimeMillis());
        controllerJSON.put(AgentConstants.DATA_INTERVAL,15);
        controllerJSON.put(AgentConstants.WRITABLE,true);
        return controllerJSON;
    }

    public static JSONObject getBacnetControllerJSON(String controllerName,long networkNumber,long instanceNumber,String ip){
        JSONObject bacnetIpControllerDiscoverJSON = new JSONObject();
        bacnetIpControllerDiscoverJSON.putAll(getControllerJSON(controllerName));
        bacnetIpControllerDiscoverJSON.put(AgentConstants.TYPE,FacilioControllerType.BACNET_IP.asInt());
        bacnetIpControllerDiscoverJSON.put(AgentConstants.CONTROLLER,getBacnetIpControllerChildJSON(networkNumber, instanceNumber, ip));
        return bacnetIpControllerDiscoverJSON;
    }

    private static JSONObject getBacnetIpControllerChildJSON(long networkNumber, long instanceNumber, String ip) {
        JSONObject bacnetIpControllerDiscoverJSON = new JSONObject();
        bacnetIpControllerDiscoverJSON.put(AgentConstants.NETWORK_NUMBER,networkNumber);
        bacnetIpControllerDiscoverJSON.put(AgentConstants.INSTANCE_NUMBER,instanceNumber);
        bacnetIpControllerDiscoverJSON.put(AgentConstants.IP_ADDRESS,ip);
        return bacnetIpControllerDiscoverJSON;
    }


    public static JSONObject getBacnetIpDiscoverControllerResponseJSON(String agentName){
        JSONObject bacnetIpControllerDiscoveryJSON = new JSONObject();
        bacnetIpControllerDiscoveryJSON.putAll(getAgentJSON(agentName));
        bacnetIpControllerDiscoveryJSON.put(AgentConstants.PUBLISH_TYPE, PublishType.controllers.getKey());
        JSONArray controllerArray = new JSONArray();
        controllerArray.add(getBacnetControllerJSON("controller1",1,1,"1.1.1.1"));
        controllerArray.add(getBacnetControllerJSON("controller2",2,2,"2.2.2.2"));
        controllerArray.add(getBacnetControllerJSON("controller3",3,3,"3.3.3.3"));

        bacnetIpControllerDiscoveryJSON.put(AgentConstants.DATA,controllerArray);
        return bacnetIpControllerDiscoveryJSON;
    }

    public static JSONObject getBacnetIpPoints(int instanceType,int instanceNumber,String name){
        JSONObject bacnetIpPoints = new JSONObject();
        bacnetIpPoints.put(AgentConstants.INSTANCE_NUMBER,instanceNumber);
        bacnetIpPoints.put(AgentConstants.INSTANCE_TYPE,instanceType);
        bacnetIpPoints.put(AgentConstants.NAME,name);
        bacnetIpPoints.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());
        return bacnetIpPoints;
    }

    public static JSONObject getBAcnetDiscoverPointsJSON(String agentName,JSONObject controllerJSON){
        JSONObject bacntIpDiscoverPointsJSON = new JSONObject();
        bacntIpDiscoverPointsJSON.putAll(getAgentJSON(agentName));
        bacntIpDiscoverPointsJSON.put(AgentConstants.PUBLISH_TYPE, com.facilio.agent.fw.constants.PublishType.DEVICE_POINTS.asInt());
        bacntIpDiscoverPointsJSON.putAll(controllerJSON);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(getBacnetIpPoints(1,1,"bcip1"));
        jsonArray.add(getBacnetIpPoints(2,2,"bcip2"));
        jsonArray.add(getBacnetIpPoints(3,3,"bcip3"));
        bacntIpDiscoverPointsJSON.put(AgentConstants.DATA,jsonArray);
        return bacntIpDiscoverPointsJSON;
     }

    public static JSONObject getAgentJSON(String agentName){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.EXCEPTION,null);
        jsonObject.put(AgentConstants.AGENT,agentName);
        return jsonObject;
    }

    public static void main(String[] args) throws ParseException {
        //System.out.println(getBacnetIpDiscoverControllerResponseJSON("testAgent").toJSONString());
        //sendToKafka(getBacnetIpDiscoverControllerResponseJSON("testAgent"));
       //sendToKafka(getBAcnetDiscoverPointsJSON("testAgent",getBacnetControllerJSON("controller1",1,1,"1.1.1.1")));
        sendToKafka(parseString("{\n" +
                "  \"agent\": \"testAgent\",\n" +
                "  \"publishType\": 2,\n" +
                "  \"msgid\": 9,\n" +
                "  \"command\": 110,\n" +
                "  \"timestamp\": 1581579383354\n" +
                "}\n"));
    }

    public static JSONObject parseString(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(str);
    }

    public static void sendToKafka(JSONObject payload) {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        System.out.println(ids);
        System.out.println("here");
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        JSONObject jsonObject = new JSONObject();

        JSONParser parser = new JSONParser();
        jsonObject.put("data", payload.toString());
        //jsonObject.put("data",getControllerJSON("iamcvijay").toString());
        //jsonObject.put("data",getDiscoverPointsJSON("iamcvijay","5_#_6_#_2").toString());
        System.out.println(" sending " + jsonObject.toJSONString());
        producer.send(new ProducerRecord<String, String>("iamcvijay", "1", (jsonObject.toJSONString())));

        producer.close();

    }
}
