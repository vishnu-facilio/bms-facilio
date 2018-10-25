package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.MultiModuleReadingData;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateCheckPointAndAddControllerActivityCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(UpdateCheckPointAndAddControllerActivityCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Record record = (Record) context.get(FacilioConstants.ContextNames.KINESIS_RECORD);
		if (record != null) {
			LOGGER.debug("Updating check point for controller : "+record.getPartitionKey()+" at "+record.getApproximateArrivalTimestamp().getTime());
			IRecordProcessorCheckpointer checkPointer = (IRecordProcessorCheckpointer) context.get(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER);
			checkPointer.checkpoint(record);
			ControllerContext controller = ControllerAPI.getController(record.getPartitionKey());
			if (controller != null) {
				
				if (!controller.isActive()) {
					StringBuilder msg = new StringBuilder()
											.append("The Controller with Mac Addr - ")
											.append(controller.getMacAddr())
											.append(" has been made active because data was received at time : ")
											.append(DateTimeUtil.getFormattedTime(record.getApproximateArrivalTimestamp().getTime()))
											;
					
					CommonCommandUtil.emailAlert("Making Controller as active - "+controller.getMacAddr(), msg.toString());
					ControllerAPI.makeControllerActive(Collections.singletonList(controller.getId()));
				}
				
				context.put(FacilioConstants.ContextNames.CONTROLLER, controller);
				context.put(FacilioConstants.ContextNames.CONTROLLER_TIME, record.getApproximateArrivalTimestamp().getTime());
				
				addControllerActivity((FacilioContext) context, controller, record.getApproximateArrivalTimestamp().getTime());
			}
			else {
				CommonCommandUtil.emailAlert("No controller with client id - "+record.getPartitionKey(), "No controller with client id - "+record.getPartitionKey()+"\nKindly add proper controller for this");
			}
		}
		else {
			ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
			if (controller != null) {
				long controllerTime = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
				addControllerActivity((FacilioContext) context, controller, controllerTime);
			}
			else {
				String controllerId = (String) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
				if (controllerId != null && !controllerId.isEmpty()) {
					controller = ControllerAPI.getActiveController(controllerId);
					if (controller != null) {
						//TODO Have to figure what to do with time
					}
				}
			}
		}
		return false;
	}
	
	private void addControllerActivity(FacilioContext context, ControllerContext controller, long recordTime) throws Exception {
		MultiModuleReadingData readingData = new MultiModuleReadingData();
		readingData.setReadingMap(CommonCommandUtil.getReadingMap(context));
		readingData.setReadingDataMeta((Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META));
		
		ControllerAPI.addControllerActivity(controller, recordTime, readingData);
	}

}
