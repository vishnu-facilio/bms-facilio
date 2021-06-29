package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;

public class FetchSingleMatchingRecordScheduledRuleCommand  extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(FetchSingleMatchingRecordScheduledRuleCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext = (ScheduledRuleJobsMetaContext) context.get(FacilioConstants.ContextNames.SCHEDULE_RULE_META);
		 
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(scheduledRuleJobsMetaContext.getRuleId());	
		if (rule == null || !rule.isActive() || !scheduledRuleJobsMetaContext.isActive()) {
			return false;
		}	
		
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getModule().getName());
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getMatchingRecord(scheduledRuleJobsMetaContext.getModuleId(),scheduledRuleJobsMetaContext.getRecordId()));			
			
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<? extends ModuleBaseWithCustomFields> getMatchingRecord(long moduleId, long recordId) throws Exception {		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(modBean.getAllFields(module.getName()))
																				.module(module)
																				.andCondition(CriteriaAPI.getIdCondition(recordId, module))
																				.beanClass(beanClassName)
																				;
		
		List<ModuleBaseWithCustomFields> records = selectBuilder.get();
		return records;
	}

}
