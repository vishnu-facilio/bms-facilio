package com.facilio.kafka.agent;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;
import com.facilio.kafka.FacilioKafkaConsumer;
import com.facilio.kafka.FacilioKafkaProducer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AgentProcessor is a dedicated processor for processing payloads with PUBLISH_TYPE set to 'agent', 'devicepoints' and 'ack'.
 *
 */
public class AgentProcessor extends FacilioProcessor
{
    private final List<FacilioField> fields = new ArrayList<>();
    private final Condition orgIdCondition = new Condition();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    private final FacilioField orgIdField = FieldFactory.getOrgIdField();
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;

    private static final Logger LOGGER = LogManager.getLogger(AgentProcessor.class.getName());

    /**
     * This function calls Super's classes constructor and sets orgId,orgDomainName and topic.<br>
     * Also assigns values to all other parameters and initializes modules using:- {@link #initializeModules()} <br>
     * And sets Event Type using:- {@link #setEventType(String)}  }
     * @param orgId Organization Id unique to each Organization
     * @param orgDomainName is Organization's Domain name
     * @param type type describes the PUBLISH_TYPE
     */
    public AgentProcessor(long orgId, String orgDomainName, String type)  {

        super(orgId, orgDomainName);
        String clientName = orgDomainName +"-agent-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        LOGGER.info("agentUtil created for "+orgDomainName);
        agentUtil = new AgentUtil(orgId, orgDomainName);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        initializeModules();
        setEventType(type);
        LOGGER.info("Initializing agent processor " + orgDomainName);
    }

    /**
     * This function initialized Modules and makes deviceMap
     */
    private void initializeModules() {

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
        orgIdField.setModule(deviceDetailsModule);

        orgIdCondition.setField(orgIdField);
        orgIdCondition.setOperator(NumberOperators.EQUALS);
        orgIdCondition.setValue(String.valueOf(getOrgId()));

        deviceIdField.setName(AgentKeys.DEVICE_ID);
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());
        try {
            deviceMap.putAll(getDeviceMap());
        } catch (Exception e) {
            LOGGER.info("Exception while populating device details ", e);
        }
    }

    /**
     * This method iterates through all the record in the input records and processes them using :- {@link #processRecord(FacilioRecord)}  }
     * @param records Input Record
     */
    public void processRecords(List<FacilioRecord> records)  {

        for(FacilioRecord record : records) {
            processRecord(record);
        }
    }

    /**
     * This method is used to Process each individual Record from {@link #processRecords(List)} <br>
     * gets the JSONObject payload and check for its PUBLISH_TYPE and processes the JSONObject accordingly<br>
     *  if PUBLISH_TYPE is:- <br>
     *      "agent" - {@link com.facilio.agent.AgentUtil #writeToDb(payLoad,getOrgId()) }
     * @param record Input Record
     *
     */
    private void processRecord (FacilioRecord record)
    {
        String partitionKey = "";
        JSONObject payLoad = record.getData();
        try {
            int numberOfRows = 0;
            partitionKey = record.getPartitionKey();
            String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);
             if(dataType != null ) {
                switch (dataType) {
                    case AgentKeys.AGENT:
                        numberOfRows = agentUtil.processAgent(payLoad);
                        break;
                    case AgentKeys.DEVICE_POINTS:
                        devicePointsUtil.processDevicePoints(payLoad,getOrgId(),deviceMap);
                        break;
                    case AgentKeys.ACK:
                        ackUtil.processAck(payLoad,getOrgId());
                        break;


                }
                 if((numberOfRows == 0)){
                     GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().useExternalConnection(DriverManager.getConnection("jdbc:mysql://localhost:3306/bmslocal","root","facilio123")).table(AgentKeys.TABLE_NAME).fields(FieldFactory.getAgentDataFields()).andCustomWhere( AgentKeys.NAME+"= '"+payLoad.get(AgentKeys.AGENT)+"'");
                     Map<String,Object> toUpdate = new HashMap<>();
                     toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME,System.currentTimeMillis());
                     genericUpdateRecordBuilder.update(toUpdate);
                 }
            }
        } catch (Exception e) {
            CommonCommandUtil.emailException("KAgentProcessor", "Error in processing records in TimeSeries ", e, payLoad.toJSONString());
            LOGGER.info("Exception occurred ", e);
        } finally {
            updateDeviceTable(partitionKey);
        }
    }


    /**
     * this method is used to Update deviceMap with deviceId
     * @param deviceId String Id unique to each Device
     */
    private void updateDeviceTable(String deviceId) {
        try {
            if( ! deviceMap.containsKey(deviceId)) {
                addDeviceId(deviceId);
            }
            if(deviceMap.containsKey(deviceId)) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("lastUpdatedTime", System.currentTimeMillis());
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(deviceDetailsModule.getTableName())
                        .fields(fields).andCondition(getDeviceIdCondition(deviceId)).andCondition(orgIdCondition);
                builder.update(map);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while updating time for device id " + deviceId, e);
        }
    }

    /**
     * gets device condition.
     * @param deviceId String Id unique to each Device
     * @return CriteriaApi condition of device
     */
    private Condition getDeviceIdCondition(String deviceId) {
        return  CriteriaAPI.getCondition("DEVICE_ID", "DEVICE_ID", deviceId, StringOperators.IS);
    }

    private Map<String, Long> getDeviceMap() throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
        return bean.getDeviceMap();
    }

    /**
     * this function adds device to deviceMap with all the details
     * @param deviceId String Id unique to each Device
     *
     */
    private void addDeviceId(String deviceId) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(fields);
        HashMap<String, Object> device = new HashMap<>();
        device.put(AgentKeys.ORG_ID, getOrgId());
        device.put(AgentKeys.DEVICE_ID, deviceId);
        device.put("inUse", true);
        device.put("lastUpdatedTime", System.currentTimeMillis());
        device.put("lastAlertedTime", 0L);
        device.put("alertFrequency", 2400000L);
        long id = builder.insert(device);
        deviceMap.put(deviceId, id);
    }
}
