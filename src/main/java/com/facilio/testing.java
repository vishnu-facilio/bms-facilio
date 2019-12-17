package com.facilio;

import com.facilio.agent.PublishType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.controller.FacilioDataType;
import com.facilio.agentv2.AgentConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;


public class testing {


    private static String IDENTIFIER_SEPERATOR = "_#_";

    static JSONObject getControllerJSON(String agentName){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.PUBLISH_TYPE, PublishType.controllers.getKey());
        jsonObject.put(AgentConstants.EXCEPTION,null);
        jsonObject.put(AgentConstants.AGENT,agentName);
        jsonObject.put(AgentConstants.VERSION,"2");

        JSONObject controllerJSON = new JSONObject();
        //controllerJSON.put(AgentConstants.IDENTIFIER, FacilioControllerType.MISC.asInt() +"test");
        controllerJSON.put(AgentConstants.IP_ADDRESS,"4.4.4.4");

        controllerJSON.put(AgentConstants.LAST_DATA_SENT_TIME,System.currentTimeMillis());
        controllerJSON.put(AgentConstants.ACTIVE,true);
        controllerJSON.put(AgentConstants.CONTROLLER_PROPS,new JSONObject());
        controllerJSON.put(AgentConstants.DELETED_TIME,-1);
        controllerJSON.put(AgentConstants.WRITABLE,true);
        controllerJSON.put(AgentConstants.AVAILABLE_POINTS,11);
        controllerJSON.put(AgentConstants.LAST_MODIFIED_TIME,System.currentTimeMillis());
        //controllerJSON.put(AgentConstants.ID,9);
        controllerJSON.put(AgentConstants.DATA_INTERVAL,15);
        controllerJSON.put(AgentConstants.PORT_NUMBER,0);

        //controllerJSON.putAll(getBacnetControllerJSON(4,3,1));
        controllerJSON.putAll(getModbusRtuController(6,2));
        //controllerJSON.putAll(getOpcUaControllerJSON("www.test.com","test cert path","password",101,101));
        //controllerJSON.putAll(getOpcXmlDaControllerJSON("vijay","www.test.com","success@123"));
        //controllerJSON.putAll(getModbusTcpController(108,"ip.m.tcp.success"));
        //controllerJSON.putAll(getNiagaraController("niagara.test.success",101));
        //controllerJSON.putAll(getBacnetControllerJSON(2,2,2));
        //controllerJSON.putAll(getMiscControllerJSON());


