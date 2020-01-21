<%@page import="com.facilio.modules.fields.EnumFieldValue"%>
<%@page import="com.facilio.modules.fields.EnumField"%>
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
<%@page
	import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.EventType"%>
<%@page
	import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType"%>
<%@page import="com.facilio.bmsconsole.util.WorkflowRuleAPI"%>
<%@page
	import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType"%>
<%@page
	import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext"%>
<%@page import="com.facilio.modules.FacilioStatus.StatusType"%>
<%@page import="com.facilio.modules.FacilioStatus"%>
<%@page import="com.facilio.bmsconsole.util.TicketAPI"%>
<%@page
	import="com.facilio.bmsconsole.context.ServiceRequestPriorityContext"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="com.facilio.modules.fields.BooleanField"%>
<%@page
	import="com.facilio.bmsconsole.context.TabularColumnContext.DisplayType"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page
	import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
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
			for (Organization org : organizations) {
				Long orgId = org.getOrgId();
				AccountUtil.setCurrentAccount(orgId);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule serviceRequestModule = modBean.getModule("serviceRequest");
				if (serviceRequestModule != null) {
					FacilioField classificationField = modBean.getField("classification", "serviceRequest");
					if (classificationField != null) {
						modBean.deleteField(classificationField.getFieldId());
					}
					FacilioField requestTypeField = modBean.getField("requestType", "serviceRequest");
					if (requestTypeField != null) {
						modBean.deleteField(requestTypeField.getFieldId());
					}
					
					List<EnumFieldValue> fields = new ArrayList<>();
					fields.add(new EnumFieldValue(1, "Question", 1, true));
					fields.add(new EnumFieldValue(2, "Problem", 2, true));
					fields.add(new EnumFieldValue(3, "Feature", 3, true));
					EnumField enumField = new EnumField(serviceRequestModule, "classification",
							"Classification", FieldDisplayType.SELECTBOX, "CLASSIFICATION", FieldType.ENUM,
							false, false, true, false, fields);
					modBean.addField(enumField);
					
					List<EnumFieldValue> serviceRequestTypeFields = new ArrayList<>();
					serviceRequestTypeFields.add(new EnumFieldValue(1, "Feedback", 1, true));
					serviceRequestTypeFields.add(new EnumFieldValue(2, "Rating", 2, true));
					EnumField typeField = new EnumField(serviceRequestModule, "requestType",
							"Request Type", FieldDisplayType.SELECTBOX, "REQUEST_TYPE", FieldType.ENUM,
							false, false, true, false, serviceRequestTypeFields);
					modBean.addField(typeField);
					

					LOGGER.log(Level.INFO, "Migrated upto org: " + orgId);
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


