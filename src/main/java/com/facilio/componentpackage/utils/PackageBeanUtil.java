package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsoleV3.context.asset.V3AssetTypeContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    public static final List<String> SUPPORTED_PICKLIST_MODULES = new ArrayList<String>(){{
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

    public static void bulkDeleteV3Records(String moduleName, List<Long> ids) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put(moduleName, ids);

        V3Util.deleteRecords(moduleName, data,null,null,false);
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
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("DISPLAYNAME", "displayName", formField.getDisplayName(), StringOperators.IS));
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

                if (StringUtils.isNotEmpty(condition.getValue()) && !DYNAMIC_CONDITION_VALUES.contains(condition.getValue())
                                                && (condition.getOperatorId() == 36 || condition.getOperatorId() == 37)) {
                    String[] valueArr = condition.getValue().trim().split(FacilioUtil.COMMA_SPLIT_REGEX);

                    String fieldName = condition.getFieldName();
                    moduleName = fieldName.contains(".") ? fieldName.split("\\.")[0] : moduleName;

                    FacilioModule facilioModule = moduleBean.getModule(moduleName);
                    FacilioField field = moduleBean.getField(fieldName, moduleName);

                    if (field instanceof LookupField) {
                        FacilioModule lookupModule = ((LookupField) field).getLookupModule();
                        if (lookupModule.getName().equals(FacilioConstants.ContextNames.TICKET_STATUS)) {
                            conditionElement.addElement(pickListXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.PICKLIST_ELEMENT), facilioModule.getName(), valueArr, true));
                        } else if (SUPPORTED_PICKLIST_MODULES.contains(lookupModule.getName())){
                            conditionElement.addElement(pickListXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.PICKLIST_ELEMENT), lookupModule.getName(), valueArr, false));
                        } else if (FacilioConstants.ContextNames.USERS.equals(lookupModule.getName())){
                            XMLBuilder userElementList = conditionElement.element(PackageConstants.CriteriaConstants.USER_ELEMENT_LIST);
                            for (String val : valueArr) {
                                userElementList.addElement(userXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.USER_ELEMENT), Long.parseLong(val)));
                            }
                        } else if (FacilioConstants.ContextNames.PEOPLE.equals(lookupModule.getName())){
                            XMLBuilder peopleElementList = conditionElement.element(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT_LIST);
                            for (String val : valueArr) {
                                peopleElementList.addElement(peopleXMLBuilder(conditionElement.element(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT), Long.parseLong(val)));
                            }
                        } else {
                            LOGGER.info("####Sandbox Tracking - Condition contains Module Record - " + moduleName + " FieldName - " + fieldName + " ConditionId - " + condition.getConditionId());
                        }
                    }
                }
            }
        }
        return xmlBuilder;
    }

    public static Criteria constructCriteriaFromBuilder(XMLBuilder xmlBuilder) throws Exception {
        if (xmlBuilder.getElement(PackageConstants.CriteriaConstants.PATTERN) == null) {
            return null;
        }
        String pattern = xmlBuilder.getElement(PackageConstants.CriteriaConstants.PATTERN).getText();
        XMLBuilder conditionsListElement = xmlBuilder.getElement(PackageConstants.CriteriaConstants.CONDITIONS_LIST);

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
                if (!DYNAMIC_CONDITION_VALUES.contains(value) && (operatorId == 36 || operatorId == 37)) {
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

                        List<String> valueArr = new ArrayList<>();
                        for (XMLBuilder valueElement : userElementList) {
                            long orgUserId = userValueBuilder(valueElement);
                            if (orgUserId > 0) {
                                valueArr.add(String.valueOf(orgUserId));
                            }
                        }
                        value = StringUtils.join(valueArr, ",");
                    } else if (peopleElement != null) {
                        List<XMLBuilder> peopleElementList = conditionElement.getElementList(PackageConstants.CriteriaConstants.PEOPLE_ELEMENT);

                        List<String> valueArr = new ArrayList<>();
                        for (XMLBuilder valueElement : peopleElementList) {
                            long peopleId = peopleValueBuilder(valueElement);
                            if (peopleId > 0) {
                                valueArr.add(String.valueOf(peopleId));
                            }
                        }
                        value = StringUtils.join(valueArr, ",");
                    }
                }

                Condition condition = new Condition();
                condition.setValue(value);
                condition.setFieldName(fieldName);
                condition.setColumnName(columnName);
                condition.setOperatorId(operatorId);
                condition.setJsonValueStr(jsonValue);
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
            return -1;
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
            return -1;
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

        return workFlowBuilder;
    }

    public static WorkflowContext constructWorkflowContextFromBuilder(XMLBuilder workFlowBuilder) {
        if (workFlowBuilder == null) {
            return null;
        }

        String v2WorkFlowStr = workFlowBuilder.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
        boolean isV2Script = Boolean.parseBoolean(workFlowBuilder.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());

        WorkflowContext workflowContext = new WorkflowContext();
        workflowContext.setWorkflowV2String(v2WorkFlowStr);
        workflowContext.setIsV2Script(isV2Script);
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

    public static void constructBuilderFromSharingContext(SharingContext<SingleSharingContext> sharingContext, XMLBuilder sharingElement) throws Exception {
        if (CollectionUtils.isEmpty(sharingContext)) {
            return;
        }

        List<Role> allRoles = getAllRoles();
        Map<Long, String> roleIdVsRoleName = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allRoles)) {
            roleIdVsRoleName = allRoles.stream().collect(Collectors.toMap(Role::getRoleId, Role::getName));
        }

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

                    break;

                default:
                    break;
            }
        }
    }

    public static SharingContext<SingleSharingContext> constructSharingContextFromBuilder(XMLBuilder sharingElement) throws Exception {
        XMLBuilder sharingContextElement = sharingElement.getElement(PackageConstants.SharingContextConstants.SHARING_CONTEXT);
        if (sharingContextElement == null) {
            return null;
        }

        List<Role> allRoles = getAllRoles();
        Map<String, Long> roleNameVsRoleId = new HashMap<>();

        if (CollectionUtils.isNotEmpty(allRoles)) {
            roleNameVsRoleId = allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId, (a, b) -> b));
        }

        SharingContext<SingleSharingContext> sharingContexts = new SharingContext<>();

        List<XMLBuilder> singleSharingElementsList = sharingContextElement.getElementList(PackageConstants.SharingContextConstants.SINGLE_SHARING_CONTEXT);
        for (XMLBuilder singleSharingElement : singleSharingElementsList) {
            String sharingTypeStr = singleSharingElement.getElement(PackageConstants.SharingContextConstants.SHARING_TYPE).text();
            SingleSharingContext.SharingType sharingType = SingleSharingContext.SharingType.valueOf(sharingTypeStr);

            SingleSharingContext singleSharingContext = new SingleSharingContext();
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
            if (actionContext.getDefaultTemplateId() > 0){
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
                if (StringUtils.isNotEmpty(templateContext.getPlaceholderStr()) && !(templateContext.getPlaceholderStr().equals("[]"))){
                    templateElement.element(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER).cData(templateContext.getPlaceholderStr());
                }
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_FTL).text(String.valueOf(templateContext.isFtl()));
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_ATTACHMENT_ADDED).text(String.valueOf(templateContext.getIsAttachmentAdded()));
                if (templateContext.getWorkflow() != null) {
                XMLBuilder workflow = templateElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW);
                workflow.element(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS).text(String.valueOf(templateContext.getWorkflow().getExpressions()));
                XMLBuilder parameterBuilder = workflow.element(PackageConstants.WorkFlowRuleConstants.PARAMETERS);
                List<ParameterContext> parameterList = templateContext.getWorkflow().getParameters();
                for (ParameterContext parameter : parameterList){
                   XMLBuilder parameters = parameterBuilder.element("parameter");
                   parameters.element("name").text(parameter.getName());
                   parameters.element("typeString").text(parameter.getTypeString());
                   if(parameter.getValue() != null) {
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
                        String appLinkName = appId > 0 ? ApplicationApi.getApplicationForId(appId).getLinkName() : null;
                        JSONObject obj = ((PushNotificationTemplate) templateContext).getOriginalTemplate();
                        UserNotificationContext userNotification = UserNotificationContext.instance(obj);
                        String moduleName = (String) ((JSONObject)obj.get("notification")).get("module_name");
                        long parentModuleId = moduleBean.getModule(moduleName).getModuleId();
                        userNotification.setParentModule(parentModuleId);
                        JSONObject structureObj = UserNotificationContext.getFcmObjectMaintainence(userNotification);
                        pushNotificationElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(appLinkName);
                        pushNotificationElement.element("id").text(((PushNotificationTemplate) templateContext).getTo());
                        pushNotificationElement.element("isSendNotification").text(String.valueOf(((PushNotificationTemplate) templateContext).getIsSendNotification()));
                        pushNotificationElement.element("name").text(templateContext.getName());
                        pushNotificationElement.element("data").text(structureObj.get("data").toString());
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
                        try {
                            JSONParser parser = new JSONParser();
                            String expString = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS).getText();
                            JSONArray expArray = (JSONArray) parser.parse(expString);
                            workflowParamExp.setExpressions(expArray);
                        } catch (Exception e) {

                        }
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
                                    Template emailStructureTemplate = TemplateAPI.getTemplate(emailStructureName, Template.Type.EMAIL_STRUCTURE);
                                    if (emailStructureTemplate != null) {
                                        templateJson.put(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_ID, emailStructureTemplate.getId());
                                        templateJson.put("message", ((EMailStructure)emailStructureTemplate).getMessage());
                                        templateJson.put("name", templateName);
                                        templateJson.put("subject", ((EMailStructure) emailStructureTemplate).getSubject());
                                    }else {
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
                                String data = valueElement.getElement("data").getText();
                                JSONParser parser = new JSONParser();
                                JSONObject dataObj = (JSONObject) parser.parse(data);
                                templateJson.put(PackageConstants.AppXMLConstants.APPLICATION,appId);
                                templateJson.put("id",valueElement.getElement("id").getText());
                                templateJson.put("body",valueElement.getElement("body").getText());
                                templateJson.put("isPushNotification",Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                templateJson.put("isSendNotification",Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                templateJson.put("message",dataObj.get("text"));
                                templateJson.put("subject",dataObj.get("title"));
                                templateJson.put("name",templateName);
                                templateJson.put("to",valueElement.getElement("id").getText());
                                templateJson.put("type",templateType);
                                templateJson.put("userWorkflow",FieldUtil.getAsProperties(userWorkflowContext));
                                templateJson.put("workflow",FieldUtil.getAsProperties(workflowParamExp));
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
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        return builder.get();
    }

    public static List<?> getModuleData(Criteria criteria, FacilioModule module, Class<?> clazz, boolean fetchDeleted) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordBuilder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()));

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


    public static long getFormRuleId(FormRuleContext formRule) throws Exception{

        GenericSelectRecordBuilder ruleSelectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormRuleModule().getTableName())
                .select(FieldFactory.getFormRuleFields());

        if(formRule.getFormId()>0){
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("FORM_ID", "formId", String.valueOf(formRule.getFormId()), NumberOperators.EQUALS));
        }

        if(formRule.getSubFormId()>0){
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("SUB_FORM_ID", "subFormId", String.valueOf(formRule.getSubFormId()), NumberOperators.EQUALS));
        }

        if(StringUtils.isNotEmpty(formRule.getName())){
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", formRule.getName(), StringOperators.IS));
        }

        if(StringUtils.isNotEmpty(formRule.getDescription())){
            ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("DESCRIPTION", "description", formRule.getDescription(), StringOperators.IS));
        }
        ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT_RULE","isDefault",String.valueOf(formRule.getIsDefault()), BooleanOperators.IS));

        Map<String, Object> prop = ruleSelectBuilder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;

    }

    public static void deleteV3OldRecordFromTargetOrg(String moduleName, Map<String, Long> sourceOrgComponentUIdVsId , List<Long> targetOrgComponentIds) throws Exception {
        if (CollectionUtils.isNotEmpty(targetOrgComponentIds) && MapUtils.isNotEmpty(sourceOrgComponentUIdVsId)) {
            targetOrgComponentIds.removeAll(sourceOrgComponentUIdVsId.values());
            if (CollectionUtils.isNotEmpty(targetOrgComponentIds)) {
                for (long id : targetOrgComponentIds) {
                    JSONObject data = new JSONObject();
                    data.put(moduleName, id);
                    V3Util.deleteRecords(moduleName, data,null,null,false);
                }
            }
        }
    }

}
