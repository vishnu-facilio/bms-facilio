package com.facilio.bmsconsole.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ShiftEndOneTimeJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(ShiftEndOneTimeJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {
			JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			Long shiftId = (Long) props.get("shiftId");
			if (shiftId == null) {
				return;
			}
			List<Long> userIds = ShiftAPI.getOuidFromShift(shiftId);
			if(userIds == null || userIds.isEmpty()) {
				return;
			}
			
			long execTime = jc.getExecutionTime()*1000;
			List<ReadingContext> userShiftReadings = ShiftAPI.getUserShiftReading(userIds, "Check-Out", execTime);
			
			Map<String, List<ReadingContext>> readingMap = new HashMap<>();
			if(userShiftReadings != null && !userShiftReadings.isEmpty()) {
				readingMap.put("usershiftreading", userShiftReadings);
			}
			
			if(readingMap.isEmpty()) {
				return;
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.SHIFT_READING);
			Chain c = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			c.execute(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}