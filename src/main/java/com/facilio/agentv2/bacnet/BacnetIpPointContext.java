package com.facilio.agentv2.bacnet;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class BacnetIpPointContext extends Point  implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(BacnetIpPointContext.class.getName());


    private long instanceNumber = -1;
    private int instanceType = -1;
    private String actualUnit;

    public String getActualUnit() {
        return actualUnit;
    }

    public void setActualUnit(String actualUnit) {
        this.actualUnit = actualUnit;
    }

    @Deprecated
    public BacnetIpPointContext() {
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.BACNET_IP;
    }

    public BacnetIpPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }


    public long getInstanceNumber() {
        return instanceNumber;
    }
    public void setInstanceNumber(long instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public int getInstanceType() {
        return instanceType;
    }
    public void setInstanceType(int instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * used to generate point for processPoints.
     * @param pointMap
     * @return
     * @throws Exception
     */
    public static Point getPointFromMap( Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.INSTANCE_NUMBER, pointMap) && containsValueCheck(AgentConstants.INSTANCE_TYPE, pointMap)) {
          JSONObject jsonObject = new JSONObject();
          if(pointMap.containsKey(AgentConstants.POINT_UNIT)){
              pointMap.put(AgentConstants.ACTUAL_UNIT, pointMap.remove(AgentConstants.POINT_UNIT));
          }
          jsonObject.putAll(pointMap);
            BacnetIpPointContext point = FieldUtil.getAsBeanFromJson(jsonObject, BacnetIpPointContext.class);
          return point;
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.INSTANCE_NUMBER + "," + AgentConstants.INSTANCE_TYPE + " might be missing form input params -> " + pointMap);
    }



    @Override
    public JSONObject getChildJSON() {
        JSONObject bacnetPointJSON = new JSONObject();
        bacnetPointJSON.put(AgentConstants.ID,getId());
        bacnetPointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        bacnetPointJSON.put(AgentConstants.INSTANCE_NUMBER,instanceNumber);
        bacnetPointJSON.put(AgentConstants.INSTANCE_TYPE,instanceType);
        bacnetPointJSON.put(AgentConstants.ACTUAL_UNIT, actualUnit);

        return bacnetPointJSON;
    }

}

