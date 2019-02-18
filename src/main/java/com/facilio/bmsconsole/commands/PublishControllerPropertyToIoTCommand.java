package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerContext.ControllerType;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.constants.FacilioConstants;

public class PublishControllerPropertyToIoTCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(PublishControllerPropertyToIoTCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		ControllerContext oldController = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
		if (controller.getDataInterval() != -1 && controller.getDataInterval() != oldController.getDataInterval() && controller.getControllerTypeEnum() == ControllerType.BACNET) {
			JSONObject prop = new JSONObject();
			prop.put("key", "interval");
			prop.put("value", controller.getDataInterval() * 60 * 1000);
			IoTMessageAPI.publishIotMessage(controller.getId(), prop, null, data -> rollBackIntervalTime(controller.getId(), oldController.getDataInterval()));
		}

		return false;
	}

	public static void rollBackIntervalTime (long id, long interval) { //static because this is used in lambda
		try {
			ControllerAPI.updateController(Collections.singletonMap("dataInterval", interval), Collections.singletonList(id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred while reverting interval time ", e);
			CommonCommandUtil.emailException("PublishControllerPropertyToIoTCommand", "Error occurred while reverting interval time", e);
		}
	}

}
