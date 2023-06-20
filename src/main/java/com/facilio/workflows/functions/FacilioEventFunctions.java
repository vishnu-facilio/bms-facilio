package com.facilio.workflows.functions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import org.apache.commons.chain.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.EVENT_FUNCTION)
public class FacilioEventFunctions {
	public Object add(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {


		String moduleName = (String) objects[0];

		boolean isHistorical = (boolean) objects[1];

		List<Map<String,Object>> events = new ArrayList<>();
		if(objects[2] instanceof Map) {
			events.add((Map<String,Object>) objects[2]);
		}
		else if (objects[2] instanceof List) {
			events = (List<Map<String,Object>>) objects[2];
		}
		else {
			return null;
		}

		FacilioContext context = new FacilioContext();

		switch(moduleName) {

			case FacilioConstants.ContextNames.READING_EVENT: {
				List<ReadingEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, ReadingEventContext.class);
				context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
			}
			break;

			case FacilioConstants.ContextNames.BMS_EVENT: {
				List<BMSEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, BMSEventContext.class);
				context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
			}
			break;
			default: {
				List<BaseEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, BaseEventContext.class);
				context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
			}
			break;
		}

		Chain chain = TransactionChainFactory.getV2AddEventChain(isHistorical);
		chain.execute(context);

		return true;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}