package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.job.JobContext;

public class FetchRuleMatchingSpecificRecordCommand implements Command{

private static final Logger LOGGER = LogManager.getLogger(FetchRuleMatchingSpecificRecordCommand.class.getName());
	

@Override
public boolean execute(Context context) throws Exception {
	// TODO Auto-generated method stub
	WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
	List<? extends ModuleBaseWithCustomFields> records = getRecords(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));
	// LOGGER.info("Matching records of rule : "+rule.getId()+" is : "+records);
	
	context.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getEvent().getModule().getName());
	context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
	
	return false;
}


@SuppressWarnings({ "unchecked", "rawtypes" })
private List<? extends ModuleBaseWithCustomFields> getRecords(WorkflowRuleContext rule, JobContext jc) throws Exception {
	FacilioModule module = rule.getEvent().getModule();
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	
	Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(module.getName());
	if (beanClassName == null) {
		beanClassName = ModuleBaseWithCustomFields.class;
	}
	SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																			.select(modBean.getAllFields(module.getName()))
																			.module(module)
																			.beanClass(beanClassName)
																			;

	if (rule.getCriteria() != null) {
		selectBuilder.andCriteria(rule.getCriteria());
	}
    
	if(rule.getParentId() > 0) {
		selectBuilder.andCondition(CriteriaAPI.getIdCondition(rule.getParentId(), module));
	}
	
	List<ModuleBaseWithCustomFields> records = selectBuilder.get();
	// LOGGER.info(selectBuilder.toString());
	return records;
}
}
