package com.facilio.agentnew.point;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.controller.FacilioDataType;
import com.facilio.agent.controller.FacilioPoint;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public abstract class Point extends FacilioPoint{

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(Point.class.getName());
    @Deprecated
    public Point() { }

    public abstract FacilioControllerType getControllerType();

    public Point(long agentId, long controllerId) {
        this.agentId = agentId;
        setControllerId(controllerId);
        this.orgId = AccountUtil.getCurrentOrg().getOrgId();
        setPointType(getControllerType().asInt());
        setInUse(false);
        setSubscribed(false);
    }

    private long agentId;
    private long orgId;
    private String displayName;
    private int pointType;
    private String deviceName;
    private Long assetCategoryId;
    private Long resourceId;
    private Long fieldId;
    private boolean pseudo;
    private long mappedTime;
    private PointEnum.ConfigureStatus configureStatus = PointEnum.ConfigureStatus.UNCONFIGURED;
    private PointEnum.SubscribeStatus subscribestatus = PointEnum.SubscribeStatus.UNSUBSCRIBED;

    /**
     * This method is used to get point as map which can be used to insert point to point table,
     * so add fields which are columns in point table
     * @return
     */
    public JSONObject getPointJSON(){
        JSONObject pointJSON = new JSONObject();
        if (getId() > 0) {
            pointJSON.put(AgentConstants.ID,getId());
        }
        pointJSON.put(AgentConstants.ORGID,orgId);
        pointJSON.put(AgentConstants.NAME,getName());
        pointJSON.put(AgentConstants.DISPLAY_NAME,getDisplayName());
        pointJSON.put(AgentConstants.DESCRIPTION,getDescription());
        pointJSON.put(AgentConstants.DATA_TYPE,getDataTypeAsInt());
        pointJSON.put(AgentConstants.POINT_TYPE,getPointType());
        pointJSON.put(AgentConstants.DEVICE_NAME,getDeviceName());
        pointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        pointJSON.put(AgentConstants.ASSET_CATEGORY_ID,getAssetCategoryId());
        pointJSON.put(AgentConstants.RESOURCE_ID,getResourceId());
        pointJSON.put(AgentConstants.FIELD_ID,getFieldId());
        pointJSON.put(AgentConstants.IN_USE,isInUse());
        pointJSON.put(AgentConstants.WRITABLE,isWritable());
        pointJSON.put(AgentConstants.SUBSCRIBED,isSubscribed());
        pointJSON.put(AgentConstants.PSEUDO,isPseudo());
        pointJSON.put(AgentConstants.THRESHOLD_JSON,getThresholdJSON());
        pointJSON.put(AgentConstants.CREATED_TIME,getCreatedTime());
        pointJSON.put(AgentConstants.MAPPED_TIME,getMappedTime());
        pointJSON.put(AgentConstants.UNIT,getUnit());
        pointJSON.put(AgentConstants.CONFIGURE_STATUS,getConfigureStatus().getIndex());
        pointJSON.put(AgentConstants.SUBSCRIBE_STATUS,getSubscribestatus().getIndex());
        return pointJSON;
    }

    public boolean isPseudo() { return pseudo; }
    public void setPseudo(boolean pseudo) { this.pseudo = pseudo; }

    public long getAgentId() { return agentId; }

    public long getOrgId() { return orgId; }
    public void setOrgId(long orgId) { this.orgId = orgId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getPointType() { return pointType; }
    public void setPointType(int pointType) { this.pointType = pointType; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public Long getAssetCategoryId() { return assetCategoryId; }
    public void setAssetCategoryId(Long assetCategoryId) { this.assetCategoryId = assetCategoryId; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public long getMappedTime() { return mappedTime; }
    public void setMappedTime(long mappedTime) { this.mappedTime = mappedTime; }

    public PointEnum.ConfigureStatus getConfigureStatus() { return configureStatus; }
    public void setConfigureStatus(PointEnum.ConfigureStatus configureStatus) { this.configureStatus = configureStatus; }
    public void setConfigureStatus(int configureStatus) { this.configureStatus = PointEnum.ConfigureStatus.valueOf(configureStatus); }

    public PointEnum.SubscribeStatus getSubscribestatus() { return subscribestatus; }
    public void setSubscribestatus(PointEnum.SubscribeStatus subscribestatus) { this.subscribestatus = subscribestatus; }
    public void setSubscribestatus(int subscribestatus) { this.subscribestatus = PointEnum.SubscribeStatus.valueOf(subscribestatus); }

    /**
     * This method builds pointObject using map
     * used in case populating points from db or creating point from agent-message.
     * json returns long for all types numbers while map doesn't and so use JsonUtil for casting.
     * @param row can be {@link JSONObject} from agent or {@link Map<String,Object>} from db
     * @return
     */
    public Point getPointFromMap(Map<String,Object> row){
        if( (row == null) || (row.isEmpty()) ){
            return this;
        }
        if(containsValueCheck(AgentConstants.NAME,row)){
            setName((String) row.get(AgentConstants.NAME));
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
            setSubscribestatus(JsonUtil.getInt(row.get(AgentConstants.SUBSCRIBE_STATUS)));
        }
        if(containsValueCheck(AgentConstants.CONFIGURE_STATUS,row)){
            setConfigureStatus(JsonUtil.getInt(row.get(AgentConstants.CONFIGURE_STATUS)));
        }
        if(row.containsKey(AgentConstants.IDENTIFIER)){
            setDeviceName(String.valueOf(row.get(AgentConstants.IDENTIFIER)));
        }
        return this;
    }


    /**
     * overridden methods in child classes will be called.
     * @return
     */
    public abstract JSONObject getChildJSON();

    /**
     * This method gets a point as {@link JSONObject} with values for child point table's columns.
     * @return {@link JSONObject}
     */
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
