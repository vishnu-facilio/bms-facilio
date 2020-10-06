package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddOrUpdateTabCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WebTabContext tab = (WebTabContext) context.get(FacilioConstants.ContextNames.WEB_TAB);
        if (tab != null) {
            if (StringUtils.isEmpty(tab.getName())) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            if (tab.getApplicationId() == -1) {
                throw new IllegalArgumentException("Application cannot empty");
            }

//			if (tab.getTypeEnum() == null || tab.getConfig() == null) {
//				throw new IllegalArgumentException("Type or config cannot be empty");
//			}
//
//			// Validate configuration of tab
//			tab.validateConfig();

            if (checkIfRouteAlreadyFound(tab)) {
                throw new IllegalArgumentException("Route is already found for this app");
            }

           if (tab.getId() > 0) {
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields())
                        .andCondition(CriteriaAPI.getIdCondition(tab.getId(), ModuleFactory.getWebTabModule()));
                builder.update(FieldUtil.getAsProperties(tab));
                context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tab.getId());
                deleteEntriesForTab(tab.getId());
                insertIntoTabIdAppIdMappingTable(tab);
            } else {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields());
                long tabId = builder.insert(FieldUtil.getAsProperties(tab));
                context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tabId);
                tab.setId(tabId);
                insertIntoTabIdAppIdMappingTable(tab);
            }
        }
        return false;
    }

    private boolean checkRouteAlreadyFound(WebTabContext tab) throws Exception {
        if (StringUtils.isEmpty(tab.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", tab.getRoute(), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(tab.getApplicationId()),
                        NumberOperators.EQUALS));

        if (tab.getId() > 0) {
            builder.andCondition(
                    CriteriaAPI.getCondition("ID", "id", String.valueOf(tab.getId()), NumberOperators.NOT_EQUALS));
        }

        Map<String, Object> map = builder.fetchFirst();
        return map != null;
    }

    private boolean checkIfRouteAlreadyFound(WebTabContext webTab) throws Exception {
        if (StringUtils.isEmpty(webTab.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }
        List<WebTabContext> webTabs = ApplicationApi.getWebTabsForApplication(webTab.getApplicationId());
        if (webTabs != null && !webTabs.isEmpty()) {
            for (WebTabContext webTabContext : webTabs) {
                if (webTabContext.getRoute().equals(webTab.getRoute())) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getLastOrderNumber(Long groupId) throws Exception {
        if (groupId == null) {
            throw new IllegalArgumentException("Group Id cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(Arrays.asList(FieldFactory.getField("order", "MAX(TAB_ORDER)", FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId),
                        NumberOperators.EQUALS));
        Map<String, Object> map = builder.fetchFirst();
        if (MapUtils.isNotEmpty(map)) {
            Integer order = (Integer) map.get("order");
            if (order != null) {
                return order;
            }
        }
        return 0;
    }

    private void insertIntoTabIdAppIdMappingTable(WebTabContext webTab) throws Exception {
        List<Map<String, Object>> tabIdAppIdProps = new ArrayList<>();
        if (webTab.getModuleIds() != null && !webTab.getModuleIds().isEmpty()) {
            for (Long moduleId : webTab.getModuleIds()) {
                /*if (webTab.getTypeEnum() == Type.MODULE) {
                    if (isModuleIdForModuleTabForAppIdAlreadyAdded(webTab.getId(), moduleId, webTab.getAppId(), "", false)) {
                        throw new IllegalStateException("Module of this tab type is already added.");
                    }
                }*/
                TabIdAppIdMappingContext tabIdAppIdMappingContext = new TabIdAppIdMappingContext(webTab.getId(),
                        moduleId, webTab.getApplicationId());
                tabIdAppIdProps.add(FieldUtil.getAsProperties(tabIdAppIdMappingContext));
            }
        } else if (webTab.getConfigJSON() != null) {
            String specialType = (String) webTab.getConfigJSON().get("type");
           /* if (webTab.getTypeEnum() == Type.MODULE) {
                if (isModuleIdForModuleTabForAppIdAlreadyAdded(webTab.getId(), -1, webTab.getAppId(), specialType, true)) {
                    throw new IllegalStateException(specialType + "Module of this tab type is already added.");
                }
            }*/
            TabIdAppIdMappingContext tabIdAppIdMappingContext = new TabIdAppIdMappingContext(webTab.getId(), webTab.getApplicationId(), specialType);
            tabIdAppIdProps.add(FieldUtil.getAsProperties(tabIdAppIdMappingContext));
        }
        if (!tabIdAppIdProps.isEmpty()) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                    .fields(FieldFactory.getTabIdAppIdMappingFields()).addRecords(tabIdAppIdProps);
            builder.save();
        }
    }

    private boolean isModuleIdForModuleTabForAppIdAlreadyAdded(long tabId, long moduleId, long appId, String specialType, boolean isSpecialType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.APP_ID", "appId",
                        String.valueOf(appId), NumberOperators.EQUALS));
        if (!isSpecialType) {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.MODULE_ID", "moduleId",
                    String.valueOf(moduleId), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.SPECIAL_TYPE", "specialType",
                    specialType, StringOperators.IS));
        }
        List<TabIdAppIdMappingContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
                TabIdAppIdMappingContext.class);
        if (webTabGroups != null && !webTabGroups.isEmpty()) {
            return true;
        }
        return false;
    }

    private void deleteEntriesForTab(long tabId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName()).andCondition(CriteriaAPI.getCondition(
                        "TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
        builder.delete();
    }

}
