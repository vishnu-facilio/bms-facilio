
package com.facilio.remotemonitoring.commands;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.signup.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AddRMSetupTabsAndTabGroups extends FacilioCommand {

    public static void addSetupLayoutWebGroups(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();


            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("General", "general", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Organization", "organizationsettings", WebTabContext.Type.ORGANIZATION_SETTINGS, null, appId, null));
            webTabs.add(new WebTabContext("Portals", "portal", WebTabContext.Type.PORTALS, null, appId, null));
            webTabs.add(new WebTabContext("Operating Hours", "operatinghours", WebTabContext.Type.OPERATING_HOURS, null, appId, null));

            groupNameVsWebTabsMap.put("general", webTabs);


            webTabGroups.add(new WebTabGroupContext("Users and Access", "usersandaccess", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Users", "users", WebTabContext.Type.USERS, null, appId, null));
            webTabs.add(new WebTabContext("Roles", "roles", WebTabContext.Type.ROLES, null, appId, null));
            webTabs.add(new WebTabContext("Single Sign-On", "sso", WebTabContext.Type.SINGLE_SIGN_ON, null, appId, null));
            webTabs.add(new WebTabContext("Security Policy", "securitypolicy", WebTabContext.Type.SECURITY_POLICY, null, appId, null));
            webTabs.add(new WebTabContext("Scope", "scope", WebTabContext.Type.SCOPE, null, appId, null,AccountUtil.FeatureLicense.SCOPE_VARIABLE.getFeatureId()));
            webTabs.add(new WebTabContext("Data Sharing", "datasharing", WebTabContext.Type.DATA_SHARING, null, appId, null,AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING.getFeatureId()));
            groupNameVsWebTabsMap.put("usersandaccess", webTabs);


            webTabGroups.add(new WebTabGroupContext("Resources", "resources", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("People", "people", WebTabContext.Type.PEOPLE, null, appId, null));
            webTabs.add(new WebTabContext("Teams", "teams", WebTabContext.Type.TEAMS, null, appId, null));
            groupNameVsWebTabsMap.put("resources", webTabs);


            webTabGroups.add(new WebTabGroupContext("Customization", "customization", layout.getId(), 1, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Modules", "modules", WebTabContext.Type.MODULES, null, appId, null));
            webTabs.add(new WebTabContext("Tabs and Layouts", "tabsandlayouts", WebTabContext.Type.TABS_AND_LAYOUTS, null, appId, null));
            webTabs.add(new WebTabContext("Email Templates", "emailtemplates", WebTabContext.Type.EMAIL_TEMPLATES, null, appId, null));


            groupNameVsWebTabsMap.put("customization", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation", "automation", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Workflows", "workflows", WebTabContext.Type.WORKFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Notifications", "notifications", WebTabContext.Type.NOTIFICATIONS, null, appId, null));
            webTabs.add(new WebTabContext("Scheduler", "scheduler", WebTabContext.Type.SCHEDULER, null, appId, null));

            groupNameVsWebTabsMap.put("automation", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation Plus", "automationplus", layout.getId(), 206, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("SLA Policies", "slapolicies", WebTabContext.Type.SLA_POLICIES, null, appId, null));
            webTabs.add(new WebTabContext("Assignment Rules", "assignmentrules", WebTabContext.Type.ASSIGNMENT_RULES, null, appId, null));
            webTabs.add(new WebTabContext("Transaction Rules", "transactionrules", WebTabContext.Type.TRANSACTION_RULES, null, appId, null,AccountUtil.FeatureLicense.BUDGET_MONITORING.getFeatureId()));

            groupNameVsWebTabsMap.put("automationplus", webTabs);

            webTabGroups.add(new WebTabGroupContext("Process", "process", layout.getId(), 0, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Stateflows", "stateflows", WebTabContext.Type.STATEFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVALS, null, appId, null,AccountUtil.FeatureLicense.APPROVAL.getFeatureId()));

            groupNameVsWebTabsMap.put("process", webTabs);

            webTabGroups.add(new WebTabGroupContext("Portfolio Settings", "portfoliosettings", layout.getId(), 204, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Readings", "readings", WebTabContext.Type.READINGS, null, appId, null));
            webTabs.add(new WebTabContext("Space Categories", "spacecategory", WebTabContext.Type.SPACE_CATEGORIES, null, appId, null));
            webTabs.add(new WebTabContext("Asset Customization", "assetcustomization", WebTabContext.Type.SPACE_ASSET_CUSTOMIZATION, null, appId, null));
            webTabs.add(new WebTabContext("Asset Depreciation", "depreciation", WebTabContext.Type.ASSET_DEPRECIATION, null, appId, null));
            webTabs.add(new WebTabContext("Decommission", "decommission", WebTabContext.Type.DECOMMISSION, null, appId, null,AccountUtil.FeatureLicense.COMMISSIONING.getFeatureId()));
            groupNameVsWebTabsMap.put("portfoliosettings", webTabs);

            webTabGroups.add(new WebTabGroupContext("Workorder Settings", "workordersettings", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Settings", "emailsettings", WebTabContext.Type.EMAIL_SETTINGS, null, appId, null));
            webTabs.add(new WebTabContext("Customization", "customization", WebTabContext.Type.WORKORDER_CUSTOMIZATION, null, appId, null));
            groupNameVsWebTabsMap.put("workordersettings", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Logs","logs", layout.getId(),207, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Logs", "emaillogs", WebTabContext.Type.EMAIL_LOGS, null, appId, null,AccountUtil.FeatureLicense.EMAIL_TRACKING.getFeatureId()));
            webTabs.add(new WebTabContext("Audit Logs", "auditlogs", WebTabContext.Type.AUDIT_LOGS, null, appId, null));
            webTabs.add(new WebTabContext("Script Logs","scriptlogs", WebTabContext.Type.SCRIPT_LOGS,null,appId,null,AccountUtil.FeatureLicense.WORKFLOW_LOG.getFeatureId()));
            webTabs.add(new WebTabContext("Background Activity","backgroundactivity", WebTabContext.Type.BACKGROUND_ACTIVITY,null,appId,null,AccountUtil.FeatureLicense.BACKGROUND_ACTIVITY.getFeatureId()));
            webTabs.add(new WebTabContext("Rule Logs", "readingrulelogs", WebTabContext.Type.RULE_LOGS, null, appId, null, AccountUtil.FeatureLicense.NEW_READING_RULE.getFeatureId()));
            webTabs.add(new WebTabContext("Inbound Mail Conversion", "inboundmailconversion", WebTabContext.Type.INBOUND_MAIL_CONVERSION, null, appId, null));
            webTabs.add(new WebTabContext("Workflow Logs","workflowrulelogs", WebTabContext.Type.WORKFLOW_RULE_LOGS,null,appId,null,AccountUtil.FeatureLicense.WORKFLOW_RULE_LOG.getFeatureId()));

            groupNameVsWebTabsMap.put("logs", webTabs);


            webTabGroups.add(new WebTabGroupContext( "Energy Analytics","energyanalytics", layout.getId(),2, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Fault Impact","faultimpact", WebTabContext.Type.FAULT_IMPACT_TEMPLATE,null,appId,null));


            groupNameVsWebTabsMap.put("energyanalytics", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Data Administration","dataadministration", layout.getId(),101, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Import Data","importdata", WebTabContext.Type.IMPORT_DATA,null,appId,null,AccountUtil.FeatureLicense.IMPORT_DATA.getFeatureId()));

            groupNameVsWebTabsMap.put("dataadministration", webTabs);
            
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
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if(CollectionUtils.isNotEmpty(tabs)){
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ApplicationContext rmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        List<ApplicationLayoutContext> layouts = ApplicationApi.getLayoutsForAppId(rmApp.getId());
        ApplicationLayoutContext layout = null;
        for (ApplicationLayoutContext appLayout : layouts) {
            if (appLayout.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.SETUP) {
                layout = appLayout;
            }
        }
        addSetupLayoutWebGroups(layout);
        return false;
    }
}