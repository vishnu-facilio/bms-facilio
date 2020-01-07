<%@page import="com.facilio.auth.actions.FacilioAuthAction"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.EventType"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType"%>
<%@page import="com.facilio.bmsconsole.util.WorkflowRuleAPI"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext"%>
<%@page import="com.facilio.modules.FacilioStatus.StatusType"%>
<%@page import="com.facilio.modules.FacilioStatus"%>
<%@page import="com.facilio.bmsconsole.util.TicketAPI"%>
<%@page import="com.facilio.bmsconsole.context.ServiceRequestPriorityContext"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="com.facilio.modules.fields.BooleanField"%>
<%@page import="com.facilio.bmsconsole.context.TabularColumnContext.DisplayType"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.bmsconsole.util.RecordAPI"%>
<%@page import="com.facilio.modules.fields.NumberField"%>
<%@page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction"%>
<%@page import="java.util.*"%>
<%
	final class MigrationCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		final Logger LOGGER = Logger.getLogger(FacilioAuthAction.class.getName());
		List<Organization> organizations = AccountUtil.getOrgBean().getOrgs();
		List<FacilioField> fieldsFields = new ArrayList<>();
		fieldsFields.addAll(FieldFactory.getSelectFieldFields());
		for(Organization org : organizations) {
			Long orgId = org.getOrgId();
			AccountUtil.setCurrentAccount(orgId);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule serviceRequestPriority = new FacilioModule();
			serviceRequestPriority.setOrgId(orgId);
			serviceRequestPriority.setName("servicerequestpriority");
			serviceRequestPriority.setDisplayName("Service Requests Priority");
			serviceRequestPriority.setTableName("ServiceRequestPriority");
			serviceRequestPriority.setType(ModuleType.PICK_LIST);
			long serviceRequestPriorityModuleId = modBean.addModule(serviceRequestPriority);
			serviceRequestPriority.setModuleId(serviceRequestPriorityModuleId);
			
			if(modBean.getModule("ticketstatus") != null && modBean.getModule("resource") != null) {
			List<FacilioField> serviceRequestPriorityFields = new ArrayList<>();
			FacilioField spDisplayName = new FacilioField(serviceRequestPriority, "displayName", "Display Name", FieldDisplayType.TEXTBOX, "DISPLAY_NAME", FieldType.STRING, false, false, true, true);
			modBean.addField(spDisplayName);
			serviceRequestPriorityFields.add(spDisplayName);
			
			FacilioField spPriority = new FacilioField(serviceRequestPriority,"priority", "Priority", FieldDisplayType.TEXTBOX, "PRIORITY", FieldType.STRING, true, false, true, false);
			modBean.addField(spPriority);
			serviceRequestPriorityFields.add(spPriority);
			
			NumberField spSequenceNumberNF = new NumberField(serviceRequestPriority,"sequenceNumber", "Sequence", FieldDisplayType.NUMBER, "SEQUENCE_NUMBER", FieldType.NUMBER, true, false, true, false);
			modBean.addField(spSequenceNumberNF);
			serviceRequestPriorityFields.add(spSequenceNumberNF);
			
			BooleanField spisDefaultBF = new BooleanField(serviceRequestPriority,"isDefault", "Is Default", FieldDisplayType.DECISION_BOX, "ISDEFAULT", FieldType.BOOLEAN, false, false, true, false);
			modBean.addField(spisDefaultBF);
			serviceRequestPriorityFields.add(spisDefaultBF);
			
			FacilioField spColour = new FacilioField(serviceRequestPriority,"colour", "Colour", FieldDisplayType.TEXTBOX, "COLOUR", FieldType.STRING, true, false, true, false);
			modBean.addField(spColour);
			serviceRequestPriorityFields.add(spColour);
			
			List<ServiceRequestPriorityContext> serviceRequestPriorityList = new ArrayList<>();
			serviceRequestPriorityList.add(new ServiceRequestPriorityContext("High", 1, "#f00", "High", true));
			serviceRequestPriorityList.add(new ServiceRequestPriorityContext("Medium",2, "#fb9b00", "Medium", true));
			serviceRequestPriorityList.add(new ServiceRequestPriorityContext("Low",3, "#f0d200", "Low", true));
			
			RecordAPI.addRecord(false, serviceRequestPriorityList, serviceRequestPriority, serviceRequestPriorityFields);
			
			FacilioModule serviceRequest = new FacilioModule();
			serviceRequest.setOrgId(orgId);
			serviceRequest.setName("serviceRequest");
			serviceRequest.setDisplayName("Service Requests");
			serviceRequest.setTableName("Service_Requests");
			serviceRequest.setType(ModuleType.BASE_ENTITY);
			serviceRequest.setTrashEnabled(true);
			long serviceRequestModuleId = modBean.addModule(serviceRequest);
			serviceRequest.setModuleId(serviceRequestModuleId);
			
			FacilioField srSubject = new FacilioField(serviceRequest, "subject", "Subject", FieldDisplayType.TEXTBOX, "SUBJECT", FieldType.STRING,true, false, true, true);
			modBean.addField(srSubject);
			
			FacilioField srDescription = new FacilioField(serviceRequest, "description", "Description", FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING,false, false, true, false);
			modBean.addField(srDescription);
			
			LookupField srmoduleStateLF = new LookupField(serviceRequest, "moduleState", "Module State", FieldDisplayType.LOOKUP_SIMPLE, "MODULE_STATE", FieldType.LOOKUP, true, false, true, false,modBean.getModule("ticketstatus"));
			modBean.addField(srmoduleStateLF);
			
			NumberField srstateFlowNF = new NumberField(serviceRequest, "stateFlowId", "State Flow Id", FieldDisplayType.NUMBER, "STATE_FLOW_ID", FieldType.NUMBER, true, false, true, false);
			modBean.addField(srstateFlowNF);
			
			LookupField srassignmentGroupLF = new LookupField(serviceRequest, "assignmentGroup", "Team", FieldDisplayType.LOOKUP_POPUP, "ASSIGNMENT_GROUP_ID", FieldType.LOOKUP, false, false, true, false,"groups");
			modBean.addField(srassignmentGroupLF);
					
			LookupField srassignedToLF = new LookupField(serviceRequest, "assignedTo", "Staff", FieldDisplayType.LOOKUP_POPUP, "ASSIGNED_TO_ID", FieldType.LOOKUP, false, false, true, false, "users");
			modBean.addField(srassignedToLF);
			
			LookupField srrequesterLF = new LookupField(serviceRequest, "requester", "Requester", FieldDisplayType.LOOKUP_POPUP, "REQUESTER_ID", FieldType.LOOKUP, false, false, true, false,"users");
			modBean.addField(srrequesterLF);
			
			NumberField srlocalIdNF = new NumberField(serviceRequest, "localId", "ID", FieldDisplayType.NUMBER, "LOCALID", FieldType.NUMBER, false, false, true, false);
			modBean.addField(srlocalIdNF);
			
			FacilioField srdueDate = new FacilioField(serviceRequest, "dueDate", "Due Date", FieldDisplayType.DATETIME, "DUE_DATE", FieldType.DATE_TIME, false, false, true, false);
			modBean.addField(srdueDate);

			NumberField srsourceTypeNF = new NumberField(serviceRequest, "sourceType", "Source Type", FieldDisplayType.NUMBER, "SOURCE_TYPE", FieldType.NUMBER, false, false, true, false);
			modBean.addField(srsourceTypeNF);
			
			NumberField srclassificationNF = new NumberField(serviceRequest, "classification", "Classification", FieldDisplayType.NUMBER, "CLASSIFICATION", FieldType.NUMBER, false, false, true, false);
			modBean.addField(srclassificationNF);
			
			LookupField srpriorityLF = new LookupField(serviceRequest, "priority", "Priority", FieldDisplayType.LOOKUP_SIMPLE, "PRIORITY", FieldType.LOOKUP, true, false, true, false,modBean.getModule("servicerequestpriority"));
			modBean.addField(srpriorityLF);
		
			LookupField srresourceLF = new LookupField(serviceRequest, "resource", "Space / Asset", FieldDisplayType.LOOKUP_SIMPLE, "RESOURCE_ID", FieldType.LOOKUP, true, false, true, false, modBean.getModule("resource"));
			modBean.addField(srresourceLF);
			
			FacilioModule serviceRequestNotes = new FacilioModule();
			serviceRequestNotes.setOrgId(orgId);
			serviceRequestNotes.setName("servicerequestsnotes");
			serviceRequestNotes.setDisplayName("Service Request Notes");
			serviceRequestNotes.setTableName("Service_Requests_Notes");
			serviceRequestNotes.setType(ModuleType.NOTES);
			long serviceRequestNotesModuleId = modBean.addModule(serviceRequestNotes);
			serviceRequestNotes.setModuleId(serviceRequestNotesModuleId);
			modBean.addSubModule(serviceRequestModuleId, serviceRequestNotesModuleId);
			
			FacilioField srncreatedTime = new FacilioField(serviceRequestNotes, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, false);
			modBean.addField(srncreatedTime);
			
			LookupField srncreatedByLF = new LookupField(serviceRequestNotes, "createdBy", "Created By", FieldDisplayType.LOOKUP_POPUP, "CREATED_BY", FieldType.LOOKUP, false, false, true, false, "users");
			modBean.addField(srncreatedByLF);
			
			NumberField srnparentIdNF = new NumberField(serviceRequestNotes, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, false, false, true, false);
			modBean.addField(srnparentIdNF);
			
			FacilioField srntitle = new FacilioField(serviceRequestNotes, "title", "Title", FieldDisplayType.TEXTBOX, "TITLE", FieldType.STRING,false, false, true, true);
			modBean.addField(srntitle);
			
			FacilioField srnbody = new FacilioField(serviceRequestNotes, "body", "Body", FieldDisplayType.TEXTAREA, "BODY", FieldType.STRING,false, false, true, false);
			modBean.addField(srnbody);
			
			BooleanField srnnotifyRequesterBF = new BooleanField(serviceRequestNotes,"notifyRequester", "Notify Requester", FieldDisplayType.DECISION_BOX, "NOTIFY_REQUESTER", FieldType.BOOLEAN, false, false, true, false);
			modBean.addField(srnnotifyRequesterBF);
			
			FacilioModule servicerequestsattachments = new FacilioModule();
			servicerequestsattachments.setOrgId(orgId);
			servicerequestsattachments.setName("servicerequestsattachments");
			servicerequestsattachments.setDisplayName("Service Requests Attachments");
			servicerequestsattachments.setTableName("Service_Requests_Attachments");
			servicerequestsattachments.setType(ModuleType.ATTACHMENTS);
			// module added
			long servicerequestsattachmentsModuleId = modBean.addModule(servicerequestsattachments);
			servicerequestsattachments.setModuleId(servicerequestsattachmentsModuleId);
			modBean.addSubModule(serviceRequestModuleId, servicerequestsattachmentsModuleId);
			
			NumberField srafileIdNF = new NumberField(servicerequestsattachments, "fileId", "File ID", FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
			modBean.addField(srafileIdNF);
			
			NumberField sraparentIdNF = new NumberField(servicerequestsattachments, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, false);
			modBean.addField(sraparentIdNF);
			
			NumberField sracreatedTimeNF = new NumberField(servicerequestsattachments, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, false);
			modBean.addField(sracreatedTimeNF);
			
			NumberField sratypeNF = new NumberField(servicerequestsattachments, "type", "Type", FieldDisplayType.NUMBER, "ATTACHMENT_TYPE", FieldType.NUMBER, true, false, true, false);
			modBean.addField(sratypeNF);
			
			FacilioModule servicerequestsactivity = new FacilioModule();
			servicerequestsactivity.setOrgId(orgId);
			servicerequestsactivity.setName("servicerequestsactivity");
			servicerequestsactivity.setDisplayName("Service Requests Activity");
			servicerequestsactivity.setTableName("Service_Requests_Activity");
			servicerequestsactivity.setType(ModuleType.ACTIVITY);
			long servicerequestsactivityModuleId = modBean.addModule(servicerequestsactivity);
			servicerequestsactivity.setModuleId(servicerequestsactivityModuleId);
			modBean.addSubModule(serviceRequestModuleId, servicerequestsactivityModuleId);

			NumberField srtparentIdNF = new NumberField(servicerequestsactivity, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, false);
			modBean.addField(srtparentIdNF);
			
			FacilioField srtttime = new FacilioField(servicerequestsactivity, "ttime", "Timestamp", FieldDisplayType.NUMBER, "TTIME", FieldType.DATE_TIME, true, false, true, false);
			modBean.addField(srtttime);
			
			NumberField srttypeNF = new NumberField(servicerequestsactivity, "type", "Type", FieldDisplayType.NUMBER, "ACTIVITY_TYPE", FieldType.NUMBER, true, false, true, false);
			modBean.addField(srttypeNF);
			
			LookupField srtdoneByLF = new LookupField(servicerequestsactivity, "doneBy", "Done By", FieldDisplayType.LOOKUP_POPUP, "DONE_BY_ID", FieldType.LOOKUP, false, false, true, false,"users");
			modBean.addField(srtdoneByLF);
			
			FacilioField srtinfoJsonStr = new FacilioField(servicerequestsactivity, "infoJsonStr", "Info", FieldDisplayType.TEXTAREA, "INFO", FieldType.STRING,false, false, true, false);
			modBean.addField(srtinfoJsonStr);
			
			
			FacilioStatus openStatus = new FacilioStatus();
			openStatus.setStatus("Open");
			openStatus.setTypeCode(StatusType.OPEN.getIntVal());
			openStatus.setDisplayName("Open");
			openStatus.setParentModuleId(serviceRequestModuleId);
			openStatus.setTimerEnabled(false);
			openStatus.setRecordLocked(false);
			openStatus.setRequestedState(false);
			TicketAPI.addStatus(openStatus, serviceRequest);
			
			FacilioStatus onHoldStatus = new FacilioStatus();
			onHoldStatus.setStatus("On Hold");
			onHoldStatus.setTypeCode(StatusType.OPEN.getIntVal());
			onHoldStatus.setDisplayName("On Hold");
			onHoldStatus.setParentModuleId(serviceRequestModuleId);
			onHoldStatus.setTimerEnabled(false);
			onHoldStatus.setRecordLocked(false);
			onHoldStatus.setRequestedState(false);
			TicketAPI.addStatus(onHoldStatus, serviceRequest);
			
			FacilioStatus closeStatus = new FacilioStatus();
			closeStatus.setStatus("Closed");
			closeStatus.setTypeCode(StatusType.CLOSED.getIntVal());
			closeStatus.setDisplayName("Closed");
			closeStatus.setParentModuleId(serviceRequestModuleId);
			closeStatus.setTimerEnabled(false);
			closeStatus.setRecordLocked(false);
			closeStatus.setRequestedState(false);
			TicketAPI.addStatus(closeStatus, serviceRequest);
			
			StateFlowRuleContext defaultStateFlowRule = new StateFlowRuleContext();
			defaultStateFlowRule.setName("Default Service Request Stateflow");
			defaultStateFlowRule.setExecutionOrder(1);
			defaultStateFlowRule.setStatus(true);
			defaultStateFlowRule.setRuleType(RuleType.STATE_FLOW);
			defaultStateFlowRule.setLatestVersion(true);
			defaultStateFlowRule.setModuleId(serviceRequestModuleId);
			defaultStateFlowRule.setActivityType(EventType.CREATE);
			defaultStateFlowRule.setDefaultStateId(openStatus.getId());
			defaultStateFlowRule.setDefaltStateFlow(true);
			WorkflowRuleAPI.addWorkflowRule(defaultStateFlowRule);
			
			StateflowTransitionContext openClosedStateFlow = new StateflowTransitionContext();
			openClosedStateFlow.setName("Close");
			openClosedStateFlow.setRuleType(RuleType.STATE_RULE);
			openClosedStateFlow.setLatestVersion(true);
			openClosedStateFlow.setModuleId(serviceRequestModuleId);
			openClosedStateFlow.setActivityType(EventType.STATE_TRANSITION);
			openClosedStateFlow.setFromStateId(openStatus.getId());
			openClosedStateFlow.setToStateId(closeStatus.getId());
			openClosedStateFlow.setType(TransitionType.NORMAL);
			openClosedStateFlow.setStateFlowId(defaultStateFlowRule.getId());
			WorkflowRuleAPI.addWorkflowRule(openClosedStateFlow);
			
			StateflowTransitionContext openOnHoldStateFlow = new StateflowTransitionContext();
			openOnHoldStateFlow.setName("On Hold");
			openOnHoldStateFlow.setRuleType(RuleType.STATE_RULE);
			openOnHoldStateFlow.setLatestVersion(true);
			openOnHoldStateFlow.setModuleId(serviceRequestModuleId);
			openOnHoldStateFlow.setActivityType(EventType.STATE_TRANSITION);
			openOnHoldStateFlow.setFromStateId(openStatus.getId());
			openOnHoldStateFlow.setToStateId(onHoldStatus.getId());
			openOnHoldStateFlow.setType(TransitionType.NORMAL);
			openOnHoldStateFlow.setStateFlowId(defaultStateFlowRule.getId());
			WorkflowRuleAPI.addWorkflowRule(openOnHoldStateFlow);
			
			StateflowTransitionContext closeReopenStateFlow = new StateflowTransitionContext();
			closeReopenStateFlow.setName("Reopen");
			closeReopenStateFlow.setRuleType(RuleType.STATE_RULE);
			closeReopenStateFlow.setLatestVersion(true);
			closeReopenStateFlow.setModuleId(serviceRequestModuleId);
			closeReopenStateFlow.setActivityType(EventType.STATE_TRANSITION);
			closeReopenStateFlow.setFromStateId(closeStatus.getId());
			closeReopenStateFlow.setToStateId(openStatus.getId());
			closeReopenStateFlow.setType(TransitionType.NORMAL);
			closeReopenStateFlow.setStateFlowId(defaultStateFlowRule.getId());
			WorkflowRuleAPI.addWorkflowRule(closeReopenStateFlow);
			
			StateflowTransitionContext onHoldResumeStateFlow = new StateflowTransitionContext();
			onHoldResumeStateFlow.setName("Resume");
			onHoldResumeStateFlow.setRuleType(RuleType.STATE_RULE);
			onHoldResumeStateFlow.setLatestVersion(true);
			onHoldResumeStateFlow.setModuleId(serviceRequestModuleId);
			onHoldResumeStateFlow.setActivityType(EventType.STATE_TRANSITION);
			onHoldResumeStateFlow.setFromStateId(onHoldStatus.getId());
			onHoldResumeStateFlow.setToStateId(openStatus.getId());
			onHoldResumeStateFlow.setType(TransitionType.NORMAL);
			onHoldResumeStateFlow.setStateFlowId(defaultStateFlowRule.getId());
			WorkflowRuleAPI.addWorkflowRule(onHoldResumeStateFlow);
			LOGGER.log(Level.INFO, "Migrated upto org: "+ orgId);
			} else {
				LOGGER.log(Level.INFO, "Migration didn't happen for: " + orgId);
			}
		}
		LOGGER.log(Level.INFO, "Migration Completed");
		return false;
	}
}
%>

<%
	FacilioChain c = FacilioChain.getTransactionChain();
	c.addCommand(new MigrationCommand());
	c.execute();
%>