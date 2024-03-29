package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddJobPlanCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		JobPlanContext jobPlan = (JobPlanContext) context.get(ContextNames.JOB_PLAN);
		FacilioModule module = ModuleFactory.getJobPlanModule();
		
		Map<String, Object> prop = FieldUtil.getAsProperties(jobPlan);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getJobPlanFields())
				.addRecord(prop);

		insertBuilder.save();
		long id = (long) prop.get("id");
		jobPlan.setId(id);
		
		TemplateAPI.addTaskTemplate(jobPlan.getTasks(), Type.JOB_PLAN_TASK, Type.JOB_PLAN_SECTION, id);
		return false;
	}

}
