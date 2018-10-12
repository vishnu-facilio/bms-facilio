package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateCheckPointAndAddControllerActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Record record = (Record) context.get(FacilioConstants.ContextNames.KINESIS_RECORD);
		if (record != null) {
			IRecordProcessorCheckpointer checkPointer = (IRecordProcessorCheckpointer) context.get(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER);
			checkPointer.checkpoint(record);
			ControllerContext controller = ControllerAPI.getController(record.getPartitionKey());
			if (controller != null) {
				context.put(FacilioConstants.ContextNames.CONTROLLER, controller);
				context.put(FacilioConstants.ContextNames.CONTROLLER_TIME, record.getApproximateArrivalTimestamp().getTime());
				ControllerAPI.addControllerActivity(controller, record.getApproximateArrivalTimestamp().getTime(), CommonCommandUtil.getReadingMap((FacilioContext) context));
			}
			else {
				CommonCommandUtil.emailException(this.getClass().getName(), "No controller with client id - "+record.getPartitionKey(), "Kindly add proper controller for this");
			}
		}
		else {
			String controllerId = (String) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
			if (controllerId != null && !controllerId.isEmpty()) {
				ControllerContext controller = ControllerAPI.getController(controllerId);
				if (controller != null) {
					//TODO Have to figure what to do with time
				}
			}
		}
		return false;
	}

}
