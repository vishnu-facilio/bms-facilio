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
            ApplicationContext applicationContext = com.facilio.bmsconsole.util.ApplicationApi.getDefaultApplication();
            int groupOrder = 1;
            int webTabOrder = 1;
            if (applicationContext != null) {
                long appId = applicationContext.getId();
                List<WebTabGroupContext> webTabGroups = new ArrayList<>();
                Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
                List<WebTabContext> webTabs = new ArrayList<>();
                JSONObject configJSON;
                webTabGroups.add(new WebTabGroupContext("Home", "home", appId, 1, groupOrder++));
                webTabs = new ArrayList<>();
                webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("Reports", "reports", WebTabContext.Type.REPORT, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("KPIs", "kpi", WebTabContext.Type.KPI, webTabOrder++, null, appId, null));
                webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVAL, webTabOrder++, null, appId, null));
                groupNameVsWebTabsMap.put("home", webTabs);

                webTabGroups.add(new WebTabGroupContext("Space", "space", appId, 2, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("basespace").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("space", webTabs);

                webTabGroups.add(new WebTabGroupContext("Asset", "at", appId, 3, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Asset", "assets", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("asset").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("at", webTabs);

                webTabGroups.add(new WebTabGroupContext("Maintenance", "wo", appId, 4, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Work Orders", "orders", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "preventivemaintenance");
                webTabs.add(new WebTabContext("Planned", "planned", WebTabContext.Type.MODULE, webTabOrder++, null, appId, configJSON));
                webTabs.add(new WebTabContext("Calendar", "calendar", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("wo", webTabs);

                webTabGroups.add(new WebTabGroupContext("Inventory", "in", appId, 5, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Storerooms", "storerooms", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("storeRoom").getModuleId()), appId, null));
//                webTabs.add(new WebTabContext("Items", "items", WebTabContext.Type.MODULE, 3, Arrays.asList(modBean.getModule("item").getModuleId()), appId));
                webTabs.add(new WebTabContext("Items", "items", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("item").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Tools", "tools", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("tool").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Service", "service", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("service").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Item Types", "itemtypes", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("itemTypes").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Tool Types", "tooltypes", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("toolTypes").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Gate Pass", "gatepass", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("gatePass").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shipment", "shipment", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shipment").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("in", webTabs);

                webTabGroups.add(new WebTabGroupContext("Purchase", "pu", appId, 6, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Purchase Request", "purchaserequest", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchaserequest").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Purchase Order", "purchaseorder", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchaseorder").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Receivables", "receivable", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("receivable").getModuleId()), appId, null));
//                webTabs.add(new WebTabContext("Terms & Conditions", "tandc", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("termsandconditions").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("pu", webTabs);

                webTabGroups.add(new WebTabGroupContext("Contracts", "ct", appId, 7, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Purchase Contracts", "purchase", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("purchasecontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Rental Lease Contracts", "rental", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("rentalleasecontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Warranty Contracts", "warranty", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("warrantycontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Labour Contracts", "labour", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("labourcontracts").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Terms & Conditions", "tandc", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("termsandconditions").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("ct", webTabs);

                webTabGroups.add(new WebTabGroupContext("Bookings", "bo", appId, 7, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Room Booking", "roombooking", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("reservation").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Calendar Booking", "calendarbooking", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("reservation").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("bo", webTabs);

                webTabGroups.add(new WebTabGroupContext("Visitor", "vi", appId, 7, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitorlogging").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Invites", "invite", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitorinvite").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Visitor", "visitor", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("visitor").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Watchlist", "watchlist", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("watchlist").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("vi", webTabs);

                webTabGroups.add(new WebTabGroupContext("Tenant", "te", appId, 8, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Tenants", "tenant", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("tenant").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Contacts", "contact", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("contact").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("te", webTabs);

                webTabGroups.add(new WebTabGroupContext("Vendor", "ve", appId, 9, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Vendors", "vendor", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("vendors").getModuleId()), appId, null));
//                webTabs.add(new WebTabContext("Contacts", "contact", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("contact").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("ve", webTabs);

                webTabGroups.add(new WebTabGroupContext("Service Desk", "se", appId, 10, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Service Requests", "servicerequest", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("serviceRequest").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("se", webTabs);

                webTabGroups.add(new WebTabGroupContext("People", "pl", appId, 11, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Attendance", "attendance", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("attendance").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shifts", "shift", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shift").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shift Planner", "shiftplanner", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("shift").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Shift Rotation", "shiftrotation", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("shiftRotation").getModuleId()), appId, null));
                groupNameVsWebTabsMap.put("pl", webTabs);

                webTabGroups.add(new WebTabGroupContext("Analytics", "an", appId, 12, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                configJSON = new JSONObject();
                configJSON.put("type", "analytics_portfolio");
                webTabs.add(new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.ANALYTICS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "analytics_building");
                webTabs.add(new WebTabContext("Building", "building", WebTabContext.Type.ANALYTICS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "analytics_regression");
                webTabs.add(new WebTabContext("Regression", "regression", WebTabContext.Type.ANALYTICS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type","analytics_datavisualization");
                webTabs.add(new WebTabContext("Data Visualization", "datavisualization", WebTabContext.Type.ANALYTICS, webTabOrder++, null, appId, configJSON));
                webTabs.add(new WebTabContext("M&v", "mvproject", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("mvproject").getModuleId()), appId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "analytics_reports");
                webTabs.add(new WebTabContext("Reports", "reports", WebTabContext.Type.REPORT, webTabOrder++, null, appId, configJSON));
                groupNameVsWebTabsMap.put("an", webTabs);

                webTabGroups.add(new WebTabGroupContext("Diagnostics", "di", appId, 12, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Alarms", "alarms", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("alarm").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("Anomalies", "anomalies", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("mlalarm").getModuleId()), appId, null));
                webTabs.add(new WebTabContext("KPI Violations", "kpi", WebTabContext.Type.CALENDAR, webTabOrder++, Arrays.asList(modBean.getModule("violationalarm").getModuleId()), appId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "readingrule");
                webTabs.add(new WebTabContext("FDD Rules", "fdd", WebTabContext.Type.MODULE, webTabOrder++, null, appId, configJSON));
                groupNameVsWebTabsMap.put("di", webTabs);

                webTabGroups.add(new WebTabGroupContext("control", "co", appId, 12, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                configJSON = new JSONObject();
                configJSON.put("type", "control_graphics");
                webTabs.add(new WebTabContext("Graphics", "graphics", WebTabContext.Type.CUSTOM, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "control_controls");
                webTabs.add(new WebTabContext("Controls", "controls", WebTabContext.Type.CUSTOM, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "control_sequence");
                webTabs.add(new WebTabContext("Sequence","sequence", WebTabContext.Type.CUSTOM, webTabOrder++, null, appId, configJSON));
                groupNameVsWebTabsMap.put("co", webTabs);

                webTabGroups.add(new WebTabGroupContext("Settings", "setup", appId, 13, groupOrder++));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                configJSON = new JSONObject();
                configJSON.put("type", "general");
                webTabs.add(new WebTabContext("General", "general", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "users_management");
                webTabs.add(new WebTabContext("User Management", "resource", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "workorder_settings");
                webTabs.add(new WebTabContext("Work Order Settings", "workordersettings", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "alarm_settings");
                webTabs.add(new WebTabContext("Alarm Settings", "alarm_settings", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "space_asset_settings");
                webTabs.add(new WebTabContext("Space / Asset Settings", "space_asset_settings", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "automations");
                webTabs.add(new WebTabContext("Automations", "automations", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "customization");
                webTabs.add(new WebTabContext("Customization", "customization", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "energy_analytics");
                webTabs.add(new WebTabContext("Energy Analytics", "energy_analytics", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "agent_configurations");
                webTabs.add(new WebTabContext("Agent Configurations", "agent_configurations", WebTabContext.Type.SETTINGS, webTabOrder++, null, appId, configJSON));
                groupNameVsWebTabsMap.put("setup", webTabs);

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
        e.printStackTrace();
    }
    out.println("Done");
%>
