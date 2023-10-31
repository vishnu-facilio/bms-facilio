package com.facilio.dataprocessor;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.modules.*;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.*;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.DataProcessorV2;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSource;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.AckUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DataProcessorUtil {

    private static final Logger LOGGER = LogManager.getLogger(DataProcessorUtil.class.getName());

    private long orgId;
    private AgentUtilV2 agentUtilV2;
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";
    private MessageSource messageSource;
    private DataProcessorV2 dataProcessorV2;

    public DataProcessorUtil(long orgId, String orgDomainName, MessageSource source) {
        LOGGER.info(" initializing dataprocessorutil ");
        this.orgId = orgId;
        this.messageSource = source;
        agentUtilV2 = new AgentUtilV2(orgId, orgDomainName);
        try {
            dataProcessorV2 = new DataProcessorV2(orgId, agentUtilV2);
        } catch (Exception e) {
            dataProcessorV2 = null;
            LOGGER.info("Exception occurred ", e);
        }
    }

    /**
     * @param record
     * @return
     */
    public boolean processRecord(FacilioRecord record) {
        long recordId = record.getId();
        int partitionId = record.getPartition();
        Attributes entryEvent = Attributes.of(AttributeKey.longKey("recordId"), recordId, AttributeKey.longKey("partitionId"), (long) partitionId);
        Span.current().addEvent("Record Entry", entryEvent);
        long agentId = -1L;
        long startTime = System.currentTimeMillis();
        boolean processed = false;
        try {
            AccountUtil.getCurrentAccount().clearStateVariables();
            AccountUtil.getCurrentAccount().setRequestUri(partitionId + "-" + recordId);
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

            if (checkIfDuplicate(recordId, partitionId)) {
                LOGGER.info(" skipping record "+recordId);
                return false;
            }
            JSONObject payLoad = record.getData();
            if ((payLoad != null) && (payLoad.isEmpty())) {
                LOGGER.info(" Empty or null message received " + recordId);
                updateAgentMessage(recordId, partitionId, MessageStatus.DATA_EMPTY);
                return false;
            }

            int processorVersion = 0;
            if(payLoad.containsKey(AgentConstants.AGENT) && ( payLoad.get(AgentConstants.AGENT) != null ) ) {
                String agentName = (String) payLoad.get(AgentConstants.AGENT);
                AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
                FacilioAgent agent = agentBean.getAgent(agentName);
                if (agent != null) {
                    agentId = agent.getId();
                    processorVersion = agent.getProcessorVersion();
                    Span.current().setAllAttributes(Attributes.of(AttributeKey.stringKey(AgentConstants.AGENT), agent.getName()));
                }
                LOGGER.debug(" checking agent version for agent "+payLoad.get(AgentConstants.AGENT)+"  version "+processorVersion);
            }else {
                LOGGER.error(" payload missing key 'agent' "+payLoad);
            }

            Span.current().setAllAttributes(Attributes.of(AttributeKey.stringKey("processor"), "V2"));
            processed = sendToProcessorV2(payLoad, recordId, record.getPartitionKey(), partitionId, startTime);

        } catch (Exception e) {
            LOGGER.info("Exception while processing record",e);
//            CommonCommandUtil.emailException("processor", "Error in processing records : " + record.getId() + " in TimeSeries ", e, record.getData().toString());
            // TODO add in common error logs
            return false;
        } finally {
            logQueryTime(startTime,recordId,agentId);
        }
        return processed;
    }

    private void logQueryTime(long start, long recordId, long agentId) {
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
        } catch (Exception e) {
            LOGGER.error("record: " + recordId);
        }
    }

    private boolean sendToProcessorV2(JSONObject payload, long recordId, String partitionKey, int partitionId, long startTime) throws Exception {
        List<JSONObject> payloads = new ArrayList<>();
        com.facilio.agentv2.FacilioAgent agent = getFacilioAgentV2FromPayload(payload);
        //preprocess the JSON data if necessary
        if (agent.getTransformWorkflowId() > 0) {
            List<Map<String, Object>> resultsFromPreProcessor = getResultsFromPreProcessor(payload, agent);
            for (Map<String, Object> item : resultsFromPreProcessor) {
                payload = (JSONObject) new JSONParser().parse(JSONObject.toJSONString(item));
                payloads.add(payload);
            }
        } else {
            payloads.add(payload);
        }
        boolean processed = true;
        int payloadIndex=1;
        for (JSONObject jsonObject : payloads) {
            processed = processed && dataProcessorV2.processRecord(jsonObject, agent,recordId,partitionId,messageSource.getName(),payloadIndex, startTime);
            payloadIndex++;
        }
        if (processed) {
            updateAgentMessage(recordId, partitionId, MessageStatus.PROCESSED);
        }
        return processed;
    }

    @WithSpan("AgentScriptPreProcessor")
    private List<Map<String, Object>> getResultsFromPreProcessor(JSONObject payload, com.facilio.agentv2.FacilioAgent agent) throws Exception {
        WorkflowContext transformWorkflow = WorkflowUtil.getWorkflowContext(agent.getTransformWorkflowId());
        FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
        FacilioContext context = chain.getContext();
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, transformWorkflow);
        List params = new ArrayList<>();
        params.add(payload);
        params.add(FieldUtil.getAsProperties(agent));
        context.put(WorkflowV2Util.WORKFLOW_PARAMS, params);
        long workflowStartTime = System.currentTimeMillis();
        chain.execute();
        List<Map<String, Object>> resultsFromPreProcessor = (List<Map<String, Object>>) transformWorkflow.getReturnValue();
        LOGGER.debug("Time taken to transform payload : " + (System.currentTimeMillis() - workflowStartTime) + " ms");
        return resultsFromPreProcessor;
    }

    /**
     * gets {@link com.facilio.agentv2.FacilioAgent } for a payload.
     *
     * @param payload JSONObject which must contain the key 'agent' to which agent's name is mapped.
     * @return {@link com.facilio.agentv2.FacilioAgent}
     * @throws Exception if the key 'agent' is missing from payload or if no agent is found for a name.
     */
    private com.facilio.agentv2.FacilioAgent getFacilioAgentV2FromPayload(JSONObject payload) throws Exception {
        String agentName = null;
        if (payload.containsKey(AgentConstants.AGENT)) {
            agentName = payload.get(AgentConstants.AGENT).toString().trim();
            com.facilio.agentv2.FacilioAgent agent = agentUtilV2.getFacilioAgent(agentName);
            if (agent != null) {
                return agent;
            } else {
                throw new Exception(" No such agent found ");
            }
        } else {
            throw new Exception(" payload missing agent name");
        }
    }

    private boolean checkIfDuplicate(long recordId, int partitionId) {
        try {
            Map<String, Object> prop = getRecord(recordId, partitionId);
            if (prop == null) {
                addAgentMessage(recordId, partitionId);
            } else {
                Long statusValue = (Long) prop.getOrDefault(AgentKeys.MSG_STATUS, 0L);
                if (statusValue == 1L) {
                    LOGGER.info("Message already processed " + recordId);
                    return true;
                } else {
                    LOGGER.info("Reprocessing the failed message " + recordId);
                    updateAgentStartTime(recordId);
                }
            }
        } catch (Exception e1) {
            LOGGER.info("Exception Occurred while checking message originality", e1);
        }
        return false;
    }
    // LOGGER.debug("TOTAL PROCESSOR DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
    
    private static boolean updateAgentStartTime(long recordId) throws SQLException {
    	Map<String, Object> map = new HashMap<>();
        map.put(AgentKeys.RECORD_ID, recordId);
        map.put(AgentKeys.START_TIME, System.currentTimeMillis());
        List<FacilioField> fields = FieldFactory.getAgentMessageFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
        
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
        		.fields(fields)
        		.table(messageModule.getTableName())
        		.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.RECORD_ID), String.valueOf(recordId), NumberOperators.EQUALS));
       int count = builder.update(map);
       if(count > 0) {
    	   LOGGER.info("Agent Msg startTime updated for record Id : "+recordId);
    	   return true;
       }
       return false;
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


    /**
     * Checks if a message is present in the Messages table.
     *
     * @param recordId - id of the record
     * @return true if not present and false if present
     * @throws Exception
     */
    private Map<String, Object> getRecord(long recordId, int partitionId) {
        try {
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentMessageFields());
            FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.RECORD_ID), String.valueOf(recordId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.PARTITION_ID), String.valueOf(partitionId), NumberOperators.EQUALS));
            
            Criteria sourceCrtieria = new Criteria();
            sourceCrtieria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.MESSAGE_SOURCE), messageSource.getName(), StringOperators.IS));
            if (messageSource.getName().equals("facilio")) {
            	sourceCrtieria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.MESSAGE_SOURCE), CommonOperators.IS_EMPTY));
            }
            
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(messageModule.getTableName())
                    .select(fieldMap.values())
                    .andCriteria(criteria)
                    .andCriteria(sourceCrtieria);
            List<Map<String, Object>> rows = builder.get();
            if (CollectionUtils.isNotEmpty(rows)) {
                return rows.get(0);
            }
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ", e);
        }
        return null;
    }

    private boolean addAgentMessage(long recordId, int partitionId) throws Exception {
        return addOrUpdateAgentMessage(recordId, partitionId, MessageStatus.RECIEVED);
    }

    private boolean updateAgentMessage(long recordId, int partitionId, MessageStatus messageStatus) throws Exception {
        return addOrUpdateAgentMessage(recordId, partitionId, messageStatus);
    }

    private boolean  addOrUpdateAgentMessage(long recordId, int partitionId, MessageStatus messageStatus) throws Exception {
        boolean status = false;

        Map<String, Object> map = new HashMap<>();
        map.put(AgentKeys.RECORD_ID, recordId);
        map.put(AgentKeys.MSG_STATUS, messageStatus.getStatusKey());
        map.put(AgentKeys.START_TIME, System.currentTimeMillis());
        map.put(AgentConstants.PARTITION_ID, partitionId);
        FacilioChain updateAgentMessageChain = TransactionChainFactory.getUpdateAgentMessageChain();
        FacilioChain addAgentMessageChain = TransactionChainFactory.getAddAgentMessageChain();

        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID, AccountUtil.getCurrentAccount().getOrg().getOrgId());


        if (messageStatus == MessageStatus.RECIEVED) {
            try {
            	map.put(AgentConstants.MESSAGE_SOURCE, messageSource.getName());
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
