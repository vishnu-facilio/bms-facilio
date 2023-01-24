package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            if(tab.getConfig() != null) {
            	JSONParser parser = new JSONParser();
                JSONObject configJson = (JSONObject) parser.parse(tab.getConfig());
                if(tab.getTypeEnum().equals(Type.REPORT) && configJson.get("type").equals("analytics_reports") ) {
                	FacilioModule module = modBean.getModule("energydata");
                	tab.setModules(Collections.singletonList(module));
                	Long moduleId = modBean.getModule("energydata").getModuleId();
                	tab.setModuleIds(Collections.singletonList(moduleId));
                }
            }

            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            if (tab.getId() > 0) {
                tabBean.updateWebTab(tab);
                context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tab.getId());
                tabBean.deleteTabMappingEntriesForTab(tab.getId());
                tabBean.insertIntoTabIdAppIdMappingTable(tab);
            } else {
                long tabId = tabBean.addTab(tab);
                context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tabId);
                tab.setId(tabId);
                tabBean.insertIntoTabIdAppIdMappingTable(tab);
            }
           context.put(FacilioConstants.ContextNames.WEB_TAB,tab);
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
                if (webTabContext.getId() != webTab.getId() && webTabContext.getRoute().equals(webTab.getRoute())) {
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
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (webTab.getModuleIds() != null && !webTab.getModuleIds().isEmpty()) {
            for (Long moduleId : webTab.getModuleIds()) {
                TabIdAppIdMappingContext tabIdAppIdMappingContext = new TabIdAppIdMappingContext(webTab.getId(),
                        moduleId, webTab.getApplicationId());
                tabIdAppIdProps.add(FieldUtil.getAsProperties(tabIdAppIdMappingContext));
                modules.add(modBean.getModule(moduleId));
            }
        }
        if (CollectionUtils.isNotEmpty(webTab.getSpecialTypeModules())) {
            for (String specialType : webTab.getSpecialTypeModules()) {
                TabIdAppIdMappingContext tabIdAppIdMappingContext = new TabIdAppIdMappingContext(webTab.getId(), webTab.getApplicationId(), specialType);
                tabIdAppIdProps.add(FieldUtil.getAsProperties(tabIdAppIdMappingContext));
                modules.add(modBean.getModule(specialType));
            }
        }
        if (CollectionUtils.isNotEmpty(modules)) {
            webTab.setModules(modules);
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
}
