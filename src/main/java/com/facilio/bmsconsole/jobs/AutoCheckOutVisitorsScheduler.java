package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class AutoCheckOutVisitorsScheduler extends FacilioJob{

	@Override
	public void execute(JobContext jc) throws Exception {
		V3VisitorManagementAPI.autoCheckOutVisitors();
	}
		
}
