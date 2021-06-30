package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.ScheduledRuleJobsMetaUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;

public class ExecuteScheduleRecordUpdateCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteScheduleRecordUpdateCommand.class.getName());
	List<ScheduledRuleJobsMetaContext> scheduledRuleJobsMetaContextList = new ArrayList<ScheduledRuleJobsMetaContext>();	

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {	
			//fetch matched schedule rules from field
			//check for last executed time
			//trigger one time record job if time matches with job context added, if record time < last executed time
			//delete job and update to inactive
			
			if(!ScheduledRuleJobsMetaUtil.checkNewOrOldScheduleRuleExecution()) {
				return false;
			}
			
			Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
				
			if(recordMap != null && !recordMap.isEmpty()) 
			{		
				for(Map.Entry<String, List> entry : recordMap.entrySet()) 
				{				
					String moduleName = entry.getKey();
					List records = new LinkedList<>(entry.getValue());
					if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
						LOGGER.log(Level.WARNING, "Module Name / Records is null/ empty while upating records in rollUpField ==> "+moduleName+"==>"+entry.getValue());
						continue;
					}
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(moduleName);
					List<WorkflowRuleContext> scheduleRules = ScheduledRuleJobsMetaUtil.fetchMatchedScheduledRulesFromModule(module.getModuleId());
					
					if(scheduleRules != null && !scheduleRules.isEmpty()) 
					{	
						for(WorkflowRuleContext scheduleRule:scheduleRules) 
						{
							FacilioField dateField = scheduleRule.getDateField();
							long lastExecutedTime = scheduleRule.getLastScheduleRuleExecutedTime();
							long ruleId = scheduleRule.getId();

							if(dateField != null && lastExecutedTime != -1) 
							{
								for(Object record:records) 
								{
									Map<String, Object> recordData = FieldUtil.getAsProperties(record);
									if(recordData != null && recordData.get("id") != null && recordData.get(dateField.getName()) != null) {
										long recordId = (long)recordData.get("id");
										long recordTime = (long)recordData.get(dateField.getName());
										long executionTimeInSeconds = recordTime/1000; //seconds in jobtable
										
										if(recordTime <= lastExecutedTime) {
											ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext = new ScheduledRuleJobsMetaContext();
											scheduledRuleJobsMetaContext.setIsActive(true);
											scheduledRuleJobsMetaContext.setModuleId(module.getModuleId());
											scheduledRuleJobsMetaContext.setRecordId(recordId);
											scheduledRuleJobsMetaContext.setRuleId(ruleId);
											
											List<ScheduledRuleJobsMetaContext> oldScheduledRuleJobsMetaContexts = ScheduledRuleJobsMetaUtil.fetchAlreadyPresentScheduledRuleJobsMeta(ruleId, module.getModuleId(), recordId);
											if(oldScheduledRuleJobsMetaContexts != null && !oldScheduledRuleJobsMetaContexts.isEmpty()) {
												for(ScheduledRuleJobsMetaContext oldScheduledRuleJobsMetaContext:oldScheduledRuleJobsMetaContexts) 
												{
													oldScheduledRuleJobsMetaContext.setIsActive(false);
													ScheduledRuleJobsMetaUtil.updateScheduledRuleJobsMeta(oldScheduledRuleJobsMetaContext);
													FacilioTimer.deleteJob(oldScheduledRuleJobsMetaContext.getId(), "ScheduleRuleExecuteJob");
												}
											}
											
											scheduledRuleJobsMetaContext.setExecutionTime(executionTimeInSeconds);
											ScheduledRuleJobsMetaUtil.addScheduledRuleJobsMeta(scheduledRuleJobsMetaContext);
											scheduledRuleJobsMetaContextList.add(scheduledRuleJobsMetaContext);
										}	
									}	
								}
							}
							
						}
					}

				}
			}
		
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in ExecuteScheduleRecordUpdateCommand, Exception: " + e.getMessage() , e);
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
