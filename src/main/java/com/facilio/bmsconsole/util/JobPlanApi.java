package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class JobPlanApi {
	
	public static JobPlanContext getJobPlan(long id) throws Exception {
		List<JobPlanContext> jobPlans = getJobPlans(Collections.singletonList(id));
		if (CollectionUtils.isNotEmpty(jobPlans)) {
			JobPlanContext jobPlan = jobPlans.get(0);
			setExtendedJobPlanProps(jobPlan);
			return jobPlan;
		}
		return null;
	}
	
	public static List<JobPlanContext> getJobPlans(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getJobPlanModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getJobPlanFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		
		if (CollectionUtils.isNotEmpty(ids)) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}

		List<JobPlanContext> jobPlans = null;
		List<Map<String, Object>> props = builder.get();
		if(props != null && !props.isEmpty()) {
			jobPlans = FieldUtil.getAsBeanListFromMapList(props, JobPlanContext.class);
		}
		return jobPlans;
	}
	
	private static void setExtendedJobPlanProps(JobPlanContext jobPlan) throws Exception {
		 Map<String, List<TaskContext>> tasks = TemplateAPI.getTasksFromTemplate(jobPlan);
		 jobPlan.setTasks(tasks);
	}

}
