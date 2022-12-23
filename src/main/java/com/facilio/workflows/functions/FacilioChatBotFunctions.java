package com.facilio.workflows.functions;

import com.facilio.cb.context.ChatBotConfirmContext;
import com.facilio.cb.context.ChatBotExecuteContext;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CHAT_BOT_FUNCTION)
public class FacilioChatBotFunctions {
	public Object param(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		ChatBotParamContext params = new ChatBotParamContext();

		params.setParamName(objects[0].toString());

		if(objects[1] instanceof String) {
			params.setMessage(Collections.singletonList(objects[1].toString()));
		}
		else if (objects[1] instanceof List) {
			params.setMessage((List<String>)objects[1]);
		}
		if(objects.length > 2) {
			if(objects[2] instanceof List) {
				params.setOptions((List<JSONObject>) objects[2]);
			}
			else if (objects[2] instanceof Criteria) {
				params.setCriteria((Criteria)objects[2]);
				params.setModuleName((String)objects[3]);
				if(objects.length > 4) {
					params.setOrderByString((String)objects[4]);
				}
			}
			else if (objects[2] instanceof Map) {
				params.setDateSlot((JSONObject) objects[2]);
				if(objects.length > 3) {
					params.setPreviousValue(objects[3]);
				}
			}
			else {
				params.setPreviousValue(objects[2]);
			}
		}
		return params;
	}

	public Object confirm(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		ChatBotConfirmContext params = new ChatBotConfirmContext();

		params.setParamMap((Map<String, Object>) objects[0]);
		if(objects.length > 1) {
			params.setInCardMessage((String)objects[1]);

			if(objects.length > 2) {
				if(objects[2] instanceof String) {
					params.setMessage(Collections.singletonList((String)objects[2]));
				}
				else if (objects[2] instanceof List) {
					params.setMessage((List<String>)objects[2]);
				}
			}
		}

		return params;
	}

	public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

//			checkParam(objects);

		ChatBotExecuteContext params = new ChatBotExecuteContext();

		return params;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}