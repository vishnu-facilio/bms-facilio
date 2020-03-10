<%@page import="com.facilio.constants.FacilioConstants.TicketStatus" %>
<%@page import="com.facilio.chain.FacilioContext" %>
<%@page import="com.facilio.modules.FieldFactory" %>
<%@page import="java.util.Collections" %>
<%@page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@page import="com.facilio.modules.FieldUtil" %>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType" %>
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

        	try{

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule floorModule = modBean.getModule("floor");
                
                NumberField field = new NumberField(floorModule, "defaultFloorPlanId", "Default Floor Plan", FacilioField.FieldDisplayType.NUMBER,"DEFAULT_FLOOR_PLAN_ID", FieldType.NUMBER, false, false, true,false);
          		modBean.addField(field);
          		
            }
            catch(Exception e) {
                LOGGER.info(e.getMessage());
            }

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            System.out.println("modbean executed");
            ApplicationContext applicationContext = com.facilio.bmsconsole.util.ApplicationApi.getDefaultApplication();
            System.out.println("application executed");
            int groupOrder = 1;
            int webTabOrder = 1;
            if (applicationContext != null) {
                System.out.println("appId: " + applicationContext.getId());
                long appId = applicationContext.getId();
                List<WebTabGroupContext> webTabGroups = new ArrayList<>();
                Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
                List<WebTabContext> webTabs = new ArrayList<>();
                System.out.println("1:**");
                webTabGroups.add(new WebTabGroupContext("Home", "home", appId, 1, groupOrder++));
                webTabs = new ArrayList<>();
                webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("Reports", "reports", WebTabContext.Type.REPORT, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("KPIs", "kpi", WebTabContext.Type.KPI, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVAL, webTabOrder++, null, appId, null));
                groupNameVsWebTabsMap.put("home", webTabs);
                System.out.println("2:**");
                webTabGroups.add(new WebTabGroupContext("Space", "space", appId, 2, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("basespace").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("space", webTabs);
                System.out.println("3:**");
                webTabGroups.add(new WebTabGroupContext("Asset", "at", appId, 3, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Asset", "assets", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("asset").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("at", webTabs);
                System.out.println("4:**");
                webTabGroups.add(new WebTabGroupContext("Maintenance", "wo", appId, 4, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Work Orders", "orders", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
//                webTabs.add(new WebTabContext("Planned", "planned", WebTabContext.Type.MODULE, 2, 1, appId));
                webTabs.add(new WebTabContext("Calendar", "calendar", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("wo", webTabs);
                System.out.println("5:**");
                webTabGroups.add(new WebTabGroupContext("Inventory", "in", appId, 5, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Storerooms", "storerooms", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("storeRoom").getModuleId()), appId, null));
                System.out.println("storeroom");
//                webTabs.add(new WebTabContext("Items", "items", WebTabContext.Type.MODULE, 3, Arrays.asList(modBean.getModule("item").getModuleId()), appId));
                webTabs.add(new WebTabContext("Items", "items", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("item").getModuleId()), appId, null));
                System.out.println("item");
                webTabs.add(new WebTabContext("Tools", "tools", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("tool").getModuleId()), appId, null));
                System.out.println("tool");
                webTabs.add(new WebTabContext("Service", "service", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("service").getModuleId()), appId, null));
                System.out.println("service");
                webTabs.add(new WebTabContext("Item Types", "itemtypes", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("itemTypes").getModuleId()), appId, null));
                System.out.println("itemtypes");
                webTabs.add(new WebTabContext("Tool Types", "tooltypes", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("toolTypes").getModuleId()), appId, null));
                System.out.println("tooltypes");
                webTabs.add(new WebTabContext("Gate Pass", "gatepass", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("gatePass").getModuleId()), appId, null));
                System.out.println("gatepass");
                webTabs.add(new WebTabContext("Shipment", "shipment", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shipment").getModuleId()), appId, null));
                System.out.println("shipment");
                groupNameVsWebTabsMap.put("in", webTabs);
                System.out.println("6:**");
                webTabGroups.add(new WebTabGroupContext("Purchase", "pu", appId, 6, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Purchase Request", "purchaserequest", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchaserequest").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Purchase Order", "purchaseorder", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchaseorder").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Receivables", "receivable", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("receivable").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Terms & Conditions", "tandc", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("termsandconditions").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("pu", webTabs);
                System.out.println("7:**");
                webTabGroups.add(new WebTabGroupContext("Contracts", "co", appId, 7, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Purchase Contracts", "purchase", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchasecontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Rental Lease Contracts", "rental", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("rentalleasecontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Warranty Contracts", "warranty", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("warrantycontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Labour Contracts", "labour", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("labourcontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Terms & Conditions", "tandc", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("termsandconditions").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("co", webTabs);
                System.out.println("8:**");
                webTabGroups.add(new WebTabGroupContext("Visitor", "vi", appId, 7, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitorlogging").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Invites", "invite", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitorinvite").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Visitor", "visitor", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitor").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Watchlist", "watchlist", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("watchlist").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("vi", webTabs);
                System.out.println("9:**");
                webTabGroups.add(new WebTabGroupContext("Tenant", "te", appId, 8, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Tenants", "tenant", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("tenant").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Contacts", "contact", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("contact").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("te", webTabs);
                System.out.println("10:**");
                webTabGroups.add(new WebTabGroupContext("Vendor", "ve", appId, 9, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Vendors", "vendor", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("vendors").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Contacts", "contact", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("contact").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("ve", webTabs);
                System.out.println("11:**");
                webTabGroups.add(new WebTabGroupContext("Service Desk", "se", appId, 10, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Service Requests", "servicerequest", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("serviceRequest").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("se", webTabs);
                System.out.println("12:**");
                webTabGroups.add(new WebTabGroupContext("People", "pl", appId, 11, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Attendance", "attendance", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("attendance").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shifts", "shift", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shift").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shift Planner", "shiftplanner", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("shift").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shift Rotation", "shuftrotation", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shiftRotation").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("pl", webTabs);
                System.out.println("13:**");
                webTabGroups.add(new WebTabGroupContext("Diagnostics", "di", appId, 12, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Alarms", "alarms", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("alarm").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Anomalies", "anomalies", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("mlalarm").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("KPI Violations", "kpi", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("violationalarm").getModuleId()), appId, null));
//                webTabs.add(new WebTabContext("FDD Rules", "fdd", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("readingrule").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("di", webTabs);
                System.out.println("we: " + webTabGroups.size());
                for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                    System.out.println("we: " + webTabGroupContext.getRoute());
                    FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                    FacilioContext chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                    chain.execute();
                    long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                    webTabGroupContext.setId(webGroupId);
                    List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                    for (WebTabContext webTabContext : tabs) {
                        webTabContext.setGroupId(webTabGroupContext.getId());
                        chain = TransactionChainFactory.getAddOrUpdateTabChain();
                        chainContext = chain.getContext();
                        chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                        chain.execute();
                    }
                }
            }
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
        System.out.println("migration error: " + e.getMessage());
        e.printStackTrace();
    }
    out.println("Done");
%>
