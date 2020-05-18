<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
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
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.BooleanField" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabGroupContext" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabContext" %>
<%@ page import="com.facilio.bmsconsole.context.ApplicationContext" %>
<%@ page import="com.facilio.bmsconsole.util.ApplicationApi" %>
<%@ page import="com.facilio.bmsconsole.commands.TransactionChainFactory" %>
<%@ page import="com.facilio.chain.FacilioContext" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.facilio.iam.accounts.util.IAMAppUtil" %>
<%@ page import="com.facilio.accounts.dto.AppDomain" %>


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
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            long applicationId = -1;
            ApplicationContext agent = null;
            try {
                agent  = ApplicationApi.getApplicationForLinkName("agent");
                applicationId = agent.getId();
            }
            catch(Exception e){
                ApplicationContext applicationContext = new ApplicationContext();
                applicationContext.setIsDefault(false);
                applicationContext.setName("Agent");
                applicationContext.setLinkName("agent");
                AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
                if(appDomain != null) {
                    applicationContext.setAppDomainId(appDomain.getId());
                }
                applicationContext.setLayoutType(ApplicationContext.AppLayoutType.SINGLE.getIndex());
                applicationId = ApplicationApi.addApplicationApi(applicationContext);
                applicationContext.setId(applicationId);
            }
            int groupOrder = 1;
            int webTabOrder = 1;
            if (applicationId > 0) {
                List<WebTabGroupContext> webTabGroups = new ArrayList<>();
                Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
                List<WebTabContext> webTabs = new ArrayList<>();
                JSONObject configJSON;

                webTabGroups.add(new WebTabGroupContext("Agent", "agent", applicationId, 1, groupOrder));
                webTabs = new ArrayList<>();
                webTabOrder = 1;
                webTabs.add(new WebTabContext("Overview", "overview", WebTabContext.Type.DASHBOARD, webTabOrder, null, applicationId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "agents");
                webTabs.add(new WebTabContext("Agents", "list", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "device");
                webTabs.add(new WebTabContext("Device", "device", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                webTabs.add(new WebTabContext("Controllers", "controllers", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("controller").getModuleId()), applicationId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "points");
                webTabs.add(new WebTabContext("Points", "points", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "commissioning");
                webTabs.add(new WebTabContext("Commissioning", "commissioning", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                webTabs.add(new WebTabContext("Alarm", "alarm", WebTabContext.Type.MODULE, webTabOrder++, Arrays.asList(modBean.getModule("agentAlarm").getModuleId()), applicationId, null));
                configJSON = new JSONObject();
                configJSON.put("type", "rule");
                webTabs.add(new WebTabContext("Alarm Rule", "notification", WebTabContext.Type.MODULE, webTabOrder++, null, applicationId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "metrics");
                webTabs.add(new WebTabContext("Metrics", "metrics", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "log");
                webTabs.add(new WebTabContext("Log", "log", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                configJSON = new JSONObject();
                configJSON.put("type", "agent_data");
                webTabs.add(new WebTabContext("Agent Data", "data", WebTabContext.Type.AGENT, webTabOrder++, null, applicationId, configJSON));
                groupNameVsWebTabsMap.put("agent", webTabs);

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
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();
        AccountUtil.cleanCurrentAccount();
    }
%>


%>