        controllerJSON.put(AgentConstants.NAME,"miscController");
        controllerJSON.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());

        JSONArray controllerArray = new JSONArray();
        controllerArray.add(controllerJSON);
        jsonObject.put(AgentConstants.DATA,controllerArray);

        return jsonObject;
    }

    private static Map getMiscControllerJSON() {

        Map<String, Object> controllerJSON = new HashMap<>();
        controllerJSON.put(AgentConstants.TYPE, FacilioControllerType.MISC.asInt());
        controllerJSON.put(AgentConstants.IDENTIFIER," trst misc controller");
        return controllerJSON;
    }

    private static JSONObject getBacnetControllerJSON(int istanceNumber, int networkNumber, int ipAddress) {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.BACNET_IP.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.BACNET_IP.asInt()+IDENTIFIER_SEPERATOR+istanceNumber+IDENTIFIER_SEPERATOR+networkNumber+IDENTIFIER_SEPERATOR+ipAddress);
        object.put(AgentConstants.INSTANCE_NUMBER,istanceNumber);
        object.put(AgentConstants.NETWORK_NUMBER,networkNumber);
        object.put(AgentConstants.IP_ADDRESS,ipAddress);
        return object;
    }

    private static JSONObject getModbusRtuController(int networkId,int slaveId) {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.MODBUS_RTU.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.MODBUS_RTU.asInt()+IDENTIFIER_SEPERATOR+networkId+IDENTIFIER_SEPERATOR+slaveId);
        object.put(AgentConstants.NETWORK_ID,networkId);
        object.put(AgentConstants.SLAVE_ID,slaveId);
        return object;
    }

    static JSONObject getModbusTcpController(long slaveId,String ip){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.MODBUS_IP.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.MODBUS_IP.asInt()+IDENTIFIER_SEPERATOR+ip+IDENTIFIER_SEPERATOR+slaveId);
        object.put(AgentConstants.SLAVE_ID,slaveId);
        object.put(AgentConstants.IP_ADDRESS,ip);
        return object;
    }

    static JSONObject getNiagaraController(String ipAdress, int portNumber){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.NIAGARA.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.NIAGARA.asInt()+IDENTIFIER_SEPERATOR+portNumber+IDENTIFIER_SEPERATOR+ipAdress);
        object.put(AgentConstants.IP_ADDRESS,ipAdress);
        object.put(AgentConstants.PORT_NUMBER,portNumber);
        return object;
    }

    static JSONObject getOpcUaControllerJSON(String url, String certPath, String password, int sm, int sp){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.OPC_UA.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.OPC_UA.asInt()+IDENTIFIER_SEPERATOR+url+IDENTIFIER_SEPERATOR+certPath);
        object.put(AgentConstants.URL,url);
        object.put(AgentConstants.CERT_PATH,certPath);
        object.put(AgentConstants.PASSWORD,password);
        object.put(AgentConstants.SECURITY_MODE,sm);
        object.put(AgentConstants.SECURITY_POLICY,sp);
        return object;
    }

    static JSONObject getOpcXmlDaControllerJSON(String userName,String url,String password){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.TYPE, FacilioControllerType.OPC_XML_DA.asInt());
        object.put(AgentConstants.IDENTIFIER,FacilioControllerType.OPC_XML_DA.asInt()+IDENTIFIER_SEPERATOR+userName+IDENTIFIER_SEPERATOR+url+IDENTIFIER_SEPERATOR+password);
        object.put(AgentConstants.URL,url);
        object.put(AgentConstants.USER_NAME,userName);
        object.put(AgentConstants.PASSWORD,password);
        return object;
    }

    /**
     *  setName((String) row.get(AgentConstants.NAME));
     *         setDataType(FacilioDataType.valueOf((Integer) row.get(AgentConstants.DATA_TYPE)));
     *         setControllerId((Long) row.get(AgentConstants.CONTROLLER_ID));
     *         setWritable((Boolean) row.get(AgentConstants.WRITABLE));
     *         setInUse((Boolean) row.get(AgentConstants.IN_USE));
     *         setSubscribed((Boolean) row.get(AgentConstants.SUBSCRIBED));
     *         setThresholdJSON((String) row.get(AgentConstants.THRESHOLD_JSON));
     *         setCreatedTime((Long) row.get(AgentConstants.CREATED_TIME));
     *         setUnit((Integer) row.get(AgentConstants.UNIT));
     * @param agentName
     * @param controllerIdentifier
     * @return
     */
    static JSONObject getDiscoverPointsJSON(String agentName,String controllerIdentifier){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.PUBLISH_TYPE, com.facilio.agent.fw.constants.PublishType.DEVICE_POINTS.asInt());
        jsonObject.put(AgentConstants.EXCEPTION,null);
        jsonObject.put(AgentConstants.AGENT,agentName);
        jsonObject.put(AgentConstants.VERSION,"2");
        jsonObject.put(AgentConstants.CONTROLLER,controllerIdentifier);
        jsonObject.put(AgentConstants.TYPE,FacilioControllerType.MODBUS_RTU.asInt());
        //jsonObject.put(AgentConstants.SITE_ID,2);

        JSONObject pointJSON = new JSONObject();
        pointJSON.put(AgentConstants.NAME,"mrp2");
        pointJSON.put(AgentConstants.DATA_TYPE, FacilioDataType.STRING.asInt());
        //pointJSON.put(AgentConstants.CONTROLLER_ID,5);
        pointJSON.put(AgentConstants.WRITABLE,true);
        pointJSON.put(AgentConstants.IN_USE,false);
        pointJSON.put(AgentConstants.SUBSCRIBED,true);
        pointJSON.put(AgentConstants.THRESHOLD_JSON,new JSONObject().toString());
        pointJSON.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());
        pointJSON.put(AgentConstants.UNIT,2);
        //pointJSON.putAll(getMiscPointJSON("path/test/success"));
        //pointJSON.putAll(getBacnetPointJSON(4,1));
        pointJSON.putAll(getModbusTcpPoint(4,3,3));
        //pointJSON.putAll(getOpcXmlDaPointJSON("path/test/success"));
        //pointJSON.putAll(getOpcUaPointJSON(108,"identifier"));
        //pointJSON.putAll(getModbusPoint(1,1,1));
        //pointJSON.putAll(getMiscPointJSON("hello path for misc point"));

        JSONArray pointsArray = new JSONArray();
        pointsArray.add(pointJSON);
        jsonObject.put(AgentConstants.DATA,pointsArray);
        return jsonObject;
    }

    private static JSONObject getModbusRtuPoint(int registerNumber,int functionCode, int modbusDataType) {
        JSONObject point = new JSONObject();
        point.put(AgentConstants.REGISTER_NUMBER, registerNumber);
        point.put(AgentConstants.FUNCTION_CODE,functionCode);
        point.put(AgentConstants.MODBUS_DATA_TYPE,modbusDataType);
        point.put(AgentConstants.POINT_TYPE,FacilioControllerType.MODBUS_RTU.asInt());
        return point;
    }

    private static JSONObject getModbusPoint(int dataType,int funcCode,int registerNumber) {
        JSONObject point = new JSONObject();
        point.put(AgentConstants.MODBUS_DATA_TYPE,dataType);
        point.put(AgentConstants.FUNCTION_CODE,funcCode);
        point.put(AgentConstants.REGISTER_NUMBER,registerNumber);
        return point;
    }

    static JSONObject getBacnetPointJSON(int instanceNumber,int instanceType){
        JSONObject point = new JSONObject();
        point.put(AgentConstants.INSTANCE_NUMBER,instanceNumber);
        point.put(AgentConstants.INSTANCE_TYPE,instanceType);
        return point;
    }

    static JSONObject getOpcUaPointJSON(int namespace,String identifier){
        JSONObject point = new JSONObject();
        point.put(AgentConstants.NAMESPACE,namespace);
        point.put(AgentConstants.IDENTIFIER,identifier);
        return point;
    }

    static JSONObject getOpcXmlDaPointJSON(String path){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.PATH,path);
        return object;
    }

    static JSONObject getModbusTcpPoint( int registerNumber, int functionCode, int modbusDataType){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.REGISTER_NUMBER, registerNumber);
        object.put(AgentConstants.FUNCTION_CODE,functionCode);
        object.put(AgentConstants.MODBUS_DATA_TYPE,modbusDataType);
        object.put(AgentConstants.POINT_TYPE,FacilioControllerType.MODBUS_IP.asInt());
        //object.put(AgentConstants.FUNCTION_CODE,functionCode);
        return object;
    }

    static JSONObject getMiscPointJSON(String path){
        JSONObject object = new JSONObject();
        object.put(AgentConstants.PATH,path);
        object.put(AgentConstants.PSEUDO,1);
        return object;
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {

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

            String data = "{\"status\":3,\n" +
                    "\"agent\":\"iamcvijay\",\n" +
                    "\"clientId\":1909,\n" +
                    "\"networkNumber\":0,\n" +
                    "\"PUBLISH_TYPE\": \"devicepoints\",\n" +
                    "\"msgid\":1235,\n" +
                    "\"instanceNumber\":7,\n" +
                    "\"deviceName\":\"PANORAMA_IN_JA_BRSH_3895_7\"\n" +
                    ",\"deviceId\":\"emrill\",\n" +
                    "\"interval\":1,\n" +
                    "\"broadcastAddress\":\"1717.189.255\",\n" +
                    "\"macAddress\":\"7_192.168.1:47808_0\",\n" +
                    "\"subnetPrefix\":241,\n" +
                    "\"device\":\"emrill\"}";
            // "{\"status\":\"EXECUTED\",\"agent\":\"iamcvijay\",\"clientId\":1909,\"networkNumber\":0,\"PUBLISH_TYPE\":\"ack\",\"msgid\":685,\"instanceNumber\":7,\"message\":\"xxxxx\",\"deviceName\":\"PANORAMA_IN_GREENS_JA_BRSH_3895_7\",\"interval\":1,\"command\":\"discoverPoints\",\"broadcastAddress\":\"172.17.189.255\",\"macAddress\":\"7_192.168.1.5:47808_0\",\"subnetPrefix\":24,\"status\":1,\"device\":\"emrill\"}";

            String testData =
                    "{\"PUBLISH_TYPE\":\"agent\",\n" +
                            "\"clientId\":2635,\"networkNumber\":1,\"msgid\":1535,\"agent\":\"iamcvijay\",\"state\":1,\"instanceNumber\":1101,\"message\":\"Executed\",\"deviceName\":\"ECY-S1000-EF99A8\",\"deviceId\":\"Win-A2EB-2F0C-77D3-B783\",\"command\":\"unsubscribe\",\"broadcastAddress\":\"Win-A2EB-2F0C-77D3-B783\",\"macAddress\":\"1101_c0 a8 01 65 ba c0_1\",\"subnetPrefix\":2,\"status\":3}";
            Producer<String, String> producer = new KafkaProducer<>(props);
            JSONObject jsonObject = new JSONObject();


            JSONParser parser = new JSONParser();
            JSONObject stringStr = (JSONObject) parser.parse(getStringJSON());
            jsonObject.put("data", stringStr.toString());
            //jsonObject.put("data",getControllerJSON("iamcvijay").toString());
            //jsonObject.put("data",getDiscoverPointsJSON("iamcvijay","5_#_6_#_2").toString());
            System.out.println(" sending " + jsonObject.toJSONString());
            producer.send(new ProducerRecord<String, String>("iamcvijay", "1", (jsonObject.toJSONString())));

            producer.close();
        }
    }

    private static String getStringJSON() {

        //return "{\"agent\":\"cofelybesix\",\"FCU-GF-04\":{\"BV 40\":1,\"BV 21\":1,\"Unoccupied Cooling SP\":26.0,\"BV 56\":1,\"Current Cooling SP\":24.0,\"Setpoint (SP)\":24.0,\"BV 50\":1,\"BV 61\":0,\"BV 60\":0,\"AV 20\":3.0,\"AV 41\":4.0,\"AV 01\":100.0,\"Microset Room Temp.\":25.0,\"AV 82\":9196.0},\"PUBLISH_TYPE\":\"timeseries\",\"deviceId\":\"FCU-GF-04\",\"timestamp\":1576212077361}";

     return "{\n" +
             "  \"agent\": \"sam\",\n" +
             "  \"data\": [\n" +
             "    {\n" +
             "      \"props\": \"controllerProps\",\n" +
             "      \"lastDataSentTime\": 1576074634839,\n" +
             "      \"identifier\": \"1_#_23_#_0_#_192.168.1.140\",\n" +
             "      \"instanceNumber\": 23,\n" +
             "      \"ip\": \"192.168.1.140\",\n" +
             "      \"id\": 1,\n" +
             "      \"type\": 1,\n" +
             "      \"availablePoints\": -1,\n" +
             "      \"lastModifiedTime\": 1576074634839,\n" +
             "      \"name\": \"spinfo_23\",\n" +
             "      \"networkNumber\": 0,\n" +
             "      \"portNumber\": 0,\n" +
             "      \"interval\": 900000,\n" +
             "      \"active\": false,\n" +
             "      \"writable\": false,\n" +
             "      \"createdTime\": 1576074634839,\n" +
             "      \"deletedTime\": -1\n" +
             "    },\n" +
             "    {\n" +
             "      \"props\": \"controllerProps\",\n" +
             "      \"lastDataSentTime\": 1576074634839,\n" +
             "      \"identifier\": \"1_#_1002_#_0_#_192.168.1.2\",\n" +
             "      \"instanceNumber\": 1002,\n" +
             "      \"ip\": \"192.168.1.2\",\n" +
             "      \"id\": 2,\n" +
             "      \"type\": 1,\n" +
             "      \"availablePoints\": -1,\n" +
             "      \"lastModifiedTime\": 1576074634839,\n" +
             "      \"name\": \"ECY-S1000-EF99A8\",\n" +
             "      \"networkNumber\": 0,\n" +
             "      \"portNumber\": 0,\n" +
             "      \"interval\": 900000,\n" +
             "      \"active\": false,\n" +
             "      \"writable\": false,\n" +
             "      \"createdTime\": 1576074634839,\n" +
             "      \"deletedTime\": -1\n" +
             "    },\n" +
             "    {\n" +
             "      \"props\": \"controllerProps\",\n" +
             "      \"lastDataSentTime\": 1576074634839,\n" +
             "      \"identifier\": \"1_#_11_#_0_#_192.168.1.221\",\n" +
             "      \"instanceNumber\": 11,\n" +
             "      \"ip\": \"192.168.1.221\",\n" +
             "      \"id\": 3,\n" +
             "      \"type\": 1,\n" +
             "      \"availablePoints\": -1,\n" +
             "      \"lastModifiedTime\": 1576074634839,\n" +
             "      \"name\": \"Fliostation_11\",\n" +
             "      \"networkNumber\": 0,\n" +
             "      \"portNumber\": 0,\n" +
             "      \"interval\": 900000,\n" +
             "      \"active\": false,\n" +
             "      \"writable\": false,\n" +
             "      \"createdTime\": 1576074634839,\n" +
             "      \"deletedTime\": -1\n" +
             "    }\n" +
             "  ],\n" +
             "  \"publishType\": 7,\n" +
             "  \"version\": \"2\"\n" +
             "}";
        /* return "{\n" +
                "\n" +
                "    \"agent\":\"wattsense-stctb\",\n" +
                "    \"controller\":\"7_#_opc.tcp://192.168.2.214:53530/OPCUA/SimulationServer/discovery\",\n" +
                "    \"data\":[\n" +
                "        {\n" +
                "            \"name\":\"Objects_0_85\",\n" +
                "            \"value\":2\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\":\"BuildInfo_0_2260\",\n" +
                "            \"value\":12\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\":\"ProductName_0_2261\",\n" +
                "            \"value\":4\n" +
                "        }\n" +
                "    ],\n" +
                "    \"publishType\":6,\n" +
                "    \"failure\":0,\n" +
                "    \"type\":7,\n" +
                "    \"version\":\"2\"\n" +
                "\n" +
                "}";*/
    }
}