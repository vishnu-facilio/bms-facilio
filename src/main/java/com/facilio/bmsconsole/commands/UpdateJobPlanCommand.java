package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateJobPlanCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		/*JobPlanContext jobPlan = (JobPlanContext) context.get(ContextNames.JOB_PLAN);
		FacilioModule module = ModuleFactory.getJobPlanModule();
		
		if (jobPlan.getTasks() != null) {
			JobPlanContext oldPlan = JobPlanApi.getJobPlan(jobPlan.getId());
		}
		
		Map<String, Object> prop = FieldUtil.getAsProperties(jobPlan);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getJobPlanFields())
				.andCondition(CriteriaAPI.getIdCondition(jobPlan.getId(), module));

		int count = updateBuilder.update(prop);
		
		TemplateAPI.addTaskTemplate(jobPlan.getTasks(), Type.JOB_PLAN_TASK, Type.JOB_PLAN_SECTION, id);*/
		
		return false;
	}

}
