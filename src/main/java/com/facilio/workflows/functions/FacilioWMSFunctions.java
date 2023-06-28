package com.facilio.workflows.functions;

import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.handler.WmsHandler;
import com.facilio.wmsv2.handler.WmsProcessor;
import com.facilio.wmsv2.message.TopicHandler;
import com.facilio.wmsv2.message.WebMessage;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.WMS_FUNCTION)
public class FacilioWMSFunctions {
	public Object sendMessageToUser(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		Map<String,Object> msgMap = (Map<String,Object>) objects[0];
		WebMessage msg = FieldUtil.getAsBeanFromMap(msgMap, WebMessage.class);

		msg.setTopic("__custom__/user/"+msg.getTopic());

		WmsHandler handler = WmsProcessor.getInstance().getHandler(msg.getTopic());

		if (handler.getDeliverTo() != TopicHandler.DELIVER_TO.USER) {
			throw new FunctionParamException("Topic handler will not send to user");
		}
		if ((msg.getTo() == null || msg.getTo() < 0)) {
			throw new FunctionParamException("To cannot be null here for delivery type USER");
		}

		Broadcaster.getBroadcaster().sendMessage(msg);

		return null;
	}

	public Object sendMessageToOrg(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		Map<String,Object> msgMap = (Map<String,Object>) objects[0];

		WebMessage msg = FieldUtil.getAsBeanFromMap(msgMap, WebMessage.class);

		msg.setTopic("__custom__/org/"+msg.getTopic());

		WmsHandler handler = WmsProcessor.getInstance().getHandler(msg.getTopic());
		if (handler.getDeliverTo() != TopicHandler.DELIVER_TO.ORG) {
			throw new FunctionParamException("Topic handler will not send to org");
		}
		// no need to check orgId.. we replace the orgId in sendMessage
//			if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.ORG && (msg.getOrgId() == null || msg.getOrgId() < 0)) {
//				throw new FunctionParamException("Orgid cannot be null for delivery type ORG");
//			}

		Broadcaster.getBroadcaster().sendMessage(msg);

		return null;
	}

	public Object sendMessage(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		Map<String,Object> msgMap = (Map<String,Object>) objects[0];

		WebMessage msg = FieldUtil.getAsBeanFromMap(msgMap, WebMessage.class);

		WmsHandler handler = WmsProcessor.getInstance().getHandler(msg.getTopic());

		if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.ORG && (msg.getOrgId() == null || msg.getOrgId() < 0)) {
			throw new FunctionParamException("Orgid cannot be null for delivery type ORG");
		}

		if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.USER && (msg.getTo() == null || msg.getTo() < 0)) {
			throw new FunctionParamException("To cannot be null here for delivery type USER");
		}

		Broadcaster.getBroadcaster().sendMessage(msg);

		return null;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
