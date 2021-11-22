package com.facilio.bundle.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.jobs.ScheduledActionExecutionJob;
import com.facilio.bmsconsole.jobs.ScheduledWorkflowJob;
import com.facilio.bmsconsole.templates.WorkflowTemplate;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class ScheduledActionBundleComponent extends CommonBundleComponent {
	
	public static final String START_TIME = "startTime";
	public static final String SCHEDULE = "schedule";
	public static final String TIMES = "times";
	public static final String FREQUENCY = "facilioFrequency";
	public static final String ACTIONS = "actions";
	public static final String ACTION = "action";
	public static final String ACTION_TYPE = "actionType";
	public static final String ACTION_SCRIPT = "actionScript";
	

	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long scheduledActionRuleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		ScheduledWorkflowContext scheduledAction = ScheduledWorkflowJob.getScheduledWorkflowContext(scheduledActionRuleId, null);		
		return scheduledAction.getLinkName();
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long scheduledActionRuleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		ScheduledWorkflowContext scheduledAction = ScheduledWorkflowJob.getScheduledWorkflowContext(scheduledActionRuleId, null);
		
		xmlBuilder
				  .element(BundleConstants.Components.NAME).text(scheduledAction.getLinkName()).p()
				  .element(BundleConstants.Components.DISPLAY_NAME).text(scheduledAction.getName()).p()
				  .element(START_TIME).text(scheduledAction.getStartTime()+"").p()
				  .element(SCHEDULE)
				  	.element(FREQUENCY).text(scheduledAction.getSchedule().getFrequencyType()+"").p()
				  	.element(TIMES).text(StringUtils.join(scheduledAction.getSchedule().getTimes(), ",")).p()
				  .p()
				  ;	
		
		if(scheduledAction.getActions() !=  null) {
			
			XMLBuilder actionElement = xmlBuilder.element(ACTIONS);
			for(ActionContext action : scheduledAction.getActions()) {
				
				XMLBuilder actionXml = actionElement.element(ACTION);
				
				actionXml.element(ACTION_TYPE).text(action.getActionType()+"");
				
				if(action.getActionTypeEnum() == ActionType.WORKFLOW_ACTION) {
					WorkflowTemplate workflowTemplate = (WorkflowTemplate) action.getTemplate();
					WorkflowContext workflow = WorkflowUtil.getWorkflowContext(workflowTemplate.getResultWorkflowId());
					actionXml.element(ACTION_SCRIPT).text(workflow.getWorkflowV2String());
				}
			}
		}
	}

	@Override
	public BundleModeEnum getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(ScheduledWorkflowJob.getScheduledWorkflowContext(name) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		return installMode;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		switch(modeEnum) {
			case ADD: 
			case UPDATE: {
				
				ScheduledWorkflowContext scheduledWorkflow = new ScheduledWorkflowContext();
				
				scheduledWorkflow.setSourceBundle(installedBundle.getId());
				
				String linkName = xmlContent.getElement(BundleConstants.Components.NAME).getText();
				
				ScheduledWorkflowContext scheduledWorkflowTemp = ScheduledWorkflowJob.getScheduledWorkflowContext(linkName);
				
				if(scheduledWorkflowTemp != null) {
					scheduledWorkflow.setId(scheduledWorkflowTemp.getId());
				}
				
				scheduledWorkflow.setName(xmlContent.getElement(BundleConstants.Components.DISPLAY_NAME).getText());
				scheduledWorkflow.setStartTime(Long.parseLong(xmlContent.getElement(START_TIME).getText()));
				
				ScheduleInfo schedule = new ScheduleInfo();
				
				schedule.setFrequency(Integer.parseInt(xmlContent.getElement(SCHEDULE).getElement(FREQUENCY).getText()));
				schedule.setFrequencyType(Integer.parseInt(xmlContent.getElement(SCHEDULE).getElement(FREQUENCY).getText()));
				
				String times = xmlContent.getElement(SCHEDULE).getElement(TIMES).getText();
				String[] timesArray = times.split(",");
				
				schedule.setTimes(Arrays.asList(timesArray));
				
				scheduledWorkflow.setSchedule(schedule);
				
				List<ActionContext> actions = new ArrayList<ActionContext>();
				
				List<XMLBuilder> actionsXml = xmlContent.getElementList(ACTIONS);
				
				System.out.println(actionsXml.toString());
				
				for(XMLBuilder actionXml : actionsXml) {
					
					ActionContext action = new ActionContext();
					action.setActionType(Integer.parseInt(actionXml.getElement(ACTION_TYPE).text()));
					
					if(action.getActionTypeEnum() == ActionType.WORKFLOW_ACTION) {
						
						JSONObject templateJSON = new JSONObject();
						JSONObject workflowJSON = new JSONObject();
						workflowJSON.put("isV2Script", true);
						workflowJSON.put("workflowV2String", actionXml.getElement(ACTION_SCRIPT).text());
						templateJSON.put("resultWorkflowContext", workflowJSON);
						action.setTemplateJson(templateJSON);
						
						actions.add(action);
					}
				}
				
				scheduledWorkflow.setActions(actions);
				
				FacilioChain addWorkflowChain =  TransactionChainFactory.getAddOrUpdateScheduledWorkflowChain();
				
				FacilioContext newContext = addWorkflowChain.getContext();
				
				newContext.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, scheduledWorkflow);
				
				addWorkflowChain.execute();
			}
			break;
			case DELETE: {
				
				Long scheduledActionId = (Long)context.get(BundleConstants.COMPONENT_ID);
				
				FacilioChain addWorkflowChain =  TransactionChainFactory.getDeleteScheduledWorkflowChain();
				FacilioContext newContext = addWorkflowChain.getContext();
				
				newContext.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, ScheduledWorkflowJob.getScheduledWorkflowContext(scheduledActionId, null));
				addWorkflowChain.execute();
			}
			break;
		}
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
