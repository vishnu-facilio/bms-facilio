package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.JobContext;

public class FetchScheduledRuleMatchingRecordsCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(FetchScheduledRuleMatchingRecordsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ScheduledRuleContext rule = (ScheduledRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		List<ModuleBaseWithCustomFields> records = getRecords(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));
		LOGGER.info("Matching records of rule : "+rule.getId()+" is : "+records);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getEvent().getModule().getName());
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		
		return false;
	}

	private DateRange getRange(ScheduledRuleContext rule, JobContext jc) {
		long startTime = -1, endTime = -1;
		if (rule.getTime() == null) { //DATE_TIME field
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
	
	private List<ModuleBaseWithCustomFields> getRecords(ScheduledRuleContext rule, JobContext jc) throws Exception {
		FacilioModule module = rule.getEvent().getModule();
		FacilioField dateField = rule.getDateField();
		DateRange range = getRange(rule, jc);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(modBean.getAllFields(module.getName()))
																				.module(module)
																				.andCondition(CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN))
																				.beanClass(ModuleBaseWithCustomFields.class)
																				;
		return selectBuilder.get();
	}
}
