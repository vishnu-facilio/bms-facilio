package com.facilio.dataprocessor;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentContent;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.CommandStatus;
import com.facilio.agent.ControllerCommand;
import com.facilio.agent.FacilioAgent;
import com.facilio.agent.MessageStatus;
import com.facilio.agent.PublishType;
import com.facilio.agent.agentcontrol.AgentControl;
import com.facilio.agent.integration.queue.preprocessor.AgentMessagePreProcessor;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.DataProcessorV2;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.services.kinesis.ErrorDataProducer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.AckUtil;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DataProcessorUtil {

    private static final Logger LOGGER = LogManager.getLogger(DataProcessorUtil.class.getName());

    private long orgId;
    private String orgDomainName;

    private String errorStream;
    private HashMap<String, HashMap<String, Long>> deviceVsPublishTypeVsMessageTime = new HashMap<>();
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private EventUtil eventUtil;
    private Boolean isStage = !FacilioProperties.isProduction();
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";


    private static boolean isRestarted = true;

    public static boolean isIsRestarted() {
        return isRestarted;
    }
    public static void setIsRestarted(boolean isRestarted) {
        DataProcessorUtil.isRestarted = isRestarted;
    }

    private DataProcessorV2 dataProcessorV2;
    private List<EventRuleContext> eventRules = new ArrayList<>();

    public DataProcessorUtil(long orgId, String orgDomainName) {
        LOGGER.info(" initializing dataprocessorutil ");
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap(null, null);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        try {
            dataProcessorV2 = new DataProcessorV2(orgId, orgDomainName);
        } catch (Exception e) {
            dataProcessorV2 = null;
            LOGGER.info("Exception occurred ", e);
        }
    }

    private void sendToErrorStream(FacilioRecord record) {
        try {
            ErrorDataProducer.send(new ProducerRecord<>(errorStream, record.getPartitionKey(), record.getData().toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + record.getData().toString());
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    /**
     * @param record
     * @return
     */
    public boolean processRecord(FacilioRecord record) {
        long recordId = record.getId();
        long agentMsgId = -1L;
        long start = System.currentTimeMillis();
        try {
            AccountUtil.getCurrentAccount().clearStateVariables();
            AccountUtil.getCurrentAccount().setRequestUri(String.valueOf(recordId));
            Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DATA_PROCESSING_LOGGER_LEVEL);
            String loggerLevel = orgInfoMap.get(FacilioConstants.OrgInfoKeys.DATA_PROCESSING_LOGGER_LEVEL);
            try {
                if (StringUtils.isNotEmpty(loggerLevel)) {
                    AccountUtil.getCurrentAccount().setLoggerLevel(FacilioUtil.parseInt(loggerLevel));
                }
            }
            catch (Exception e) {
                LOGGER.info(MessageFormat.format("Error occurred while setting logger level for data processing. Logger Level prop = {0}. Exception msg => {1}", loggerLevel, e.getMessage()));
            }
            if (checkIfDuplicate(recordId)) {
                LOGGER.info(" skipping record "+recordId);
                return false;
            }
            JSONObject payLoad = record.getData();
            if ((payLoad != null) && (payLoad.isEmpty())) {
                LOGGER.info(" Empty or null message received " + recordId);
                updateAgentMessage(recordId,MessageStatus.DATA_EMPTY);
                return false;
            }
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<EventRuleContext> ruleList = bean.getActiveEventRules();
            if (ruleList != null) {
                eventRules = ruleList;
            }

            int processorVersion = 0;
            try {
                if(payLoad.containsKey(AgentConstants.AGENT) && ( payLoad.get(AgentConstants.AGENT) != null ) ){
                    com.facilio.agentv2.FacilioAgent agentV2 = AgentApiV2.getAgent((String) payLoad.get(AgentConstants.AGENT));
                    if(AccountUtil.getCurrentOrg().getOrgId() == 152) {
                    	int value = Integer.parseInt(payLoad.get(AgentConstants.PUBLISH_TYPE).toString());
                    	if((value == PublishType.agentAction.getKey()) && payLoad.containsKey(AgentConstants.MESSAGE))  {
                    		agentDisableAction(recordId, payLoad, agentV2);
                    		return false;
                    	}else if(agentV2 != null && AgentControl.isAgentEnabled(agentV2.getId())) {
                    		return false;
                    	}
                    }
                    if(agentV2 != null){
                    	agentMsgId = agentV2.getId();
                    	processorVersion = agentV2.getProcessorVersion();
                    }
                    LOGGER.debug(" checking agent version for agent "+payLoad.get(AgentConstants.AGENT)+"  version "+processorVersion);
                }else {
                    LOGGER.debug(" payload missing key 'agent' "+payLoad);
                }
            }catch (Exception e){
                LOGGER.info(" Exception occurred while checking agent version ",e);
            }

            switch (processorVersion) {
                case 1:
//                    LOGGER.info("PreProcessor for V1 to V2 data");
                    AgentMessagePreProcessor preProcessor = new v1ToV2PreProcessor();
                    List<JSONObject> messages = preProcessor.preProcess(payLoad);
                    boolean isEveryMessageProcessed = true;
                    for (JSONObject msg :
                            messages) {
                        isEveryMessageProcessed = isEveryMessageProcessed && sendToProcessorV2(msg, recordId);
                    }
                    return isEveryMessageProcessed;
                case 2:
//                    LOGGER.info(" new processor data ");
                    return sendToProcessorV2(payLoad,recordId);

                default:
//                    LOGGER.info(" old processor data ");
                    // will divert to another class in future.
            }


           /* try {
                if (payLoad.containsKey(AgentConstants.VERSION)) {
                    Object version = payLoad.get(AgentConstants.VERSION);
                    if (version instanceof String) {
                        if (("2#".equalsIgnoreCase((String) version))) {

                        } else {
                            LOGGER.info(" version not V2 -> " + version);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Exception occurred while processing new agent's message", e);
            }*/

            String dataType = PublishType.event.getValue();
            if (payLoad.containsKey(EventUtil.DATA_TYPE)) {
                dataType = (String) payLoad.remove(EventUtil.DATA_TYPE);
            }

            // Temp fix - bug: Publish_Type wrongly set to "agents"
            if ("agents".equals(dataType)) {
                dataType = PublishType.agent.getValue();
            }
            //Temp fix  - bug: Publish_Type wrongly set to "agents"
            PublishType publishType = PublishType.valueOf(dataType);
            String agentName = orgDomainName.trim(); // trim missing
            if (payLoad.containsKey(PublishType.agent.getValue())) {
                agentName = (String) payLoad.get(PublishType.agent.getValue()); // trim missing
            }

            String deviceId = orgDomainName;
            if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
                deviceId = (String) payLoad.remove(AgentKeys.DEVICE_ID);
            }

            long lastMessageReceivedTime = System.currentTimeMillis();
            if (payLoad.containsKey(AgentKeys.TIMESTAMP)) {
                Object lastTime = payLoad.get(AgentKeys.TIMESTAMP);
                if (payLoad.containsKey("actual_timestamp")){
                    lastTime = payLoad.get("actual_timestamp");
                }
                lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
            }

            FacilioAgent agent = agentUtil.getFacilioAgent(agentName, null);

            if (agent == null) {
                agent = getFacilioAgent(agentName);
                long agentId = agentUtil.addAgent(agent);
                if (agentId < 1L) {
                    LOGGER.info(" Error in AgentId generation ");
                    return false;
                }
                if(agentMsgId == -1L) {
                	agentMsgId = agent.getId();
                }
                agent.setId(agentId);
            }

            // LOGGER.info("Agent ID : " + agent.getId());
            agentUtil.addAgentMetrics(Math.toIntExact(record.getSize()), agent.getId(), publishType.getKey()); //TODO make size long


            long i = 0;
            HashMap<String, Long> publishTypeVsLastMessageTime = deviceVsPublishTypeVsMessageTime.getOrDefault(deviceId, new HashMap<>());
            long deviceLastMessageTime = publishTypeVsLastMessageTime.getOrDefault(dataType, 0L);

            if (deviceLastMessageTime != lastMessageReceivedTime) {
                switch (publishType) {
                	case custom:
                		
                		break;
                    case timeseries:
                        processTimeSeries(record, payLoad, true); //NC
                        // updateDeviceTable(record.getPartitionKey());
                        break;
                    case cov:
                        processTimeSeries(record, payLoad, false);//NC
                        // updateDeviceTable(record.getPartitionKey());
                        break;
                    case agent:
                        i = agentUtil.processAgent(payLoad, agentName);
                        processLog(payLoad, agent.getId());
                        break;
                    case devicepoints:
                        devicePointsUtil.processDevicePoints(payLoad, orgId, agent.getId());
                        break;
                    case ack:
                        ackUtil.processAck(payLoad, agentName, orgId);
                        processLog(payLoad, agent.getId());
                        break;
                    case event:
                        boolean alarmCreated = eventUtil.processEvents(record.getTimeStamp(), payLoad, record.getPartitionKey(), orgId, eventRules);
                        if (alarmCreated) {

                            /*processRecordsInput.getCheckpointer().checkpoint(record);
                             */
                        }
                        break;

                }

                publishTypeVsLastMessageTime.put(dataType, lastMessageReceivedTime);
                deviceVsPublishTypeVsMessageTime.put(deviceId, publishTypeVsLastMessageTime);
            } else {
                LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType + " data : " + record.getData());
            }
            if (i == 0) {
                FacilioChain updateAgentTable = TransactionChainFactory.updateAgentTable();
                FacilioContext context = updateAgentTable.getContext();
                context.put(AgentConstants.ID,agent.getId());
                updateAgentTable.execute(context);
            }
            markMessageProcessed(recordId);
        } catch (Exception e) {
            LOGGER.info("Exception while processing record",e);
            try {
                if (FacilioProperties.isProduction()) {
                    LOGGER.info("Sending data to " + errorStream);
                    sendToErrorStream(record);
                }
            } catch (Exception e1) {
                LOGGER.info("Exception while sending data to " + errorStream, e1);
            }
            CommonCommandUtil.emailException("processor", "Error in processing records : "
                    + record.getId() + " in TimeSeries ", e, record.getData().toString());
            LOGGER.info("Exception occurred in processing payload ", e);
            return false;
        } finally {
            try {
                long time = System.currentTimeMillis() - start;
                Account account = AccountUtil.getCurrentAccount();
                LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, recordId, null);
                event.setProperty("fselect", String.valueOf(account.getSelectQueries()));
                event.setProperty("finsert", String.valueOf(account.getInsertQueries()));
                event.setProperty("fdelete", String.valueOf(account.getDeleteQueries()));
                event.setProperty("fupdate", String.valueOf(account.getUpdateQueries()));
                event.setProperty("fstime", String.valueOf(account.getSelectQueriesTime()));
                event.setProperty("fitime", String.valueOf(account.getInsertQueriesTime()));
                event.setProperty("fdtime", String.valueOf(account.getDeleteQueriesTime()));
                event.setProperty("futime", String.valueOf(account.getUpdateQueriesTime()));
                event.setProperty("frget", String.valueOf(account.getRedisGetCount()));
                event.setProperty("frput", String.valueOf(account.getRedisPutCount()));
                event.setProperty("frdel", String.valueOf(account.getRedisDeleteCount()));
                event.setProperty("frgtime", String.valueOf(account.getRedisGetTime()));
                event.setProperty("frptime", String.valueOf(account.getRedisPutTime()));
                event.setProperty("frdtime", String.valueOf(account.getRedisDeleteTime()));
                event.setProperty("ftqueries", String.valueOf(account.getTotalQueries()));
                event.setProperty("ftqtime", String.valueOf(account.getTotalQueryTime()));
                event.setProperty(TIME_TAKEN, String.valueOf(time / 1000));
                event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(time));
                LOGGER.callAppenders(event);
                if(isStage) {
                	Map<String,Object> prop = new HashMap<>();
                	prop.put(AgentConstants.SELECT_QUERIES, account.getSelectQueries());
                	prop.put(AgentConstants.INSERT_QUERIES, account.getInsertQueries());
                	prop.put(AgentConstants.UPDATE_QUERIES, account.getUpdateQueries());
                	prop.put(AgentConstants.DELETE_QUERIES, account.getDeleteQueries());
                	prop.put(AgentConstants.REDIS_GET_COUNT, account.getRedisGetCount());
                	prop.put(AgentConstants.REDIS_PUT_COUNT, account.getRedisPutCount());
                	prop.put(AgentConstants.REDIS_DELETE_COUNT, account.getRedisDeleteCount());
                	prop.put(AgentConstants.SELECT_QUERIES_TIME, account.getSelectQueriesTime());
                	prop.put(AgentConstants.INSERT_QUERIES_TIME, account.getInsertQueriesTime());
                	prop.put(AgentConstants.UPDATE_QUERIES_TIME, account.getUpdateQueriesTime());
                	prop.put(AgentConstants.DELETE_QUERIES_TIME, account.getDeleteQueriesTime());
                	prop.put(AgentConstants.REDIS_GET_TIME, account.getRedisGetTime());
                	prop.put(AgentConstants.REDIS_PUT_TIME, account.getRedisPutTime());
                	prop.put(AgentConstants.REDIS_DELETE_TIME, account.getRedisDeleteTime());
                	prop.put(AgentConstants.PUBLIC_SELECT_QUERIES, account.getPublicSelectQueries());
                	prop.put(AgentConstants.PUBLIC_INSERT_QUERIES, account.getPublicInsertQueries());
                	prop.put(AgentConstants.PUBLIC_UPDATE_QUERIES, account.getPublicUpdateQueries());
                	prop.put(AgentConstants.PUBLIC_DELETE_QUERIES, account.getPublicDeleteQueries());
                	prop.put(AgentConstants.PUBLIC_REDIS_GET_COUNT, account.getPublicRedisGetCount());
                	prop.put(AgentConstants.PUBLIC_REDIS_PUT_COUNT, account.getPublicRedisPutCount());
                	prop.put(AgentConstants.PUBLIC_REDIS_DELETE_COUNT, account.getPublicRedisDeleteCount());
                	prop.put(AgentConstants.PUBLIC_SELECT_QUERIES_TIME, account.getPublicSelectQueriesTime());
                	prop.put(AgentConstants.PUBLIC_INSERT_QUERIES_TIME, account.getPublicInsertQueriesTime());
                	prop.put(AgentConstants.PUBLIC_UPDATE_QUERIES_TIME, account.getPublicUpdateQueriesTime());
                	prop.put(AgentConstants.PUBLIC_DELETE_QUERIES_TIME, account.getPublicDeleteQueriesTime());
                	prop.put(AgentConstants.PUBLIC_REDIS_GET_TIME, account.getPublicRedisGetTime());
                	prop.put(AgentConstants.PUBLIC_REDIS_PUT_TIME, account.getPublicRedisPutTime());
                	prop.put(AgentConstants.PUBLIC_REDIS_DELETE_TIME, account.getPublicRedisDeleteTime());
                	prop.put(AgentConstants.AGENT_ID, agentMsgId);
                	updateAgentMsg(prop,recordId);
                }
            } catch (Exception e) {
                LOGGER.info("record: " + recordId);
            }
        }
        // LOGGER.info(" processing successful");
        return true;
    }
	private void agentDisableAction(long recordId, JSONObject payLoad, com.facilio.agentv2.FacilioAgent agentV2)
			throws SQLException, Exception {
		LOGGER.info("Agent Control called -- Data ProcessorUtil -- agent :"+ payLoad.get(AgentConstants.AGENT));
		boolean disable =  (boolean) payLoad.get(AgentConstants.MESSAGE);
		AgentControl object = new AgentControl();
		if(disable) {
				 object.setAction(disable);
				 object.setAgentId(agentV2.getId());
				 object.setOrgId(orgId);
				 if(agentV2 != null) {
					 object.updateAgent(recordId);
				 }
				FacilioService.runAsService(()-> object.insertAgentDisable(recordId));
		 }else{
			 object.setAction(disable);
			 object.setAgentId(agentV2.getId());
			 if(payLoad.containsKey(AgentConstants.AGENT)) {
				 String agentName = payLoad.get(AgentConstants.AGENT).toString();
				if( StringUtils.isEmpty(agentName)){
					 throw new IllegalArgumentException("agentName is null :"+agentName);
				 }
				 object.setAgentName(agentName);
			 }
			 object.updateAgent(recordId);
			 FacilioService.runAsService(()-> object.updateAgentDisable(recordId));
		 }
	}

    private void updateAgentMsg(Map<String, Object> prop, long recordId) throws SQLException {
    	List<FacilioField> fields = FieldFactory.getAgentMessageFields();
		int count = new GenericUpdateRecordBuilder().fields(fields).table(ModuleFactory.getAgentMessageModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get(AgentKeys.RECORD_ID), String.valueOf(recordId), NumberOperators.EQUALS))
				.update(prop);
		if(count == 0) {
			LOGGER.error("Cann't able to update log count in Agent Message Table ");
		}
    }
	private boolean sendToProcessorV2(JSONObject payLoad, long recordId) {
        if (dataProcessorV2 != null) {
            try {
                if (!dataProcessorV2.processRecord(payLoad)) {
                    return false;
                }
                updateAgentMessage(recordId, MessageStatus.PROCESSED);
                return true;
            } catch (Exception newProcessorException) {
                LOGGER.info("Exception occurred ", newProcessorException);
                return false;
            }
        } else {
            LOGGER.info(" DATAPROCESSOR object is null ");
            return false;
        }
    }

    public static boolean checkIfDuplicate(long recordId) {
        try {
            boolean isDuplicateMessage = isDuplicate(recordId);
            if (isDuplicateMessage) {
                if (isRestarted) {
                    LOGGER.info(" Duplicate message received but can be processed due to server-restart " + recordId);
                    isRestarted = false;
                } else {
                    LOGGER.info(" Duplicate message received and cannot be reprocessed " + recordId);
                    return true;
                }
            } else {
                boolean originalFlag = false;
                originalFlag = addAgentMessage(recordId);
                if (!originalFlag) {
                    LOGGER.info("tried adding duplicate message " + recordId);
                    return true;
                }
            }
        } catch (Exception e1) {
            LOGGER.info("Exception Occurred while checking message originality", e1);
        }
        return false;
    }
    // LOGGER.debug("TOTAL PROCESSOR DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));

    private FacilioAgent getFacilioAgent(String agentName) throws Exception {
        if( (agentName != null) && ( ! agentName.isEmpty() ) ) {
            FacilioAgent agent = new FacilioAgent();
            agent.setAgentName(agentName);
            agent.setAgentConnStatus(Boolean.TRUE);
            agent.setAgentState(1);
            agent.setInterval(15L);
            agent.setWritable(false);
            return agent;
        }else {
            throw new Exception(" Agent name can't be null");
        }
    }

    private void processLog(JSONObject object, Long agentId) {
        JSONObject payLoad = (JSONObject) object.clone();
        if ((payLoad.containsKey(AgentKeys.COMMAND_STATUS) || payLoad.containsKey(AgentKeys.CONTENT))) {
            int connectionCount = -1;
            //checks for key status in payload and if it 'agent'-publishype
            if (payLoad.containsKey(AgentKeys.COMMAND_STATUS) && !payLoad.containsKey(AgentKeys.COMMAND)) {

                Long status = (Long) payLoad.remove(AgentKeys.COMMAND_STATUS);
                if ((1 == status)) { // Connected block -- getting Connection count
                    payLoad.put(AgentKeys.COMMAND_STATUS, CommandStatus.CONNECTED.getKey());
                    payLoad.put(AgentKeys.COMMAND, ControllerCommand.connect.getCommand());
                    if (payLoad.containsKey(AgentKeys.CONNECTION_COUNT)) {
                        connectionCount = Integer.parseInt(payLoad.get(AgentKeys.CONNECTION_COUNT).toString());
                    }

                    if (connectionCount == -1) {
                        payLoad.put(AgentKeys.CONTENT, AgentContent.Connected.getKey());
                    } else {
                        if (connectionCount == 1) {
                            payLoad.put(AgentKeys.CONTENT, AgentContent.Restarted.getKey());
                            AgentUtil.putLog(payLoad, orgId, agentId, false);
                        }
                        payLoad.put(AgentKeys.CONTENT, AgentContent.Connected.getKey() + connectionCount);
                    }

                } else if (0 == status) { // disconnected block -
                    payLoad.put(AgentKeys.COMMAND_STATUS, CommandStatus.DISCONNECTED.getKey());
                    payLoad.put(AgentKeys.CONTENT, AgentContent.Disconnected.getKey());
                    payLoad.put(AgentKeys.COMMAND, ControllerCommand.connect.getCommand());
                } else { // avoids any status pther than 0 and 1
                    LOGGER.info("Exception Occured, wrong status in payload.--" + payLoad);
                    return;
                }
            } else if ((!payLoad.containsKey(AgentKeys.COMMAND)) && payLoad.containsKey(AgentKeys.CONTENT)) {
                long checkOrgId = 152;
                if (orgId == checkOrgId) {
                    LOGGER.info("debugging payload--With content alone-1-" + payLoad);
                }
                payLoad.put(AgentKeys.CONTENT, AgentContent.Subscribed.getKey());
                payLoad.put(AgentKeys.COMMAND, ControllerCommand.subscribe.getCommand());
                if (orgId == checkOrgId) {
                    LOGGER.info("debugging payload--With content alone-2-" + payLoad);
                }
            }
            // ack type - so content is always msgid.
            else {
                payLoad.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.MESSAGE_ID));
            }
            AgentUtil.putLog(payLoad, orgId, agentId, false);
        }
    }


    private void processTimeSeries(FacilioRecord record, JSONObject payLoad, boolean isTimeSeries) throws Exception {
        long timeStamp = record.getTimeStamp();
        long startTime = System.currentTimeMillis();
        // LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + " TIME::::" +timeStamp);
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        /*if (AccountUtil.getCurrentOrg().getId() == 146 ) {
            LOGGER.info("Payload in processor : "+payLoad);
        }*/
        bean.processTimeSeries(timeStamp, payLoad, record, isTimeSeries);
        long timeTaken = (System.currentTimeMillis() - startTime);
        // LOGGER.info("timetaken to process timeseries data2 : " + timeTaken);
        if (timeTaken > 100000L) {
            LOGGER.info("timetaken to process timeseries is  > 100000  : " + timeTaken);
        }
    }

        /*private void updateDeviceTable(String deviceId) {
            try {
                // LOGGER.info("Device ID : "+deviceId);
                if (deviceId == null || deviceId.isEmpty()) {
                    return;
                }
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

        private Condition getDeviceIdCondition(String deviceId) {
            return  CriteriaAPI.getCondition("DEVICE_ID", "DEVICE_ID", deviceId, StringOperators.IS);
        }

        private Map<String, Long> getDeviceMap() {
            try {
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                return bean.getDeviceMap();
            } catch (Exception e) {
                LOGGER.info("Exception while getting device data", e);
                return new HashMap<>();
            }
        }

        private void addDeviceId(String deviceId) throws Exception {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            deviceMap.put(deviceId, bean.addDeviceId(deviceId));
        }*/

    
    public static String getLastRecordChecked() throws Exception {
        Map<String, FacilioField> fieldmap = FieldFactory.getAsMap(FieldFactory.getAgentMessageFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldmap.get(AgentKeys.MSG_STATUS), String.valueOf(MessageStatus.PROCESSED.getStatusKey()), NumberOperators.EQUALS));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentMessageModule().getTableName())
                .select(fieldmap.values())
                .andCriteria(criteria)
                .orderBy(AgentKeys.ID + " DESC ")
                .limit(1);
        List<Map<String, Object>> record = builder.get();
        //LOGGER.info(" select query -> " + builder.toString());
        if (!record.isEmpty()) {
            if (record.get(0).containsKey(AgentKeys.MSG_STATUS)) {
                //OGGER.info(" record selected is -> " + record.get(0));
                return record.get(0).get(AgentKeys.RECORD_ID).toString();
            } else {
                throw new Exception(AgentKeys.MSG_STATUS + " field missing from record selected ->" + record.get(0));
            }
        } else {
            throw new Exception(" No such last record selected ");
        }

    }

    /**
     * Checks if a message is present in the Messages table.
     *
     * @param recordId - id of the record
     * @return true if not present and false if present
     * @throws Exception
     */
    public static boolean isDuplicate(long recordId) {
        boolean isNew = true;
        try {
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentMessageFields());
            FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.RECORD_ID), String.valueOf(recordId), NumberOperators.EQUALS));
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(messageModule.getTableName())
                    .select(fieldMap.values())
                    .andCriteria(criteria);
            List<Map<String, Object>> rows = builder.get();
            if (((rows == null) || (rows.isEmpty()))) {
                isNew = false;
            }
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ", e);
        }
        return isNew;
    }

    public static boolean markMessageProcessed(long recordId) {
        try {
            return updateAgentMessage(recordId, MessageStatus.PROCESSED);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while marking message processed ", e);
        }
        return false;
    }

    public static boolean addAgentMessage(long recordId) throws Exception {
        return addOrUpdateAgentMessage(recordId, MessageStatus.RECIEVED);
    }

    public static boolean updateAgentMessage(long recordId, MessageStatus messageStatus) throws Exception {
        return addOrUpdateAgentMessage(recordId, messageStatus);
    }

    private static boolean addOrUpdateAgentMessage(long recordId, MessageStatus messageStatus) throws Exception {
        boolean status = false;

        Map<String, Object> map = new HashMap<>();
        map.put(AgentKeys.RECORD_ID, recordId);
        map.put(AgentKeys.MSG_STATUS, messageStatus.getStatusKey());
        map.put(AgentKeys.START_TIME, System.currentTimeMillis());

        FacilioChain updateAgentMessageChain = TransactionChainFactory.getUpdateAgentMessageChain();
        FacilioChain addAgentMessageChain = TransactionChainFactory.getAddAgentMessageChain();

        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID, AccountUtil.getCurrentAccount().getOrg().getOrgId());


        if (messageStatus == MessageStatus.RECIEVED) {
            try {
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, map);
                if (addAgentMessageChain.execute(context)) {
                   // LOGGER.info(" agentMessage added ");
                    status = true;
                }
            } catch (MySQLIntegrityConstraintViolationException e) {
                LOGGER.info("Duplicate Message " + e.getMessage());
            }
        } else if (messageStatus == MessageStatus.DATA_EMPTY) {
            map.put(AgentKeys.FINISH_TIME, System.currentTimeMillis());
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, map);
            if (updateAgentMessageChain.execute(context)) {
               // LOGGER.info(" agentMessage updated ");
                status = true;
            }
        } else {
            map.put(AgentKeys.FINISH_TIME, System.currentTimeMillis());
            map.remove(AgentKeys.START_TIME);
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, map);
            if (updateAgentMessageChain.execute(context)) {
                //LOGGER.info(" agentMessage finish time updated ");
                status = true;
            }
        }
        return status;
    }

}
