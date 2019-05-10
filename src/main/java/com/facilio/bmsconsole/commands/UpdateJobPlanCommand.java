package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateJobPlanCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		JobPlanContext jobPlan = (JobPlanContext) context.get(ContextNames.JOB_PLAN);
		FacilioModule module = ModuleFactory.getJobPlanModule();
		
		if (jobPlan.getTasks() != null) {
			TemplateAPI.deleteJobPlanTaskTemplates(jobPlan);
		}
		
		Map<String, Object> prop = FieldUtil.getAsProperties(jobPlan);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getJobPlanFields())
				.andCondition(CriteriaAPI.getIdCondition(jobPlan.getId(), module));

		updateBuilder.update(prop);
		
		TemplateAPI.addTaskTemplate(jobPlan.getTasks(), Type.JOB_PLAN_TASK, Type.JOB_PLAN_SECTION, jobPlan.getId());
		
		return false;
	}

}
