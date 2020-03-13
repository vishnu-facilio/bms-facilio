<%@page import="com.facilio.constants.FacilioConstants.TicketStatus" %>
<%@page import="com.facilio.chain.FacilioContext" %>
<%@page import="com.facilio.modules.FieldFactory" %>
<%@page import="java.util.Collections" %>
<%@page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@page import="com.facilio.modules.FieldUtil" %>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.EventType" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.modules.FacilioStatus" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext" %>
<%@page import="org.apache.commons.collections4.CollectionUtils" %>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext" %>
<%@page import="com.facilio.modules.SelectRecordsBuilder" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>

<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.SelectRecordsBuilder" %>
<%@ page import="com.facilio.bmsconsole.actions.RollUpRecommendedRuleAction" %>
<%@ page import="com.facilio.bmsconsole.commands.TransactionChainFactory" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabGroupContext" %>
<%@ page import="com.facilio.bmsconsole.context.ApplicationContext" %>
<%@ page import="com.facilio.bmsconsole.util.*" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabContext" %>
<%@ page import="com.facilio.accounts.dto.NewPermission" %>
<%@ page import="org.json.simple.JSONObject" %>

<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());

        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            return false;
        }
    }
%>

<%
    List<Organization> orgs = null;
    try {
        orgs = AccountUtil.getOrgBean().getOrgs();
        for (Organization org : orgs) {
            System.out.println("org: " + org.getOrgId());
            AccountUtil.setCurrentAccount(org.getOrgId());
            FacilioChain c = FacilioChain.getTransactionChain();
            c.addCommand(new OrgLevelMigrationCommand());
            c.execute();

            AccountUtil.cleanCurrentAccount();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("Done");
%>
