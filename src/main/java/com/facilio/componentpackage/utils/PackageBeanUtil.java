package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
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
        if (StringUtils.isNotEmpty(formField.getDisplayName())) {
            fieldSelectBuilder.andCondition(CriteriaAPI.getCondition("DISPLAYNAME", "displayName", formField.getDisplayName(), StringOperators.IS));
        }

        Map<String, Object> prop = fieldSelectBuilder.fetchFirst();
        return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;
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

                if (condition.getOperatorId() == 36 || condition.getOperatorId() == 37) {
                    String fieldName = condition.getFieldName();
                    moduleName = fieldName.contains(".") ? fieldName.split("\\.")[0] : moduleName;

                    FacilioModule facilioModule = moduleBean.getModule(moduleName);
                    if (!(facilioModule.getTypeEnum() == FacilioModule.ModuleType.PICK_LIST)) {
                        LOGGER.info("####Sandbox Tracking - Condition contains Module Record - " + moduleName + " FieldName - " + fieldName);
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

    public static void constructBuilderFromActionsList(List<ActionContext> actionsContextList, XMLBuilder actionsList) throws Exception {
        if (CollectionUtils.isEmpty(actionsContextList)) {
            return;
        }

        ModuleBean moduleBean = Constants.getModBean();
        for (ActionContext actionContext : actionsContextList) {
            // TODO - Handle DEFAULT_TEMPLATE_ID
            XMLBuilder actionElement = actionsList.element(PackageConstants.WorkFlowRuleConstants.ACTION);
            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_STATUS).text(String.valueOf(actionContext.isActive()));
            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_TYPE).text(actionContext.getActionTypeEnum().name());

            if (actionContext.getTemplate() != null) {
                // TODO - Handle WORKFLOW_ID, USER_WORKFLOW_ID
                Template templateContext = actionContext.getTemplate();
                XMLBuilder templateElement = actionElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE);
                templateElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE_NAME).text(templateContext.getName());
                templateElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE_TYPE).text(templateContext.getTypeEnum().name());
                templateElement.element(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER).text(templateContext.getPlaceholderStr());
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_FTL).text(String.valueOf(templateContext.isFtl()));
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_ATTACHMENT_ADDED).text(String.valueOf(templateContext.getIsAttachmentAdded()));

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

                    default:
                        break;
                }
            }
        }
    }

    public static List<ActionContext> constructActionContextsFromBuilder(XMLBuilder actionsList) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<ActionContext> actionContextList = new ArrayList<>();
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
                String placeholderStr = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.PLACEHOLDER).getText();
                String templateName = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE_NAME).getText();
                String templateTypeStr = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE_TYPE).getText();
                boolean isFtl = Boolean.parseBoolean(templateElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_FTL).getText());
                boolean isAttachmentAdded = Boolean.parseBoolean(templateElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_ATTACHMENT_ADDED).getText());

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

                        default:
                            break;
                    }

                    actionContext.setTemplateJson(templateJson);
                }
            }
        }
        return actionContextList;
    }
}
