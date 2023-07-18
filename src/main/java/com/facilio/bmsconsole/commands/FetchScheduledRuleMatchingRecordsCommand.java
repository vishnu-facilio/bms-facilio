package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class FetchScheduledRuleMatchingRecordsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(FetchScheduledRuleMatchingRecordsCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		List<? extends ModuleBaseWithCustomFields> records = getRecords(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));
		// LOGGER.info("Matching records of rule : "+rule.getId()+" is : "+records);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getModule().getName());
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, getRange(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT)));
		
		return false;
	}

	private DateRange getRange(WorkflowRuleContext rule, JobContext jc) {
		long startTime = -1, endTime = -1;
		if(rule.getTime() == null){ //DATE_TIME field
			startTime = jc.getExecutionTime() * 1000;
			endTime =  (jc.getNextExecutionTime() * 1000) - 1;
		}
		else { //DATE Field
			startTime = DateTimeUtil.getDayStartTime();
			endTime = DateTimeUtil.getDayStartTime(1) - 1;
		}

		switch (rule.getScheduleTypeEnum()) {
			case BEFORE:
				long interval = rule.getInterval() * 1000;
				return new DateRange(startTime + interval, endTime + interval);
			case ON:
				return new DateRange(startTime, endTime);
			case AFTER:
				interval = rule.getInterval() * 1000;
				return new DateRange(startTime - interval, endTime - interval);
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<? extends ModuleBaseWithCustomFields> getRecords(WorkflowRuleContext rule, JobContext jc) throws Exception {
		FacilioModule module = rule.getModule();
		FacilioField dateField = rule.getDateField();
		DateRange range = getRange(rule, jc);
		// LOGGER.info("Range for rule : "+rule.getId()+" is "+range.toString());
		// LOGGER.info("Date field id : "+rule.getId()+" is "+rule.getDateFieldId());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(modBean.getAllFields(module.getName()))
																				.module(module)
																				.andCondition(CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN))
																				.beanClass(beanClassName)
																				;

		if (rule.getCriteria() != null) {
			selectBuilder.andCriteria(rule.getCriteria());
		}

		addSkipModuleCriteriaForModules(rule, module, selectBuilder);
		
		List<ModuleBaseWithCustomFields> records = selectBuilder.get();
		rule.setRuleEndTime(range.getEndTime());
		// LOGGER.info(selectBuilder.toString());
		return records;
	}

	private void addSkipModuleCriteriaForModules(WorkflowRuleContext rule, FacilioModule module, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		if(module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)
				&& rule.getRuleTypeEnum() == WorkflowRuleContext.RuleType.PM_NOTIFICATION_RULE
				&& rule.getScheduleTypeEnum() == WorkflowRuleContext.ScheduledRuleType.BEFORE) {
			selectBuilder.skipModuleCriteria();
		}
	}
}
