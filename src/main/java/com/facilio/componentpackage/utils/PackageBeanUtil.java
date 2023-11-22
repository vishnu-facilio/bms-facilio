package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetTypeContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.field.validation.date.DateValidatorType;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.fs.FileInfo;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.ns.context.*;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.xml.builder.XMLBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class PackageBeanUtil {
    public static final List<Integer> INCLUDE_MODULE_TYPES = new ArrayList<Integer>() {{
        add(FacilioModule.ModuleType.Q_AND_A.getValue());
        add(FacilioModule.ModuleType.BASE_ENTITY.getValue());
        add(FacilioModule.ModuleType.TRANSACTION.getValue());
        add(FacilioModule.ModuleType.Q_AND_A_RESPONSE.getValue());
    }};

    public static final List<String> SUPPORTED_PICKLIST_MODULES = new ArrayList<String>() {{
        add(FacilioConstants.ContextNames.ASSET_TYPE);
        add(FacilioConstants.ContextNames.TICKET_TYPE);
        add(FacilioConstants.ContextNames.SPACE_CATEGORY);
        add(FacilioConstants.ContextNames.ASSET_CATEGORY);
        add(FacilioConstants.ContextNames.TICKET_PRIORITY);
        add(FacilioConstants.ContextNames.TICKET_CATEGORY);
        add(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
    }};

    public static final List<String> DYNAMIC_CONDITION_VALUES = new ArrayList<String>() {{
        add(FacilioConstants.Criteria.LOGGED_IN_USER);
        add(FacilioConstants.Criteria.LOGGED_IN_PEOPLE);
    }};

//    public static final Map<String, FacilioField> getFieldsForFieldName(String moduleName, List<String> fieldNames) throws Exception {
//
//    }

    public static void addAllPackageChangesetsToThread(long packageId) throws Exception {
        Map<ComponentType, List<PackageChangeSetMappingContext>> allPackageChangesets = PackageUtil.getAllPackageChangsets(packageId);
        for (ComponentType componentType : allPackageChangesets.keySet()) {
            List<PackageChangeSetMappingContext> packageChangeSetMappingContexts = allPackageChangesets.get(componentType);
            if (CollectionUtils.isNotEmpty(packageChangeSetMappingContexts)) {
                Map<String, Long> uIDVsCompId = packageChangeSetMappingContexts.stream().collect(Collectors.toMap(PackageChangeSetMappingContext::getUniqueIdentifier, PackageChangeSetMappingContext::getComponentId));
                PackageUtil.addComponentsUIdVsComponentId(componentType, uIDVsCompId);
            }
        }
    }

    public static Map<String, Long> getAppNameVsAppId() throws Exception {
        List<ApplicationContext> applicationContexts = ApplicationApi.getAllApplicationsWithOutFilter();
        Map<String, Long> appNameVsAppId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applicationContexts)) {
            for (ApplicationContext applicationContext : applicationContexts) {
                if (applicationContext.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
                    continue;
                }
                appNameVsAppId.put(applicationContext.getLinkName(), applicationContext.getId());
            }
        }

        return appNameVsAppId;
    }

    public static Map<String, Long> getStateNameVsId(String moduleName) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        Map<String, Long> stateNameVsId = new HashMap<>();
        List<FacilioStatus> ticketStatusesForModule = TicketAPI.getAllStatus(module, true);
        if (CollectionUtils.isNotEmpty(ticketStatusesForModule)) {
            for (FacilioStatus status : ticketStatusesForModule) {
                stateNameVsId.put(status.getStatus(), status.getId());
            }
        }
        return stateNameVsId;
    }

    public static Map<String, Long> getStateTransitionNameVsId(String moduleName, String stateflowName) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        WorkflowRuleContext stateflow = WorkflowRuleAPI.getWorkflowRule(stateflowName, module, WorkflowRuleContext.RuleType.STATE_FLOW, false);
        long stateflowId = stateflow != null ? stateflow.getId() : -1l;
        Map<String, Long> stateTransitionNameVsId = new HashMap<>();
        List<WorkflowRuleContext> stateTransitionsForStateFlow = StateFlowRulesAPI.getAllStateTransitionList(stateflowId);
        if (CollectionUtils.isNotEmpty(stateTransitionsForStateFlow)) {
            for (WorkflowRuleContext workflowRuleContext : stateTransitionsForStateFlow) {
                stateTransitionNameVsId.put(workflowRuleContext.getName(), workflowRuleContext.getId());
            }
        }
        return stateTransitionNameVsId;
    }

    public static void bulkDeleteV3Records(String moduleName, List<Long> ids) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put(moduleName, ids);

        V3Util.deleteRecords(moduleName, data, null, null, false);
    }

    public static void addPickListConfForXML(String moduleName, String nameFieldName, List<? extends ModuleBaseWithCustomFields> records, Class<?> clazz, boolean isTicketStatus) throws Exception {
        Map<String, String> recordMapToAdd = new HashMap<>();
        List<Map<String, Object>> recordsMap = FieldUtil.getAsMapList(records, clazz);

        if (CollectionUtils.isNotEmpty(recordsMap)) {
            for (Map<String, Object> record : recordsMap) {
                Object recordName = record.getOrDefault(nameFieldName, null);
                if (recordName instanceof String) {
                    String recordId = String.valueOf(record.get("id"));
                    recordMapToAdd.put(recordId, String.valueOf(recordName));
                }
            }
            if (isTicketStatus) {
                PackageUtil.addTicketStatusIdVsNameForModule(moduleName, recordMapToAdd);
            } else {
                PackageUtil.addRecordIdVsNameForPickListModule(moduleName, recordMapToAdd);
            }
        }
    }

    public static void addPickListConfForContext(String moduleName, String nameFieldName, Class<?> clazz) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        List<?> records = PackageBeanUtil.getModuleData(null, module, clazz, false);

        List<Map<String, Object>> recordsMap = FieldUtil.getAsMapList(records, clazz);
        addPickListRecordsToThreadLocal(moduleName, nameFieldName, recordsMap, false);
    }

    public static void addTicketStatusConfForContext(String moduleName, String nameFieldName, Class<?> clazz) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioStatus> records = (List<FacilioStatus>) PackageBeanUtil.getModuleData(null, module, clazz, false);

        List<Map<String, Object>> recordsMap = FieldUtil.getAsMapList(records, clazz);
        Map<String, List<Map<String, Object>>> moduleNameVsRecordsMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(recordsMap)) {
            for (Map<String, Object> record : recordsMap) {
                Object parentModuleId = record.getOrDefault("parentModuleId", null);
                if (parentModuleId instanceof Long) {
                    FacilioModule facilioModule = moduleBean.getModule((Long) parentModuleId);
                    if (facilioModule == null) {
                        continue;
                    }
                    if (!moduleNameVsRecordsMap.containsKey(facilioModule.getName())) {
                        moduleNameVsRecordsMap.put(facilioModule.getName(), new ArrayList<>());
                    }
                    moduleNameVsRecordsMap.get(facilioModule.getName()).add(record);
                }
            }

            for (String modName : moduleNameVsRecordsMap.keySet()) {
                addPickListRecordsToThreadLocal(modName, nameFieldName, moduleNameVsRecordsMap.get(modName), true);
            }
        }
    }

    public static void addPickListRecordsToThreadLocal(String moduleName, String nameFieldName, List<Map<String, Object>> recordsMap, boolean isTicketStatus) {
        Map<String, String> recordMapToAdd = new HashMap<>();
        if (CollectionUtils.isNotEmpty(recordsMap)) {
            for (Map<String, Object> record : recordsMap) {
                Object recordName = record.getOrDefault(nameFieldName, null);
                if (recordName instanceof String) {
                    String recordId = String.valueOf(record.get("id"));
                    recordMapToAdd.put(String.valueOf(recordName), recordId);
                }
            }

            if (isTicketStatus) {
                PackageUtil.addTicketStatusNameVsIdForModule(moduleName, recordMapToAdd);
            } else {
                PackageUtil.addNameVsRecordIdForPickListModule(moduleName, recordMapToAdd);
            }
        }
    }

    public static Map<Long, Map<Integer, Long>> getAllLayoutConfiguration() throws Exception {
        Map<Long, Map<Integer, Long>> appIdVsDeviceIdVsLayoutId = new HashMap<>();
        List<ApplicationLayoutContext> allLayouts = ApplicationApi.getAllLayouts();
        if (CollectionUtils.isEmpty(allLayouts)) {
            return null;
        }
        for (ApplicationLayoutContext applicationLayout : allLayouts) {
            long layoutId = applicationLayout.getId();
            long appId = applicationLayout.getApplicationId();
            int deviceType = applicationLayout.getLayoutDeviceType();
            if (appId < 0 || deviceType < 0) {
                continue;
            }
            if (!appIdVsDeviceIdVsLayoutId.containsKey(appId)) {
                appIdVsDeviceIdVsLayoutId.put(appId, new HashMap<>());
            }
            appIdVsDeviceIdVsLayoutId.get(appId).put(deviceType, layoutId);
        }
        return appIdVsDeviceIdVsLayoutId;
    }

    public static Map<Long, Map<String, Long>> getAllWebTabGroups() throws Exception {
        Map<Long, Map<String, Long>> layoutIdVsRouteVsGroupId = new HashMap<>();
        List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabgroups();
        if (CollectionUtils.isEmpty(webTabGroups)) {
            return null;
        }
        for (WebTabGroupContext webTabGroup : webTabGroups) {
            long groupId = webTabGroup.getId();
            String route = webTabGroup.getRoute();
            long layoutId = webTabGroup.getLayoutId();
            if (layoutId < 0 || StringUtils.isEmpty(route)) {
                continue;
            }
            if (!layoutIdVsRouteVsGroupId.containsKey(layoutId)) {
                layoutIdVsRouteVsGroupId.put(layoutId, new HashMap<>());
            }
            layoutIdVsRouteVsGroupId.get(layoutId).put(route, groupId);
        }
        return layoutIdVsRouteVsGroupId;
    }

    public static Map<Long, Map<String, Long>> getAllWebTabs() throws Exception {
        Map<Long, Map<String, Long>> appIdVsRouteNameVsTabId = new HashMap<>();
        List<WebTabContext> allWebTabs = ApplicationApi.getAllWebTabs();
        if (CollectionUtils.isEmpty(allWebTabs)) {
            return null;
        }
        for (WebTabContext webTab : allWebTabs) {
            long webTabId = webTab.getId();
            String route = webTab.getRoute();
            long applicationId = webTab.getApplicationId();
            if (!appIdVsRouteNameVsTabId.containsKey(applicationId)) {
                appIdVsRouteNameVsTabId.put(applicationId, new HashMap<>());
            }
            appIdVsRouteNameVsTabId.get(applicationId).put(route, webTabId);
        }
        return appIdVsRouteNameVsTabId;
    }

    public static Map<Long, Map<String, Long>> getFormDetailsFromDB() throws Exception {
        Map<Long, Map<String, Long>> moduleIdVsFormNameVsFormId = new HashMap<>();
        List<FacilioForm> allDBForms = FormsAPI.getAllDBForms();
        if (CollectionUtils.isNotEmpty(allDBForms)) {
            moduleIdVsFormNameVsFormId = getModuleIdVsFormNameVsFormId(allDBForms);
        }
        return moduleIdVsFormNameVsFormId;
    }

    public static Map<Long, Map<String, Long>> getFormDetailsFromPackage() throws Exception {
        Map<String, Long> formsUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.FORM);
        Map<Long, Map<String, Long>> moduleIdVsFormNameVsFormId = new HashMap<>();
        if (MapUtils.isNotEmpty(formsUIdVsIdsFromPackage)) {
            Criteria formIdCriteria = new Criteria();
            formIdCriteria.addAndCondition(CriteriaAPI.getIdCondition(formsUIdVsIdsFromPackage.values(), ModuleFactory.getFormModule()));
            List<FacilioForm> allForms = FormsAPI.getDBFormList(null, formIdCriteria, null, false, false, true, -1L, true);

            moduleIdVsFormNameVsFormId = getModuleIdVsFormNameVsFormId(allForms);
        }
        return moduleIdVsFormNameVsFormId;
    }

    public static Map<Long, Map<String, Long>> getModuleIdVsFormNameVsFormId(List<FacilioForm> allForms) throws Exception {
        if (CollectionUtils.isEmpty(allForms)) {
            return null;
        }

        Map<Long, Map<String, Long>> moduleIdVsFormNameVsFormId = new HashMap<>();

        for (FacilioForm form : allForms) {
            long formId = form.getId();
            String formName = form.getName();
            long moduleId = form.getModuleId();

            if (moduleId < 0 || StringUtils.isEmpty(formName)) {
                continue;
            }
            if (!moduleIdVsFormNameVsFormId.containsKey(moduleId)) {
                moduleIdVsFormNameVsFormId.put(moduleId, new HashMap<>());
            }
            moduleIdVsFormNameVsFormId.get(moduleId).put(formName, formId);
        }
        return moduleIdVsFormNameVsFormId;
    }

    public static Map<Long, Map<String, Long>> getFormIdVsSectionNameVsSectionId(Collection<Long> formIds) throws Exception {
        Map<Long, Map<String, Long>> formIdVsSectionNameVsSectionId = new HashMap<>();
        List<FormSection> sectionsForFormIds = FormsAPI.getAllSectionsForFormIds(formIds);

        if (CollectionUtils.isEmpty(sectionsForFormIds)) {
            return null;
        }
        for (FormSection section : sectionsForFormIds) {
            String name = section.getName();
            long sectionId = section.getId();
            long formId = section.getFormId();

            if (StringUtils.isEmpty(name) || formId < 0) {
                continue;
            }
            if (!formIdVsSectionNameVsSectionId.containsKey(formId)) {
                formIdVsSectionNameVsSectionId.put(formId, new HashMap<>());
            }
            formIdVsSectionNameVsSectionId.get(formId).put(name, sectionId);
        }
        return formIdVsSectionNameVsSectionId;
    }

    public static Map<Long, Map<Long, Map<String, Long>>> getFormIdVsSectionIdVsFieldNameVsFieldId(Collection<Long> formIds) throws Exception {
        Map<Long, Map<Long, Map<String, Long>>> formIdVsSectionIdVsFieldNameVsFieldId = new HashMap<>();
        List<FormField> formFieldsForFormIds = FormsAPI.getAllFormFieldsForFormIds(formIds);

        if (CollectionUtils.isEmpty(formFieldsForFormIds)) {
            return null;
        }

        // TODO - Change DisplayName to LinkName
        for (FormField formField : formFieldsForFormIds) {
            long fieldId = formField.getId();
            long formId = formField.getFormId();
            long sectionId = formField.getSectionId();
            String displayName = formField.getDisplayName();

            if (formId < 0 || sectionId < 0 || StringUtils.isEmpty(displayName)) {
                continue;
            }
            if (!formIdVsSectionIdVsFieldNameVsFieldId.containsKey(formId)) {
                formIdVsSectionIdVsFieldNameVsFieldId.put(formId, new HashMap<>());
            }
            if (!formIdVsSectionIdVsFieldNameVsFieldId.get(formId).containsKey(sectionId)) {
                formIdVsSectionIdVsFieldNameVsFieldId.get(formId).put(sectionId, new HashMap<>());
            }
            formIdVsSectionIdVsFieldNameVsFieldId.get(formId).get(sectionId).put(displayName, fieldId);
        }
        return formIdVsSectionIdVsFieldNameVsFieldId;
    }

    public static FacilioForm getFormFromId(long formId) throws Exception {
        FacilioModule formModule = ModuleFactory.getFormModule();
        List<FacilioField> formFields = FieldFactory.getFormFields();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(formModule.getTableName())
                .select(formFields)
                .andCondition(CriteriaAPI.getIdCondition(formId, formModule));

        Map<String, Object> prop = builder.fetchFirst();

        return MapUtils.isNotEmpty(prop) ? FieldUtil.getAsBeanFromMap(prop, FacilioForm.class) : null;
    }

    public static long getFormIdFromName(String formName, long moduleId) throws Exception {
        Map<String, Long> formIdsFromName = getFormIdsFromNames(Collections.singletonList(formName), moduleId);
        return MapUtils.isNotEmpty(formIdsFromName) ? formIdsFromName.get(formName) : -1;
    }

    public static Map<String, Long> getFormIdsFromNames(List<String> formNames, long moduleId) throws Exception {
        FacilioModule formModule = ModuleFactory.getFormModule();
        List<FacilioField> formFields = FieldFactory.getFormFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(formFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(formModule.getTableName())
                .select(formFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(formNames, ","), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), Collections.singleton(moduleId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.get();

        Map<String, Long> formNameVsId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                formNameVsId.put((String) prop.get("name"), (Long) prop.get("id"));
            }
        }

        return formNameVsId;
    }

    public static FormSection getSectionFromId(long sectionId) throws Exception {
        FacilioModule sectionModule = ModuleFactory.getFormSectionModule();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(sectionModule.getTableName())
                .select(FieldFactory.getFormSectionFields())
                .andCondition(CriteriaAPI.getIdCondition(sectionId, sectionModule));

        Map<String, Object> prop = builder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? FieldUtil.getAsBeanFromMap(prop, FormSection.class) : null;
    }

    public static long getSectionIdFromName(long formId, String sectionName) throws Exception {
        FacilioModule sectionModule = ModuleFactory.getFormSectionModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormSectionFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(sectionModule.getTableName())
                .select(Collections.singleton(FieldFactory.getIdField(sectionModule)))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(sectionName)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), sectionName, StringOperators.IS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), "NULL", CommonOperators.IS_EMPTY));
        }

        Map<String, Object> prop = builder.fetchFirst();

        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;
    }

    public static long getFormFieldId(FormField formField) throws Exception {
        GenericSelectRecordBuilder fieldSelectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormFieldsModule().getTableName())
                .select(FieldFactory.getFormFieldsFields());

        if (formField.getFormId() > 0) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(formField.getFormId()), NumberOperators.EQUALS));
        }
        if (formField.getSectionId() > 0) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("SECTIONID", "sectionId", String.valueOf(formField.getSectionId()), NumberOperators.EQUALS));
        }
        if (formField.getFieldId() > 0) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(formField.getFieldId()), NumberOperators.EQUALS));
        }
        if (StringUtils.isNotEmpty(formField.getName())) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", formField.getName(), StringOperators.IS));
        }
        if (StringUtils.isNotEmpty(formField.getDisplayName())) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("DISPLAYNAME", "displayName", formField.getDisplayName(), StringOperators.CONTAINS));
        }

        Map<String, Object> prop = fieldSelectBuilder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;
    }

    public static Criteria getFormAppIdCriteria(List<Long> applicationIds) {
        Criteria appIdCriteria = new Criteria();
        appIdCriteria.addAndCondition(CriteriaAPI.getCondition("APP_ID", "appId", StringUtils.join(applicationIds, ","), NumberOperators.EQUALS));
        appIdCriteria.addOrCondition(CriteriaAPI.getCondition("APP_ID", "appId", "NULL", CommonOperators.IS_EMPTY));
        return appIdCriteria;
    }

    public static XMLBuilder constructBuilderFromCriteria(Criteria criteria, XMLBuilder xmlBuilder, String moduleName) throws Exception {
        if (criteria == null || criteria.isEmpty()) {
            return xmlBuilder;
        }

        ModuleBean moduleBean = Constants.getModBean();
        xmlBuilder.element(PackageConstants.CriteriaConstants.PATTERN).text(criteria.getPattern());

        Map<String, Condition> criteriaConditions = criteria.getConditions();
        if (MapUtils.isNotEmpty(criteriaConditions)) {
            XMLBuilder conditionsList = xmlBuilder.element(PackageConstants.CriteriaConstants.CONDITIONS_LIST);
            for (String sequence : criteriaConditions.keySet()) {
                XMLBuilder conditionElement = conditionsList.element(PackageConstants.CriteriaConstants.CONDITION);

                Condition condition = criteriaConditions.get(sequence);
                conditionElement.element(PackageConstants.CriteriaConstants.SEQUENCE).text(sequence);
                conditionElement.element(PackageConstants.CriteriaConstants.VALUE).text(condition.getValue());
                conditionElement.element(PackageConstants.CriteriaConstants.FIELD_NAME).text(condition.getFieldName());
                conditionElement.element(PackageConstants.CriteriaConstants.COLUMN_NAME).text(condition.getColumnName());
                conditionElement.element(PackageConstants.CriteriaConstants.JSON_VALUE).text(condition.getJsonValueStr());
                conditionElement.element(PackageConstants.CriteriaConstants.OPERATOR).text(String.valueOf(condition.getOperatorId()));
                conditionElement.element(PackageConstants.CriteriaConstants.IS_EXPRESSION_VALUE).text(String.valueOf(condition.isExpressionValue()));

                if (condition.getCriteriaValueId() > 0) {
                    conditionElement.element(PackageConstants.CriteriaConstants.CRITERIA_VALUE)
                            .addElement(constructBuilderFromCriteria(condition.getCriteriaValue(), conditionElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                }

                if (StringUtils.isNotEmpty(condition.getValue()) && (condition.getOperatorId() == 36 || condition.getOperatorId() == 37)) {
                    String[] valueArr = condition.getValue().trim().split(FacilioUtil.COMMA_SPLIT_REGEX);

                    String fieldName = condition.getFieldName();
                    moduleName = fieldName.contains(".") ? fieldName.split("\\.")[0] : moduleName;

                    FacilioModule facilioModule = moduleBean.getModule(moduleName);
                    FacilioField field = moduleBean.getField(fieldName, moduleName);

                    if (field instanceof LookupField) {
                        FacilioModule lookupModule = ((LookupField) field).getLookupModule();
                        if (lookupModule.getName().equals(FacilioConstants.ContextNames.TICKET_STATUS)) {
                            conditionElement.addElement(pickListXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.PICKLIST_ELEMENT), facilioModule.getName(), valueArr, true));
                        } else if (SUPPORTED_PICKLIST_MODULES.contains(lookupModule.getName())) {
                            conditionElement.addElement(pickListXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.PICKLIST_ELEMENT), lookupModule.getName(), valueArr, false));
                        } else if (FacilioConstants.ContextNames.USERS.equals(lookupModule.getName())) {
                            XMLBuilder userElementList = conditionElement.element(PackageConstants.CriteriaConstants.USER_ELEMENT_LIST);
                            for (String val : valueArr) {
                                if (DYNAMIC_CONDITION_VALUES.contains(val)) {
                                    XMLBuilder dynamicValueElement = userElementList.element(PackageConstants.CriteriaConstants.USER_ELEMENT);
                                    dynamicValueElement.element(PackageConstants.CriteriaConstants.DYNAMIC_VALUE).text(val);
                                    dynamicValueElement.element(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).text(Boolean.TRUE.toString());
                                } else {
                                    XMLBuilder userElement = userXMLBuilder(userElementList.element(PackageConstants.CriteriaConstants.USER_ELEMENT), Long.parseLong(val));
                                    userElement.element(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).text(Boolean.FALSE.toString());
                                    userElementList.addElement(userElement);
                                }
                            }
                        } else if (FacilioConstants.ContextNames.PEOPLE.equals(lookupModule.getName())) {
                            XMLBuilder peopleElementList = conditionElement.element(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT_LIST);
                            for (String val : valueArr) {
                                if (DYNAMIC_CONDITION_VALUES.contains(val)) {
                                    XMLBuilder dynamicValueElement = peopleElementList.element(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT);
                                    dynamicValueElement.element(PackageConstants.CriteriaConstants.DYNAMIC_VALUE).text(val);
                                    dynamicValueElement.element(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).text(Boolean.TRUE.toString());
                                } else {
                                    XMLBuilder peopleElement = peopleXMLBuilder(peopleElementList.element(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT), Long.parseLong(val));
                                    peopleElement.element(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).text(Boolean.FALSE.toString());
                                    peopleElementList.addElement(peopleElement);
                                }
                            }
                        } else {
                            LOGGER.info("####Sandbox Tracking - Condition contains Module Record - " + moduleName + " FieldName - " + fieldName + " ConditionId - " + condition.getConditionId());
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(moduleName)) {
            xmlBuilder.element(PackageConstants.CriteriaConstants.CRITERIA_MODULENAME).text(moduleName);
        }
        return xmlBuilder;
    }

    public static Criteria constructCriteriaFromBuilder(XMLBuilder xmlBuilder) throws Exception {
        if (xmlBuilder.getElement(PackageConstants.CriteriaConstants.PATTERN) == null) {
            return null;
        }
        String pattern = xmlBuilder.getElement(PackageConstants.CriteriaConstants.PATTERN).getText();
        XMLBuilder conditionsListElement = xmlBuilder.getElement(PackageConstants.CriteriaConstants.CONDITIONS_LIST);

        String criteriaModuleName = null;
        if (xmlBuilder.getElement(PackageConstants.CriteriaConstants.CRITERIA_MODULENAME) != null) {
            criteriaModuleName = xmlBuilder.getElement(PackageConstants.CriteriaConstants.CRITERIA_MODULENAME).getText();
        }

        Criteria newCriteria = new Criteria();
        newCriteria.setPattern(pattern);

        if (conditionsListElement != null) {
            List<XMLBuilder> conditionsList = conditionsListElement.getFirstLevelElementListForTagName(PackageConstants.CriteriaConstants.CONDITION);
            Map<String, Condition> newCriteriaConditions = new LinkedHashMap<>();
            for (XMLBuilder conditionElement : conditionsList) {
                String sequence = conditionElement.getElement(PackageConstants.CriteriaConstants.SEQUENCE).getText();
                String value = conditionElement.getElement(PackageConstants.CriteriaConstants.VALUE).getText();
                String fieldName = conditionElement.getElement(PackageConstants.CriteriaConstants.FIELD_NAME).getText();
                String columnName = conditionElement.getElement(PackageConstants.CriteriaConstants.COLUMN_NAME).getText();
                String jsonValue = conditionElement.getElement(PackageConstants.CriteriaConstants.JSON_VALUE).getText();
                int operatorId = Integer.parseInt(conditionElement.getElement(PackageConstants.CriteriaConstants.OPERATOR).getText());
                boolean isExpressionValue = Boolean.parseBoolean(conditionElement.getElement(PackageConstants.CriteriaConstants.IS_EXPRESSION_VALUE).getText());

                // User (or) PickList values
                if (operatorId == 36 || operatorId == 37) {
                    XMLBuilder userElement = conditionElement.getElement(PackageConstants.CriteriaConstants.USER_ELEMENT_LIST);
                    XMLBuilder peopleElement = conditionElement.getElement(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT_LIST);
                    XMLBuilder pickListElement = conditionElement.getElement(PackageConstants.CriteriaConstants.PICKLIST_ELEMENT);

                    if (pickListElement != null) {
                        String moduleName = pickListElement.getElement(PackageConstants.MODULENAME).getText();

                        if (StringUtils.isNotEmpty(moduleName) && (SUPPORTED_PICKLIST_MODULES.contains(moduleName) || moduleName.equals(FacilioConstants.ContextNames.TICKET_STATUS))) {
                            value = pickListValueBuilder(pickListElement);
                        }
                    } else if (userElement != null) {
                        List<XMLBuilder> userElementList = conditionElement.getElementList(PackageConstants.CriteriaConstants.USER_ELEMENT);

                        Set<String> valueArr = new HashSet<>();
                        for (XMLBuilder valueElement : userElementList) {
                            boolean isDynamicValue = Boolean.parseBoolean(valueElement.getElement(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).getText());
                            String currValue = null;
                            if (isDynamicValue) {
                                currValue = valueElement.getElement(PackageConstants.CriteriaConstants.DYNAMIC_VALUE).getText();
                            } else {
                                long orgUserId = userValueBuilder(valueElement);
                                currValue = orgUserId > 0 ? String.valueOf(orgUserId) : null;
                            }
                            if (StringUtils.isNotEmpty(currValue)) {
                                valueArr.add(currValue);
                            }
                        }
                        value = StringUtils.join(valueArr, ",");
                    } else if (peopleElement != null) {
                        List<XMLBuilder> peopleElementList = conditionElement.getElementList(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT);

                        Set<String> valueArr = new HashSet<>();
                        for (XMLBuilder valueElement : peopleElementList) {
                            boolean isDynamicValue = Boolean.parseBoolean(valueElement.getElement(PackageConstants.CriteriaConstants.IS_DYNAMIC_VALUE).getText());
                            String currValue = null;
                            if (isDynamicValue) {
                                currValue = valueElement.getElement(PackageConstants.CriteriaConstants.DYNAMIC_VALUE).getText();
                            } else {
                                long peopleId = peopleValueBuilder(valueElement);
                                currValue = peopleId > 0 ? String.valueOf(peopleId) : null;
                            }
                            if (StringUtils.isNotEmpty(currValue)) {
                                valueArr.add(currValue);
                            }
                        }
                        value = StringUtils.join(valueArr, ",");
                    }

                    if (StringUtils.isEmpty(value)) {
                        value = conditionElement.getElement(PackageConstants.CriteriaConstants.VALUE).getText();
                    }
                }

                // For MultiLookup, Currency fields (eg. moduleName.fieldName)
                if (fieldName.contains(".")) {
                    String[] fieldNameSplit = fieldName.split("\\.");
                    fieldName = fieldNameSplit[1];
                }

                Condition condition = new Condition();
                condition.setValue(value);
                condition.setFieldName(fieldName);
                condition.setColumnName(columnName);
                condition.setOperatorId(operatorId);
                condition.setJsonValueStr(jsonValue);
                condition.setModuleName(criteriaModuleName);
                condition.setIsExpressionValue(isExpressionValue);
                condition.setSequence(Integer.parseInt(sequence));

                XMLBuilder criteriaValueElement = conditionElement.getElement(PackageConstants.CriteriaConstants.CRITERIA_VALUE);
                if (criteriaValueElement != null) {
                    condition.setCriteriaValue(constructCriteriaFromBuilder(criteriaValueElement));
                }

                newCriteriaConditions.put(sequence, condition);
            }
            newCriteria.setConditions(newCriteriaConditions);
        }
        return newCriteria;
    }

    public static XMLBuilder pickListXMLBuilder(XMLBuilder xmlBuilder, String moduleName, String[] valueArr, boolean isTicketStatus) throws Exception {
        if (StringUtils.isEmpty(moduleName)) {
            return xmlBuilder;
        }

        Map<String, String> recordIdVsNameForModule = null;
        if (isTicketStatus) {
            recordIdVsNameForModule = PackageUtil.getTicketStatusIdVsNameForModule(moduleName);
            xmlBuilder.element(PackageConstants.CriteriaConstants.PARENT_MODULE_NAME).text(moduleName);
            xmlBuilder.element(PackageConstants.MODULENAME).text(FacilioConstants.ContextNames.TICKET_STATUS);
        } else {
            xmlBuilder.element(PackageConstants.MODULENAME).text(moduleName);
            recordIdVsNameForModule = PackageUtil.getRecordIdVsNameForPicklistModule(moduleName);
        }

        if (MapUtils.isNotEmpty(recordIdVsNameForModule)) {
            for (String val : valueArr) {
                if (recordIdVsNameForModule.containsKey(val)) {
                    xmlBuilder.element(PackageConstants.CriteriaConstants.PICKLIST_VALUE).text(recordIdVsNameForModule.get(val));
                }
            }
        }

        return xmlBuilder;
    }

    public static String pickListValueBuilder(XMLBuilder xmlBuilder) {
        String value = null;
        String moduleName = xmlBuilder.getElement(PackageConstants.MODULENAME).getText();
        List<XMLBuilder> valueElementList = xmlBuilder.getElementList(PackageConstants.CriteriaConstants.PICKLIST_VALUE);

        if (StringUtils.isEmpty(moduleName) || CollectionUtils.isEmpty(valueElementList)) {
            return null;
        }

        List<String> valueArr = new ArrayList<>();
        Map<String, String> recordNameVsIdForModule = null;
        if (SUPPORTED_PICKLIST_MODULES.contains(moduleName)) {
            recordNameVsIdForModule = PackageUtil.getNameVsRecordIdForPicklistModule(moduleName);
        } else if (moduleName.equals(FacilioConstants.ContextNames.TICKET_STATUS)) {
            String parentModuleName = xmlBuilder.getElement(PackageConstants.CriteriaConstants.PARENT_MODULE_NAME).getText();
            recordNameVsIdForModule = PackageUtil.getTicketStatusNameVsIdForModule(parentModuleName);
        }

        if (MapUtils.isNotEmpty(recordNameVsIdForModule)) {
            for (XMLBuilder valueElement : valueElementList) {
                String val = valueElement.getText();
                if (StringUtils.isNotEmpty(val) && recordNameVsIdForModule.containsKey(val)) {
                    valueArr.add(recordNameVsIdForModule.get(val));
                }
            }
            return StringUtils.join(valueArr, ",");
        }

        return null;
    }

    public static XMLBuilder userXMLBuilder(XMLBuilder userElement, long orgUserId) throws Exception {
        UserInfo userInfo = PackageUtil.getUserInfo(orgUserId);
        if (userInfo != null) {
            userElement.element(PackageConstants.UserConstants.USER_NAME).text(userInfo.getUserName());
            userElement.element(PackageConstants.UserConstants.IDENTIFIER).text(userInfo.getIdentifier());
        } else {
            LOGGER.info("####Sandbox Tracking - User details not found for OrgUserId - " + orgUserId);
        }
        return userElement;
    }

    public static long userValueBuilder(XMLBuilder userElement) {
        if (userElement.getElement(PackageConstants.UserConstants.USER_NAME) == null ||
                userElement.getElement(PackageConstants.UserConstants.IDENTIFIER) == null) {
            try {
                return AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId()).getOuid();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String userName = userElement.getElement(PackageConstants.UserConstants.USER_NAME).getText();
        String identifier = userElement.getElement(PackageConstants.UserConstants.IDENTIFIER).getText();

        long orgUserId = (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(identifier))
                ? PackageUtil.getOrgUserId(userName, identifier) : -1;

        return orgUserId;
    }

    public static XMLBuilder peopleXMLBuilder(XMLBuilder peopleElement, long peopleId) throws Exception {
        String peopleMail = PackageUtil.getPeopleMail(peopleId);
        if (StringUtils.isNotEmpty(peopleMail)) {
            peopleElement.element(PackageConstants.UserConstants.EMAIL).text(peopleMail);
        } else {
            LOGGER.info("####Sandbox Tracking - People details not found for PeopleId - " + peopleId);
        }

        return peopleElement;
    }

    public static long peopleValueBuilder(XMLBuilder peopleElement) {
        if (peopleElement.getElement(PackageConstants.UserConstants.EMAIL) == null) {
            try {
                User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
                V3PeopleContext people = V3PeopleAPI.getPeople(superAdminUser.getEmail());
                return people != null ? people.getId() : -1L;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String peopleMail = peopleElement.getElement(PackageConstants.UserConstants.EMAIL).getText();
        long peopleMailId = StringUtils.isNotEmpty(peopleMail) ? PackageUtil.getPeopleId(peopleMail) : -1;
        return peopleMailId;
    }

    public static XMLBuilder constructBuilderFromWorkFlowContext(WorkflowContext workflowContext, XMLBuilder workFlowBuilder) throws Exception {
        if (workflowContext == null) {
            return workFlowBuilder;
        }

        workFlowBuilder.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(workflowContext.isV2Script()));
        workFlowBuilder.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(workflowContext.getWorkflowV2String());
        workFlowBuilder.element(PackageConstants.FunctionConstants.UI_MODE).t(String.valueOf(workflowContext.getWorkflowUIMode()));

        return workFlowBuilder;
    }

    public static WorkflowContext constructWorkflowContextFromBuilder(XMLBuilder workFlowBuilder) {
        if (workFlowBuilder == null) {
            return null;
        }

        String v2WorkFlowStr = workFlowBuilder.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
        boolean isV2Script = Boolean.parseBoolean(workFlowBuilder.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
        int wfUIMode = Integer.parseInt(workFlowBuilder.getElement(PackageConstants.FunctionConstants.UI_MODE).getText());

        WorkflowContext workflowContext = new WorkflowContext();
        workflowContext.setWorkflowV2String(v2WorkFlowStr);
        workflowContext.setIsV2Script(isV2Script);
        workflowContext.setWorkflowUIMode(wfUIMode);
        return workflowContext;
    }

    public static List<Role> getAllRoles() throws Exception {
        FacilioModule module = AccountConstants.getRoleModule();
        List<FacilioField> fields = AccountConstants.getRoleFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName());

        List<Map<String, Object>> props = selectBuilder.get();
        return FieldUtil.getAsBeanListFromMapList(props, Role.class);
    }

    public static <T extends SingleSharingContext> void constructBuilderFromSharingContext(SharingContext<T> sharingContext, XMLBuilder sharingElement) throws Exception {
        if (CollectionUtils.isEmpty(sharingContext)) {
            return;
        }

        List<Role> allRoles = getAllRoles();
        Map<Long, String> roleIdVsRoleName = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allRoles)) {
            roleIdVsRoleName = allRoles.stream().collect(Collectors.toMap(Role::getRoleId, Role::getName));
        }

        ModuleBean modBean = Constants.getModBean();

        XMLBuilder sharingContextElement = sharingElement.element(PackageConstants.SharingContextConstants.SHARING_CONTEXT);
        for (SingleSharingContext singleSharingContext : sharingContext) {
            XMLBuilder singleSharingElement = sharingContextElement.element(PackageConstants.SharingContextConstants.SINGLE_SHARING_CONTEXT);
            singleSharingElement.element(PackageConstants.SharingContextConstants.SHARING_TYPE).text(singleSharingContext.getTypeEnum().name());
            switch (singleSharingContext.getTypeEnum()) {
                case USER:
                    singleSharingElement.addElement(userXMLBuilder(singleSharingElement.element(PackageConstants.CriteriaConstants.USER_ELEMENT), singleSharingContext.getUserId()));
                    break;

                case ROLE:
                    singleSharingElement.element(PackageConstants.SharingContextConstants.ROLE_NAME).text(roleIdVsRoleName.get(singleSharingContext.getRoleId()));
                    break;

                case GROUP:
                    long groupId = singleSharingContext.getGroupId();
                    Group group = AccountUtil.getGroupBean().getGroup(groupId);
                    if (group != null) {
                        singleSharingElement.element(PackageConstants.GroupConstants.GROUP_NAME).text(group.getName());
                    }
                    break;

                case FIELD:
                case TENANT:
                case VENDOR:
                    long fieldId = singleSharingContext.getFieldId();
                    FacilioField field = modBean.getField(fieldId);
                    if (field != null) {
                        singleSharingElement.element(PackageConstants.FIELDNAME).text(field.getName());
                        singleSharingElement.element(PackageConstants.FIELD_MODULE_NAME).text(field.getModule().getName());
                    }
                    break;

                case APP:
                    int appType = singleSharingContext.getAppType();
                    singleSharingElement.element(PackageConstants.AppXMLConstants.APP_TYPE).text(String.valueOf(appType));
                    break;

                default:
                    break;
            }
        }
    }

    public static <T extends SingleSharingContext> SharingContext<T> constructSharingContextFromBuilder(XMLBuilder sharingElement, Class<? extends T> clazz) throws Exception {
        XMLBuilder sharingContextElement = sharingElement.getElement(PackageConstants.SharingContextConstants.SHARING_CONTEXT);
        if (sharingContextElement == null) {
            return null;
        }

        List<Role> allRoles = getAllRoles();
        Map<String, Long> roleNameVsRoleId = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allRoles)) {
            roleNameVsRoleId = allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId, (a, b) -> b));
        }

        ModuleBean modBean = Constants.getModBean();
        SharingContext<T> sharingContexts = new SharingContext<>();

        List<XMLBuilder> singleSharingElementsList = sharingContextElement.getElementList(PackageConstants.SharingContextConstants.SINGLE_SHARING_CONTEXT);
        for (XMLBuilder singleSharingElement : singleSharingElementsList) {
            String sharingTypeStr = singleSharingElement.getElement(PackageConstants.SharingContextConstants.SHARING_TYPE).text();
            SingleSharingContext.SharingType sharingType = SingleSharingContext.SharingType.valueOf(sharingTypeStr);

            T singleSharingContext = clazz.getDeclaredConstructor().newInstance();
            singleSharingContext.setType(sharingType);

            switch (sharingType) {
                case USER:
                    XMLBuilder userElement = singleSharingElement.getElement(PackageConstants.CriteriaConstants.USER_ELEMENT);
                    long orgUserId = userValueBuilder(userElement);
                    singleSharingContext.setUserId(orgUserId);
                    break;

                case ROLE:
                    String roleName = singleSharingElement.getElement(PackageConstants.SharingContextConstants.ROLE_NAME).text();
                    long roleId = roleNameVsRoleId.containsKey(roleName) ? roleNameVsRoleId.get(roleName) : -1;
                    singleSharingContext.setRoleId(roleId);
                    break;

                case GROUP:
                    if (singleSharingElement.getElement(PackageConstants.GroupConstants.GROUP_NAME) == null) {
                        continue;
                    }
                    String groupName = singleSharingElement.getElement(PackageConstants.GroupConstants.GROUP_NAME).getText();

                    Group group = AccountUtil.getGroupBean().getGroup(groupName);
                    singleSharingContext.setGroupId(group != null ? group.getId() : -1);
                    break;

                case FIELD:
                case TENANT:
                case VENDOR:
                    if (singleSharingElement.getElement(PackageConstants.FIELDNAME) == null || singleSharingElement.getElement(PackageConstants.FIELD_MODULE_NAME) == null) {
                        continue;
                    }
                    String fieldName = singleSharingElement.getElement(PackageConstants.FIELDNAME).getText();
                    String moduleName = singleSharingElement.getElement(PackageConstants.FIELD_MODULE_NAME).getText();

                    FacilioField field = modBean.getField(fieldName, moduleName);
                    singleSharingContext.setFieldId(field != null ? field.getFieldId() : -1);
                    break;

                case APP:
                    int appType = Integer.parseInt(singleSharingElement.getElement(PackageConstants.AppXMLConstants.APP_TYPE).getText());
                    singleSharingContext.setAppType(appType);
                    break;

                default:
                    break;
            }

            sharingContexts.add(singleSharingContext);
        }

        return sharingContexts;
    }

    public static void constructBuilderFromConfirmationDialogList(List<ConfirmationDialogContext> confirmationDialogList, XMLBuilder confirmationBuilder) throws Exception {
        if (CollectionUtils.isEmpty(confirmationDialogList)) {
            return;
        }

        for (ConfirmationDialogContext confirmationDialog : confirmationDialogList) {
            XMLBuilder builder = confirmationBuilder.element("confirmationDialog");
            builder.element("name").text(confirmationDialog.getName());
            builder.element("message").text(confirmationDialog.getMessage());
            if (confirmationDialog.getMessagePlaceHolderScript() != null) {
                XMLBuilder messagePlaceHolder = confirmationBuilder.element("messagePlaceHolderScript");
                messagePlaceHolder.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(confirmationDialog.getMessagePlaceHolderScript().isV2Script()));
                messagePlaceHolder.element("workflowV2String").cData(confirmationDialog.getMessagePlaceHolderScript().getWorkflowV2String());
            }
            long namedCriteriaId = confirmationDialog.getNamedCriteriaId();
            if (namedCriteriaId > 0) {
                NamedCriteria namedCriteria = NamedCriteriaAPI.getNamedCriteria(namedCriteriaId);
                builder.element("namedCriteriaName").text(namedCriteria.getName());
            }
        }
    }

    public static void constructBuilderFromValidationList(List<ValidationContext> validationList, XMLBuilder validationBuilder) throws Exception {
        if (CollectionUtils.isEmpty(validationList)) {
            return;
        }
        for (ValidationContext validation : validationList) {
            XMLBuilder builder = validationBuilder.element("validation");
            builder.element("errorMessage").text(validation.getErrorMessage());
            builder.element("name").text(validation.getName());
            if (validation.getErrorMessagePlaceHolderScript() != null) {
                XMLBuilder errorMessagePlaceHolder = validationBuilder.element("errorMessagePlaceHolderScript");
                errorMessagePlaceHolder.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(validation.getErrorMessagePlaceHolderScript().isV2Script()));
                errorMessagePlaceHolder.element("workflowV2String").cData(validation.getErrorMessagePlaceHolderScript().getWorkflowV2String());
            }
            builder.element("namedCriteriaName").text(validation.getNamedCriteria().getName());
        }
    }

    public static List<ValidationContext> constructValidationContextFromBuilder(XMLBuilder validationBuilder) throws Exception {

        List<ValidationContext> validationList = new ArrayList<>();
        List<XMLBuilder> validationBuilderList = validationBuilder.getElementList("validation");
        for (XMLBuilder validation : validationBuilderList) {
            ValidationContext validationContext = new ValidationContext();
            String errorMessage = validation.getElement("errorMessage").getText();
            String name = validation.getElement("name").getText();
            if (name != null) {
                validationContext.setName(name);
            }
            validationContext.setErrorMessage(errorMessage);
            String nameCriteriaName = validation.getElement("namedCriteriaName").text();
            NamedCriteria namedCriteria = NamedCriteriaAPI.getNamedCriteria(nameCriteriaName);
            validationContext.setNamedCriteria(namedCriteria);
            validationContext.setNamedCriteriaId(namedCriteria.getId());
            XMLBuilder placeHoldeScript = validation.getElement("errorMessagePlaceHolderScript");
            if (placeHoldeScript != null) {
                WorkflowContext workflowContext = new WorkflowContext();
                boolean isV2Script = Boolean.parseBoolean(validation.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
                String workflowV2String = validation.getElement("workflowV2String").getCData();
                workflowContext.setIsV2Script(isV2Script);
                workflowContext.setWorkflowV2String(workflowV2String);
                validationContext.setErrorMessagePlaceHolderScript(workflowContext);
            }
            validationList.add(validationContext);
        }
        return validationList;
    }

    public static List<ConfirmationDialogContext> constructConfirmationDialogFromBuilder(XMLBuilder confirmationBuilder) throws Exception {

        List<ConfirmationDialogContext> confirmationList = new ArrayList<>();
        List<XMLBuilder> confirmationBuilderList = confirmationBuilder.getElementList("validation");
        for (XMLBuilder confirmation : confirmationBuilderList) {
            ConfirmationDialogContext confirmationDialogContext = new ConfirmationDialogContext();
            String message = confirmation.getElement("message").getText();
            String name = confirmation.getElement("name").getText();
            if (name != null) {
                confirmationDialogContext.setName(name);
            }
            confirmationDialogContext.setMessage(message);
            String nameCriteriaName = confirmation.getElement("namedCriteriaName").text();
            NamedCriteria namedCriteria = NamedCriteriaAPI.getNamedCriteria(nameCriteriaName);
            confirmationDialogContext.setNamedCriteria(namedCriteria);
            confirmationDialogContext.setNamedCriteriaId(namedCriteria.getId());
            XMLBuilder placeHoldeScript = confirmation.getElement("messagePlaceHolderScript");
            if (placeHoldeScript != null) {
                WorkflowContext workflowContext = new WorkflowContext();
                boolean isV2Script = Boolean.parseBoolean(confirmation.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
                String workflowV2String = confirmation.getElement("workflowV2String").getCData();
                workflowContext.setIsV2Script(isV2Script);
                workflowContext.setWorkflowV2String(workflowV2String);
                confirmationDialogContext.setMessagePlaceHolderScript(workflowContext);
            }
            confirmationList.add(confirmationDialogContext);
        }
        return confirmationList;
    }

    public static void constructBuilderFromActionsList(List<ActionContext> actionsContextList, XMLBuilder actionsList) throws Exception {
        if (CollectionUtils.isEmpty(actionsContextList)) {
            return;
        }

        ModuleBean moduleBean = Constants.getModBean();
        for (ActionContext actionContext : actionsContextList) {
            // TODO - Handle DEFAULT_TEMPLATE_ID
            if (actionContext.getDefaultTemplateId() > 0) {
                continue;
            }

            XMLBuilder actionElement = actionsList.element(PackageConstants.WorkFlowRuleConstants.ACTION);
            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_STATUS).text(String.valueOf(actionContext.isActive()));
            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_TYPE).text(actionContext.getActionTypeEnum().name());

            if (actionContext.getTemplate() != null) {
                // TODO - Handle WORKFLOW_ID, USER_WORKFLOW_ID
                Template templateContext = actionContext.getTemplate();
                XMLBuilder templateElement = actionElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE);
                templateElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE_NAME).text(templateContext.getName());
                templateElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE_TYPE).text(templateContext.getTypeEnum().name());
                if (StringUtils.isNotEmpty(templateContext.getPlaceholderStr()) && !(templateContext.getPlaceholderStr().equals("[]"))) {
                    templateElement.element(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER).cData(templateContext.getPlaceholderStr());
                }
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_FTL).text(String.valueOf(templateContext.isFtl()));
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_ATTACHMENT_ADDED).text(String.valueOf(templateContext.getIsAttachmentAdded()));
                if (templateContext.getWorkflow() != null) {
                    XMLBuilder workflow = templateElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW);

                    XMLBuilder expressionsElement = workflow.element(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS);
                    WorkflowContext workflowObj = templateContext.getWorkflow();
                    List<WorkflowExpression> expressionArray = workflowObj.getExpressions();
                    WorkflowExpression workflowExpression = expressionArray.get(0);
                    if (workflowExpression instanceof ExpressionContext) {
                        for (WorkflowExpression obj : expressionArray) {
                            ExpressionContext exp = (ExpressionContext) obj;
                            XMLBuilder expressionElement = expressionsElement.element("Expression");
                            String constant = exp.getConstant() != null ? exp.getConstant().toString() : null;
                            String name = exp.getName() != null ? exp.getName() : null;
                            if (StringUtils.isNotEmpty(constant)) {
                                expressionElement.element("Constant").text(constant);
                            }
                            if (StringUtils.isNotEmpty(name)) {
                                expressionElement.element("Name").text(name);
                            }

                            String aggregateString = exp.getAggregateString() != null ? exp.getAggregateString().toString() : null;
                            String fieldName = exp.getFieldName() != null ? exp.getFieldName() : null;
                            String moduleName = exp.getModuleName() != null ? exp.getModuleName() : null;
                            Criteria expressionCriteria = exp.getCriteria() != null ? exp.getCriteria() : null;
                           expressionElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(expressionCriteria, expressionElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                            if (expressionCriteria != null && !expressionCriteria.isEmpty()) {
                                Map<String, Condition> conditions = expressionCriteria.getConditions();
                                XMLBuilder ouidListElement = expressionElement.element("OuidList");
                                for (String key : conditions.keySet()) {
                                    Condition condition = conditions.get(key);
                                    if (condition.getFieldName().equals("ouid")) {
                                        String[] ouids = condition.getValue().trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
                                        for (String str : ouids) {
                                            XMLBuilder ouid = ouidListElement.element("Ouid");
                                            Long ouidLong = Long.parseLong(str);
                                            PackageBeanUtil.userXMLBuilder(ouid, ouidLong);
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isNotEmpty(aggregateString)) {
                                expressionElement.element("AggregateString").text(aggregateString);
                            }
                            if (StringUtils.isNotEmpty(fieldName)) {
                                expressionElement.element("FieldName").text(fieldName);
                            }
                            if (StringUtils.isNotEmpty(moduleName)) {
                                expressionElement.element("ModuleName").text(moduleName);
                            }
                        }

                    }

                    XMLBuilder parameterBuilder = workflow.element(PackageConstants.WorkFlowRuleConstants.PARAMETERS);
                    List<ParameterContext> parameterList = templateContext.getWorkflow().getParameters();
                    for (ParameterContext parameter : parameterList) {
                        XMLBuilder parameters = parameterBuilder.element("parameter");
                        parameters.element("name").text(parameter.getName());
                        parameters.element("typeString").text(parameter.getTypeString());
                        if (parameter.getValue() != null) {
                            parameters.element("value").text(String.valueOf(parameter.getValue()));
                        }
                        parameters.element("workflowFieldType").text(String.valueOf(parameter.getWorkflowFieldType()));
                    }
                    workflow.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(templateContext.getWorkflow().getWorkflowString());
                }
                if (templateContext.getUserWorkflow() != null) {
                    XMLBuilder workFlowElement = templateElement.element(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW);
                    workFlowElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(templateContext.getUserWorkflow().isV2Script()));
                    workFlowElement.element("workflowV2String").cData(String.valueOf(templateContext.getUserWorkflow().getWorkflowV2String()));
                }

                JSONObject originalTemplate = templateContext.getOriginalTemplate();
                if (originalTemplate == null) {
                    continue;
                }

                XMLBuilder originalTemplateElement = templateElement.element(PackageConstants.WorkFlowRuleConstants.ORIGINAL_TEMPLATE);
                switch (templateContext.getTypeEnum()) {
                    case JSON:
                        switch (actionContext.getActionTypeEnum()) {
                            case CHANGE_STATE:
                                String newStateId = String.valueOf(originalTemplate.get("new_state"));
                                long ticketStatusId = StringUtils.isNotEmpty(newStateId) ? Long.parseLong(newStateId) : -1;
                                FacilioStatus ticketStatus = ticketStatusId != -1 ? TicketAPI.getStatus(ticketStatusId) : null;
                                if (ticketStatus != null) {
                                    FacilioModule parentModule = moduleBean.getModule(ticketStatus.getParentModuleId());
                                    XMLBuilder newStateElement = originalTemplateElement.element(PackageConstants.VALUE_ELEMENT);
                                    newStateElement.element(PackageConstants.WorkFlowRuleConstants.STATUS_NAME).text(ticketStatus.getStatus());
                                    newStateElement.element(PackageConstants.WorkFlowRuleConstants.PARENT_MODULE_NAME).text(parentModule.getName());
                                }
                                break;

                            case FIELD_CHANGE:
                                for (Object key : originalTemplate.keySet()) {
                                    Object value = originalTemplate.get(key);
                                    XMLBuilder valueElement = originalTemplateElement.element(PackageConstants.VALUE_ELEMENT);
                                    valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_NAME).text(String.valueOf(key));
                                    if (value instanceof JSONObject) {
                                        valueElement.attribute(PackageConstants.WorkFlowRuleConstants.CONTAINS_RECORD_ID_MAP, Boolean.TRUE.toString());
                                        XMLBuilder actionFieldValueElement = valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE);
                                        actionFieldValueElement.element("id").text(String.valueOf(((JSONObject) value).get("id")));
                                    } else {
                                        valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE).text(String.valueOf(value));
                                    }
                                }
                                break;

                            default:
                                break;
                        }
                        break;

                    case WORKFLOW:
                        if (originalTemplate.containsKey("workflowContext")) {
                            XMLBuilder workFlowElement = originalTemplateElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT);
                            JSONObject workFlowContext = (JSONObject) originalTemplate.get("workflowContext");
                            if (workFlowContext.containsKey("isV2Script")) {
                                workFlowElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(workFlowContext.get("isV2Script")));
                            }
                            if (workFlowContext.containsKey("workflowV2String")) {
                                workFlowElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(String.valueOf(workFlowContext.get("workflowV2String")));
                            }
                        }
                        break;

                    case EMAIL:
                        XMLBuilder emailElement = originalTemplateElement.element(PackageConstants.VALUE_ELEMENT);
                        long emailStructureId = ((EMailTemplate) templateContext).getEmailStructureId();
                        String emailStructureName = emailStructureId > 0 ? TemplateAPI.getTemplate(emailStructureId).getName() : null;
                        emailElement.element(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_NAME).text(emailStructureName);
                        emailElement.element("bcc").text(((EMailTemplate) templateContext).getBcc());
                        emailElement.element("cc").text(((EMailTemplate) templateContext).getCc());
                        emailElement.element("html").text(String.valueOf(((EMailTemplate) templateContext).getHtml()));
                        emailElement.element("message").cData(((EMailTemplate) templateContext).getMessage());
                        emailElement.element("sendAsSeparateMail").text(String.valueOf(((EMailTemplate) templateContext).getSendAsSeparateMail()));
                        emailElement.element("sender").text(((EMailTemplate) templateContext).getFrom());
                        emailElement.element("subject").text(((EMailTemplate) templateContext).getSubject());
                        emailElement.element("to").text(((EMailTemplate) templateContext).getTo());
                        break;

                    case PUSH_NOTIFICATION:
                        XMLBuilder pushNotificationElement = originalTemplateElement.element(PackageConstants.VALUE_ELEMENT);
                        long appId = ((PushNotificationTemplate) templateContext).getApplication();
                        ApplicationContext application = appId > 0 ? ApplicationApi.getApplicationForId(appId) : null;
                        String appLinkName = application != null ? application.getLinkName() : FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP;
                        JSONObject obj = ((PushNotificationTemplate) templateContext).getOriginalTemplate();
                        UserNotificationContext userNotification = UserNotificationContext.instance(obj);
                        String moduleName = (String) ((JSONObject) obj.get("notification")).get("module_name");
                        FacilioModule parentModule = moduleBean.getModule(moduleName);
                        long parentModuleId = StringUtils.isNotEmpty(moduleName) ? parentModule.getModuleId() : moduleBean.getModule(FacilioConstants.ContextNames.USER_NOTIFICATION).getModuleId();
                        userNotification.setParentModule(parentModuleId);
                        JSONObject structureObj = UserNotificationContext.getFcmObjectMaintainence(userNotification);
                        pushNotificationElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(appLinkName);
                        pushNotificationElement.element("id").text(((PushNotificationTemplate) templateContext).getTo());
                        pushNotificationElement.element("isSendNotification").text(String.valueOf(((PushNotificationTemplate) templateContext).getIsSendNotification()));
                        pushNotificationElement.element("name").text(templateContext.getName());
                        XMLBuilder bodyBuilder = pushNotificationElement.element("data");
                        if (userNotification.getExtraParams() != null) {
                            String clickAction = userNotification.getExtraParams().get("click_action") != null ? userNotification.getExtraParams().get("click_action").toString() : null;
                            bodyBuilder.element("click_action").text(clickAction);
                            String contentAvailable = userNotification.getExtraParams().get("content_available") != null ? userNotification.getExtraParams().get("content_available").toString() : null;
                            bodyBuilder.element("content_available").text(contentAvailable);
                            String priority = userNotification.getExtraParams().get("priority") != null ? userNotification.getExtraParams().get("priority").toString() : null;
                            bodyBuilder.element("priority").text(priority);
                            String sound = userNotification.getExtraParams().get("sound") != null ? userNotification.getExtraParams().get("sound").toString() : null;
                            bodyBuilder.element("sound").text(sound);
                            String summaryId = userNotification.getExtraParams().get("summary_id") != null ? userNotification.getExtraParams().get("summary_id").toString() : null;
                            bodyBuilder.element("summary_id").text(summaryId);
                        }
                        bodyBuilder.element("module_name").text(parentModule.getName());
                        bodyBuilder.element("text").text(userNotification.getSubject());
                        bodyBuilder.element("title").text(userNotification.getTitle());
                        pushNotificationElement.element("notification").text(structureObj.get("notification").toString());
                        pushNotificationElement.element("body").text(structureObj.toJSONString());
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public static List<ActionContext> constructActionContextsFromBuilder(XMLBuilder actionsList) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<ActionContext> actionContextList = new ArrayList<>();
        if (actionsList.getElement(PackageConstants.WorkFlowRuleConstants.ACTION) != null) {
            List<XMLBuilder> actionsListElementList = actionsList.getElementList(PackageConstants.WorkFlowRuleConstants.ACTION);
            for (XMLBuilder actionElement : actionsListElementList) {
                boolean actionStatus = Boolean.parseBoolean(actionElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_STATUS).getText());
                String actionTypeStr = actionElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_TYPE).getText();
                ActionType actionType = StringUtils.isNotEmpty(actionTypeStr) ? ActionType.valueOf(actionTypeStr) : null;

                ActionContext actionContext = new ActionContext();
                actionContext.setActionType(actionType);
                actionContext.setStatus(actionStatus);
                actionContextList.add(actionContext);

                XMLBuilder templateElement = actionElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE);
                if (templateElement != null) {
                    if (templateElement.getElement(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER) != null) {
                        String placeholderStr = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER).getCData();
                    }
                    String templateName = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE_NAME).getText();
                    String templateTypeStr = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE_TYPE).getText();
                    boolean isFtl = Boolean.parseBoolean(templateElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_FTL).getText());
                    boolean isAttachmentAdded = Boolean.parseBoolean(templateElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_ATTACHMENT_ADDED).getText());
                    WorkflowContext userWorkflowContext = new WorkflowContext();
                    if (templateElement.getElement(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW) != null) {
                        XMLBuilder userWorkflowElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW);
                        userWorkflowContext.setIsV2Script(Boolean.parseBoolean(userWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText()));
                        userWorkflowContext.setWorkflowV2String(userWorkflowElement.getElement("workflowV2String").getCData());
                    }
                    XMLBuilder emailWorkflowElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW);
                    WorkflowContext workflowParamExp = new WorkflowContext();
                    if (emailWorkflowElement != null) {
                        XMLBuilder expressionsBuilder = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS);
                        List<XMLBuilder> expressionBuilder = expressionsBuilder.getElementList("Expression");
                        JSONArray expressionArray = new JSONArray();

                        for (XMLBuilder expression : expressionBuilder) {
                            Map<String, Object> expressionProps = new HashMap<>();
                            if (expression.getElement("Constant") != null) {
                                expressionProps.put("constant", expression.getElement("Constant").getText());
                            }
                            if (expression.getElement("Name") != null) {
                                expressionProps.put("name", expression.getElement("Name").getText());
                            }
                            if (expression.getElement("AggregateString") != null) {
                                expressionProps.put("aggregateString", expression.getElement("AggregateString").getText());
                            }
                            if (expression.getElement("FieldName") != null) {
                                expressionProps.put("fieldName", expression.getElement("FieldName").getText());
                            }
                            if (expression.getElement("ModuleName") != null) {
                                expressionProps.put("moduleName", expression.getElement("ModuleName").getText());
                            }
                            XMLBuilder criteriaElement = expression.getElement(PackageConstants.CriteriaConstants.CRITERIA);
                            if (criteriaElement != null) {
                                Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                                XMLBuilder ouidList = expression.getElement("OuidList");
                                if (ouidList != null) {
                                    List<XMLBuilder> ouids = ouidList.getElementList("Ouid");
                                    List<Long> ouIds = new ArrayList<>();
                                    for (XMLBuilder ouid : ouids) {
                                        ouIds.add(PackageBeanUtil.userValueBuilder(ouid));
                                    }
                                    String ouidString = StringUtils.join(ouIds, ',');
                                    for (String key : criteria.getConditions().keySet()) {
                                        Condition condition = criteria.getConditions().get(key);
                                        if (condition.getFieldName().equals("ouid")) {
                                            condition.setValue(ouidString);
                                        }
                                    }
                                }
                                expressionProps.put("criteria", criteria);
                            }
                            expressionArray.add(expressionProps);
                        }

                        workflowParamExp.setExpressions(expressionArray);

                        XMLBuilder parameterBuilder = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.PARAMETERS);
                        List<ParameterContext> parameterContexts = new ArrayList<>();
                        if (parameterBuilder != null) {
                            List<XMLBuilder> paramBuilderList = parameterBuilder.getElementList("parameter");
                            for (XMLBuilder parameter : paramBuilderList) {
                                ParameterContext param = new ParameterContext();
                                String name = parameter.getElement("name").getText();
                                String typeString = parameter.getElement("typeString").getText();
                                String value = null;
                                if (parameter.getElement("value") != null) {
                                    value = parameter.getElement("value").getText();
                                }
                                WorkflowFieldType workflowFieldType = WorkflowFieldType.valueOf(parameter.getElement("workflowFieldType").getText());
                                param.setName(name);
                                param.setTypeString(typeString);
                                param.setValue(value);
                                param.setWorkflowFieldType(workflowFieldType);

                                parameterContexts.add(param);
                            }
                        }
                        workflowParamExp.setParameters(parameterContexts);
                        String workflowString = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
                        workflowParamExp.setWorkflowString(workflowString);

                    }

                    Template.Type templateType = StringUtils.isNotEmpty(templateTypeStr) ? Template.Type.valueOf(templateTypeStr) : null;
                    XMLBuilder originalTemplateElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.ORIGINAL_TEMPLATE);
                    if (templateType != null) {
                        JSONObject templateJson = new JSONObject();
                        switch (templateType) {
                            case JSON:
                                List<XMLBuilder> allValueElements = originalTemplateElement.getElementList(PackageConstants.VALUE_ELEMENT);

                                if (actionType == null) {
                                    continue;
                                }

                                switch (actionType) {
                                    case CHANGE_STATE:
                                        for (XMLBuilder valueElement : allValueElements) {
                                            String statusName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.STATUS_NAME).getText();
                                            String parentModuleName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.PARENT_MODULE_NAME).getText();
                                            FacilioModule parentModule = moduleBean.getModule(parentModuleName);
                                            if (parentModule != null) {
                                                FacilioStatus ticketStatus = TicketAPI.getStatus(parentModule, statusName);
                                                if (ticketStatus != null) {
                                                    templateJson.put("new_state", ticketStatus.getId());
                                                }
                                            }
                                        }
                                        break;

                                    case FIELD_CHANGE:
                                        ArrayList<Map<String, Object>> fieldMatcher = new ArrayList<>();
                                        for (XMLBuilder valueElement : allValueElements) {
                                            Map<String, Object> fieldChangeMap = new HashMap<>();
                                            boolean containsRecordIdMap = Boolean.parseBoolean(valueElement.getAttribute(PackageConstants.WorkFlowRuleConstants.CONTAINS_RECORD_ID_MAP));
                                            String actionFieldName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_NAME).getText();
                                            XMLBuilder actionValueElement = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE);
                                            if (containsRecordIdMap) {
                                                String id = actionValueElement.getElement("id").getText();
                                                JSONObject idObject = new JSONObject();
                                                idObject.put("id", id);

                                                fieldChangeMap.put("value", idObject);
                                            } else {
                                                String actionValue = actionValueElement.getText();
                                                fieldChangeMap.put("value", actionValue);
                                            }
                                            fieldChangeMap.put("field", actionFieldName);
                                            fieldMatcher.add(fieldChangeMap);
                                        }
                                        templateJson.put("fieldMatcher", fieldMatcher);
                                        break;

                                    default:
                                        break;
                                }
                                break;

                            case WORKFLOW:
                                JSONObject resultWorkflowContext = new JSONObject();
                                XMLBuilder workFlowElement = originalTemplateElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT);
                                if (workFlowElement != null) {
                                    boolean isV2Script = Boolean.parseBoolean(workFlowElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
                                    String workflowV2String = workFlowElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
                                    resultWorkflowContext.put("isV2Script", isV2Script);
                                    resultWorkflowContext.put("workflowV2String", workflowV2String);
                                }
                                templateJson.put("resultWorkflowContext", resultWorkflowContext);
                                break;

                            case EMAIL:
                                List<XMLBuilder> valueElements = originalTemplateElement.getElementList(PackageConstants.VALUE_ELEMENT);

                                for (XMLBuilder valueElement : valueElements) {
                                    String emailStructureName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_NAME).getText();
                                    Template emailStructureTemplate = StringUtils.isNotEmpty(emailStructureName) ? TemplateAPI.getTemplate(emailStructureName, Template.Type.EMAIL_STRUCTURE) : null;
                                    if (emailStructureTemplate != null) {
                                        templateJson.put(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_ID, emailStructureTemplate.getId());
                                        templateJson.put("message", ((EMailStructure) emailStructureTemplate).getMessage());
                                        templateJson.put("name", templateName);
                                        templateJson.put("subject", ((EMailStructure) emailStructureTemplate).getSubject());
                                    } else {
                                        templateJson.put("message", valueElement.getElement("message").getCData());
                                        templateJson.put("name", templateName);
                                        templateJson.put("subject", valueElement.getElement("subject").getText());
                                    }
                                    templateJson.put("ftl", isFtl);
                                    templateJson.put("isAttachmentAdded", isAttachmentAdded);
                                    templateJson.put("sendAsSeparateMail", Boolean.parseBoolean(valueElement.getElement("sendAsSeparateMail").getText()));
                                    templateJson.put("to", valueElement.getElement("to").getText());
                                    templateJson.put("type", templateType);
                                    templateJson.put("userWorkflow", FieldUtil.getAsProperties(userWorkflowContext));
                                    templateJson.put("workflow", FieldUtil.getAsProperties(workflowParamExp));
                                }
                                break;


                            case PUSH_NOTIFICATION:
                                List<XMLBuilder> pushNotificationValues = originalTemplateElement.getElementList(PackageConstants.VALUE_ELEMENT);

                                for (XMLBuilder valueElement : pushNotificationValues) {
                                    String appLinkName = valueElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
                                    long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                                    JSONObject dataObj = new JSONObject();
                                    XMLBuilder dataBuilder = valueElement.getElement("data");
                                    dataObj.put("click_action", dataBuilder.getElement("click_action").getText());
                                    dataObj.put("content_available", dataBuilder.getElement("content_available").getText());
                                    dataObj.put("module_name", dataBuilder.getElement("module_name").getText());
                                    dataObj.put("priority", dataBuilder.getElement("priority").getText());
                                    dataObj.put("sound", dataBuilder.getElement("sound").getText());
                                    dataObj.put("summary_id", dataBuilder.getElement("summary_id").getText());
                                    dataObj.put("text", dataBuilder.getElement("text").getText());
                                    dataObj.put("title", dataBuilder.getElement("title").getText());
                                    JSONObject body = new JSONObject();
                                    body.put("name", templateName);
                                    body.put("data", dataObj);
                                    body.put("notification", dataObj);
                                    templateJson.put(PackageConstants.AppXMLConstants.APPLICATION, appId);
                                    templateJson.put("id", valueElement.getElement("id").getText());
                                    templateJson.put("body", body.toJSONString());
                                    templateJson.put("isPushNotification", Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                    templateJson.put("isSendNotification", Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                    templateJson.put("message", dataBuilder.getElement("text").getText());
                                    templateJson.put("subject", dataBuilder.getElement("title").getText());
                                    templateJson.put("name", templateName);
                                    templateJson.put("to", valueElement.getElement("id").getText());
                                    templateJson.put("type", templateType);
                                    templateJson.put("userWorkflow", FieldUtil.getAsProperties(userWorkflowContext));
                                    templateJson.put("workflow", FieldUtil.getAsProperties(workflowParamExp));
                                }
                                break;

                            default:
                                break;
                        }

                        actionContext.setTemplateJson(templateJson);
                    }
                }
            }
        }
        return actionContextList;
    }

    public static List<?> getModuleDataListsForIds(Collection<Long> ids, FacilioModule module, Class<?> clazz) throws Exception {
        return getModuleDataListsForIds(ids, module, clazz, false);
    }

    public static List<?> getModuleDataListsForIds(Collection<Long> ids, FacilioModule module, Class<?> clazz, boolean fetchDeleted) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(ids, module))
                .skipModuleCriteria();

        if (fetchDeleted) {
            builder.fetchDeleted();
        }

        return builder.get();
    }

    public static List<?> getModuleDataForId(Long id, FacilioModule module, Class<?> clazz) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(id, module))
                .skipModuleCriteria();
        return builder.get();
    }

    public static List<?> getModuleData(Criteria criteria, FacilioModule module, Class<?> clazz, boolean fetchDeleted) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordBuilder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()))
                .skipModuleCriteria();

        if (fetchDeleted) {
            selectRecordBuilder.fetchDeleted();
        }

        if (criteria != null && !criteria.isEmpty()) {
            selectRecordBuilder.andCriteria(criteria);
        }

        List<ModuleBaseWithCustomFields> propsList = selectRecordBuilder.get();

        return propsList;
    }

    public static List<WebTabContext> getWebTabs(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getWebTabModule()));

        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);

        if (CollectionUtils.isNotEmpty(webTabs)) {
            for (WebTabContext webTabContext : webTabs) {
                List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webTabContext.getId());

                List<Long> moduleIds = new ArrayList<>();
                List<String> specialTypes = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(tabIdAppIdMappingContextList)) {
                    for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                        if (tabIdAppIdMappingContext.getModuleId() > 0) {
                            moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                        }
                        if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null")
                                && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                            specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                        }
                    }
                }
                webTabContext.setModuleIds(moduleIds);
                webTabContext.setSpecialTypeModules(specialTypes);
            }
        }

        return webTabs;
    }


    public static long getFormRuleId(FormRuleContext formRule) throws Exception {

        GenericSelectRecordBuilder ruleSelectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormRuleModule().getTableName())
                .select(FieldFactory.getFormRuleFields());

        if (formRule.getFormId() > 0) {
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("FORM_ID", "formId", String.valueOf(formRule.getFormId()), NumberOperators.EQUALS));
        }

        if (formRule.getSubFormId() > 0) {
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("SUB_FORM_ID", "subFormId", String.valueOf(formRule.getSubFormId()), NumberOperators.EQUALS));
        }

        if (StringUtils.isNotEmpty(formRule.getName())) {
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", formRule.getName(), StringOperators.IS));
        }

        if (StringUtils.isNotEmpty(formRule.getDescription())) {
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("DESCRIPTION", "description", formRule.getDescription(), StringOperators.IS));
        }
        ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT_RULE", "isDefault", String.valueOf(formRule.getIsDefault()), BooleanOperators.IS));

        Map<String, Object> prop = ruleSelectBuilder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;

    }

    public static XMLBuilder constructBuilderFromNameSpaceNdFields(NameSpaceContext nsCtx, XMLBuilder builder) throws Exception {

        XMLBuilder nsBuilder = builder.e("Namespace");

        nsBuilder.e("execInterval").text(String.valueOf(nsCtx.getExecInterval()));
        nsBuilder.e("type").text(String.valueOf(nsCtx.getType()));

        XMLBuilder inclAssets = nsBuilder.e("IncludedAssetIds");
        if (CollectionUtils.isNotEmpty(nsCtx.getIncludedAssetIds())) {
            for (Long assetId : nsCtx.getIncludedAssetIds()) {
                inclAssets.e("resourceId").t(String.valueOf(assetId));
            }
        }

        if (CollectionUtils.isNotEmpty(nsCtx.getFields())) {
            nsBuilder.addElement(constructBuilderFromNameSpaceFields(nsCtx.getFields(), nsBuilder));
        }

        XMLBuilder workflowBuilder = nsBuilder.e("WorkflowContext");
        constructBuilderFromWorkFlowContext(nsCtx.getWorkflowContext(), workflowBuilder);

        return nsBuilder;
    }

    public static XMLBuilder constructBuilderFromNameSpaceFields(List<NameSpaceField> nsFldCtx, XMLBuilder nsBuilder) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        XMLBuilder fields = nsBuilder.e("Fields");

        for (NameSpaceField fld : nsFldCtx) {
            XMLBuilder field = fields.e("Field");
            if (fld.getResourceId() != null) {
                field.e("resourceId").text(String.valueOf(fld.getResourceId()));
            }
            field.e("varName").text(fld.getVarName());
            field.e("aggregationType").text(String.valueOf(fld.getAggregationType()));
            field.e("dataInterval").text(String.valueOf(fld.getDataInterval()));
            field.e("nsFieldType").text(String.valueOf(fld.getNsFieldType()));
            field.e(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(moduleBean.getField(fld.getFieldId()).getName());
            field.e(PackageConstants.NameSpaceConstants.VAR_NAME).text(fld.getVarName());
            field.e(PackageConstants.NameSpaceConstants.AGG_TYPE).text(String.valueOf(fld.getAggregationType()));
            field.e(PackageConstants.NameSpaceConstants.DATA_INTERVAL).text(String.valueOf(fld.getDataInterval()));
            field.e(PackageConstants.NameSpaceConstants.NS_FIELD_TYPE).text(String.valueOf(fld.getNsFieldType()));
            field.e(PackageConstants.NameSpaceConstants.FIELD_NAME).text(moduleBean.getField(fld.getFieldId()).getName());
            field.e(PackageConstants.MODULENAME).text(moduleBean.getField(fld.getFieldId()).getModule().getName());
        }
        return fields;
    }

    public static NameSpaceContext constructNamespaceNdFieldsFromBuilder(XMLBuilder xmlBuilder) throws Exception {

        if (xmlBuilder.getElement("Namespace") == null) {
            return null;
        }
        NameSpaceContext ns = new NameSpaceContext();
        XMLBuilder namespaceBuilder = xmlBuilder.getElement("Namespace");
        if (NumberUtils.isNumber(namespaceBuilder.getElement("execInterval").getText())) {
            Long execInterval = Long.valueOf(namespaceBuilder.getElement("execInterval").getText());
            ns.setExecInterval(execInterval);
        }
        int nsType = Integer.parseInt(namespaceBuilder.getElement("type").getText());
        XMLBuilder nsFieldsElement = namespaceBuilder.getElement("Fields");

        if (nsFieldsElement != null) {
            List<NameSpaceField> nameSpaceFields = constructNamespaceFieldsFromBuilder(nsFieldsElement);
            XMLBuilder workflowBuilder = xmlBuilder.getElement("WorkflowContext");
            WorkflowContext workflowContext = constructWorkflowContextFromBuilder(workflowBuilder);

            XMLBuilder inclAssets = namespaceBuilder.getElement("IncludedAssetIds");
            List<XMLBuilder> assets = inclAssets.getElementList("resourceId");
            List<Long> includedAssets = assets.stream().map(m -> Long.valueOf(m.getText())).collect(Collectors.toList());
            ns.setIncludedAssetIds(includedAssets);

            ns.setWorkflowContext(workflowContext);
            ns.setType(nsType);
            ns.setFields(nameSpaceFields);
        }
        return ns;
    }

    public static List<NameSpaceField> constructNamespaceFieldsFromBuilder(XMLBuilder nsFieldsElement) throws Exception {
        List<XMLBuilder> nsFieldList = nsFieldsElement.getFirstLevelElementListForTagName("Field");
        List<NameSpaceField> nameSpaceFields = new ArrayList<>();
        for (XMLBuilder fld : nsFieldList) {
            NameSpaceField nsField = new NameSpaceField();
            FacilioField facilioField = getFacilioFieldFromBuilder(fld);

            if (fld.getElement("resourceId") != null) {
                nsField.setResourceId(Long.valueOf(String.valueOf(fld.getElement("resourceId").getText())));
            }
            nsField.setVarName(fld.getElement("varName").getText());
            nsField.setAggregationType(AggregationType.valueOf(fld.getElement("aggregationType").getText()));
            nsField.setDataInterval(Long.valueOf(fld.getElement("dataInterval").getText()));
            nsField.setNsFieldType(NsFieldType.valueOf(fld.getElement("nsFieldType").getText()));
            nsField.setFieldId(facilioField.getFieldId());
            nsField.setModuleId(facilioField.getModuleId());

            nameSpaceFields.add(nsField);
        }
        return nameSpaceFields;
    }

    public static FacilioField getFacilioFieldFromBuilder(XMLBuilder fldBuilder) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String fieldName = fldBuilder.getElement(PackageConstants.NameSpaceConstants.FIELD_NAME).getText();
        String fieldModuleName = fldBuilder.getElement(PackageConstants.MODULENAME).getText();
        FacilioField facilioField = moduleBean.getField(fieldName, fieldModuleName);
        return facilioField;
    }

    public static List<V3AssetCategoryContext> getAssetCategories(Boolean fetchSystem) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
        Criteria criteria = new Criteria();
        if (fetchSystem != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));
        }
        criteria.addAndCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(false), BooleanOperators.IS));
        List<V3AssetCategoryContext> props = (List<V3AssetCategoryContext>) PackageBeanUtil.getModuleData(criteria, assetCategoryModule, V3AssetCategoryContext.class, Boolean.FALSE);
        return props;
    }

    public static Map<Long, Long> getAssetCategoryIdVsModuleId(Boolean fetchSystem) throws Exception {
        Map<Long, Long> assetCategoryIdVsModuleId = new HashMap<>();
        List<V3AssetCategoryContext> props = getAssetCategories(fetchSystem);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetCategoryContext prop : props) {
                assetCategoryIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return assetCategoryIdVsModuleId;
    }


    public static Map<String, Long> getAssetCategoryNameVsId(Boolean fetchSystem) throws Exception {
        Map<String, Long> assetCategoryIdVsModuleId = new HashMap<>();
        List<V3AssetCategoryContext> props = getAssetCategories(fetchSystem);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetCategoryContext prop : props) {
                assetCategoryIdVsModuleId.put(prop.getDisplayName(), prop.getId());
            }
        }
        return assetCategoryIdVsModuleId;
    }

    public static void convertFacilioFieldToXML(FacilioField facilioField, XMLBuilder fieldElement) throws Exception {
        fieldElement.element(PackageConstants.NAME).text(facilioField.getName());
        fieldElement.element(PackageConstants.DISPLAY_NAME).text(facilioField.getDisplayName());
        fieldElement.element(PackageConstants.MODULENAME).text(facilioField.getModule().getName());
        fieldElement.element(PackageConstants.FieldXMLConstants.REQUIRED).text(String.valueOf(facilioField.isRequired()));
        fieldElement.element(PackageConstants.FieldXMLConstants.IS_DEFAULT).text(String.valueOf(facilioField.isDefault()));
        fieldElement.element(PackageConstants.FieldXMLConstants.DATA_TYPE).text(String.valueOf(facilioField.getDataType()));
        fieldElement.element(PackageConstants.FieldXMLConstants.MAIN_FIELD).text(String.valueOf(facilioField.isMainField()));
        fieldElement.element(PackageConstants.FieldXMLConstants.DISPLAY_TYPE).text(String.valueOf(facilioField.getDisplayTypeInt()));

        Map<String, Object> additionalFieldProps = fetchAdditionalFieldProps(facilioField);
        for (Map.Entry<String, Object> entry : additionalFieldProps.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            value = (value == null) ? "" : value;

            if (value instanceof List) {
                XMLBuilder valuesListElement = fieldElement.element(key);
                for (Map<String, Object> keyValuePair : (List<Map<String, Object>>) value) {
                    XMLBuilder valueElement = valuesListElement.element(PackageConstants.VALUE_ELEMENT);
                    for (Map.Entry<String, Object> specialProp : keyValuePair.entrySet()) {
                        String specialPropKey = specialProp.getKey();
                        Object specialPropValue = specialProp.getValue();
                        valueElement.element(specialPropKey).text(String.valueOf(specialPropValue));
                    }
                }
            } else {
                fieldElement.element(key).text(String.valueOf(value));
            }
        }
    }

    private static Map<String, Object> fetchAdditionalFieldProps(FacilioField oldField) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        Map<String, Object> fieldProps = new HashMap<>();

        switch (oldField.getDataTypeEnum()) {
            case STRING:
            case BIG_STRING:
                fieldProps.put("regex", ((StringField) oldField).getRegex());
                fieldProps.put("maxLength", ((StringField) oldField).getMaxLength());
                break;

            case NUMBER:
            case DECIMAL:
                fieldProps.put("unit", ((NumberField) oldField).getUnit());
                fieldProps.put("unitId", ((NumberField) oldField).getUnitId());
                fieldProps.put("metric", ((NumberField) oldField).getMetric());
                fieldProps.put("minValue", ((NumberField) oldField).getMinValue());
                fieldProps.put("maxValue", ((NumberField) oldField).getMaxValue());
                fieldProps.put("counterField", ((NumberField) oldField).getCounterField());
                break;

            case ENUM:
            case MULTI_ENUM:
                List<Map<String, Object>> enumValuesProps = getEnumFieldValuesProps(((BaseEnumField) oldField).getValues());
                fieldProps.put("values", enumValuesProps);
                break;

            case SYSTEM_ENUM:
                fieldProps.put("enumName", ((SystemEnumField) oldField).getEnumName());
                break;

            case STRING_SYSTEM_ENUM:
                fieldProps.put("enumName", ((StringSystemEnumField) oldField).getEnumName());
                break;

            case URL_FIELD:
                fieldProps.put("showAlt", ((UrlField) oldField).getShowAlt());
                fieldProps.put("target", ((UrlField) oldField).getTarget().toString());
                break;

            case DATE:
            case DATE_TIME:
                List<Map<String, Object>> daysOfWeekProps = getDaysOfWeekProps(((DateField) oldField).getAllowedDays());
                fieldProps.put("allowedDays", daysOfWeekProps);
                if (((DateField) oldField).getAllowedDate() != null) {
                    fieldProps.put("allowedDate", ((DateField) oldField).getAllowedDate().name());
                }
                break;

            case BOOLEAN:
                fieldProps.put("trueVal", ((BooleanField) oldField).getTrueVal());
                fieldProps.put("falseVal", ((BooleanField) oldField).getFalseVal());
                break;

            case LARGE_TEXT:
                fieldProps.put("skipSizeCheck", ((LargeTextField) oldField).getSkipSizeCheck());
                break;

            case LOOKUP:
            case MULTI_LOOKUP:
                long lookupModuleId = ((BaseLookupField) oldField).getLookupModuleId();
                if (lookupModuleId > 0) {
                    FacilioModule lookupModule = moduleBean.getModule(lookupModuleId);
                    fieldProps.put("lookupModuleName", lookupModule.getName());
                } else {
                    fieldProps.put("lookupModuleName", null);
                }
                fieldProps.put("specialType", ((BaseLookupField) oldField).getSpecialType());
                fieldProps.put("relatedListDisplayName", ((BaseLookupField) oldField).getRelatedListDisplayName());
                break;

            case FILE:
                if (((FileField) oldField).getFormatEnum() != null) {
                    fieldProps.put("format", ((FileField) oldField).getFormatEnum().name());
                }
                break;

            default:
                break;
        }
        return fieldProps;
    }

    private static List<Map<String, Object>> getDaysOfWeekProps(List<DayOfWeek> values) {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        List<Map<String, Object>> propsList = new ArrayList<>();
        for (DayOfWeek value : values) {
            Map<String, Object> additionalProp = new HashMap<>();
            additionalProp.put("value", value.name());
            propsList.add(additionalProp);
        }

        return propsList;
    }

    private static List<Map<String, Object>> getEnumFieldValuesProps(List<EnumFieldValue<Integer>> enumFieldValues) {
        if (CollectionUtils.isEmpty(enumFieldValues)) {
            return null;
        }

        List<Map<String, Object>> propsList = new ArrayList<>();

        for (EnumFieldValue<Integer> enumFieldValue : enumFieldValues) {
            Map<String, Object> additionalProp = new HashMap<>();
            additionalProp.put("index", enumFieldValue.getIndex());
            additionalProp.put("value", enumFieldValue.getValue());
            additionalProp.put("visible", enumFieldValue.getVisible());
            additionalProp.put("sequence", enumFieldValue.getSequence());
            propsList.add(additionalProp);
        }

        return propsList;
    }

    public static FacilioField getFieldFromXMLComponent(XMLBuilder fieldElement) throws Exception {
        Map<String, Object> fieldProp = new HashMap<>();
        fieldProp.put("name", fieldElement.getElement(PackageConstants.NAME).getText());
        fieldProp.put("moduleName", fieldElement.getElement(PackageConstants.MODULENAME).getText());
        fieldProp.put("displayName", fieldElement.getElement(PackageConstants.DISPLAY_NAME).getText());
        fieldProp.put("dataType", Integer.parseInt(fieldElement.getElement(PackageConstants.FieldXMLConstants.DATA_TYPE).getText()));
        fieldProp.put("isRequired", Boolean.parseBoolean(fieldElement.getElement(PackageConstants.FieldXMLConstants.REQUIRED).getText()));
        fieldProp.put("isDefault", Boolean.parseBoolean(fieldElement.getElement(PackageConstants.FieldXMLConstants.IS_DEFAULT).getText()));
        fieldProp.put("displayType", Integer.parseInt(fieldElement.getElement(PackageConstants.FieldXMLConstants.DISPLAY_TYPE).getText()));
        fieldProp.put("isMainField", Boolean.parseBoolean(fieldElement.getElement(PackageConstants.FieldXMLConstants.MAIN_FIELD).getText()));

        FacilioField facilioField = setAdditionalFieldProps(fieldProp, fieldElement);
        return facilioField;
    }


    private static FacilioField setAdditionalFieldProps(Map<String, Object> fieldProp, XMLBuilder fieldElement) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        int dataTypeInt = (int) (fieldProp.get("dataType"));
        FacilioField facilioField;

        switch (FieldType.getCFType(dataTypeInt)) {
            case STRING:
            case BIG_STRING:
                String regex = fieldElement.getElement("regex").getText();
                int maxLength = Integer.parseInt(fieldElement.getElement("maxLength").getText());
                facilioField = (StringField) FieldUtil.getAsBeanFromMap(fieldProp, StringField.class);
                ((StringField) facilioField).setRegex(regex);
                ((StringField) facilioField).setMaxLength(maxLength);
                break;

            case NUMBER:
            case DECIMAL:
                String unit = fieldElement.getElement("unit").getText();
                int unitId = Integer.parseInt(fieldElement.getElement("unitId").getText());
                int metric = Integer.parseInt(fieldElement.getElement("metric").getText());
                Double minValue = StringUtils.isEmpty(fieldElement.getElement("minValue").getText()) ? null :
                        Double.parseDouble(fieldElement.getElement("minValue").getText());
                Double maxValue = StringUtils.isEmpty(fieldElement.getElement("maxValue").getText()) ? null :
                        Double.parseDouble(fieldElement.getElement("maxValue").getText());
                boolean counterField = Boolean.parseBoolean(fieldElement.getElement("counterField").getText());
                facilioField = (NumberField) FieldUtil.getAsBeanFromMap(fieldProp, NumberField.class);
                ((NumberField) facilioField).setUnit(unit);
                ((NumberField) facilioField).setUnitId(unitId);
                ((NumberField) facilioField).setMetric(metric);
                ((NumberField) facilioField).setMinValue(minValue);
                ((NumberField) facilioField).setMaxValue(maxValue);
                ((NumberField) facilioField).setCounterField(counterField);
                break;

            case ENUM:
            case MULTI_ENUM:
                XMLBuilder enumValuesBuilder = fieldElement.getElement("values");
                List<EnumFieldValue<Integer>> enumValues = getEnumFieldValues(enumValuesBuilder);
                if (dataTypeInt == 8) {
                    facilioField = (EnumField) FieldUtil.getAsBeanFromMap(fieldProp, EnumField.class);
                    ((EnumField) facilioField).setValues(enumValues);
                } else {
                    facilioField = (MultiEnumField) FieldUtil.getAsBeanFromMap(fieldProp, MultiEnumField.class);
                    ((MultiEnumField) facilioField).setValues(enumValues);
                }
                break;

            case SYSTEM_ENUM:
                facilioField = (SystemEnumField) FieldUtil.getAsBeanFromMap(fieldProp, SystemEnumField.class);
                String enumName = fieldElement.getElement("enumName").getText();
                ((SystemEnumField) facilioField).setEnumName(enumName);
                break;

            case STRING_SYSTEM_ENUM:
                facilioField = (StringSystemEnumField) FieldUtil.getAsBeanFromMap(fieldProp, StringSystemEnumField.class);
                String stringEnumName = fieldElement.getElement("enumName").getText();
                ((StringSystemEnumField) facilioField).setEnumName(stringEnumName);
                break;

            case URL_FIELD:
                facilioField = (UrlField) FieldUtil.getAsBeanFromMap(fieldProp, UrlField.class);
                boolean showAlt = Boolean.parseBoolean(fieldElement.getElement("showAlt").getText());
                String targetStr = fieldElement.getElement("target").getText();
                UrlField.UrlTarget urlTarget = UrlField.UrlTarget.valueOf(targetStr);
                ((UrlField) facilioField).setShowAlt(showAlt);
                ((UrlField) facilioField).setTarget(urlTarget);
                break;

            case DATE:
            case DATE_TIME:
                facilioField = (DateField) FieldUtil.getAsBeanFromMap(fieldProp, DateField.class);
                if (fieldElement.getElement("allowedDate") != null) {
                    String allowedDateStr = fieldElement.getElement("allowedDate").getText();
                    DateValidatorType allowedDate = DateValidatorType.valueOf(allowedDateStr);
                    ((DateField) facilioField).setAllowedDate(allowedDate);
                }
                XMLBuilder valuesBuilder = fieldElement.getElement("allowedDays");
                List<DayOfWeek> daysOfWeek = getDaysOfWeek(valuesBuilder);
                ((DateField) facilioField).setAllowedDays(daysOfWeek);
                break;

            case BOOLEAN:
                facilioField = (BooleanField) FieldUtil.getAsBeanFromMap(fieldProp, BooleanField.class);
                String trueVal = fieldElement.getElement("trueVal").getText();
                String falseVal = fieldElement.getElement("falseVal").getText();
                ((BooleanField) facilioField).setTrueVal(trueVal);
                ((BooleanField) facilioField).setFalseVal(falseVal);
                break;

            case LARGE_TEXT:
                facilioField = (LargeTextField) FieldUtil.getAsBeanFromMap(fieldProp, LargeTextField.class);
                boolean skipSizeCheck = Boolean.parseBoolean(fieldElement.getElement("skipSizeCheck").getText());
                ((LargeTextField) facilioField).setSkipSizeCheck(skipSizeCheck);
                break;

            case FILE:
                facilioField = (FileField) FieldUtil.getAsBeanFromMap(fieldProp, FileField.class);
                if (fieldElement.getElement("format") != null) {
                    String fileFormatString = fieldElement.getElement("format").getText();
                    FileInfo.FileFormat fileFormat = FileInfo.FileFormat.valueOf(fileFormatString);
                    ((FileField) facilioField).setFormat(fileFormat);
                }
                break;

            case LOOKUP:
            case MULTI_LOOKUP:
                long lookupModuleId = -1;
                FacilioModule lookupModule = null;
                String specialType = fieldElement.getElement("specialType").getText();
                String lookupModuleName = fieldElement.getElement("lookupModuleName").getText();
                String relatedListDisplayName = fieldElement.getElement("relatedListDisplayName").getText();
                if (StringUtils.isNotEmpty(lookupModuleName)) {
                    lookupModule = moduleBean.getModule(lookupModuleName);
                    if (lookupModule != null) lookupModuleId = lookupModule.getModuleId();
                }

                if (dataTypeInt == FieldType.LOOKUP.getTypeAsInt()) {
                    facilioField = (LookupField) FieldUtil.getAsBeanFromMap(fieldProp, LookupField.class);
                    ((LookupField) facilioField).setRelatedListDisplayName(relatedListDisplayName);
                    ((LookupField) facilioField).setLookupModuleId(lookupModuleId);
                    ((LookupField) facilioField).setLookupModule(lookupModule);
                    ((LookupField) facilioField).setSpecialType(specialType);
                } else {
                    facilioField = (MultiLookupField) FieldUtil.getAsBeanFromMap(fieldProp, MultiLookupField.class);
                    ((MultiLookupField) facilioField).setRelatedListDisplayName(relatedListDisplayName);
                    ((MultiLookupField) facilioField).setLookupModuleId(lookupModuleId);
                    ((MultiLookupField) facilioField).setLookupModule(lookupModule);
                    ((MultiLookupField) facilioField).setSpecialType(specialType);
                }
                break;

            case CURRENCY_FIELD:
                facilioField = (CurrencyField) FieldUtil.getAsBeanFromMap(fieldProp, CurrencyField.class);
                break;

            default:
                facilioField = (FacilioField) FieldUtil.getAsBeanFromMap(fieldProp, FacilioField.class);
                break;
        }
        facilioField.setDisplayType(FacilioField.FieldDisplayType.TYPE_MAP.get(fieldProp.get("displayType")));
        return facilioField;
    }

    private static List<EnumFieldValue<Integer>> getEnumFieldValues(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<EnumFieldValue<Integer>> enumFieldValues = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList(PackageConstants.VALUE_ELEMENT);
        for (XMLBuilder xmlBuilder : allValues) {
            String value = xmlBuilder.getElement("value").getText();
            Integer index = Integer.valueOf(xmlBuilder.getElement("index").getText());
            int sequence = Integer.parseInt(xmlBuilder.getElement("sequence").getText());
            boolean visible = Boolean.parseBoolean(xmlBuilder.getElement("visible").getText());

            EnumFieldValue<Integer> enumFieldValue = new EnumFieldValue<>(index, value, sequence, visible);
            enumFieldValues.add(enumFieldValue);
        }

        return enumFieldValues;
    }

    private static List<DayOfWeek> getDaysOfWeek(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList(PackageConstants.VALUE_ELEMENT);
        for (XMLBuilder xmlBuilder : allValues) {
            String value = xmlBuilder.getElement("value").getText();
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(value);
            dayOfWeeks.add(dayOfWeek);
        }

        return dayOfWeeks;
    }

    public static void deleteV3OldRecordFromTargetOrg(String moduleName, Map<String, Long> sourceOrgComponentUIdVsId, List<Long> targetOrgComponentIds) throws Exception {
        if (CollectionUtils.isNotEmpty(targetOrgComponentIds) && MapUtils.isNotEmpty(sourceOrgComponentUIdVsId)) {
            targetOrgComponentIds.removeAll(sourceOrgComponentUIdVsId.values());
            if (CollectionUtils.isNotEmpty(targetOrgComponentIds)) {
                for (long id : targetOrgComponentIds) {
                    JSONObject data = new JSONObject();
                    data.put(moduleName, id);
                    V3Util.deleteRecords(moduleName, data, null, null, false);
                }
            }
        }
    }

    public static FacilioField getFieldFromDB(FacilioModule module, String fieldName) throws Exception {
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(fieldsModule.getTableName())
                .select(Collections.singleton(FieldFactory.getNumberField("fieldId", "FIELDID", fieldsModule)))
                .andCondition(CriteriaAPI.getCondition("NAME", "name", fieldName, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        Map<String, Object> fieldObj = selectBuilder.fetchFirst();

        if (MapUtils.isNotEmpty(fieldObj)) {
            return FieldUtil.getAsBeanFromMap(fieldObj, FacilioField.class);
        }
        return null;
    }

    public static void deleteStateTransitions(long stateFlowId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("STATE_FLOW_ID", "stateFlowId", String.valueOf(stateFlowId), NumberOperators.EQUALS));
        List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getStateTransitions(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), criteria);
        List<Long> transitionIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(stateTransitions)) {
            transitionIds = stateTransitions.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList());
        }
        PackageUtil.deleteWorkFlowRules(transitionIds);
    }

    public static List<Map<String, Object>> addEMailAttachments(XMLBuilder element) throws Exception {
        XMLBuilder attachmentListElement = element.getElement(PackageConstants.EmailConstants.ATTACHMENT_LIST);
        List<Map<String, Object>> attachmentList = new ArrayList<>();
        if (attachmentListElement != null) {
            List<XMLBuilder> attachmentElements = attachmentListElement.getElementList(PackageConstants.EmailConstants.ATTACHMENT);
            for (XMLBuilder attachmentElement : attachmentElements) {
                if (attachmentElement != null) {
                    FileContext fileContext = PackageFileUtil.addMetaFileAndGetContext(attachmentElement);
                    if (fileContext != null) {
                        Map<String, Object> attachmentObj = new HashMap<>();
                        attachmentObj.put("fileId", fileContext.getFileId());
                        attachmentObj.put("type", 1L);
                        attachmentList.add(attachmentObj);
                    }
                }
            }
        }
        return attachmentList;
    }
}
