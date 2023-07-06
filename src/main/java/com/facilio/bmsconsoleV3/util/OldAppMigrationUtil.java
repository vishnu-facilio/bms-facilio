package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.PortalUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class OldAppMigrationUtil {
    public static void addTabsForCustomModule(ApplicationLayoutContext layout) {
        try {
            int maxOrder = 0;

            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getField("order", "MAX(TABGROUP_ORDER)", FieldType.NUMBER));
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getWebTabGroupModule().getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layout.getId()), NumberOperators.EQUALS));
            List<Map<String, Object>> orderList = builder.get();

            if (CollectionUtils.isNotEmpty(orderList)) {
                maxOrder = (int) orderList.get(0).get("order");
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> customModulesList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true);
            List<WebTabContext> webTabs = new ArrayList<>();

            WebTabGroupContext connectedApp = new WebTabGroupContext(webTabs, "Connected App", "connectedapp", 9, ++maxOrder, 18l, layout.getId(), IconType.connected_apps);
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            long webTabGroupId = tabBean.addWebTabGroup(connectedApp);
            connectedApp.setId(webTabGroupId);

            int i = 0;
            for (FacilioModule customModule : customModulesList) {
                long webTabId = -1;
                WebTabContext webTab = PortalUtil.getModuleTab(layout.getApplicationId(), customModule.getName());
                if (webTab != null) {
                    webTabId = webTab.getId();
                }
                if (webTabId <= 0) {
                    webTab = new WebTabContext(customModule.getDisplayName(), customModule.getName(), WebTabContext.Type.MODULE, Arrays.asList(customModule.getModuleId()), null, 18, null, layout.getApplicationId());
                    webTabId = tabBean.addTab(webTab);
                    webTab.setId(webTabId);
                    tabBean.insertIntoTabIdAppIdMappingTable(webTab);
                }
                webTabs.add(webTab);
            }

            FacilioChain chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webTabGroupId);
            context.put(FacilioConstants.ContextNames.WEB_TABS, webTabs);
            chain.execute();

        } catch (Exception e) {

        }

    }

    public static WebTabGroupContext findWebTabGroupForWebTab(long webTabId, Integer deviceType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("WebTab_Group.ID = WebTab_WebGroup.WEBTAB_GROUP_ID")
                .innerJoin(ModuleFactory.getApplicationLayoutModule().getTableName())
                .on("WebTab_Group.LAYOUT_ID = Application_Layout.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_ID", "webTabId",
                        String.valueOf(webTabId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application_Layout.DEVICE_TYPE", "layoutDeviceType",
                        String.valueOf(deviceType), NumberOperators.EQUALS));
        Map<String, Object> webTabGroup = builder.fetchFirst();
        if(MapUtils.isNotEmpty(webTabGroup)){
            return FieldUtil.getAsBeanFromMap(webTabGroup, WebTabGroupContext.class);
        }
        return new WebTabGroupContext();
    }

    public static WebTabContext getTabForTabType(long appId, int tabType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(appId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE","type",String.valueOf(tabType),NumberOperators.EQUALS));
        Map<String, Object> webTab = builder.fetchFirst();
        if(MapUtils.isNotEmpty(webTab)){
            return FieldUtil.getAsBeanFromMap(webTab, WebTabContext.class);
        }
        return new WebTabContext();
    }
    public static WebTabContext getModuleTab(Long appId, int deviceType, int tabType, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        boolean isSpecialType = LookupSpecialTypeUtil.isSpecialType(moduleName);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .innerJoin(ModuleFactory.getWebTabModule().getTableName())
                .on("TABID_MODULEID_APPID_MAPPING.TAB_ID = WebTab.ID")
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("TABID_MODULEID_APPID_MAPPING.TAB_ID = WebTab_WebGroup.WEBTAB_ID")
                .innerJoin(ModuleFactory.getWebTabGroupModule().getTableName())
                .on("WebTab_WebGroup.WEBTAB_GROUP_ID = WebTab_Group.ID")
                .innerJoin(ModuleFactory.getApplicationLayoutModule().getTableName())
                .on("WebTab_Group.LAYOUT_ID =  Application_Layout.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab.APPLICATION_ID", "applicationId",String.valueOf(appId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("WebTab.TYPE","type", String.valueOf(tabType),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application_Layout.DEVICE_TYPE","layoutDeviceType", String.valueOf(deviceType),NumberOperators.EQUALS));

        if (!isSpecialType) {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.MODULE_ID", "moduleId",String.valueOf(modBean.getModule(moduleName).getModuleId()), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.SPECIAL_TYPE", "specialType",moduleName, StringOperators.IS));
        }

        List<TabIdAppIdMappingContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),TabIdAppIdMappingContext.class);

        if(webTabGroups != null && !webTabGroups.isEmpty()) {
            long tabId =  webTabGroups.get(0).getTabId();
            return ApplicationApi.getWebTab(tabId);
        }

        return null;
    }

    public static WebTabContext getCustomTab(Long appId, String type) throws Exception{
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(appId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("NAME","name","Portfolio",StringOperators.CONTAINS));
        Map<String, Object> webTab = builder.fetchFirst();
        if(MapUtils.isNotEmpty(webTab)){
            return FieldUtil.getAsBeanFromMap(webTab, WebTabContext.class);
        }
        return new WebTabContext();
    }
}
