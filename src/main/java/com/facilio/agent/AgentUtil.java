package com.facilio.agent;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class writes agent's payload data to a table in DB.
 */
public  class AgentUtil
{
    private long orgId ;
    private String orgDomainName;
    private static final long DEFAULT_TIME = 10L;
    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    public Map<String, FacilioAgent> getAgentMap() { return agentMap; }

    public int getAgentCount() {
        return agentMap.size();
    }

    private int agentCount;

    private static final Logger LOGGER = LogManager.getLogger(AgentUtil.class.getName());

    public AgentUtil(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    public void populateAgentContextMap(String agentName,AgentType type) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String, Object>> records = bean.getAgentDataMap(agentName,type);
            for (Map<String,Object> record :records) {
                JSONObject payload = new JSONObject();
                payload.putAll(record);
                FacilioAgent agent = getFacilioAgentFromJson(payload);
                agentMap.put(agent.getAgentName(), agent);
            }
        } catch (Exception e1) {
            LOGGER.info("exception ",e1);
        }
    }

    public FacilioAgent getFacilioAgent(String agentName,AgentType type) {
        FacilioAgent agent = agentMap.get(agentName);
        if(agent == null){
            populateAgentContextMap(agentName,type);
            agent =agentMap.get(agentName);
        }
        return agent;
    }

    /**
     * This method writes data from the Agent to its table in database using {@link GenericInsertRecordBuilder}<br>
     * this also adds appropriate values to  each<br>
     * @param payload is the JSONObject from Record and contains data to process
     */

    private FacilioAgent getFacilioAgentFromJson(JSONObject payload){
        FacilioAgent agent = new FacilioAgent();
        if(payload.containsKey(AgentKeys.STATE)) {
            agent.setAgentState(Integer.parseInt(payload.get(AgentKeys.STATE).toString()));
        } else{
            agent.setAgentState(0);
        }
        if(payload.containsKey(AgentKeys.ID)){
            agent.setId(Long.parseLong(payload.get(AgentKeys.ID).toString()));
        }
        if(payload.containsKey(AgentKeys.SITE_ID)){
            agent.setSiteId(Long.parseLong(payload.get(AgentKeys.SITE_ID).toString()));
        }
        if(payload.containsKey(AgentKeys.VERSION)) {
            agent.setAgentDeviceDetails(payload.get(AgentKeys.VERSION).toString());

            if(payload.containsKey(AgentKeys.FACILIO_MQTT_VERSION)) {
                agent.setAgentVersion(payload.get(AgentKeys.FACILIO_MQTT_VERSION).toString());
            }
        }

        if(payload.containsKey(AgentKeys.CONNECTION_STATUS)) {
            agent.setAgentConnStatus(Boolean.valueOf(payload.get(AgentKeys.CONNECTION_STATUS).toString()));
        } else {
            agent.setAgentConnStatus(true);
        }

        if(payload.containsKey(AgentKeys.DATA_INTERVAL)) {
            agent.setAgentDataInterval(Long.parseLong( payload.get(AgentKeys.DATA_INTERVAL).toString()));
        } else {
            agent.setAgentDataInterval(DEFAULT_TIME);
        }

        if(payload.containsKey(AgentKeys.NUMBER_OF_CONTROLLERS)){
            agent.setAgentNumberOfControllers(Integer.parseInt(payload.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString()));
        }

        if(payload.containsKey(AgentKeys.NAME)){
            agent.setAgentName(payload.get(AgentKeys.NAME).toString().trim());
        }
        else if(payload.containsKey(PublishType.agent.getValue())){
            agent.setAgentName(payload.get(PublishType.agent.getValue()).toString().trim());
        }
        else{
            agent.setAgentName(orgDomainName);
        }

        if(payload.containsKey(AgentKeys.AGENT_TYPE)){
            
                agent.setAgentType(payload.get(AgentKeys.AGENT_TYPE).toString());
        }
        if(payload.containsKey(AgentKeys.DELETED_TIME)){
            agent.setDeletedTime(Long.parseLong(payload.get(AgentKeys.DELETED_TIME).toString()));
        }
        if(payload.containsKey(AgentKeys.WRITABLE)){
            agent.setWritable(Boolean.parseBoolean(payload.get(AgentKeys.WRITABLE).toString()));
        }else {
            agent.setWritable(false);
        }
        return agent;

    }

    public long processAgent(JSONObject jsonObject,String agentName) {
       /* String agentName = agentName;
        if (StringUtils.isEmpty(agentName)) { //Temp fix to avoid NPE
        	agentName = orgDomainName;
            LOGGER.info(" in process agent agentName="+agentName);
        }*/
        FacilioAgent agent = getFacilioAgent(agentName,null);
       if(jsonObject.containsKey(AgentKeys.DATA_INTERVAL)){
           Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
           if(currDataInterval.longValue() > 120L){
               currDataInterval = 15L;
               jsonObject.replace(AgentKeys.DATA_INTERVAL,currDataInterval);
           }
       }
       if(agent==null) {
           agent = getFacilioAgentFromJson(jsonObject);
           return addAgent(agent);
       }

           else  {
               Condition agentNameCondition = new Condition();
               agentNameCondition.setField(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()));
               agentNameCondition.setOperator(StringOperators.IS);
               agentNameCondition.setValue(agentName);
               FacilioContext context = new FacilioContext();
               context.put(FacilioConstants.ContextNames.CRITERIA,agentNameCondition);

           Chain updateAgentChain = TransactionChainFactory.updateAgent();

           try {

                   Map<String, Object> toUpdate = new HashMap<>();
                   if (jsonObject.containsKey(AgentKeys.CONNECTION_STATUS)) {
                       Boolean connectionStatus = Boolean.valueOf((jsonObject.get(AgentKeys.CONNECTION_STATUS).toString()));
                       if (agent.getAgentConnStatus() != connectionStatus) {
                           toUpdate.put(AgentKeys.CONNECTION_STATUS, connectionStatus);
                           agent.setAgentConnStatus(connectionStatus);
                       }
                   } else {
                       toUpdate.put(AgentKeys.CONNECTION_STATUS, true);
                       agent.setAgentConnStatus(true);
                   }

                   if (jsonObject.containsKey(AgentKeys.DISPLAY_NAME)) {
                       if (!jsonObject.get(AgentKeys.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                           toUpdate.put(AgentKeys.DISPLAY_NAME, jsonObject.get(AgentKeys.DISPLAY_NAME));
                           agent.setDisplayName(jsonObject.get(AgentKeys.DISPLAY_NAME).toString());
                       }
                   }

                   if (jsonObject.containsKey(AgentKeys.STATE)) {
                       Integer currStatus = Integer.parseInt(jsonObject.get(AgentKeys.STATE).toString());
                       if (!agent.getAgentState().equals(currStatus)) {
                           toUpdate.put(AgentKeys.STATE, currStatus);
                           agent.setAgentState(currStatus);

                       }
                   } else {
                       toUpdate.put(AgentKeys.STATE, 0);
                       agent.setAgentState(0);
                   }

                   if (jsonObject.containsKey(AgentKeys.DATA_INTERVAL)) {
                       Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
                       if (agent.getAgentDataInterval().longValue() != currDataInterval.longValue()) {
                           toUpdate.put(AgentKeys.DATA_INTERVAL, currDataInterval);
                           agent.setAgentDataInterval(currDataInterval);
                       }
                   }
                   if(jsonObject.containsKey(AgentKeys.AGENT_TYPE)){
                	   agent.setAgentType(jsonObject.get(AgentKeys.AGENT_TYPE).toString().trim());
                	   toUpdate.put(AgentKeys.AGENT_TYPE,agent.getAgentType());
                   }
                   if (jsonObject.containsKey(AgentKeys.NUMBER_OF_CONTROLLERS)) {
                       Integer currNumberOfControllers = Integer.parseInt(jsonObject.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString());
                       if ((agent.getAgentNumberOfControllers().intValue() != currNumberOfControllers.intValue())) {
                           toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, currNumberOfControllers);
                           agent.setAgentNumberOfControllers(currNumberOfControllers);
                       }
                   } else {
                       toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, 0);
                       agent.setAgentNumberOfControllers(0);
                   }

                   if (jsonObject.containsKey(AgentKeys.VERSION)) {
                       Object currDeviceDetails = jsonObject.get(AgentKeys.VERSION);
                       String currDeviceDetailsString = currDeviceDetails.toString();
                       String currVersion = getVersion(currDeviceDetails);
                       if ( (agent.getAgentDeviceDetails() == null) || (agent.getAgentDeviceDetails() != null && !(agent.getAgentDeviceDetails().equalsIgnoreCase(currDeviceDetailsString))) ) {
                           toUpdate.put(AgentKeys.DEVICE_DETAILS, currDeviceDetailsString);
                           toUpdate.put(AgentKeys.VERSION, currVersion);
                           agent.setAgentDeviceDetails(currDeviceDetailsString);
                           agent.setAgentVersion(currVersion);
                       }
                   }
                   if (jsonObject.containsKey(AgentKeys.WRITABLE)) {
                       Boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentKeys.WRITABLE).toString());
                       if (agent.getWritable() != currWriteble) {
                           toUpdate.put(AgentKeys.WRITABLE, currWriteble);
                           agent.setWritable(currWriteble);
                       }
                   }
                   if (jsonObject.containsKey(AgentKeys.DELETED_TIME)) {
                       Long currDeletedTime = Long.parseLong(jsonObject.get(AgentKeys.DELETED_TIME).toString());
                       if (agent.getDeletedTime().longValue() != currDeletedTime.longValue()) {
                           agent.setDeletedTime(currDeletedTime);
                           toUpdate.put(AgentKeys.DELETED_TIME, currDeletedTime);
                       }
                   }
                   if(agent.getAgentType() == null && (jsonObject.containsKey(AgentKeys.AGENT_TYPE)) ){
                       toUpdate.put(AgentKeys.AGENT_TYPE, (AgentType.valueOf(jsonObject.get(AgentKeys.AGENT_TYPE).toString())).getKey());
                   }
                   if (!toUpdate.isEmpty()) {
                       toUpdate.put(AgentKeys.LAST_MODIFIED_TIME, System.currentTimeMillis());
                   }
                   if (!toUpdate.isEmpty()) {
                       toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                       context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,toUpdate);
                       if(updateAgentChain.execute(context)){
                           return 1;
                       }
                   }
                   return 0;
               } catch (Exception e) {
                   LOGGER.info("Exception occurred ", e);
               }
           }

       return 0;
    }


    public long addAgent(FacilioAgent agent) {
        JSONObject payload = new JSONObject();
        long currTime = System.currentTimeMillis();
        payload.put(AgentKeys.NAME,  agent.getAgentName());
        payload.put(AgentKeys.DISPLAY_NAME, agent.getDisplayName());
        payload.put(AgentKeys.STATE, agent.getAgentState());
        payload.put(AgentKeys.CONNECTION_STATUS, agent.getAgentConnStatus());
        payload.put(AgentKeys.SITE_ID, agent.getSiteId());
        payload.put(AgentKeys.AGENT_TYPE, agent.getAgentType());
        payload.put(AgentKeys.DATA_INTERVAL, agent.getAgentDataInterval());
        payload.put(AgentKeys.WRITABLE, agent.getWritable());
        payload.put(AgentKeys.DEVICE_DETAILS, agent.getAgentDeviceDetails());
        payload.put(AgentKeys.VERSION, agent.getAgentVersion());
        payload.put(AgentKeys.CREATED_TIME, currTime);
        payload.put(AgentKeys.LAST_MODIFIED_TIME, currTime);
        payload.put(AgentKeys.LAST_DATA_RECEIVED_TIME, currTime);
        Chain chain = TransactionChainFactory.getAddAgentChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PAY_LOAD,payload);
        try {
            chain.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_TABLE).select(FieldFactory.getAgentDataFields())
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(agentDataModule))
              .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(agentDataModule),agent.getAgentName(),StringOperators.IS));
        long id = 0L;
        try {
            List<Map<String,Object>> row = selectRecordBuilder.get();
            if(row.size() == 1 && row.get(0).containsKey(AgentKeys.ID)){
               id = Long.parseLong(row.get(0).get(AgentKeys.ID).toString());
            }else {
                LOGGER.info("Exception multiple entries with same AgentId ");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while fetching agent detail for agentId ",e);
        }

        try {
            agent.setId(id);
            if(id > 0)  {
                agentMap.put(agent.getAgentName(), agent);
            }
            return id;

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return 0L;
    }


    private String getVersion(Object payload) {
        JSONObject jsonObject2 = (JSONObject)payload;
        return jsonObject2.get(AgentKeys.FACILIO_MQTT_VERSION).toString();
    }



    //This method fetches Details of all the agent in the current Org and which aren't deleted.
    public List<Map<String,Object>> agentDetails()
    {
        FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getControllerIdCount(ModuleFactory.getControllerModule()));
        fields.addAll(FieldFactory.getAgentDataFields());
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_TABLE)
                .select(fields).leftJoin(AgentKeys.CONTROLLER_TABLE)
                .on(AgentKeys.AGENT_TABLE +".ID=Controller.Agent_Id")
                .groupBy(AgentKeys.AGENT_TABLE +".ID").andCondition(CriteriaAPI.getOrgIdCondition(orgId,agentDataModule))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(agentDataModule),"NULL", CommonOperators.IS_EMPTY));

        try {
            return genericSelectRecordBuilder.get();
        } catch (Exception e) {
            LOGGER.info("Exception occured ",e);
        }
        return new ArrayList<>();
    }

    /**
     * This method inserts system's current time to the specified agent's Deleted_Time column so that it will be considered deleted.
     * @return true if the deletion process happens and false if account is null or deletion doesn't happen.
     */
    public static boolean agentDelete(String agentName)  {
        if(AccountUtil.getCurrentOrg()!= null) {
            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getDeletedTimeField(ModuleFactory.getAgentDataModule()));
            HashMap<String, Object> toUpdate = new HashMap<>();
            toUpdate.put(AgentKeys.DELETED_TIME, System.currentTimeMillis());
            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(AgentKeys.AGENT_TABLE)
                    .fields(fields);
//                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));
            if (agentName!=null) {
                int deletedRows = 0;
                try {
                    deletedRows = genericUpdateRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()),agentName, StringOperators.IS))
                            .update(toUpdate);
                } catch (SQLException e) {
                    LOGGER.info("Exception occurred");
                }
                return (deletedRows > 0);
            } else {
                return false;
            }
        }
        return false;
    }

    static boolean agentEdit(JSONObject payload) throws SQLException {
        boolean status = false;
        if(AccountUtil.getCurrentOrg() != null && payload.containsKey(AgentKeys.ID)) {
            Chain agentEditChain = TransactionChainFactory.getAgentEditChain();
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.PAY_LOAD,payload);
            try {
                status = agentEditChain.execute(context);
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return status;
    }

    /**
     * This method inserts agent-log data into the log table, each time a message is sent to or received from an agent.
     * @param payLoad Contains data needed for the log-table.
     * @param orgId Tells which org's lod data to be fetched.
     * @param agentId If passed, fetches only that particular agent's log data.NOT MANDATORY.
     * @param sent
     */
    public static void putLog(JSONObject payLoad, Long orgId,Long agentId,boolean sent) {
        Chain addLogChain = TransactionChainFactory.addLogChain();
        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID,orgId);
            Map<String, Object> toUpdate = new HashMap<>();
            if (sent) {
                payLoad.put(AgentKeys.COMMAND_STATUS, CommandStatus.SENT.getKey());
                toUpdate.put(AgentKeys.COMMAND_STATUS, CommandStatus.SENT.getKey());
                LOGGER.info(" Sent message logged ");
                if (payLoad.containsKey(AgentKeys.MESSAGE_ID)) {
                    LOGGER.info(" sent message logged and messageId - "+payLoad.get(AgentKeys.MESSAGE_ID).toString() );
                }
            } else {
                if (payLoad.containsKey(AgentKeys.COMMAND_STATUS)) {
                    toUpdate.put(AgentKeys.COMMAND_STATUS, Integer.parseInt(payLoad.get(AgentKeys.COMMAND_STATUS).toString()));
                }
            }
            if (payLoad.containsKey(AgentKeys.COMMAND)) {
                toUpdate.put(AgentKeys.COMMAND, ControllerCommand.valueOf(payLoad.get(AgentKeys.COMMAND).toString()).getValue());
            }
            toUpdate.put(AgentKeys.AGENT_ID, agentId);
            toUpdate.put(AgentKeys.TIMESTAMP, System.currentTimeMillis());
            if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
                toUpdate.put(AgentKeys.DEVICE_ID, payLoad.get(AgentKeys.DEVICE_ID).toString());
            }
            if (payLoad.containsKey(AgentKeys.MESSAGE_ID)) {
                toUpdate.put(AgentKeys.MESSAGE_ID, payLoad.get(AgentKeys.MESSAGE_ID).toString());
            }
            if (payLoad.containsKey(AgentKeys.CONTENT)) {
                toUpdate.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.CONTENT));
            }
            try {
                context.put(FacilioConstants.ContextNames.TO_INSERT_MAP,toUpdate);
                addLogChain.execute(context);
               /* ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                bean.addLog(toUpdate);*/
            } catch (Exception e)
            {
                LOGGER.info("Exception occured ", e);
            }
        }



    /**
     * This method fetches agent-log data of the entire organization or a particular agent.
     * @param agentId If passed, only those ogs which belongs this particular agent is returned, NOT MANDATORY.
     * @return agent-log data from database
     * @throws Exception
     */
    public static List<Map<String,Object>> getAgentLog(Long agentId) throws Exception {
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_LOG_TABLE)
                .select(FieldFactory.getAgentLogFields());
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentLogModule()));
        if(agentId != null){
             genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(ModuleFactory.getAgentLogModule()),agentId.toString(),NumberOperators.EQUALS));
        }
        return genericSelectRecordBuilder.get();
    }

    public boolean addAgentMessage(String recordId) throws Exception{
        return addOrUpdateAgentMessage(recordId,MessageStatus.RECIEVED);
    }
    public boolean updateAgentMessage(String recordId,MessageStatus messageStatus) throws Exception{
        return addOrUpdateAgentMessage(recordId,messageStatus);
    }

    private   boolean addOrUpdateAgentMessage(String recordId, MessageStatus messageStatus)throws Exception{
        boolean status = false;

            Map<String,Object> map = new HashMap<>();
            map.put(AgentKeys.RECORD_ID,recordId);
            map.put(AgentKeys.MSG_STATUS,messageStatus.getStatusKey());
            map.put(AgentKeys.START_TIME,System.currentTimeMillis());

            Chain updateAgentMessageChain = TransactionChainFactory.getUpdateAgentMessageChain();
            Chain addAgentMessageChain = TransactionChainFactory.getAddAgentMessageChain();

            FacilioContext context = new FacilioContext();
            context.put(AgentKeys.ORG_ID,orgId);


            if(messageStatus == MessageStatus.RECIEVED ){
                try {
                    context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,map);
                    if (addAgentMessageChain.execute(context)) {
                        status = true;
                    }
                }catch (MySQLIntegrityConstraintViolationException e){
                    LOGGER.info("Duplicate Message "+e.getMessage());
                }
            }

            else if(messageStatus == MessageStatus.DATA_EMPTY){
                map.put(AgentKeys.FINISH_TIME, System.currentTimeMillis());
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,map);
                if(updateAgentMessageChain.execute(context)){
                    status = true;
                }
            }
            else {
                map.put(AgentKeys.FINISH_TIME, System.currentTimeMillis());
                map.remove(AgentKeys.START_TIME);
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,map);
                if(updateAgentMessageChain.execute(context)){
                    status = true;
                }
            }
           return status;
    }

    // not used
    /**
     * This method fetches metrics data of a particular agent and for a particular Publish-type.
     * If agentId and publishType is absent results with just ORGID condition is returned.
     * @param agentId This parameter if passed fetches only those data which belongs to an agent,NOT MANDATORY.
     * @param publishType This parameter if passed fetches only those data which belongs to this publish-type,NOT MANDATORY.
     * @throws Exception
     */
    public static List<Map<String,Object>> getAgentMetrics(Long agentId,Integer publishType) throws Exception {
        FacilioModule agentMetriceModule = ModuleFactory.getAgentMetricsModule();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.METRICS_TABLE)
                .select(FieldFactory.getAgentMetricsFields());
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentMetricsModule()));
        if(agentId != null){
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(agentMetriceModule),agentId.toString(),NumberOperators.EQUALS));
        }
        if(publishType != null){
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(agentMetriceModule),publishType.toString(),NumberOperators.EQUALS));
        }
        return genericSelectRecordBuilder.get();
    }


    /**
     * This method adds metrics to the database, which holds data explaining size and number of messages that were sent by the agent per publishtype, per day.
     * If message already there for a particular Publishtype tha day, updation takes place else insertion.
     * @param messageSize Size of an individual message.
     * @param agentId If of the agent that sent this message.
     * @param publishType Publish-Type of the message.
     */
    public void addAgentMetrics(Integer messageSize, Long agentId, int publishType){
        Long createdTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis());
        Long lastUpdatedTime = System.currentTimeMillis();
        ModuleCRUDBean bean;
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> record;
        Chain addAgentMetricsChain = TransactionChainFactory.getAddAgentMetricsChain();
        Chain updateAgentMetricsChain = TransactionChainFactory.getUpdateAgentMetricsChain();
        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID,orgId);
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String,Object>> records = bean.getMetrics(agentId,publishType,createdTime);
            if(!records.isEmpty()) {
                record = records.get(0);
                if ( (!record.isEmpty()) && createdTime == Long.parseLong(record.get(AgentKeys.CREATED_TIME).toString())) {
                        HashMap<String, Object> criteria = new HashMap<>();
                        criteria.put(AgentKeys.AGENT_ID, agentId);
                        criteria.put(EventUtil.DATA_TYPE, publishType);

                        metrics.put(AgentKeys.SIZE, Integer.parseInt(record.get(AgentKeys.SIZE).toString()) + messageSize);
                        metrics.put(AgentKeys.NO_OF_MESSAGES, Integer.parseInt(record.get(AgentKeys.NO_OF_MESSAGES).toString()) + 1);
                        metrics.put(AgentKeys.LAST_UPDATED_TIME, lastUpdatedTime);
                        context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,metrics);
                        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);

                        updateAgentMetricsChain.execute(context);
                        //bean.updateAgentMetrics(metrics, criteria);
                    }
            }
            else {
                metrics.put(AgentKeys.AGENT_ID, agentId);
                metrics.put(EventUtil.DATA_TYPE, publishType);
                metrics.put(AgentKeys.NO_OF_MESSAGES, 1);
                metrics.put(AgentKeys.SIZE, messageSize);
                metrics.put(AgentKeys.CREATED_TIME, createdTime);
                metrics.put(AgentKeys.LAST_UPDATED_TIME, lastUpdatedTime);
                context.put(FacilioConstants.ContextNames.TO_INSERT_MAP,metrics);
                addAgentMetricsChain.execute(context);
                //bean.insertAgentMetrics(metrics);
            }


        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    public boolean isDuplicate(String recordId) throws Exception{
        boolean status = true;
            FacilioModule messageModule = ModuleFactory.getAgentMessageModule();
            FacilioContext context = new FacilioContext();

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentMessagePartitionKeyField(messageModule), recordId, StringOperators.IS));

            context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentKeys.AGENT_MESSAGE_TABLE);
            context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getAgentMessageFields());
            context.put(FacilioConstants.ContextNames.MODULE, messageModule);
            context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            try {
                ModuleCRUDBean bean;
                bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                List<Map<String, Object>> rows = bean.getRows(context);
                if (((rows == null) || (rows.isEmpty()))) {
                    status = false;
                }
            } catch (Exception e) {
                LOGGER.info("Exception Occurred ", e);
                throw e;
            }
        return status;
    }



}




