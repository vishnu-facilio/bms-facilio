package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.job.JobContext;

public class JobsAPI {

	public static void addJobs(List<JobContext> jobs) throws Exception {
		List<Map<String, Object>> jobProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(JobContext job:jobs) {
			job.setOrgId(orgId);
			jobProps.add(FieldUtil.getAsProperties(job));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table("Jobs")
														.fields(FieldFactory.getJobFields())
														.addRecords(jobProps);
		insertBuilder.save();
		
	}
}
