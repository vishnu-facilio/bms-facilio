package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class ScheduleNewVisitorLogCommand extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ScheduleNewVisitorLogCommand.class.getName());
	
	public static final long serialVersionUID = -1436898572846920375L;
	
	public ScheduleNewVisitorLogCommand() {
	}

	private boolean execute(Context context) throws Exception {
		VisitorLoggingContext visitorLog = (VisitorLoggingContext) context
				.get(FacilioConstants.ContextNames.VISITOR_LOGGING);
		Boolean isEdit = (Boolean)context.get(FacilioConstants.ContextNames.IS_EDIT);

		if (visitorLog.getTrigger() != null) {
			try {
				long currentTime = System.currentTimeMillis();
				long startTime = visitorLog.getExpectedCheckInTime() > 0 ? visitorLog.getExpectedCheckInTime() : currentTime;
				if(isEdit) {
					deleteUpcomingChildren(visitorLog.getId(),startTime);
				}
				scheduleVLogs(visitorLog, context, startTime/1000);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Exception scheduling " + visitorLog.getId());
				CommonCommandUtil.emailException("Exception scheduling ", "Parent log ID " + visitorLog.getId(), e);
				throw e;
			}
		}

		return false;
	}

	private void scheduleVLogs(VisitorLoggingContext parentLog, Context context, long startTime) throws Exception {
		LOGGER.log(Level.FINE, "Generating vLogs for visitorLogs: " + parentLog.getId());
		List<VisitorLoggingContext> children = new ArrayList<>();
		long endTime = 0;
		long generatedUpto = 0;
		if (parentLog.getTrigger() != null) {
			PMTriggerContext trigger = parentLog.getTrigger();
			
			// Calculating start time & skipping for first date because parent log will act as childlog for first date
			startTime = trigger.getSchedule().nextExecutionTime(startTime);
			parentLog.setExpectedCheckInTime(startTime * 1000);
			parentLog.setExpectedCheckOutTime((startTime * 1000) + parentLog.getExpectedVisitDuration());
			
			startTime = trigger.getSchedule().nextExecutionTime(startTime);
			
			// calculating end time
			endTime = trigger.getEndTime() > 0 ? trigger.getEndTime()/1000
					: VisitorManagementAPI.getEndTime(-1, Collections.singletonList(parentLog.getTrigger()));
				
			while (startTime <= endTime) {
				generatedUpto = startTime;
				VisitorLoggingContext childLog = parentLog.getChildLog(startTime);
				children.add(childLog);
				startTime = trigger.getSchedule().nextExecutionTime(startTime);
			}
			
			parentLog.setLogGeneratedUpto(generatedUpto * 1000);
			parentLog.setParentLogId(parentLog.getId());
			trigger.setLastGeneratedTime(generatedUpto * 1000);
			VisitorManagementAPI.updateGeneratedUptoInLogAndAddChildren(trigger, parentLog, children);
			
		}

	}

	
	@Override
	public void execute(JobContext jc) throws Exception {
		FacilioContext context = new FacilioContext();
		VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLoggingTriggers(jc.getJobId(), null, true);
		 JSONObject jobProps = BmsJobUtil.getJobProps(jc.getJobId(), "ScheduleNewVisitorLogs");
	     Boolean isEdit = (Boolean) jobProps.getOrDefault("isEdit", true);
	     context.put(FacilioConstants.ContextNames.IS_EDIT, isEdit);
	     context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, vLog);
	     execute(context);

	}
	
	private void deleteUpcomingChildren(long parentId, long currentTime) throws Exception {
		VisitorManagementAPI.deleteUpcomingChildLogs(parentId, currentTime);
	}
}
