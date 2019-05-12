package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.db.criteria.StringOperators;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class BmsJobUtil {
	public static void scheduleCalendarJobWithProps(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.scheduleCalendarJob(jobId, jobName, startTime, schedule, executorName);
	}
	
	public static void scheduleCalendarJobWithProps(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, int maxExecution, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.scheduleCalendarJob(jobId, jobName, startTime, schedule, executorName, maxExecution);
	}
	
	public static void scheduleCalendarJobWithProps(long jobId, String jobName, long startTime, ScheduleInfo schedule, String executorName, long endTime, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.scheduleCalendarJob(jobId, jobName, startTime, schedule, executorName, endTime);
	}
	
	public static void schedulePeriodicJobWithProps(long jobId, String jobName, long delay, int period, String executorName, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.schedulePeriodicJob(jobId, jobName, delay, period, executorName);
	}
	
	public static void schedulePeriodicJobWithProps(long jobId, String jobName, long delay, int period, String executorName, int maxExecution, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.schedulePeriodicJob(jobId, jobName, delay, period, executorName, maxExecution);
	}
	
	public static void schedulePeriodicJobWithProps(long jobId, String jobName, long delay, int period, String executorName, long endTime, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.schedulePeriodicJob(jobId, jobName, delay, period, executorName, endTime);
	}
	
	public static void scheduleOneTimeJobWithProps(long jobId, String jobName, int delayInSec, String executorName, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.scheduleOneTimeJob(jobId, jobName, delayInSec, executorName);
	}
	
	public static void scheduleOneTimeJobWithProps(long jobId, String jobName, long nextExecutionTime, String executorName, JSONObject props) throws Exception {
		addJobProps(jobId, jobName, props);
		FacilioTimer.scheduleOneTimeJob(jobId, jobName, nextExecutionTime, executorName);
	}
	
	public static void deleteJobWithProps(long jobId, String jobName) throws Exception {
		deleteJobProps(Collections.singletonList(jobId), jobName);
		FacilioTimer.deleteJob(jobId, jobName);
	}
	
	public static void deleteJobsWithProps(List<Long> jobIds, String jobName) throws Exception {
		deleteJobProps(jobIds, jobName);
		FacilioTimer.deleteJobs(jobIds, jobName);
	}
	
	private static void addJobProps(long jobId, String jobName, JSONObject props) throws Exception {
		if (props != null && !props.isEmpty()) {
			Map<String, Object> record = new HashMap<>();
			record.put("jobId", jobId);
			record.put("orgId", AccountUtil.getCurrentOrg().getId());
			record.put("jobName", jobName);
			record.put("props", props.toJSONString());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getCommonJobPropsModule().getTableName())
															.fields(FieldFactory.getCommonJobPropsFields());
			
			insertBuilder.insert(record);
		}
	}
	
	private static void deleteJobProps (Collection<Long> jobIds, String jobName) throws Exception {
		FacilioModule module = ModuleFactory.getCommonJobPropsModule();
		List<FacilioField> fields = FieldFactory.getCommonJobPropsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField jobIdField = fieldMap.get("jobId");
		FacilioField jobNameField = fieldMap.get("jobName");
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(jobIdField, StringUtils.join(jobIds, ","), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(jobNameField, jobName, StringOperators.IS))
														;
		
		deleteBuilder.delete();
	}
	
	public static JSONObject getJobProps (long jobId, String jobName) throws Exception {
		FacilioModule module = ModuleFactory.getCommonJobPropsModule();
		List<FacilioField> fields = FieldFactory.getCommonJobPropsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField jobIdField = fieldMap.get("jobId");
		FacilioField jobNameField = fieldMap.get("jobName");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(jobIdField, String.valueOf(jobId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(jobNameField, jobName, StringOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			String jsonStr = (String) props.get(0).get("props");
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(jsonStr);
		}
		return null;
	}
}
