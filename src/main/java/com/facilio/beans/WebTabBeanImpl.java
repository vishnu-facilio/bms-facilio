package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.cache.CacheUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WebTabBeanImpl implements WebTabBean{
    @Override
    public WebTabCacheContext getWebTab(long tabId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(tabId, ModuleFactory.getWebTabModule()));
        WebTabCacheContext webTab = new WebTabCacheContext(FieldUtil.getAsBeanFromMap(builder.fetchFirst(), WebTabContext.class));
        return webTab;
    }

    @Override
    public WebTabGroupCacheContext getWebTabGroup(long groupId) throws Exception {
        FacilioModule module = ModuleFactory.getWebTabGroupModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(groupId, module));
        return new WebTabGroupCacheContext(FieldUtil.getAsBeanFromMap(builder.fetchFirst(), WebTabGroupContext.class));
    }

    public List<TabIdAppIdMappingCacheContext> getTabIdModules(long tabId) throws Exception {
        List<TabIdAppIdMappingCacheContext> tabidMappings = null;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId",
                        String.valueOf(tabId), NumberOperators.EQUALS));
        tabidMappings = getObjectList(FieldUtil.getAsBeanListFromMapList(builder.get(), TabIdAppIdMappingContext.class));
        return tabidMappings;
    }

    public List<WebTabGroupCacheContext> getWebTabGroupForLayoutID(ApplicationLayoutContext layout) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layout.getId()),
                        NumberOperators.EQUALS));

        List<WebTabGroupCacheContext> grps = getObjectList(FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class));
        return grps;
    }

    public List<WebTabCacheContext> getWebTabsForWebGroup(long webTabGroupId) throws Exception {
        List<FacilioField> fields = FieldFactory.getWebTabFields();
        fields.add(
                FieldFactory.getField("order", "TAB_ORDER", ModuleFactory.getWebTabWebGroupModule(), FieldType.NUMBER));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(fields)
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("WebTab_WebGroup.WEBTAB_ID = WebTab.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_GROUP_ID", "groupId",
                        String.valueOf(webTabGroupId), NumberOperators.EQUALS));
        List<WebTabCacheContext> webTabs = getObjectList(FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class));
        return webTabs;
    }

    public List<WebTabCacheContext> getWebTabsForApplication(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS));
        List<WebTabCacheContext> webTabs = getObjectList(FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class));
        return webTabs;
    }

    public void updateWebTab(WebTabContext webTab) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(webTab.getId(), ModuleFactory.getWebTabModule()))
                .ignoreSplNullHandling();
        Map<String,Object> objectMap = FieldUtil.getAsProperties(webTab);
        if(webTab.getConfig() == null && webTab.getType() == 7) {
            objectMap.put("config",null);
        }
        builder.update(objectMap);
    }


    public void insertIntoTabIdAppIdMappingTable(WebTabContext webTab) throws Exception {
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


    public long addTab(WebTabContext tab) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields());
        long tabId = builder.insert(FieldUtil.getAsProperties(tab));
        return tabId;
    }


    public void deleteTabMappingEntriesForTab(long tabId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName()).andCondition(CriteriaAPI.getCondition(
                        "TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
        builder.delete();
    }

    @Override
    public void deleteTab(long tabId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(tabId, ModuleFactory.getWebTabModule()));
        builder.delete();
    }


    @Override
    public void associateTabGroup(List<WebTabGroupContext> tabsGroups) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .fields(FieldFactory.getWebTabWebGroupFields());

        List<Map<String, Object>> props = FieldUtil.getAsMapList(tabsGroups, WebtabWebgroupContext.class);

        insertBuilder.addRecords(props);
        insertBuilder.save();
    }

    @Override
    public void disassociateTabGroup(List<Long> tabIds, long groupId) throws Exception {

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", StringUtils.join(tabIds, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "webTabGroupId", String.valueOf(groupId), NumberOperators.EQUALS));

        builder.delete();
    }

    @Override
    public void deleteWebTabGroup(long groupId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(groupId, ModuleFactory.getWebTabGroupModule()));
        builder.delete();
    }

    @Override
    public void updateWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContext) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .fields(FieldFactory.getWebTabWebGroupFields())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", String.valueOf(webtabWebgroupContext.getWebTabId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "webTabGroupId", String.valueOf(webtabWebgroupContext.getWebTabGroupId()), NumberOperators.EQUALS));
        builder.update(FieldUtil.getAsProperties(webtabWebgroupContext));
    }

    @Override
    public void addWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContext) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .fields(FieldFactory.getWebTabWebGroupFields());

        Map<String, Object> props = FieldUtil.getAsProperties(webtabWebgroupContext);

        insertBuilder.addRecord(props);
        insertBuilder.save();
    }

    @Override
    public void deleteTabForGroupCommand(long groupId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "tab_groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        builder.delete();
    }

    @Override
    public long addWebTabGroup(WebTabGroupContext tabGroup) throws Exception{
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .fields(FieldFactory.getWebTabGroupFields());
        long id = builder.insert(FieldUtil.getAsMapList(Collections.singletonList(tabGroup), WebTabGroupContext.class).get(0));
        return id;
    }

    @Override
    public void updateWebTabGroup(WebTabGroupContext tabGroup) throws Exception{
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .fields(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(tabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
        builder.update(FieldUtil.getAsProperties(tabGroup));
    }



    private <T,U> List<U> getObjectList(List<T> data) throws Exception {
        if(data != null) {
            List<U> result = new ArrayList<>();
            for(T datum : data) {
                result.add(getCopiedValue(datum));
            }
            return result;
        }
        return null;
    }

    private <T,U> U getCopiedValue(T data) {
        if(data instanceof WebTabContext) {
            return (U) new WebTabCacheContext((WebTabContext) data);
        } else if(data instanceof WebTabGroupContext) {
            return (U) new WebTabGroupCacheContext((WebTabGroupContext) data);
        } else if(data instanceof TabIdAppIdMappingContext) {
            return (U) new TabIdAppIdMappingCacheContext((TabIdAppIdMappingContext) data);
        }
        return null;
    }
}