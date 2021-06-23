package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.MultiModuleReadingData;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class UpdateCheckPointAndAddControllerActivityCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(UpdateCheckPointAndAddControllerActivityCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioRecord fRecord = (FacilioRecord) context.get(FacilioConstants.ContextNames.FACILIO_RECORD);
		if ( fRecord != null) {
			String partitionKey = updateCheckPointAndGetPartitionKey(fRecord);
			LOGGER.info(" partition key chich must match mac is -> "+partitionKey);
			if( partitionKey == null){
				throw new Exception(" Partition key can't be null ");
			}
			ControllerContext controller = ControllerAPI.getController(partitionKey);
			if (controller != null) {
				
				if (!controller.isActive()) {
					StringBuilder msg = new StringBuilder()
											.append("The Controller with Mac Addr - ")
											.append(controller.getMacAddr())
											.append(" has been made active because data was received at time : ")
											.append(DateTimeUtil.getFormattedTime( fRecord.getTimeStamp()))
											;
					
					CommonCommandUtil.emailAlert("Making Controller as active - "+controller.getMacAddr(), msg.toString());
					ControllerAPI.makeControllerActive(Collections.singletonList(controller.getId()));
				}
				
				long recordTime = (long) context.get(FacilioConstants.ContextNames.TIMESTAMP);
				context.put(FacilioConstants.ContextNames.CONTROLLER, controller);
				context.put(FacilioConstants.ContextNames.CONTROLLER_TIME, recordTime);
				
				addControllerActivity((FacilioContext) context, controller, recordTime);
			}
			else {
				LOGGER.info("No controller with client id - "+partitionKey+"\nKindly add proper controller for this");
				// CommonCommandUtil.emailAlert("No controller with client id - "+partitionKey, "No controller with client id - "+partitionKey+"\nKindly add proper controller for this");
			}
		}
		else {
			ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
			if (controller != null) {
				long controllerTime = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
				addControllerActivity((FacilioContext) context, controller, controllerTime);
			}
			else {
				Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
				if (controllerId != null) {
					controller = ControllerAPI.getController(controllerId);
					if (controller != null) {
						//TODO Have to figure what to do with time
					}
				}
			}
		}
		return false;
	}
	
	private String updateCheckPointAndGetPartitionKey( FacilioRecord fRecord) throws Exception {
		/*if (record != null) {
			LOGGER.debug("Updating kinesis check point for controller : "+record.getPartitionKey()+" at "+record.getApproximateArrivalTimestamp().getTime());
			*//*
			IRecordProcessorCheckpointer checkPointer = (IRecordProcessorCheckpointer) context.get(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER);
			checkPointer.checkpoint(record);
			*//*
			return record.getPartitionKey();
		}*/
		if( fRecord != null) {
			/*LOGGER.debug("Updating Facilio consumer check point for controller : "+fRecord.getPartitionKey()+" at "+fRecord.getTimeStamp());*/
			/*FacilioConsumer consumer = (FacilioConsumer) context.get(FacilioConstants.ContextNames.FACILIO_CONSUMER);
			consumer.commit(fRecord);*/
			return fRecord.getPartitionKey();
		}
		throw new Exception(" facilioRecord can't be null");
	}
	
	private void addControllerActivity(FacilioContext context, ControllerContext controller, long recordTime) throws Exception {
		MultiModuleReadingData readingData = new MultiModuleReadingData();
		readingData.setReadingMap(CommonCommandUtil.getReadingMap(context));
		readingData.setReadingDataMeta((Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META));
		
		ControllerAPI.addControllerActivity(controller, recordTime, readingData);
	}

}
