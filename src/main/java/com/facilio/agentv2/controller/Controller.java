package com.facilio.agentv2.controller;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.db.criteria.Condition;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Getter
@Setter

public class Controller extends AssetContext {

    public final String IDENTIFIER_SEPERATER = "-";

    private long orgId;

    @JsonInclude
    private long agentId;

    @JsonInclude
    private String name;

    private String displayName;

    @JsonInclude
    private long dataInterval;

    @JsonInclude
    private Boolean writable;

    @JsonInclude
    private Boolean active;

    @JsonInclude
    private int controllerType = -1;

    @JsonInclude
    private Object controllerProps;

    @JsonInclude
    private int availablePoints = 0;

    @JsonInclude
    private long createdTime = -1;

    @JsonInclude
    private long lastModifiedTime = -1;

    @JsonInclude
    private long lastDataRecievedTime = -1;

    private long deletedTime = -1;

    @JsonIgnore
    private Map<String, FacilioField> fieldsMap;

    private final long DEFAULT_DATA_INTERVAL = 900000;

    //private final boolean DEFAULT_ACTIVE = true;
    //private final int DEFAULT_AVAIL_POINTS = 0;
    public Controller() {
        dataInterval = DEFAULT_DATA_INTERVAL;
    /*    active = DEFAULT_ACTIVE;
        availablePoints =  DEFAULT_AVAIL_POINTS;
    */
    }

    public Controller(long agentId, long orgId) {
        this.agentId = agentId;
        this.orgId = orgId;
        dataInterval = DEFAULT_DATA_INTERVAL;
    }


    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDataInterval() {
        return dataInterval;
    }

    public void setDataInterval(long dataInterval) {
        this.dataInterval = dataInterval;
    }

    public Boolean isWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Boolean getWritable() {
        return writable;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


    public int getControllerType() {
        return controllerType;
    }

    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    public Object getControllerProps() {
        return controllerProps;
    }

    public void setControllerProps(Object controllerProps) {
        this.controllerProps = controllerProps;
    }

    public int getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(int availablePoints) {
        this.availablePoints = availablePoints;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getLastDataRecievedTime() {
        return lastDataRecievedTime;
    }

    public void setLastDataRecievedTime(long lastDataRecievedTime) {
        this.lastDataRecievedTime = lastDataRecievedTime;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }

    @JsonIgnore
    public static Map<String, FacilioField> getFieldsMap(String moduleName) throws Exception {
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (moduleName != null) {
            return FieldFactory.getAsMap(modBean.getAllFields(moduleName));
        } else {
            throw new IllegalArgumentException(" module name cant be null");
        }
    }

    /**
     * Gives Controller as JSON including child controller's details.
     *
     * @return {@link JSONObject}
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(getParentJSON());
        jsonObject.putAll(getChildJSON());
        return jsonObject;
    }

    /**
     * Gives child Controller as JSON .
     * <p>
     * used to insert respective child controller in its table.
     *
     * @return {@link JSONObject}
     */
    @JsonIgnore
    public JSONObject getChildJSON() {
        return null;
    }

    /**
     * gives Controller as JSON with just parent controller's data.
     * <p>
     * used for inserting controller in controller table.
     *
     * @return
     */
    @JsonIgnore
    public JSONObject getParentJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.NAME, getName());
        object.put(AgentConstants.AGENT_ID, agentId);
        object.put(AgentConstants.DATA_INTERVAL, getDataInterval());
        object.put(AgentConstants.WRITABLE, isWritable());
        object.put(AgentConstants.ACTIVE, isActive());
        object.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
        object.put(AgentConstants.CONTROLLER_PROPS, "controllerProps");
        object.put(AgentConstants.AVAILABLE_POINTS, getAvailablePoints());
        if (getCreatedTime() < 0) {
            object.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        }
        object.put(AgentConstants.CREATED_TIME, getCreatedTime());
//        if (getLastModifiedTime() < 0) {
//            object.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
//        }
        object.put(AgentConstants.LAST_MODIFIED_TIME, getLastModifiedTime());
        object.put(AgentConstants.LAST_DATA_SENT_TIME, getLastDataRecievedTime());
        //object.put(AgentConstants.DELETED_TIME, -1);
        return object;
    }

