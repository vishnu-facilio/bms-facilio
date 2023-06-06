package com.facilio.metamigration.beans;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.metamigration.util.MetaMigrationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class MetaMigrationBeanImpl implements MetaMigrationBean {
    @Override
    public List<ApplicationContext> getAllApplications() throws Exception {
        return ApplicationApi.getAllApplicationsWithOutFilter();
    }

    @Override
    public List<ApplicationLayoutContext> getLayoutsForAppId(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .select(FieldFactory.getApplicationLayoutFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS))
                .orderBy("Application_Layout.DEVICE_TYPE asc");

        List<ApplicationLayoutContext> applicationLayouts = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationLayoutContext.class);
        return applicationLayouts;
    }

    @Override
    public List<Long> getModuleIdsWithForms() throws Exception {
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId", "DISTINCT(Forms.MODULEID)", null);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
        selectRecordBuilder.table(ModuleFactory.getFormModule().getTableName())
                .select(Arrays.asList(moduleIdField));

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        List<Long> moduleIds = null;
        if (CollectionUtils.isNotEmpty(propsList)) {
            moduleIds = propsList.stream()
                    .filter(prop -> prop.containsKey("moduleId"))
                    .map(prop -> (Long) prop.get("moduleId")).collect(Collectors.toList());
        }
        return moduleIds;
    }

    @Override
    public void deleteForms(List<Long> formIds) throws Exception {
        FormsAPI.deleteForms(formIds);
    }

    @Override
    public FacilioForm getForm(long formId) throws Exception {
        return FormsAPI.getFormFromDB(formId);
    }

    @Override
    public FacilioForm getForm(String moduleName, String formName) throws Exception {
        return FormsAPI.getFormsFromDB(moduleName, formName);
    }

    @Override
    public List<FacilioForm> getForms(Criteria criteria) throws Exception {
        List<FacilioForm> dbForms = FormsAPI.getFormFromDB(criteria);
        return dbForms;
    }

    @Override
    public long createForm(FacilioForm form, FacilioModule module) throws Exception {
        long newFormId = FormsAPI.createForm(form, module);
        return newFormId;
    }

    @Override
    public List<Long> getFormRuleIds() throws Exception {
        FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
        FacilioField formIdField = FieldFactory.getIdField(formRuleModule);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
        selectRecordBuilder.table(formRuleModule.getTableName())
                .select(Arrays.asList(formIdField));

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        List<Long> formRuleIds = null;
        if (CollectionUtils.isNotEmpty(propsList)) {
            formRuleIds = propsList.stream()
                    .filter(prop -> prop.containsKey("id"))
                    .map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
        }
        return formRuleIds;
    }

    @Override
    public void addFormFields(long formId, FacilioForm form) throws Exception {
        FormsAPI.addFormFields(formId, form);
    }

    @Override
    public int deleteFormFields(List<Long> fieldIds) throws Exception {
        return FormsAPI.deleteFormFields(fieldIds);
    }

    @Override
    public int deleteFormSections(List<Long> sectionIds) throws Exception {
        return FormsAPI.deleteFormSections(sectionIds);
    }

    @Override
    public FormRuleContext getFormRule(long formRuleId) throws Exception {
        return FormRuleAPI.getFormRuleContext(formRuleId);
    }

    @Override
    public long addFormRule(FormRuleContext formRuleContext) throws Exception {
        FacilioChain addFormRuleChain = TransactionChainFactory.getAddFormRuleChain();
        FacilioContext context = addFormRuleChain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRuleContext);
        addFormRuleChain.execute();

        FormRuleContext newFormRule = (FormRuleContext) context.get(FormRuleAPI.FORM_RULE_CONTEXT);
        return newFormRule.getId();
    }

    @Override
    public void deleteFormRule(FormRuleContext formRuleContext) throws Exception {
        FacilioChain deleteFormRuleChain = TransactionChainFactory.getDeleteFormRuleChain();
        Context context = deleteFormRuleChain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
        deleteFormRuleChain.execute();
    }

    @Override
    public Map<String, Object> createModules(List<FacilioModule> oldModules) throws Exception {
        Map<Long, Long> oldVsNewIds = new HashMap<>();
        List<FacilioModule> newModules = new ArrayList<>();

        for (FacilioModule oldCusModule : oldModules) {
            FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();

            FacilioContext context = addModulesChain.getContext();
            context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, oldCusModule.isStateFlowEnabled());
            context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, oldCusModule.getDisplayName());
            context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, oldCusModule.getDescription());
            context.put(FacilioConstants.ContextNames.MODULE_TYPE, oldCusModule.getType());
            context.put(FacilioConstants.ContextNames.MODULE_NAME, oldCusModule.getName());
            context.put(FacilioConstants.ContextNames.FAILURE_REPORTING_ENABLED, false);
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, null);
            context.put(MetaMigrationConstants.USE_LINKNAME_FROM_CONTEXT, true);

            addModulesChain.execute();

            FacilioModule newModule = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
            oldVsNewIds.put(oldCusModule.getModuleId(), newModule.getModuleId());
            newModules.add(newModule);
        }

        Map<String, Object> result = new HashMap<>();
        result.put(MetaMigrationConstants.MODULES, newModules);
        result.put(MetaMigrationConstants.OLD_VS_NEW_IDS, oldVsNewIds);

        return result;
    }

    @Override
    public List<FacilioField> createFields(FacilioModule module, List<FacilioField> newFields) throws Exception {
        FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();

        FacilioContext addFieldsContext = addFieldsChain.getContext();
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE, module);
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, newFields);
//        addFieldsContext.put(FacilioConstants.ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, true);
        addFieldsChain.execute();

        newFields = (List<FacilioField>) addFieldsContext.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
        return newFields;
    }

    @Override
    public WebTabContext getWebTabForAppAndRoute(long appId, String route) throws Exception {
        return ApplicationApi.getWebTabForApplication(appId, route);
    }

    @Override
    public WebTabGroupContext getWebTabGroupForRouteAndLayout(long layoutId, String route) throws Exception {
        if (StringUtils.isEmpty(route)) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", route, StringOperators.IS));

        if (layoutId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layoutId), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", "", CommonOperators.IS_EMPTY));
        }

        Map<String, Object> props = builder.fetchFirst();

        return MapUtils.isNotEmpty(props) ? FieldUtil.getAsBeanFromMap(props, WebTabGroupContext.class) : null;
    }

    @Override
    public WebtabWebgroupContext getWebTabWebGroupForTabId(long webTabId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .select(FieldFactory.getWebTabWebGroupFields())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", String.valueOf(webTabId), NumberOperators.EQUALS));

        Map<String, Object> prop = builder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            WebtabWebgroupContext webtabWebgroupContext = FieldUtil.getAsBeanFromMap(prop, WebtabWebgroupContext.class);
            return webtabWebgroupContext;
        }
        return null;
    }

    @Override
    public void addOrUpdateWebTabGroups(List<WebTabGroupContext> webTabGroups, Map<String, List<WebTabContext>> groupNameVsWebTabsMap) throws Exception {
        for (WebTabGroupContext webTabGroupContext : webTabGroups) {
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
            FacilioContext chainContext = chain.getContext();
            chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
            chainContext.put(MetaMigrationConstants.USE_ORDER_FROM_CONTEXT, true);
            chain.execute();

            webTabGroupContext = (WebTabGroupContext) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);
            long webGroupId = webTabGroupContext.getId();
            webTabGroupContext.setId(webGroupId);

            List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());

            if (CollectionUtils.isNotEmpty(tabs)) {
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();

                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
            }

            if (CollectionUtils.isNotEmpty(tabs)) {
                chain = TransactionChainFactory.getUpdateTabsToGroupChain();
                chainContext = chain.getContext();
                chainContext.put(MetaMigrationConstants.USE_ORDER_FROM_CONTEXT, true);
                chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                chain.execute();
            }
        }
    }
}
