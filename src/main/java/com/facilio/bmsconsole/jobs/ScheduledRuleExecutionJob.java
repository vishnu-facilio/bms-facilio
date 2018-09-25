package com.facilio.bmsconsole.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledRuleExecutionJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(ScheduledRuleExecutionJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			Map<String, Object> scheduledRuleJob = getScheduledJob(jc.getJobId());
			
			if (scheduledRuleJob != null) {
				long recordId = (long) scheduledRuleJob.get("recordId");
				long ruleId = (long) scheduledRuleJob.get("ruleId");
				
				ScheduledRuleContext rule = (ScheduledRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId, true);
				Object record = FieldUtil.getRecord(rule.getEvent().getModule(), recordId);
				
				if (record != null) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(rule.getEvent().getModule().getName(), rule.getEvent().getModule().getName(), FieldUtil.getAsProperties(record), placeHolders);
					
					Map<String, Object> rulePlaceHolders = rule.constructPlaceHolders(rule.getEvent().getModule().getName(), record, placeHolders, null);
					rule.actualWorkflowActionExecution(record, null, rulePlaceHolders);
				}
			}
		}
		catch (Exception e) {
			LOGGER.fatal("Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
			CommonCommandUtil.emailException("ScheduledRuleExecutionJob", "Error occurred during scheduled rule execution for job : "+jc.getJobId(), e);
		}
	}
	
	private Map<String, Object> getScheduledJob(long id) throws Exception {
		FacilioModule module = ModuleFactory.getScheduledRuleJobModule();
		List<FacilioField> fields = FieldFactory.getScheduledRuleJobFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
}
