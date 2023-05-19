package com.facilio.agentv2.cacheimpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.util.Collections;
import java.util.List;
public class ControllerBeanCacheImpl extends ControllerBeanImpl implements ControllerBean{
    private static final Logger LOGGER = LogManager.getLogger(ControllerBeanCacheImpl.class.getName());
    @Override
    public Controller getController(JSONObject payload, long agentId) throws Exception {
        FacilioCache<String, Object> controllerCache = LRUCache.getControllerCache();
        if(containsCheck(AgentConstants.CONTROLLER, payload)){
            JSONObject controllerJson = (JSONObject) payload.get(AgentConstants.CONTROLLER);
            if (containsCheck(AgentConstants.CONTROLLER_TYPE, payload)) {
                FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.CONTROLLER_TYPE)).intValue());
                if(controllerType==FacilioControllerType.NIAGARA && payload.containsKey(AgentConstants.PORT_NUMBER)){
                    controllerJson.put(AgentConstants.PORT_NUMBER, payload.get(AgentConstants.PORT_NUMBER));
                }
                Controller mockController = makeControllerFromMap(controllerJson,controllerType);
                String controllerIdentifier = mockController.getIdentifier();
                String key = CacheUtil.CONTROLLER_KEY(AccountUtil.getCurrentOrg().getOrgId(), agentId,mockController.getControllerType(), controllerIdentifier);
                Controller controller = (Controller) controllerCache.get(key);
                if(controller == null){
                    // Cache Miss
                    controller = super.getController(payload, agentId);
                    controllerCache.put(key, controller);
                    LOGGER.info("Cache-miss: Get Controller from DB for Controller: "+ key);
                    return controller;
                }
                // Cache Hit
                LOGGER.debug("Cache-Hit: Get Controller from cache for Controller: "+ key);
                return controller;
            } else {
                throw new Exception("Controller type is missing in payload -> "+ payload);
            }
        } else {
            // making custom controller
            throw new Exception("Controller is missing in payload -> "+ payload);
        }
    }

    public boolean editController(long controllerId, JSONObject controllerData) throws Exception {
        boolean isEdited = super.editController(controllerId, controllerData);
        Controller controller = getControllerFromDb(controllerId);
        String key = CacheUtil.CONTROLLER_KEY(AccountUtil.getCurrentOrg().getOrgId(), controller.getAgentId(), controller.getControllerType(), controller.getIdentifier());
        if(isEdited){
            LOGGER.info("Controller : "+ controller.getDisplayName() + " edited." );
            dropControllerFromCache(key);
        }
        return isEdited;
    }

    public boolean resetController(Long controllerId) throws Exception{
        boolean isControllerResetted = super.resetController(controllerId);
        Controller controller = getControllerFromDb(controllerId);
        String key = CacheUtil.CONTROLLER_KEY(AccountUtil.getCurrentOrg().getOrgId(), controller.getAgentId(), controller.getControllerType(), controller.getIdentifier());
        if(isControllerResetted){
            LOGGER.info("Controller reset successfully: " + key);
            dropControllerFromCache(key);
        }
        return isControllerResetted;
    }
    public boolean deleteControllerApi(List<Long> ids) throws Exception {
        boolean isControllerDeleted= false;
        for (Long id : ids) {
            Controller controller = getControllerFromDb(id);
            String key = CacheUtil.CONTROLLER_KEY(AccountUtil.getCurrentOrg().getOrgId(), controller.getAgentId(), controller.getControllerType(), controller.getIdentifier());
            isControllerDeleted = super.deleteControllerApi(Collections.singletonList(id));
            if (isControllerDeleted) {
                LOGGER.info("Controller Deleted: "+ controller.getDisplayName());
                dropControllerFromCache(key);
            }
        }
        return isControllerDeleted;
    }
    public void dropControllerFromCache(String key) {
        FacilioCache<String, Object> controllerCache = LRUCache.getControllerCache();
        if (controllerCache.get(key) != null) {
            controllerCache.remove(key);
            LOGGER.info("Invalidated Controller Cache: " + key);
        }
    }
}