package com.facilio.remotemonitoring.commands;


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

import java.util.*;

public class AddRMTabsAndTabGroups extends FacilioCommand {
    private List<Long> getModuleIdsListFromModuleNames(List<String> moduleNames) throws Exception {
        List<Long> moduleIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(moduleNames)) {
            for (String moduleName : moduleNames) {
                moduleIds.add(modBean.getModule(moduleName).getModuleId());
            }
        }
        return moduleIds;
    }


    private Map<String, WebTabContext> getWebTabs(long appId) throws Exception {
        Map<String, WebTabContext> tabsMap = new HashMap<String, WebTabContext>();

        List<WebTabContext> tabs = Arrays.asList(
                new WebTabContext("Alarm Category", "alarmcategory", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmCategoryModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Alarm Type", "alarmtype", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmTypeModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Alarms", "alarms", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(RawAlarmModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Client", "client", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT)), null, null, null, appId),
                new WebTabContext("Client Contact", "clientcontact", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CLIENT_CONTACT)), null, null, null, appId),
                new WebTabContext("Alarm Definition", "alarmdefinition", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmDefinitionModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Alarm Regex Matcher", "alarmregexmatcher", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmDefinitionMappingModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Controller", "controller", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.CONTROLLER)), null, null, null, appId),
                new WebTabContext("Alarm Correlation Rule", "alarmcorrelationrule", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmFilterRuleModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Filtered Alarms", "filteredalarms", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FilteredAlarmModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Flagged Alarm Process", "flaggedalarmprocess", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FlaggedEventRuleModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Flagged Alarms", "flaggedalarms", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FlaggedEventModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, null, null,null,appId),
                new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.PORTFOLIO, null, null, 1,null,appId),
                new WebTabContext("Alarm Type Mapping", "alarmtypetagging", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmDefinitionTaggingModule.MODULE_NAME)), null, null, null, appId),
                new WebTabContext("Asset", "asset", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ContextNames.ASSET)), null, null, null, appId),
                new WebTabContext("Alarm Asset Mapping", "alarmassetmapping", WebTabContext.Type.MODULE, getModuleIdsListFromModuleNames(Arrays.asList(AlarmAssetTaggingModule.MODULE_NAME)), null, null, null, appId)
        );
        for (WebTabContext webTab : tabs) {
            tabsMap.put(webTab.getRoute(), webTab);
        }
        return tabsMap;
    }


    public List<WebTabGroupContext> getWebTabGroups(long appId, long layoutId) throws Exception {
        Map<String, WebTabContext> tabsMap = getWebTabs(appId);
        List<WebTabGroupContext> webTabGroups = Arrays.asList(
                new WebTabGroupContext(Arrays.asList(tabsMap.get("dashboard"),tabsMap.get("portfolio")), "Home", "home", 2, 1, null, layoutId, IconType.home),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("flaggedalarms"),tabsMap.get("flaggedalarmprocess")), "Flagged Alarms", "flaggedalarms", 1, 6, null, layoutId, IconType.flagged_events),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("filteredalarms"),tabsMap.get("alarmcorrelationrule")), "Filtered Alarms", "filteredalarms", 1, 5, null, layoutId, IconType.filtered_alarms),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("asset"),tabsMap.get("controller")), "Asset & Controllers", "asset", 1, 3, null, layoutId, IconType.asset),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("alarms"),tabsMap.get("alarmregexmatcher"),tabsMap.get("alarmtypetagging"),tabsMap.get("alarmassetmapping"),tabsMap.get("alarmtype"), tabsMap.get("alarmcategory"), tabsMap.get("alarmdefinition")), "Alarms", "alarms", 1, 4, null, layoutId, IconType.raw_alarm),
                new WebTabGroupContext(Arrays.asList(tabsMap.get("client"),tabsMap.get("clientcontact")), "Client", "client", 2, 2, null, layoutId, IconType.client)
        );
        return webTabGroups;
    }

    public Map<String, List<WebTabContext>> getGroupNameVsTabsMap(long appId, long layoutId) throws Exception {
        Map<String, List<WebTabContext>> groupNameVsTabsMap = new HashMap<>();
        for (WebTabGroupContext webTabGroup : getWebTabGroups(appId, layoutId)) {
            groupNameVsTabsMap.put(webTabGroup.getRoute(), webTabGroup.getWebTabs());
        }
        return groupNameVsTabsMap;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ApplicationContext rmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        List<ApplicationLayoutContext> layouts = ApplicationApi.getLayoutsForAppId(rmApp.getId());
        ApplicationLayoutContext layout = null;
        for (ApplicationLayoutContext appLayout : layouts) {
            if (appLayout.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.WEB) {
                layout = appLayout;
            }
        }
        long webGroupId = 0l;
        FacilioChain chain;
        FacilioContext chainContext;
        for (WebTabGroupContext webTabGroupContext : getWebTabGroups(layout.getApplicationId(), layout.getId())) {
            if (!webTabGroupContext.getName().equals("ONLY_TABS")) {
                chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
            }
            webTabGroupContext.setId(webGroupId);
            List<WebTabContext> tabs = getGroupNameVsTabsMap(layout.getApplicationId(), layout.getId())
                    .get(webTabGroupContext.getRoute());
            for (WebTabContext webTabContext : tabs) {
                WebTabContext webtab = ApplicationApi.getWebTabForApplication(layout.getApplicationId(), webTabContext.getRoute());
                long tabId = 0l;
                if (webtab != null) {
                    tabId = webtab.getId();
                } else {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                }
                webTabContext.setId(tabId);
            }
            if (CollectionUtils.isNotEmpty(tabs) && !webTabGroupContext.getName().equals("ONLY_TABS")) {
                chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                chain.execute();
            }
        }
        return false;
    }
}