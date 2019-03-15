package com.facilio.agent;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

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
    private static final long DEFAULT_TIME = 600000L;
    private Map<String, FacilioAgent> agentMap = new HashMap<>();


    public AgentUtil(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    public void populateAgentContextMap() {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String, Object>> records = bean.getAgentDataMap();
            for (Map<String,Object> record :records) {
                JSONObject payload = new JSONObject();
                payload.putAll(record);
                if(payload.isEmpty()){
                    return;
                }
                FacilioAgent agent = getFacilioAgentFromJson(payload);
                agentMap.put(agent.getAgentName(), agent);
            }
        } catch (Exception e1) {
            LOGGER.info("exception ",e1);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(AgentUtil.class.getName());

    /**
     * This method writes data from the Agent to its table in database using {@link com.facilio.sql.GenericInsertRecordBuilder}<br>
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

        if(payload.containsKey(AgentKeys.DEVICE_DETAILS)) {
            agent.setAgentDeviceDetails(payload.get(AgentKeys.DEVICE_DETAILS).toString());

            if(payload.containsKey(AgentKeys.VERSION)) {
                agent.setAgentVersion(payload.get(AgentKeys.VERSION).toString());
            }
        } else {
            if (payload.containsKey(AgentKeys.VERSION)) {
                agent.setAgentDeviceDetails(payload.get(AgentKeys.VERSION).toString());
            }

            if (payload.containsKey(AgentKeys.VERSION)) {
                agent.setAgentVersion(getVersion(payload.get(AgentKeys.VERSION)));
            }
        }

        if(payload.containsKey(AgentKeys.CONNECTION_STATUS)) {
            agent.setAgentConnStatus(Boolean.valueOf(payload.get(AgentKeys.CONNECTION_STATUS).toString()));
        } else {
            agent.setAgentConnStatus(false);
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
            agent.setAgentName(payload.get(AgentKeys.NAME).toString());
        }

        if(payload.containsKey(AgentKeys.AGENT)){
            agent.setAgentName(payload.get(AgentKeys.AGENT).toString());
        }

        if (agent.getAgentName() == null) {
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

    public int processAgent(JSONObject jsonObject) {
        String agentName = (String) jsonObject.get(AgentKeys.AGENT);
        
        if (StringUtils.isEmpty(agentName)) { //Temp fix to avoid NPE
        	agentName = orgDomainName;
        }
        
        if ( agentMap.containsKey(agentName) ) {
         FacilioAgent agent = agentMap.get(agentName);
            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(AgentKeys.TABLE_NAME)
                    .fields(FieldFactory.getAgentDataFields()).andCustomWhere(AgentKeys.NAME+"='"+agentName+"'");
            try {

                Map<String,Object> toUpdate = new HashMap<>();
                if(jsonObject.containsKey(AgentKeys.CONNECTION_STATUS)) {
                    Boolean connectionStatus = Boolean.valueOf((jsonObject.get(AgentKeys.CONNECTION_STATUS).toString()));
                    if (agent.getAgentConnStatus() != connectionStatus) {
                        toUpdate.put(AgentKeys.CONNECTION_STATUS, connectionStatus);
                        agent.setAgentConnStatus(connectionStatus);
                    }
                } else{
                    toUpdate.put(AgentKeys.CONNECTION_STATUS, false);
                    agent.setAgentConnStatus(false);
                }

                if(jsonObject.containsKey(AgentKeys.STATE)) {
                    Integer currStatus = Integer.parseInt(jsonObject.get(AgentKeys.STATE).toString());
                    if (agent.getAgentState() != currStatus) {
                        toUpdate.put(AgentKeys.STATE, currStatus);
                        agent.setAgentState(currStatus);

                    }
                }else{
                    toUpdate.put(AgentKeys.STATE,0);
                    agent.setAgentState(0);
                }

                if(jsonObject.containsKey(AgentKeys.DATA_INTERVAL)) {
                    Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
                    if ( agent.getAgentDataInterval() != currDataInterval .intValue() ) {
                        toUpdate.put(AgentKeys.DATA_INTERVAL, currDataInterval);
                        agent.setAgentDataInterval(currDataInterval);
                    }
                } else{
                    toUpdate.put(AgentKeys.DATA_INTERVAL, DEFAULT_TIME);
                    agent.setAgentDataInterval(DEFAULT_TIME);
                }

                if(jsonObject.containsKey(AgentKeys.NUMBER_OF_CONTROLLERS)){
                    Integer currNumberOfControllers = Integer.parseInt(jsonObject.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString());
                    if( (agent.getAgentNumberOfControllers().intValue() != currNumberOfControllers.intValue())){
                        toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS,currNumberOfControllers);
                        agent.setAgentNumberOfControllers(currNumberOfControllers);
                    }
                } else{
                    toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS,0);
                    agent.setAgentNumberOfControllers(0);
                }

                if (jsonObject.containsKey(AgentKeys.VERSION)) {
                    Object currDeviceDetails = jsonObject.get(AgentKeys.VERSION);
                    String currDeviceDetailsString = currDeviceDetails.toString();
                    if (!(agent.getAgentDeviceDetails().equalsIgnoreCase(currDeviceDetailsString))) {
                        toUpdate.put(AgentKeys.DEVICE_DETAILS, currDeviceDetails);
                        toUpdate.put(AgentKeys.VERSION, getVersion(currDeviceDetails));
                        agent.setAgentDeviceDetails(currDeviceDetailsString);
                    }
                }
                if(jsonObject.containsKey(AgentKeys.WRITABLE)){
                    Boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentKeys.WRITABLE).toString());
                    if(agent.getWritable() != currWriteble){
                        toUpdate.put(AgentKeys.WRITABLE,currWriteble);
                        agent.setWritable(currWriteble);
                    }
                }
                if(jsonObject.containsKey(AgentKeys.DELETED_TIME)){
                    Long currDeletedTime = Long.parseLong(jsonObject.get(AgentKeys.DELETED_TIME).toString());
                    if(agent.getDeletedTime().longValue() != currDeletedTime.longValue()){
                        agent.setDeletedTime(currDeletedTime);
                        toUpdate.put(AgentKeys.DELETED_TIME,currDeletedTime);
                    }
                }
                if(!toUpdate.isEmpty()) {
                    toUpdate.put(AgentKeys.LAST_MODIFIED_TIME,System.currentTimeMillis());
                    toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME,System.currentTimeMillis());
                    int n = genericUpdateRecordBuilder.update(toUpdate) ;
                    return n;
                }
                return 0;
            }
            catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }



        }
        else {
            FacilioAgent agent = getFacilioAgentFromJson(jsonObject);
            JSONObject payload = new JSONObject();
            long currTime = System.currentTimeMillis();
            payload.put(AgentKeys.CREATED_TIME,currTime);
            payload.put(AgentKeys.LAST_MODIFIED_TIME, currTime);
            payload.put(AgentKeys.STATE, agent.getAgentState());
            payload.put(AgentKeys.NAME,agent.getAgentName());
            payload.put(AgentKeys.DEVICE_DETAILS,agent.getAgentDeviceDetails());
            payload.put(AgentKeys.VERSION,agent.getAgentVersion());
            payload.put(AgentKeys.LAST_DATA_RECEIVED_TIME,currTime);
            payload.put(AgentKeys.DATA_INTERVAL,agent.getAgentDataInterval());
            payload.put(AgentKeys.CONNECTION_STATUS,agent.getAgentConnStatus());
            payload.put(AgentKeys.NUMBER_OF_CONTROLLERS,agent.getAgentNumberOfControllers());
            payload.put(AgentKeys.WRITABLE,agent.getWritable());
            GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(AgentKeys.TABLE_NAME)
                    .fields(FieldFactory.getAgentDataFields());

            try {
                int n = (int)genericInsertRecordBuilder.insert(payload);
                if(n > 0)  {
                    agentMap.put(agentName, agent);
                }
                return n;

            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }

        }
        return 0;
    }




    private String getVersion(Object payload) {
        JSONObject jsonObject2 = (JSONObject)payload;
        return jsonObject2.get(AgentKeys.FACILIO_MQTT_VERSION).toString();
    }

    public List<Map<String,Object>> agentDetailsAPI()
    {
        ModuleCRUDBean bean;
        List<Map<String, Object>> records;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            records = bean.getAgentDetails();
            return records;
        } catch (Exception e) {
            LOGGER.info("Exception occurred",e);
        }
        return null;
    }
    public List<Map<String,Object>> controllerDetailsAPI(Long agentId){
        ModuleCRUDBean bean;
        List<Map<String, Object>> records;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            records = bean.getAgentControllerDetails(agentId);
            return records;
        } catch (Exception e) {
            LOGGER.info("Exception occured",e);
        }
        return null;
    }

}
