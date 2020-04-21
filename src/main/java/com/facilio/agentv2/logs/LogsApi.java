package com.facilio.agentv2.logs;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class LogsApi
{
    private static final Logger LOGGER = LogManager.getLogger(LogsApi.class.getName());


    public static boolean logIotCommand(long agentId, long msgId ,FacilioCommand command, Status status){
        if((agentId > 0)){
            if(msgId > 0){
                if(command != null){
                    long currTime = System.currentTimeMillis();
                    try {
                        return addLog(agentId,msgId,command,status,currTime,currTime) > 0;
                    } catch (Exception e) {
                        LOGGER.info("Exception while adding iot command log ",e);
                        return false;
                    }
                }else {
                    LOGGER.info("Exception while adding iot message to log, command cant be null");
                }
            }else {
                LOGGER.info("Exception while adding iot message to log, msgid cant be less than 1 ");
            }
        }else {
            LOGGER.info(" Exception occurred while iot message to log, agentId cant be less than 1");
        }
        return false;
    }

    public static boolean logAgentConnection(long agentId,Status status,long connectionCount,long time) {
        try {
            return addLog(agentId,connectionCount,null,status,time,System.currentTimeMillis()) > 0;
        } catch (Exception e) {
            LOGGER.info("Exception while loggint agent connection ",e);
        }
        return false;
    }

    public static boolean logAgentMessages(long agentId,long msgId,Status status,long actualTime){
        if((agentId > 0)){
            if(msgId > 0){
               /* if(command != null){*/
                    long currTime = System.currentTimeMillis();
                    if(actualTime <= currTime){
                        try{
                            IotMessage iotMessage = IotMessageApiV2.getIotMessage(msgId);
                            return addLog(agentId,msgId,FacilioCommand.valueOf(iotMessage.getCommand()),status,actualTime,currTime) > 0;
                        } catch (Exception e) {
                            LOGGER.info("Exception while adding agent message log ",e);
                            return false;
                        }
                    }else {
                        LOGGER.info(" Exception while adding agent message  log, actual time can't be greater than curr time");
                    }
                /*}else {
                    LOGGER.info("Exception while adding agent message  log, command cant be null");
                }*/
            }else {
                LOGGER.info("Exception while adding agent message  log, msgid cant be less than 1 ");
            }
        }else {
            LOGGER.info(" Exception occurred while adding log, agentId cant be less than 1");
        }
        return false;
    }

    private static long addLog(long agentId, Long msgId,FacilioCommand command, Status status,long timestamp,long createdTime) throws Exception {
        FacilioModule agentV2LogModule = ModuleFactory.getAgentV2LogModule();
        List<FacilioField> fields = FieldFactory.getAgentV2LogFields();
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(AgentConstants.AGENT_ID, agentId);
        toInsert.put(AgentConstants.MESSAGE_ID, msgId);
        if (command != null) {
            toInsert.put(AgentConstants.COMMAND, command.asInt());
        }
        if (status != null) {
            toInsert.put(AgentConstants.STATUS, status.asInt());
        }
        toInsert.put(AgentConstants.ACTUAL_TIME, timestamp);
        toInsert.put(AgentConstants.CREATED_TIME,createdTime);
        try {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(agentV2LogModule.getTableName())
                    .fields(fields);
            return builder.insert(toInsert);
        } catch (Exception e) {
            LOGGER.info("Exception while adding log ", e);
        }
        return -1;
    }

    public static long getCount() {
        FacilioModule agentV2LogModule = ModuleFactory.getAgentV2LogModule();
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(agentV2LogModule.getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(agentV2LogModule));
            List<Map<String, Object>> result = builder.get();
            return (long) result.get(0).get(AgentConstants.ID);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count ", e);
        }
        return 0;
    }

    public static List<Map<String, Object>> getLogs(long agentId, FacilioContext context) throws Exception {
        FacilioModule agentV2LogModule = ModuleFactory.getAgentV2LogModule();
        List<FacilioField> fields = FieldFactory.getAgentV2LogFields();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentV2LogModule.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(agentV2LogModule), String.valueOf(agentId), NumberOperators.EQUALS))
                .orderBy(FieldFactory.getAgentV2MsgIdField(agentV2LogModule).getColumnName() + " DESC," + FieldFactory.getCreatedTime(agentV2LogModule).getColumnName() + " DESC")
                .limit(150);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
        if (pagination != null && !fetchCount) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        } else if (fetchCount) {
            selectRecordBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(agentV2LogModule));
            selectRecordBuilder.select(new ArrayList<>());
        }

        return selectRecordBuilder.get();
    }

}
