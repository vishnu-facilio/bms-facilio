package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.util.ScheduledRuleJobsMetaUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;

public class CreateScheduleRuleJobsCommand extends FacilioCommand implements PostTransactionCommand{

	private static final Logger LOGGER = LogManager.getLogger(CreateScheduleRuleJobsCommand.class.getName());
	List<ScheduledRuleJobsMetaContext> scheduledRuleJobsMetaContextList = new ArrayList<ScheduledRuleJobsMetaContext>();

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		LOGGER.info("Record Map : "+recordMap);
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
		FacilioField dateField = rule.getDateField();

		if (rule != null && recordMap != null && !recordMap.isEmpty()) {
			for (Map.Entry<String, List> entry : recordMap.entrySet()) {
				String moduleName = entry.getKey();
				if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
					LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
					continue;
				}
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				
				for (Object record : entry.getValue()) {
					Map<String, Object> recordData = FieldUtil.getAsProperties(record);
					if(recordData != null && recordData.get("id") != null && recordData.get(dateField.getName()) != null) {
						long recordId = (long)recordData.get("id");
						long executionTime = (long)recordData.get(dateField.getName());
						executionTime = executionTime/1000; //in seconds
						
						ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext = new ScheduledRuleJobsMetaContext();
						scheduledRuleJobsMetaContext.setIsActive(true);
						scheduledRuleJobsMetaContext.setModuleId(module.getModuleId());
						scheduledRuleJobsMetaContext.setRecordId(recordId);
						scheduledRuleJobsMetaContext.setRuleId(rule.getId());
						scheduledRuleJobsMetaContext.setExecutionTime(executionTime);
						scheduledRuleJobsMetaContextList.add(scheduledRuleJobsMetaContext);
					}
					
				}
			}	
			
			if(scheduledRuleJobsMetaContextList != null && !scheduledRuleJobsMetaContextList.isEmpty()) {
				for(ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext:scheduledRuleJobsMetaContextList)
				{
					ScheduledRuleJobsMetaUtil.addScheduledRuleJobsMeta(scheduledRuleJobsMetaContext);										
				}
				
			}
			
			DateRange dateRange = (DateRange)context.get(FacilioConstants.ContextNames.DATE_RANGE);
			if(dateRange != null && dateRange.getEndTime() != -1) {
				long lastExecutedTime = dateRange.getEndTime();
				WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
				workflowRuleContext.setId(rule.getId());
				workflowRuleContext.setLastScheduleRuleExecutedTime(lastExecutedTime);
				WorkflowRuleAPI.updateWorkflowRule(workflowRuleContext);
			}			
		}
			
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		
		if(scheduledRuleJobsMetaContextList != null && !scheduledRuleJobsMetaContextList.isEmpty()) {
			for(ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext:scheduledRuleJobsMetaContextList) {
				FacilioTimer.scheduleOneTimeJobWithTimestampInSec(scheduledRuleJobsMetaContext.getId(), "ScheduleRuleExecuteJob", scheduledRuleJobsMetaContext.getExecutionTime(), "priority");
			}
		}
		
		return false;
	}

}
