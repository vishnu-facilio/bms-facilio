package com.facilio.kafka;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor extends FacilioProcessor {
    private final List<FacilioField> fields = new ArrayList<>();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private EventUtil eventUtil;
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private long orgId;
    private String orgDomainName;


    private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());


    public Processor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        String clientName = orgDomainName + "-processor-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap();
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        initializeModules();
        setEventType("processor");
        LOGGER.info("Initializing processor " + orgDomainName);
    }

    private void initializeModules() {

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
        deviceIdField.setName("deviceId");
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());

        deviceMap.putAll(getDeviceMap());
    }


    @Override
    public void processRecords(List<FacilioRecord> records) {

        for (FacilioRecord record : records) {
                boolean alarmCreated = false;
                long numberOfRows = 0;
            try {
                String data = "";
                data = record.getData().toString();
                if (data.isEmpty()) {
                    continue;
                }

                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                List<EventRuleContext> ruleList = bean.getActiveEventRules();
                if (ruleList != null) {
                    eventRules = ruleList;
                }

                JSONParser parser = new JSONParser();
                JSONObject payLoad = (JSONObject) parser.parse(data);


                String dataType = AgentKeys.EVENT;
                if (payLoad.containsKey(EventUtil.DATA_TYPE)) {
                    dataType = (String) payLoad.remove(EventUtil.DATA_TYPE);
                }

                String agentName = orgDomainName;
                if (payLoad.containsKey(AgentKeys.AGENT)) {
                    agentName = payLoad.remove(AgentKeys.AGENT).toString();
                }

                String deviceId = orgDomainName;
                if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
                    deviceId = payLoad.get(AgentKeys.DEVICE_ID).toString();
                }

                long lastMessageReceivedTime = System.currentTimeMillis();
                if (payLoad.containsKey(AgentKeys.TIMESTAMP)) {
                    Object lastTime = payLoad.get(AgentKeys.TIMESTAMP);
                    lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
                }

                FacilioAgent agent = agentUtil.getFacilioAgent(agentName);
                if (agent == null && !AgentKeys.AGENT.equals(dataType)) {
                    agent = getFacilioAgent(agentName);
                    agentUtil.addAgent(agent);
                }

                HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
                long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);

                if (deviceLastMessageTime != lastMessageReceivedTime) {
                    String partitionKey = "";
                    try {
                        partitionKey = record.getPartitionKey();
                        if (dataType != null) {
                            switch (dataType) {
                                case AgentKeys.TIMESERIES:
                                    processTimeSeries(record);
                                    break;
                                case AgentKeys.DEVICE_POINTS:
                                    devicePointsUtil.processDevicePoints(payLoad, orgId, deviceMap, agent.getId());
                                    break;
                                case AgentKeys.ACK:
                                    ackUtil.processAck(payLoad, orgId);
                                    break;
                                case AgentKeys.AGENT:
                                    numberOfRows = agentUtil.processAgent(payLoad);
                                    break;
                                case AgentKeys.EVENT:
                                    alarmCreated = eventUtil.processEvents(record.getTimeStamp(), payLoad, record.getPartitionKey(), orgId, eventRules);
                                    break;

                            }
                            dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                            deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                        }if (numberOfRows == 0) {
                            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.TABLE_NAME).fields(FieldFactory.getAgentDataFields()).andCustomWhere(AgentKeys.NAME + "= '" + payLoad.get(AgentKeys.AGENT) + "'");
                            Map<String, Object> toUpdate = new HashMap<>();
                            toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                            genericUpdateRecordBuilder.update(toUpdate);

                        }

                    }
                    catch (Exception e) {
                        CommonCommandUtil.emailException("Processor", "Error in processing records ", e, payLoad.toJSONString());
                        LOGGER.info("Exception occurred ", e);
                    } finally {
                        updateDeviceTable(partitionKey);
                        if (alarmCreated) {
                            getConsumer().commit(record);
                        }
                    }
                }
                else {
                    LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType);
                }
            }
            catch (Exception e) {
                LOGGER.info("Exception occured", e);
            }

        }
    }

    private void processTimeSeries(FacilioRecord record) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",orgId);
        bean.processTimeSeries(getConsumer(), record);
    }

    private FacilioAgent getFacilioAgent(String agentName) {
        FacilioAgent agent = new FacilioAgent();
        agent.setAgentName(agentName);
        agent.setAgentConnStatus(Boolean.TRUE);
        agent.setAgentState(1);
        agent.setAgentDataInterval(900000L);
        return agent;
    }

    private void updateDeviceTable(String deviceId) {
        try {
            if( ! deviceMap.containsKey(deviceId)) {
                addDeviceId(deviceId);
            }
            if(deviceMap.containsKey(deviceId)) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("lastUpdatedTime", System.currentTimeMillis());
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(deviceDetailsModule.getTableName())
                        .fields(fields).andCondition(getDeviceIdCondition(deviceId));
                builder.update(map);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while updating time for device id " + deviceId, e);
        }
    }

    private Condition getDeviceIdCondition(String deviceId) {
        return  CriteriaAPI.getCondition("DEVICE_ID", "DEVICE_ID", deviceId, StringOperators.IS);
    }

    private HashMap<String, Long> getDeviceMap() {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName()).select(fields);
        HashMap<String, Long> deviceData = new HashMap<>();
        try {
            List<Map<String, Object>> data = builder.get();
            for(Map<String, Object> obj : data) {
                String deviceId = (String)obj.get("deviceId");
                Long id = (Long)obj.get("id");
                deviceData.put(deviceId, id);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting device data", e);
        }

        return deviceData;
    }

    private void addDeviceId(String deviceId) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(fields);
        HashMap<String, Object> device = new HashMap<>();
        device.put("orgId", orgId);
        device.put("deviceId", deviceId);
        device.put("inUse", true);
        device.put("lastUpdatedTime", System.currentTimeMillis());
        device.put("lastAlertedTime", 0L);
        device.put("alertFrequency", 2400000L);
        long id = builder.insert(device);
        deviceMap.put(deviceId, id);
    }


}
