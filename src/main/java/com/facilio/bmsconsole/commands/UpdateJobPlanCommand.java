package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateJobPlanCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
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
