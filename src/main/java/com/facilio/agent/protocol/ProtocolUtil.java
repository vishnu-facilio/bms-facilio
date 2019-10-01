package com.facilio.agent.protocol;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;

public class ProtocolUtil {
	
	public static JSONArray getPointsToPublish(ControllerContext controller, List<Map<String, Object>> instances, IotCommandType command) throws Exception {
		JSONArray points = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(Map<String, Object> instance : instances) {
			addPointData(controller.getControllerTypeEnum(), command, instance, points, modBean);
		}
		return points;
	}
	
	private static void addPointData(ControllerType controllerType, IotCommandType command, Map<String, Object> instance, JSONArray points, ModuleBean modBean) throws Exception {
		JSONObject point = new JSONObject();
		switch(controllerType) {
			case BACNET_IP:
			case BACNET_MSTP:
				point.put("instanceType", instance.get("instanceType"));
				point.put("objectInstanceNumber", instance.get("objectInstanceNumber"));
				point.put("instance", instance.get("instance"));
				break;
				
			case NIAGARA:
				point.put("pointPath", instance.get("pointPath"));
				break;
				
			default:
				point.put("instance", instance.get("instance"));
				break;
		}

		point.put("pointId", instance.get("id"));
		
		setCommandData(command, controllerType, instance, point, modBean);
		
		points.add(point);
	}
	
	private static void setCommandData(IotCommandType command, ControllerType controllerType, Map<String, Object> instance, Map<String, Object> point, ModuleBean modBean) throws Exception {
		switch(command) {
			case CONFIGURE:
				if (controllerType == ControllerType.BACNET_IP || controllerType == ControllerType.BACNET_MSTP) {
					point.put("device", instance.get("device"));
					point.put("instanceDescription", instance.get("instanceDescription"));
				}
				break;
			case SUBSCRIBE:
				if (instance.containsKey("thresholdJson")) {
					JSONParser parser = new JSONParser();
					JSONObject threshold = (JSONObject) parser.parse((String) instance.get("thresholdJson"));
					point.putAll(threshold);
				}
				break;
			case SET:
				if (instance.containsKey("value")) {
					point.put("newValue", instance.get("value"));
					point.put("valueType", getValueType(modBean.getField((long) instance.get("fieldId")).getDataTypeEnum()));
				}
				break;
		}
	}

	private static String getValueType(FieldType fieldType) {
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

}
