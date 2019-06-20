package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class NotificationConfigAPI {

	public static void deleteUsersConfigured(FacilioModule module, long configId) throws Exception{
		 DeleteRecordBuilder<NotificationUserContext> deleteBuilder = new DeleteRecordBuilder<NotificationUserContext>()
					.module(module)
					.andCondition(CriteriaAPI.getCondition("CONFIGURATION_ID", "configurationId", String.valueOf(configId), NumberOperators.EQUALS));
		 deleteBuilder.delete();
	}
	
	public static void deleteJobsConfigured(long jobId) throws Exception{
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table("Jobs")
				.andCondition(CriteriaAPI.getCondition("JOBID", "jobId", String.valueOf(jobId), NumberOperators.EQUALS));
		deleteBuilder.delete();
	}
}

