package com.facilio.agentv2.device;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceUtil
{

    private static final Logger LOGGER = LogManager.getLogger(DeviceUtil.class.getName());

    public void processDevices(FacilioAgent agent, JSONObject payload){
        LOGGER.info(" processing devices ");
        List<Device> devices = new ArrayList<>();
        if(containsValueCheck(AgentConstants.DATA,payload)){
            JSONArray devicesArray = (JSONArray) payload.get(AgentConstants.DATA);
            if(devicesArray.isEmpty()){
                LOGGER.info("Exception Occurred, Device data is empty");
                return;
            }
            for(Object deviceObject : devicesArray) {
                JSONObject deviceJSON = (JSONObject) deviceObject;
                deviceJSON.put(AgentConstants.AGENT_ID, agent.getId());
                Device device = new Device(AccountUtil.getCurrentOrg().getOrgId(), agent.getId());
                device.setSiteId(agent.getSiteId());
                if (deviceJSON.containsKey(AgentConstants.IDENTIFIER)) {
                    device.setName(String.valueOf(deviceJSON.get(AgentConstants.IDENTIFIER)));
                }else {
                    LOGGER.info("Exception occurred, no identifier found in device json -> "+payload);
                    return;
                }
                device.setCreatedTime(System.currentTimeMillis());
                if ( ! deviceJSON.containsKey(AgentConstants.CREATED_TIME) ) {
                    deviceJSON.put(AgentConstants.CREATED_TIME, device.getCreatedTime());
                }
                device.setControllerProps(deviceJSON);
                devices.add(device);
            }
            addDevices(devices);
        }
    }

    private boolean addDevices(List<Device> devices) {
        if(devices != null && ! devices.isEmpty()){
            FacilioChain chain = TransactionChainFactory.getAddDevicesChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.DATA,devices);
            try {
                return chain.execute();
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return false;
    }

    private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

    public static List<Map<String,Object>> getDevices(Long agentId, List<Long> ids){
        LOGGER.info("getting devices");
        ModuleCRUDBean bean;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",  AccountUtil.getCurrentOrg().getOrgId());
            FacilioContext context = new FacilioContext();
            Criteria criteria = new Criteria();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.ID),"0",NumberOperators.GREATER_THAN));
            if(agentId != null){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
            }
            if( ids != null &&  ( ! ids.isEmpty() ) ){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID),StringUtils.join(ids,","), NumberOperators.EQUALS));
            }
            context.put(FacilioConstants.ContextNames.TABLE_NAME, ModuleFactory.getFieldDeviceModule().getTableName());
            context.put(FacilioConstants.ContextNames.FIELDS, fieldMap.values());
            context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
            return bean.getRows(context);
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return new ArrayList<>();
    }
}
