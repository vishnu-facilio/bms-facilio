package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class NotificationConfigAPI {

	public static void deleteUsersConfigured(long configId) throws Exception{
		FacilioModule usersConfiguredModule = ModuleFactory.getNotificationUserModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(usersConfiguredModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("CONFIGURATION_ID", "configId", String.valueOf(configId), NumberOperators.EQUALS));
		deleteBuilder.delete();
	}
	
	public static void deleteJobsConfigured(long jobId) throws Exception{
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table("Jobs")
				.andCondition(CriteriaAPI.getCondition("JOBID", "jobId", String.valueOf(jobId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("JOBNAME", "jobName", FacilioConstants.Job.NOTIFICATION_TRIGGER_JOB_NAME, StringOperators.IS))
				;
		deleteBuilder.delete();
	}
	public static List<Map<String, Object>> getUsersConfigured(long configId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table("Notification_Users")
				.select(FieldFactory.getNotificationUserFields())
				.andCondition(CriteriaAPI.getCondition("CONFIGURATION_ID", "configurationId", String.valueOf(configId), NumberOperators.EQUALS));

		List<Map<String, Object>> list = selectBuilder.get();
		return list;

	}
	public static Map<String, Object> getNotificationConfigDetails(long configId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table("Notification_Configuration")
				.select(FieldFactory.getNotificationConfigFields())
				.andCondition(CriteriaAPI.getIdCondition(configId, ModuleFactory.getNotificationConfigModule()));

		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
}

