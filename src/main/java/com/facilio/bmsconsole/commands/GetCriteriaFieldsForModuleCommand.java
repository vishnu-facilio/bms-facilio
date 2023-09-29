package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.context.filters.FilterOperator;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PeopleOperator;
import com.facilio.db.criteria.operators.UserOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.MailConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import lombok.NonNull;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class GetCriteriaFieldsForModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = new ArrayList<>();
        if (module != null) {
            if(module.isCustom()) {
                fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
            }
            else {
                switch (moduleName) {
                    case FacilioConstants.ContextNames.WORK_ORDER://add module specific fields in switch cases
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.ASSET:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.SITE:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.FLOOR:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.BUILDING:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.SPACE:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.JOB_PLAN:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    case FacilioConstants.ContextNames.SERVICE_REQUEST:
                        fields = FieldUtil.getFieldsByAccessType(FacilioField.AccessType.CRITERIA.getVal(), moduleName);
                        break;
                    default:
                        fields = null;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(fields)) {
            List<FilterFieldContext> filterFields = createFilterFields(moduleName, fields);
            context.put(FacilioConstants.ContextNames.FIELDS, filterFields);
        }
        return false;
    }

    private List<FilterFieldContext> createFilterFields (String moduleName, List<FacilioField> fields) throws Exception { // Have to check how special handling can be done here for each module. These shouldn't be required once FieldAccessSpecifiers are migrated
        List<FilterFieldContext> filterFields = null;
        if (CollectionUtils.isNotEmpty(fields)) {
            filterFields = new ArrayList<>();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            Objects.requireNonNull(module, "Invalid module name when fetching filter fields");

            for (FacilioField field : filterFields(module, fields)) {
                FilterFieldContext filterField = createFilterField(module, field);
                if (filterField != null) {
                    filterFields.add(filterField);
                }
            }
        }
        Collections.sort(filterFields, Comparator.comparing(FilterFieldContext::isMain).reversed()
                .thenComparing(Comparator.comparing(FilterFieldContext::getDisplayName))
        ); //Sorting in java because we take fields from cache and Collections.sort is pretty efficient
        return filterFields;
    }


    private List<FacilioField> filterFields (FacilioModule module, List<FacilioField> fields) throws Exception {
        fields = filterModuleFields(module, fields);
        if (!module.getName().equals(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER)
                && module.getModuleId() > 1) { // Ignoring spl modules if at all this is used for those things
            fields.add(FieldFactory.getIdField(module));
        }
        return fields;
        // return filterScopeFields(module, fields);
    }

    private FilterFieldContext createFilterField(FacilioModule module, FacilioField field) throws Exception { // We can do special handling here also maybe

        switch (field.getDataTypeEnum()) {
            case FILE:
            case MISC:
            case LINE_ITEM:
            case LARGE_TEXT:
                return null;
        }

        FilterFieldContext filterField = null;
        switch (field.getName()) {
            case "id" :
                filterField = getIdFilterField(field); // Handling here just in case advanced filter is used for non modules which has ID field already
                break;
            case "stateFlowId" :
            case "slaPolicyId" :
            case "approvalFlowId" :
            case "formId" :
            case "approvalStatus" :
                filterField = null;
                break;
            case "siteId" :
                filterField = new FilterFieldContext(FieldFactory.getSiteField(module), false);
                break;
            case "moduleState" :
                filterField = module.isStateFlowEnabled() ? new FilterFieldContext(field, createStateFieldFilter(module)) : null;
                break;
            default:
                filterField = new FilterFieldContext(field);
                break;
        }

        if (filterField != null) {
            handleSpecialOperators(filterField);
        }

        return filterField;
    }

    private static final List<String> SENSOR_ALARM_FIELDS_TO_HIDE = Arrays.asList(new String[] {"readingFieldId", "type", "noOfNotes", "lastWoId", "lastOccurrenceId", "key", "description", "noOfOccurrences"});
    private static final String WORKORDER_CLIENT_FIELD = "client";
    private static final String WORKORDER_TENANT_FIELD = "tenant";
    private static final String WORKORDER_VENDOR_FIELD = "vendor";

    private List<FacilioField> filterModuleFields (FacilioModule module, List<FacilioField> fields) throws Exception {
        if (AssetsAPI.isAssetsModule(module)) {
            return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.ASSET_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
        }
        else {
            switch (module.getName()) {
                case  FacilioConstants.ContextNames.WORK_ORDER:
                    List<FacilioField> filteredFields = FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.WORK_ORDER_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                    List<String> splLicenseBasedFields = new ArrayList<>();
                    if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
                        splLicenseBasedFields.add(WORKORDER_CLIENT_FIELD);
                    }
                    if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                        splLicenseBasedFields.add(WORKORDER_TENANT_FIELD);
                    }
                    if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VENDOR)) {
                        splLicenseBasedFields.add(WORKORDER_VENDOR_FIELD);
                    }
                    return FieldFactory.Fields.filterOutFields(filteredFields, splLicenseBasedFields, FieldFactory.Fields.FilterType.EXCLUDE);
                case FacilioConstants.ContextNames.QUOTE:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.QUOTE_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM:
                    return FieldFactory.Fields.filterOutFields(fields, SENSOR_ALARM_FIELDS_TO_HIDE, FieldFactory.Fields.FilterType.EXCLUDE);
                case FacilioConstants.ContextNames.NEW_READING_RULE_MODULE:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.NEW_READING_RULE_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.POINTS:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.POINT_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.PLANNEDMAINTENANCE:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.PM_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.OUTGOING_MAIL_LOGGER_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.AGENT_ALARM:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.AGENT_ALARM_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.Workflow.WORKFLOW_LOG:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.WORKFLOW_LOG_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.BASE_MAIL_MESSAGE:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.BASEMAIL_LOG_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.AGENT:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.AGENT_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.SHIFT:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.SHIFT_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                case FacilioConstants.ContextNames.BREAK:
                    return FieldFactory.Fields.filterOutFields(fields, FieldFactory.Fields.BREAK_FIELDS_INCLUDE, FieldFactory.Fields.FilterType.INCLUDE);
                default:
                    return fields;
            }
        }
    }
    private void handleSpecialOperators (FilterFieldContext field) throws Exception {
        if (field.getLookupModule() != null && StringUtils.isNotEmpty(field.getLookupModule().getName()) ) {
            switch (field.getLookupModule().getName()) {
                case FacilioConstants.ContextNames.RESOURCE:
                case FacilioConstants.ContextNames.BASE_SPACE:
                    field.setOperators(Collections.singletonList(new FilterOperator(BuildingOperator.BUILDING_IS, "within", true)));
                    break;
                case FacilioConstants.ContextNames.USERS:
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    field.setOperators(Arrays.asList(
                            new FilterOperator(
                                    UserOperators.ROLE_IS,
                                    "with role",
                                    new FilterFieldContext.FilterFieldLookupModule(modBean.getModule(FacilioConstants.ContextNames.ROLE))
                            ),
                            new FilterOperator(
                                    PeopleOperator.CURRENT_USER,
                                    "Logged In User",
                                    true
                            )));
                    break;
                case FacilioConstants.ContextNames.PEOPLE:
                    field.setOperators(Arrays.asList(
                            new FilterOperator(
                                    PeopleOperator.CURRENT_USER,
                                    "Logged In User",
                                    true
                            )));
                    break;
                case FacilioConstants.ContextNames.TENANT:
                    field.setOperators(Arrays.asList(
                            new FilterOperator(
                                    PeopleOperator.CURRENT_TENANT,
                                    "Logged In Tenant",
                                    true
                            )));
                    break;
                case FacilioConstants.ContextNames.CLIENT:
                    field.setOperators(Arrays.asList(
                            new FilterOperator(
                                    PeopleOperator.CURRENT_CLIENT,
                                    "Logged In Client",
                                    true
                            )));
                    break;
                case FacilioConstants.ContextNames.VENDOR:
                    field.setOperators(Arrays.asList(
                            new FilterOperator(
                                    PeopleOperator.CURRENT_VENDOR,
                                    "Logged In Vendor",
                                    true
                            )));
                    break;
            }
        }
    }


    private FilterFieldContext getIdFilterField (@NonNull FacilioField field) {
        if (field.getDataTypeEnum() == FieldType.ID) {
            return new FilterFieldContext(field, FieldUtil.getRecordIdFieldName(field.getModule()));
        }
        else {
            return new FilterFieldContext(field);
        }
    }

    private JSONObject createStateFieldFilter(FacilioModule module) {
        JSONObject operator = new JSONObject();
        operator.put("operatorId", NumberOperators.EQUALS.getOperatorId());
        JSONArray values = new JSONArray();
        values.add(String.valueOf(module.getModuleId()));
        operator.put("value", values);

        JSONObject filter = new JSONObject();
        filter.put("parentModuleId", operator);
        return filter;
    }
}
