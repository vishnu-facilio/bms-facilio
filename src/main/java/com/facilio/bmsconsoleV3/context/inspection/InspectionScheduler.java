package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.ScheduleTypeInterface;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.InspectionGenerationHandler;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class InspectionScheduler implements ScheduleTypeInterface {

	private static final Logger LOGGER = Logger.getLogger(InspectionScheduler.class.getName());

	public static int INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS = 30;

	@Override
	public List<? extends ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext,boolean isUpdate, List<Map<String, Object>> parentRecordProps, boolean isManualOrScheduleTrigger) throws Exception {
		LOGGER.info("Reached Inspection Scheduler");
		JSONObject baseScheduleListObject = new JSONObject();
		baseScheduleListObject.put(com.facilio.qa.rules.Constants.Command.BASESCHEDULES, baseScheduleContext);
		Messenger.getMessenger().sendMessage(new Message()
				.setKey(InspectionGenerationHandler.KEY+"/"+baseScheduleContext.getId())
				.setContent(baseScheduleListObject));
		LOGGER.info("Pushed BaseSchedules to Session");
		return null;
	}

	@Override
	public ScheduleType getSchedulerType() {
		// TODO Auto-generated method stub
		return ScheduleType.INSPECTION;
	}

}
