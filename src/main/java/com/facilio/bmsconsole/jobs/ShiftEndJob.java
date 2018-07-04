package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ShiftEndJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		try {
			ShiftAPI.addUserShiftReading(jc.getJobId(), "Check-Out", jc.getExecutionTime()*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
