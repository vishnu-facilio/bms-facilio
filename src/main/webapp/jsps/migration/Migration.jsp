
<%@page import="com.facilio.bmsconsole.commands.TransactionChainFactory"%>
<%@page import="com.facilio.bmsconsole.util.ModuleLocalIdUtil"%>
<%@page import="com.facilio.cards.util.CardType"%>
<%@page import="com.facilio.cards.util.CardUtil"%>
<%@page import="com.facilio.bmsconsole.jobs.*"%>
<%@page import="com.facilio.tasker.job.JobContext"%>
<%@page import="com.facilio.workflows.util.WorkflowUtil"%>
<%@page import="com.facilio.bmsconsole.util.ReadingsAPI"%>
<%@page import="com.facilio.bmsconsole.util.ResourceAPI"%>
<%@page import="com.facilio.bmsconsole.context.*"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.bmsconsole.modules.*"%>
<%@page import="com.facilio.chain.*"%>
<%@page import="com.facilio.bmsconsole.actions.DashboardAction,com.facilio.constants.*"%>

<%

out.println("started");
Long orgId = 210l;

AccountUtil.setCurrentAccount(orgId);

FacilioChain chain = TransactionChainFactory.getpopulateDefaultChatBotIntentChain();

chain.execute();

out.println("done1");
%>