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
				FacilioModule ticketsModule = modBean.getModule("ticket");

				if (serviceRequestModule != null && ticketsModule != null) {
					LookupField srmoduleStateLF = new LookupField(ticketsModule, "serviceRequest", "Service Request",
							FieldDisplayType.LOOKUP_SIMPLE, "SERVICE_REQUEST_ID", FieldType.LOOKUP, true, false, true,
							false, serviceRequestModule);
					modBean.addField(srmoduleStateLF);

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


