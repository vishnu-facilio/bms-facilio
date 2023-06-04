package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PackageBeanUtil {
    public static Map<String, Long> getAppNameVsAppId() throws Exception {
        List<ApplicationContext> applicationContexts = ApplicationApi.getAllApplicationsWithOutFilter();
        Map<String, Long> appNameVsAppId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applicationContexts)) {
            appNameVsAppId = applicationContexts.stream().collect(Collectors.toMap(ApplicationContext::getLinkName, ApplicationContext::getId));
        }

        return appNameVsAppId;
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

    public static Map<String,Long> getFormIdsFromNames(List<String>formNames, long moduleId) throws Exception {
        FacilioModule formModule = ModuleFactory.getFormModule();
        List<FacilioField> formFields = FieldFactory.getFormFields();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(formFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(formModule.getTableName())
                .select(formFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(formNames, ","), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), Collections.singleton(moduleId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = builder.get();

        Map<String,Long> formNameVsId = new HashMap<>();
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

        Map<String, Object> prop = fieldSelectBuilder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;
    }

    public static XMLBuilder constructBuilderFromCriteria(Criteria criteria, XMLBuilder xmlBuilder) throws Exception {
        if (criteria == null || criteria.isEmpty()) {
            return xmlBuilder;
        }

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
                        .addElement(constructBuilderFromCriteria(condition.getCriteriaValue(), conditionElement.element(PackageConstants.CriteriaConstants.CRITERIA)));
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
            roleNameVsRoleId = allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId));
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
}
