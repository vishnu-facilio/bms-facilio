package com.facilio.agent.protocol;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.modules.FieldType;

public class ProtocolUtil {
	
	public static void setPointData(ControllerType controllerType, IotCommandType command, Map<String, Object> instance, Map<String, Object> point, ModuleBean modBean) throws Exception {
		switch(controllerType) {
			case BACNET_IP:
			case BACNET_MSTP:
				setBacnetPointData(command, instance, point, modBean);
				break;
			case NIAGARA:
				setNiagaraPoint(command, instance, point);
				break;
		}
	}

	public static void setBacnetPointData(IotCommandType command, Map<String, Object> instance, Map<String, Object> point, ModuleBean modBean) throws Exception {
		switch(command) {
			case CONFIGURE:
				point.put("instance", instance.get("instance"));
				point.put("device", instance.get("device"));
				point.put("instanceDescription", instance.get("instanceDescription"));
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

	public static void setNiagaraPoint(IotCommandType command, Map<String, Object> instance, Map<String, Object> point) throws Exception {
		switch(command) {
			case CONFIGURE:
				point.put("instance", instance.get("instance"));
				point.put("device", instance.get("device"));
				point.put("pointPath", instance.get("pointPath"));
				break;
		}
	}

}
