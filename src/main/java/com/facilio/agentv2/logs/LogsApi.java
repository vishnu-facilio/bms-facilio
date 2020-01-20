package com.facilio.agentv2.logs;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogsApi
{
    private static final FacilioModule MODULE = ModuleFactory.getAgentV2LogModule();
    private static final List<FacilioField> FIELDS = FieldFactory.getAgentV2LogFields();

    private static final Logger LOGGER = LogManager.getLogger(IotMessageApiV2.class.getName());


    public static boolean logIotCommand(long agentId, long msgId ,FacilioCommand command, Status status){
        if((agentId > 0)){
            if(msgId > 0){
                if(command != null){
                    long currTime = System.currentTimeMillis();
                    return addLog(agentId,msgId,command,status,currTime,currTime) > 0;
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

    public static boolean logAgentMessages(long agentId,long msgId,FacilioCommand command,Status status,long actualTime){
        if((agentId > 0)){
            if(msgId > 0){
               /* if(command != null){*/
                    long currTime = System.currentTimeMillis();
                    if(actualTime <= currTime){
                        return addLog(agentId,msgId,command,status,actualTime,currTime) > 0;
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

    private static long addLog(long agentId, Long msgId,FacilioCommand command, Status status,long timestamp,long createdTime){
        Map<String,Object> toInsert = new HashMap<>();
        toInsert.put(AgentConstants.AGENT_ID,agentId);
        toInsert.put(AgentConstants.MESSAGE_ID,msgId);
        if(command != null){
            toInsert.put(AgentConstants.COMMAND,command.asInt());
        }
        if(status != null){
            toInsert.put(AgentConstants.STATUS,status.asInt());
        }
        toInsert.put(AgentConstants.ACTUAL_TIME,timestamp);
        toInsert.put(AgentConstants.CREATED_TIME,createdTime);
        try {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(MODULE.getTableName())
                    .fields(FIELDS);
                    return builder.insert(toInsert);
        } catch (Exception e) {
            LOGGER.info("Exception while adding log ",e);
        }
        return -1;
    }

    public static List<Map<String,Object>> getMetrics(long agentId, int limit, int offset) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FIELDS)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        if (limit > 0) {
            selectRecordBuilder.limit(limit);
        }
        if(offset > 0){
            selectRecordBuilder.offset(offset);
        }
        return selectRecordBuilder.get();
    }

}