    /**
     * Builds a {@link Controller} from map.
     *
     * @param row can be {@link Map<String, Object>} from database or {@link JSONObject} from agent.
     * @return {@link Controller} which will be an instance of corresponding child controller's Class.
     */
    @JsonIgnore
    public Controller getControllerFromJSON(Map<String, Object> row) {
        if (containsValueCheck(AgentConstants.ID, row)) {
            setId((Long) row.get(AgentConstants.ID));
        }
        if (containsValueCheck(AgentConstants.NAME, row)) {
            setName((String) row.get(AgentConstants.NAME));
        }
        if (containsValueCheck(AgentConstants.DATA_INTERVAL, row)) {
            setDataInterval(JsonUtil.getLong(row.get(AgentConstants.DATA_INTERVAL)));
        }
        if (containsValueCheck(AgentConstants.WRITABLE, row)) {
            setWritable(JsonUtil.getBoolean(row.get(AgentConstants.WRITABLE)));
        }
        if (containsValueCheck(AgentConstants.ACTIVE, row)) {
            setActive(JsonUtil.getBoolean(row.get(AgentConstants.ACTIVE)));
        }
        if (containsValueCheck(AgentConstants.CONTROLLER_TYPE, row)) {
            setControllerType(JsonUtil.getInt(row.get(AgentConstants.CONTROLLER_TYPE)));
        }
        if (containsValueCheck(AgentConstants.CONTROLLER_PROPS, row)) {
            setControllerProps(row.get(AgentConstants.CONTROLLER_PROPS));
        }
        if (containsValueCheck(AgentConstants.AVAILABLE_POINTS, row)) {
            setAvailablePoints(JsonUtil.getInt(row.get(AgentConstants.AVAILABLE_POINTS)));
        }
        if (containsValueCheck(AgentConstants.CREATED_TIME, row)) {
            setCreatedTime(JsonUtil.getLong(row.get(AgentConstants.CREATED_TIME)));
        }
        if (containsValueCheck(AgentConstants.LAST_MODIFIED_TIME, row)) {
            setLastModifiedTime(JsonUtil.getLong(row.get(AgentConstants.LAST_MODIFIED_TIME)));
        }
        if (containsValueCheck(AgentConstants.LAST_DATA_SENT_TIME, row)) {
            setLastDataRecievedTime(JsonUtil.getLong(row.get(AgentConstants.LAST_MODIFIED_TIME)));
        }
        if (containsValueCheck(AgentConstants.DELETED_TIME, row)) {
            setDeletedTime((Long) row.get(AgentConstants.DELETED_TIME));
        }
        return this;
    }

    /**
     * This method checks {@link Map<String, Object>} for the parameter key and if it's value isn't null.
     *
     * @param key        {@link String} which is the key whose value one wants.
     * @param jsonObject {@link JSONObject} which has values with respective keys.
     * @return true if there's the specified key and its value is't null, and false if key is missing or has a null value.
     */
    public static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if an object is null or empty.
     *
     * @param object
     * @return
     */
    public static boolean isNotNull(Object object) {
        if (object != null) {
            if (object instanceof String) {
                return !((String) object).isEmpty();
            }
            if (object instanceof Collection) {
                return !(((Collection) object).isEmpty());
            }
            if (object instanceof Map) {
                return !(((Map) object).isEmpty());
            }
        }
        return (object != null);
    }

    @JsonIgnore
    public FacilioAgent getAgent() throws Exception {
        if (getAgentId() > 0) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(getAgentId());
            if (agent != null) {
                return agent;
            } else {
                throw new Exception("agent can't be null");
            }
        } else {
//            throw new Exception("agentId can't be less than 0 -> for controllerId " + getControllerId());
        }
        return null;
    }

    @JsonIgnore
    public List<Condition> getControllerConditions() throws Exception {
        return null;
    }

    @JsonIgnore
    public String getIdentifier() throws Exception {
        return null;
    }

    @Override
	public ResourceType getResourceTypeEnum() {
		return ResourceType.CONTROLLER;
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getResourceType() {
		return ResourceType.CONTROLLER.getValue();
	}

    @Override
    public boolean equals(Object o){
        if(o instanceof Controller){
            Controller obj=(Controller) o;
            return obj.getAgentId()==this.getAgentId();
        }
        else{
            return false;
        }
    }
}
