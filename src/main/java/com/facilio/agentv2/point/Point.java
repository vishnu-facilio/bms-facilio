package com.facilio.agentv2.point;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.controller.FacilioDataType;
import com.facilio.agent.controller.FacilioPoint;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.JsonUtil;
import com.facilio.connected.ResourceType;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;

public abstract class Point extends FacilioPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(Point.class.getName());
    @Deprecated
    public Point() { }

    public abstract FacilioControllerType getControllerType();
    public Point (long agentId){
        this.agentId = agentId;
    }
    public Point(long agentId, long controllerId) {
        this.agentId = agentId;
        setControllerId(controllerId);
//        this.orgId = AccountUtil.getCurrentOrg().getOrgId();
        setPointType(getControllerType().asInt());
        setInUse(false);
        setSubscribed(false);
    }

    public Integer getMappedType() {
        if(mappedType!=null){
            return mappedType.getIndex();
        }
        return null;
    }

    public PointEnum.MappedType getMappedTypeEnum() {
        return mappedType;
    }

    public void setMappedType(Integer mappedType) {
        this.mappedType = PointEnum.MappedType.valueOf(mappedType);
    }

    @JsonInclude
    private PointEnum.MappedType mappedType;

    private long agentId;
    @JsonInclude
    private long orgId;
    @JsonInclude
    private String displayName;
    @JsonInclude
    private int pointType;
    @JsonInclude
    private String deviceName;
    @JsonInclude
    private Long categoryId;
    @JsonInclude
    private Long resourceId;
    @JsonInclude
    private Long fieldId;
    @JsonInclude
    private boolean logical;
    @JsonInclude
    private long mappedTime;
    @JsonInclude
    private long lastRecordedTime;
    @JsonInclude
    private String lastRecordedValue;
    @JsonInclude
    private boolean agentWritable;
    @Getter @Setter
    private Long controlActionId;
    @Getter @Setter
    private Integer commandRetryCount;
    @JsonInclude @Setter @Getter
    private JSONObject states;

    private boolean dataMissing;
    //TODO remove actionName and overrideMillis (redundant in ControlActionCommandContext.java)
    private String actionName;
    @Getter @Setter
    private Long overrideTimeInMillis = 0L;

    @Getter @Setter @JsonIgnore
    private boolean isDataFiltered = false;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    private Long moduleId;

    @Getter @Setter
    private ResourceType readingScope;

    public boolean isAgentWritable() {
        return agentWritable;
    }

    public void setAgentWritable(boolean agentWritable) {
        this.agentWritable = agentWritable;
    }

	/*  private Integer subscribestatus = PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex();
    private Integer configureStatus = PointEnum.ConfigureStatus.UNCONFIGURED.getIndex();*/
    @JsonInclude
    private PointEnum.SubscribeStatus subscribeStatus = PointEnum.SubscribeStatus.UNSUBSCRIBED;
    @JsonInclude
    private PointEnum.ConfigureStatus configureStatus = PointEnum.ConfigureStatus.UNCONFIGURED;
    /**
     * This method is used to get point as map which can be used to insert point to point table,
     * so add fields which are columns in point table
     * @return
     */
    @JsonIgnore
    public JSONObject getPointJSON(){
        JSONObject pointJSON = new JSONObject();
        if (getId() > 0) {
            pointJSON.put(AgentConstants.ID,getId());
        }
        pointJSON.put(AgentConstants.AGENT_ID,agentId);
        pointJSON.put(AgentConstants.ORGID,orgId);
        pointJSON.put(AgentConstants.NAME,getName());
        pointJSON.put(AgentConstants.DISPLAY_NAME,getDisplayName());
        pointJSON.put(AgentConstants.DESCRIPTION,getDescription());
        pointJSON.put(AgentConstants.DATA_TYPE,getDataTypeAsInt());
        pointJSON.put(AgentConstants.POINT_TYPE,getPointType());
        pointJSON.put(AgentConstants.DEVICE_NAME,getDeviceName());
        pointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        pointJSON.put(AgentConstants.ASSET_CATEGORY_ID, getCategoryId());
        pointJSON.put(AgentConstants.RESOURCE_ID,getResourceId());
        pointJSON.put(AgentConstants.FIELD_ID,getFieldId());
        pointJSON.put(AgentConstants.IN_USE,isInUse());
        pointJSON.put(AgentConstants.WRITABLE,isWritable());
        pointJSON.put(AgentConstants.AGENT_WRITABLE,isAgentWritable());
        pointJSON.put(AgentConstants.SUBSCRIBED,isSubscribed());
        pointJSON.put(AgentConstants.LOGICAL,isLogical());
        pointJSON.put(AgentConstants.THRESHOLD_JSON,getThresholdJSON());
        pointJSON.put(AgentConstants.CREATED_TIME,getCreatedTime());
        pointJSON.put(AgentConstants.MAPPED_TIME,getMappedTime());
        pointJSON.put(AgentConstants.UNIT,getUnit());
        pointJSON.put(AgentConstants.STATES, getStates());
        if(getMappedTypeEnum() != null){
            pointJSON.put(AgentConstants.MAPPED_TYPE, getMappedType());
        }
        if(getConfigureStatusEnum() != null ){
            pointJSON.put(AgentConstants.CONFIGURE_STATUS,getConfigureStatus());
        }else {
            setConfigureStatus(PointEnum.ConfigureStatus.UNCONFIGURED.getIndex());
        }
        if(getSubscribestatusEnum() != null ){
            pointJSON.put(AgentConstants.SUBSCRIBE_STATUS,getSubscribeStatus());
        }else {
            setConfigureStatus(PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex());
        }
        if (getInterval() > 0) {
        	 pointJSON.put(AgentConstants.DATA_INTERVAL,getInterval());
        }
        if (getReadingScopeEnum() != null) {
            pointJSON.put(AgentConstants.READING_SCOPE,getReadingScope());
        }
        return pointJSON;
    }

    

    public boolean isLogical() {
		return logical;
	}

	public void setLogical(boolean virtual) {
		this.logical = virtual;
	}

	public long getAgentId() { return agentId; }
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }


    public long getOrgId() { return orgId; }
    public void setOrgId(long orgId) { this.orgId = orgId; }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public long getMappedTime() {
        return mappedTime;
    }

    public void setMappedTime(long mappedTime) {
        this.mappedTime = mappedTime;
    }

    public long getLastRecordedTime() {
        return lastRecordedTime;
    }

    public void setLastRecordedTime(long lastRecordedTime) {
        this.lastRecordedTime = lastRecordedTime;
    }

    public String getLastRecordedValue() {
        return lastRecordedValue;
    }

    public void setLastRecordedValue(String lastRecordedValue) {
        this.lastRecordedValue = lastRecordedValue;
    }

    public boolean getDataMissing() { return dataMissing; };

    public void setDataMissing(boolean status) { this.dataMissing = status; };

    public int getConfigureStatus() {
        return configureStatus.getIndex();
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName(){
        return actionName;
    }

    @JSON(serialize = false)
    public PointEnum.ConfigureStatus getConfigureStatusEnum() {
        return configureStatus;
    }

    /* @JsonIgnore
     public void setConfigureStatus(PointEnum.ConfigureStatus configureStatus) { this.configureStatus = configureStatus; }*/
    public void setConfigureStatus(int configureStatus) {
        this.configureStatus = PointEnum.ConfigureStatus.valueOf(configureStatus);
    }

    public int getSubscribeStatus() {
        return subscribeStatus.getIndex();
    }

    @JSON(serialize = false)
    public PointEnum.SubscribeStatus getSubscribestatusEnum() {
        return subscribeStatus;
    }

    /* @JsonIgnore
     public void setSubscribestatus(PointEnum.SubscribeStatus subscribestatus) { this.subscribestatus = subscribestatus; }*/
    public void setSubscribeStatus(int subscribestatus) {
        this.subscribeStatus = PointEnum.SubscribeStatus.valueOf(subscribestatus);
    }

    public int getReadingScope() {
        if (readingScope != null) {
            return readingScope.getIndex();
        }
        return -1;
    }
    public ResourceType getReadingScopeEnum(){
        return readingScope;
    }
    public void setReadingScope(int readingScope) {
        this.readingScope = ResourceType.valueOf(readingScope);
    }

    /**
     * This method builds pointObject using map
     * used in case populating points from db or creating point from agent-message.
     * json returns long for all types numbers while map doesn't and so use JsonUtil for casting.
     * @param row can be {@link JSONObject} from agent or {@link Map<String,Object>} from db
     * @return
     */
    @JsonIgnore
    public Point getPointObjectFromMap(Map<String,Object> row){
        if( (row == null) || (row.isEmpty()) ){
            return this;
        }
        if(containsValueCheck(AgentConstants.NAME,row)){
            setName((String) row.get(AgentConstants.NAME));
        }
        if(containsValueCheck(AgentConstants.DISPLAY_NAME,row)) {
        	setDisplayName((String)row.get(AgentConstants.DISPLAY_NAME));
        }
        if(containsValueCheck(AgentConstants.DATA_TYPE,row)){
            setDataType(FacilioDataType.valueOf(JsonUtil.getInt(row.get(AgentConstants.DATA_TYPE))));
        }
        if(containsValueCheck(AgentConstants.CONTROLLER_ID,row)){
            setControllerId(JsonUtil.getLong(row.get(AgentConstants.CONTROLLER_ID)));
        }
        if(containsValueCheck(AgentConstants.WRITABLE,row)){
            setWritable(JsonUtil.getBoolean(row.get(AgentConstants.WRITABLE)));
        }
        if(containsValueCheck(AgentConstants.AGENT_WRITABLE,row)){
            setAgentWritable(JsonUtil.getBoolean(row.get(AgentConstants.AGENT_WRITABLE)));
        }
        if (containsValueCheck(AgentConstants.IN_USE,row)){
            setInUse(JsonUtil.getBoolean(row.get(AgentConstants.IN_USE)));
        }
        if(containsValueCheck(AgentConstants.SUBSCRIBED,row)){
            setSubscribed(JsonUtil.getBoolean(row.get(AgentConstants.SUBSCRIBED)));
        }
        if(containsValueCheck(AgentConstants.THRESHOLD_JSON,row)){
            setThresholdJSON((String) row.get(AgentConstants.THRESHOLD_JSON));
        }
        if(containsValueCheck(AgentConstants.CREATED_TIME,row)){
            setCreatedTime(JsonUtil.getLong(row.get(AgentConstants.CREATED_TIME)));
        }
        if(containsValueCheck(AgentConstants.UNIT,row)){
            setUnit(JsonUtil.getInt(row.get(AgentConstants.UNIT)));
        }
        if(row.containsKey(AgentConstants.DEVICE_NAME)){
            setDeviceName(String.valueOf(row.get(AgentConstants.DEVICE_NAME)));
        }
        if(containsValueCheck(AgentConstants.SUBSCRIBE_STATUS,row)){
            setSubscribeStatus(JsonUtil.getInt(row.get(AgentConstants.SUBSCRIBE_STATUS)));
        }
        if(containsValueCheck(AgentConstants.CONFIGURE_STATUS,row)){
            setConfigureStatus(JsonUtil.getInt(row.get(AgentConstants.CONFIGURE_STATUS)));
        }
        if(row.containsKey(AgentConstants.IDENTIFIER)){
            setDeviceName(String.valueOf(row.get(AgentConstants.IDENTIFIER)));
        }
        if(row.containsKey(AgentConstants.RESOURCE_ID)){
            setResourceId((Long) row.get(AgentConstants.RESOURCE_ID));
        }
        if(row.containsKey(AgentConstants.FIELD_ID)){
            setFieldId((Long) row.get(AgentConstants.FIELD_ID));
        }
        if(row.containsKey(AgentConstants.ASSET_CATEGORY_ID)){
            setCategoryId((Long) row.get(AgentConstants.ASSET_CATEGORY_ID));
        }
        if(row.containsKey(AgentConstants.AGENT_ID)){
            setAgentId((Long) row.get(AgentConstants.AGENT_ID));
        }
        if(row.containsKey(AgentConstants.DATA_INTERVAL)){
            setInterval(FacilioUtil.parseInt(row.get(AgentConstants.DATA_INTERVAL)));
        }
        if(row.containsKey(AgentConstants.DATA_MISSING)){
            setDataMissing((boolean) row.get(AgentConstants.DATA_MISSING));
        }
        if (row.containsKey(AgentConstants.READING_SCOPE)){
            setReadingScope((Integer) row.get(AgentConstants.READING_SCOPE));
        }
        return this;
    }


    /**
     * overridden methods in child classes will be called.
     * @return
     */
    @JsonIgnore
    public abstract JSONObject getChildJSON();

    /**
     * This method gets a point as {@link JSONObject} with values for child point table's columns.
     * @return {@link JSONObject}
     */
    @JsonIgnore
    public JSONObject toJSON(){
        JSONObject pointJSON = new JSONObject();
        pointJSON.putAll(getPointJSON());
        pointJSON.putAll(getChildJSON());
        return pointJSON;
    }


    /**
     * This method checks {@link Map<String, Object>} for the parameter key and if it's value isn't null.
     * @param key {@link String} which is the key whose value one wants.
     * @param jsonObject {@link JSONObject} which has values with respective keys.
     * @return true if there's the specified key and its value is't null, and false if key is missing or has a null value.
     */
    public static boolean containsValueCheck(String key, Map<String, Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

}
