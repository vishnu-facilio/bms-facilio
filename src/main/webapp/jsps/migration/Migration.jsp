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
		
			FacilioModule workorderModule = modBean.getModule("workorder");
			FacilioModule siteModule = modBean.getModule("site");
			FacilioModule contactModule = modBean.getModule("contact");
			FacilioModule locationModule = modBean.getModule("location");
			FacilioModule ticketStatusModule = modBean.getModule("ticketstatus");
			if(workorderModule != null && siteModule != null && contactModule!=null && locationModule!=null && ticketStatusModule!=null) {
				
				FacilioModule clientModule = new FacilioModule();
				clientModule.setOrgId(orgId);
				clientModule.setName("client");
				clientModule.setDisplayName("Clients");
				clientModule.setTableName("Clients");
				clientModule.setType(ModuleType.BASE_ENTITY);
				clientModule.setTrashEnabled(true);
				clientModule.setStateFlowEnabled(true);
				long clientModuleId = modBean.addModule(clientModule);
				LOGGER.log(Level.INFO, "clientModuleId: "+ clientModuleId);
				clientModule.setModuleId(clientModuleId);
				
				FacilioField clName = new FacilioField(clientModule, "name", "Name", FieldDisplayType.TEXTBOX, "NAME", FieldType.STRING,true, false, true, true);
				modBean.addField(clName);
				
				FacilioField srDescription = new FacilioField(clientModule, "description", "Description", FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING,false, false, true, false);
				modBean.addField(srDescription);
				
				FacilioField clPrimaryContactName = new FacilioField(clientModule, "primaryContactName", "Primary Contact Name", FieldDisplayType.TEXTBOX, "PRIMARY_CONTACT_NAME", FieldType.STRING,false, false, true, false);
				modBean.addField(clPrimaryContactName);
				
				FacilioField clPrimaryContactEmail = new FacilioField(clientModule, "primaryContactEmail", "Primary Contact E-Mail", FieldDisplayType.TEXTBOX, "PRIMARY_CONTACT_EMAIL", FieldType.STRING,false, false, true, false);
				modBean.addField(clPrimaryContactEmail);
				
				FacilioField clPrimaryContactPhone = new FacilioField(clientModule, "primaryContactPhone", "Primary Contact Phone", FieldDisplayType.TEXTBOX, "PRIMARY_CONTACT_PHONE", FieldType.STRING,false, false, true, false);
				modBean.addField(clPrimaryContactPhone);
				
				FacilioField clWebsite = new FacilioField(clientModule, "website", "Website", FieldDisplayType.TEXTBOX, "WEBSITE", FieldType.STRING, false, false, true, false);
				modBean.addField(clWebsite);
				
				LookupField clAddressLF = new LookupField(clientModule, "address", "Address", FieldDisplayType.LOOKUP_SIMPLE, "ADDRESS", FieldType.LOOKUP, true, false, true, false, locationModule);
				modBean.addField(clAddressLF);
				
				LookupField clmoduleStateLF = new LookupField(clientModule, "moduleState", "Module State", FieldDisplayType.LOOKUP_SIMPLE, "MODULE_STATE", FieldType.LOOKUP, true, false, true, false, ticketStatusModule);
				modBean.addField(clmoduleStateLF);
				
				NumberField clstateFlowNF = new NumberField(clientModule, "stateFlowId", "State Flow Id", FieldDisplayType.NUMBER, "STATE_FLOW_ID", FieldType.NUMBER, true, false, true, false);
				modBean.addField(clstateFlowNF);
				
				FacilioModule clientNotes = new FacilioModule();
				clientNotes.setOrgId(orgId);
				clientNotes.setName("clientsNotes");
				clientNotes.setDisplayName("Clients Notes");
				clientNotes.setTableName("Clients_Notes");
				clientNotes.setType(ModuleType.NOTES);
				long clientNotesModuleId = modBean.addModule(clientNotes);
				clientNotes.setModuleId(clientNotesModuleId);
				modBean.addSubModule(clientModuleId, clientNotesModuleId);
				
				FacilioField srncreatedTime = new FacilioField(clientNotes, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, false);
				modBean.addField(srncreatedTime);
				
				LookupField srncreatedByLF = new LookupField(clientNotes, "createdBy", "Created By", FieldDisplayType.LOOKUP_POPUP, "CREATED_BY", FieldType.LOOKUP, false, false, true, false, "users");
				modBean.addField(srncreatedByLF);
				
				NumberField srnparentIdNF = new NumberField(clientNotes, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, false, false, true, false);
				modBean.addField(srnparentIdNF);
				
				FacilioField srntitle = new FacilioField(clientNotes, "title", "Title", FieldDisplayType.TEXTBOX, "TITLE", FieldType.STRING,false, false, true, true);
				modBean.addField(srntitle);
				
				FacilioField srnbody = new FacilioField(clientNotes, "body", "Body", FieldDisplayType.TEXTAREA, "BODY", FieldType.STRING,false, false, true, false);
				modBean.addField(srnbody);
				
				BooleanField srnnotifyRequesterBF = new BooleanField(clientNotes,"notifyRequester", "Notify Requester", FieldDisplayType.DECISION_BOX, "NOTIFY_REQUESTER", FieldType.BOOLEAN, false, false, true, false);
				modBean.addField(srnnotifyRequesterBF);
				
				FacilioModule clientAttachment = new FacilioModule();
				clientAttachment.setOrgId(orgId);
				clientAttachment.setName("clientsAttachment");
				clientAttachment.setDisplayName("Clients Attachments");
				clientAttachment.setTableName("Clients_Attachments");
				clientAttachment.setType(ModuleType.ATTACHMENTS);
				// module added
				long clientattachmentsModuleId = modBean.addModule(clientAttachment);
				clientAttachment.setModuleId(clientattachmentsModuleId);
				modBean.addSubModule(clientModuleId, clientattachmentsModuleId);
				
				NumberField srafileIdNF = new NumberField(clientAttachment, "fileId", "File ID", FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
				modBean.addField(srafileIdNF);
				
				NumberField sraparentIdNF = new NumberField(clientAttachment, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, false);
				modBean.addField(sraparentIdNF);
				
				NumberField sracreatedTimeNF = new NumberField(clientAttachment, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, false);
				modBean.addField(sracreatedTimeNF);
				
				NumberField sratypeNF = new NumberField(clientAttachment, "type", "Type", FieldDisplayType.NUMBER, "ATTACHMENT_TYPE", FieldType.NUMBER, true, false, true, false);
				modBean.addField(sratypeNF);
				
				LookupField siteClientLF = new LookupField(siteModule, "client", "Client", FieldDisplayType.LOOKUP_SIMPLE, "CLIENT_ID", FieldType.LOOKUP, true, false, true, false, clientModule);
				modBean.addField(siteClientLF);
				
				LookupField workorderClientLF = new LookupField(workorderModule, "client", "Client", FieldDisplayType.LOOKUP_SIMPLE, "CLIENT_ID", FieldType.LOOKUP, true, false, true, false, clientModule);
				modBean.addField(workorderClientLF);
				
				LookupField contactClientLF = new LookupField(contactModule, "client", "Client", FieldDisplayType.LOOKUP_SIMPLE, "CLIENT_ID", FieldType.LOOKUP, true, false, true, false, clientModule);
				modBean.addField(contactClientLF);
				
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


