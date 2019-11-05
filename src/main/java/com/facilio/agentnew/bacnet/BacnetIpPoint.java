package com.facilio.agentnew.bacnet;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class BacnetIpPoint extends Point {

    private long instanceNumber=-1;
    private int instanceType=-1;
    @Deprecated
    private BacnetIpPoint() { }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.BACNET_IP;
    }

    public BacnetIpPoint(long agentId, long controllerId) {
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
    public static Point getPointFromMap(long agentId, long controllerId, Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.INSTANCE_NUMBER, pointMap) && containsValueCheck(AgentConstants.INSTANCE_TYPE, pointMap)) {
           BacnetIpPoint point = new BacnetIpPoint(agentId, controllerId);
           /* point.setInstanceNumber(JsonUtil.getLong(pointMap.get(AgentConstants.INSTANCE_NUMBER)));
            point.setInstanceType(JsonUtil.getInt(pointMap.get(AgentConstants.INSTANCE_TYPE)));
            return point.getPointFromMap(pointMap);*/
          JSONObject jsonObject = new JSONObject();
          jsonObject.putAll(pointMap);
          return FieldUtil.getAsBeanFromJson(jsonObject,BacnetIpPoint.class);
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.INSTANCE_NUMBER + "," + AgentConstants.INSTANCE_TYPE + " might be missing form input params -> " + pointMap);
    }


    /**
     * [{controllerId=12, thresholdJson={}, instanceType=8, dataType=0, subscribeStatus=1, instanceNumber=23, pointType=1, deviceName=1_#_23_#_0_#_192.168.1.140, orgId=1, writable=false, subscribed=false, unit=0, mappedTime=0, name=spinfo_23, inUse=false, createdTime=1571135229314, id=3, pseudo=false, configureStatus=1}]
     * @param controller
     * @param row
     * @return
     * @throws Exception
     */
    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
    }

        @Override
    public JSONObject getChildJSON() {
        JSONObject bacnetPointJSON = new JSONObject();
        bacnetPointJSON.put(AgentConstants.ID,getId());
        bacnetPointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        bacnetPointJSON.put(AgentConstants.INSTANCE_NUMBER,instanceNumber);
        bacnetPointJSON.put(AgentConstants.INSTANCE_TYPE,instanceType);
        return bacnetPointJSON;
    }

}

