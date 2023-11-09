package com.facilio.agentv2.cacheimpl;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Condition;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ControllerBean {

    public Controller getController(JSONObject payload, long agentId) throws Exception;

    public List<Controller> getControllers(int type, long agentId) throws Exception;

    public long addController(Controller controller);

    public long addController(Controller controller, boolean fromAgent);

    public long addController(Controller controller, FacilioAgent agent, boolean fromAgent) throws Exception;

    public Controller getControllerFromDb(long controllerId) throws Exception;

    public List<Condition> getControllerCondition(JSONObject childJson, FacilioControllerType controllerType) throws Exception;

    public <T extends Controller> T makeControllerFromMap(Map<String, Object> map, FacilioControllerType controllerType) throws Exception;

    public String getControllerModuleName(FacilioControllerType controllerType);

    public boolean editController(long controllerId, JSONObject controllerData) throws Exception;

    public long getControllersCount(FacilioContext context);

    public boolean deleteControllerApi(Long id) throws Exception;

    public boolean deleteControllerApi(List<Long> ids) throws Exception;

    public boolean resetController(Long controllerId) throws Exception;

    public void resetConfiguredPoints(Long controllerId) throws Exception;

    public Set<Long> getControllerIds(List<Long> agentId) throws Exception;

    public JSONObject getControllerCountData(List<Long> agentIds) throws Exception;

    public JSONObject getControllerCountData(Long agentId) throws Exception;

    public List<Map<String, Object>> getControllerDataForAgent(FacilioContext contextProps) throws Exception;

    public List<Map<String, Object>> getControllerData(Long agentId, Long controllerId, FacilioContext contextProps) throws Exception;

    public List<Map<String, Object>> getControllerTypes(Long agentId) throws Exception;

    public List<Map<String, Object>> getControllerFilterData(Long agentId, Integer controllerType) throws Exception;

    public Controller getControllerByName(Long agentId, String deviceName) throws Exception;

    public List<Controller> getControllersByNames(Long agentId, Set<String> deviceNames, FacilioControllerType controllerType) throws Exception;

    public List<Controller> getControllersByNames(Long agentId, Set<String> deviceNames) throws Exception;

    public void discoverPoint(long controllerId, int timeout) throws Exception;

    public Controller getController(Long controllerId, Long agentId) throws Exception;

    public List<Controller> getControllers(List<Long> controllerIds, Long agentId) throws Exception;

    public List<? extends Controller> getControllersToAdd(long agentId, FacilioControllerType controllerType, List<? extends Controller> controllerList) throws Exception;

    void updateLastDataReceivedTime(Long agentId, Long controllerId, Long lastDataReceivedTime) throws Exception;
}
