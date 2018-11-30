package com.facilio.aws.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IotMessageCommand implements Command {

    private JSONObject constructIotMessage (List<Map<String, Object>> instances, String command) throws Exception {

        ControllerContext controller = ControllerAPI.getController((long) instances.get(0).get("controllerId"));

        JSONObject object = new JSONObject();
        object.put("command", command);

        object.put("deviceName", controller.getName());
        object.put("macAddress", controller.getMacAddr());
        object.put("subnetPrefix", controller.getSubnetPrefix());
        object.put("networkNumber", controller.getNetworkNumber());
        object.put("instanceNumber", controller.getInstanceNumber());
        object.put("broadcastAddress", controller.getBroadcastIp());

        JSONArray points = new JSONArray();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(Map<String, Object> instance : instances) {
            JSONObject point = new JSONObject();
            point.put("instance", instance.get("instance"));
            point.put("instanceType", instance.get("instanceType"));
            point.put("device", instance.get("device"));
            point.put("objectInstanceNumber", instance.get("objectInstanceNumber"));
            point.put("instanceDescription", instance.get("instanceDescription"));
            if (instance.containsKey("value")) {
                point.put("newValue", instance.get("value"));
                point.put("valueType", getValueType(modBean.getField((long) instance.get("fieldId")).getDataTypeEnum()));
            }
            points.add(point);
        }
        object.put("points", points);

        String objString = object.toJSONString();
        List<JSONObject> objectList = new ArrayList<>();

        if (objString.length() > 2000000) {
            JSONArray array = (JSONArray) object.get("points");
            int pointsSize = 0;
            JSONArray pointsArray = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject point = (JSONObject) array.get(i);
                pointsSize = pointsSize + point.toJSONString().length();
                if (pointsSize < 2000000) {
                    pointsArray.add(point);
                } else {
                    object.put("points", pointsArray);
                    // object.put("msgid", getIotMessageId());
                    objectList.add(getMessageObject(object));
                    pointsArray.clear();
                    pointsSize = point.toJSONString().length();
                    pointsArray.add(point);
                }
            }

            if (pointsArray.size() > 0) {
                object.put("points", pointsArray);
                // object.put("msgid", getIotMessageId());
                objectList.add(getMessageObject(object));
            }
        } else {
            // object.put("msgid", getIotMessageId());
            objectList.add(getMessageObject(object));
        }

        return object;
    }

    private JSONObject getMessageObject(JSONObject object) {
        JSONObject message = new JSONObject();
        message.putAll(object);
        return message;
    }

    private String getValueType(FieldType fieldType) {
        String type = null;
        switch(fieldType) {
            case NUMBER:
                type = "signed";
                break;
            case DECIMAL:
                type = "double";
                break;
            case BOOLEAN:
                type = "boolean";
                break;
            case STRING:
                type = "string";
        }
        return type;
    }

    @Override
    public boolean execute(Context context) throws Exception {

        if ((AccountUtil.getCurrentOrg() != null)) {
            List<Map<String, Object>> instances = (List<Map<String, Object>>) context.get("instances");
            String command = (String) context.get("command");
            JSONObject object = constructIotMessage(instances, command);
            AwsUtil.publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), object);
        }
        else {
            throw new IllegalArgumentException("User Object cannot be null");
        }
        return false;
    }
}
