package com.facilio.agentv2.E2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;

@Getter @Setter
public class E2PointContext extends Point implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(E2PointContext.class.getName());

    private String groupName;
    private String propName;
    private long parentId;
    private long parentType;
    private long appId;
    private long appIndex;
    private int propStatus;
    private int propNumber;
    private int propMode;
    private int propType;
    private int propDataType;

    @Deprecated
    public E2PointContext() {
    }


    public static Point getPointFromMap( Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.GROUP_NAME, pointMap) && containsValueCheck(AgentConstants.PROP_NAME, pointMap)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(pointMap);
            E2PointContext point = FieldUtil.getAsBeanFromJson(jsonObject, E2PointContext.class);
            return point;
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.GROUP_NAME + "," + AgentConstants.PROP_NAME + " might be missing form input params -> " + pointMap);
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.E2;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject e2PointJSON = new JSONObject();
        e2PointJSON.put(AgentConstants.ID,getId());
        e2PointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        e2PointJSON.put(AgentConstants.GROUP_NAME, groupName);
        e2PointJSON.put(AgentConstants.PROP_NAME, propName);
        e2PointJSON.put(AgentConstants.PARENT_ID, parentId);
        e2PointJSON.put(AgentConstants.PARENT_TYPE, parentType);
        e2PointJSON.put(AgentConstants.APP_ID, appId);
        e2PointJSON.put(AgentConstants.APP_INDEX, appIndex);
        e2PointJSON.put(AgentConstants.PROP_STATUS, propStatus);
        e2PointJSON.put(AgentConstants.PROP_NUMBER, propNumber);
        e2PointJSON.put(AgentConstants.PROP_MODE, propMode);
        e2PointJSON.put(AgentConstants.PROP_TYPE, propType);
        e2PointJSON.put(AgentConstants.PROP_DATA_TYPE, propDataType);

        return e2PointJSON;
    }

    @Override
    public String getIdentifier() {
        return null;
    }
}
