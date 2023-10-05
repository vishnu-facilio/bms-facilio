package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.context.filters.FilterOperator;
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
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class AdvancedFilterFieldsResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        // siteId field
        if (FieldUtil.isSiteIdFieldPresent(module, true) && fieldsList.stream().noneMatch(field -> field.getName().equals("siteId"))) {
            fieldsList.add(FieldFactory.getSiteIdField(module));
        }

        // id field (ignoring spl modules)
        if (!module.getName().equals(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER) && !module.getName().equals(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS)
                && module.getModuleId() > 1) {
            fieldsList.add(FieldFactory.getIdField(module));
        }

        List<FilterFieldContext> filterFieldContextList = new ArrayList<>();
        for (FacilioField field : fieldsList) {
            FilterFieldContext filterField = null;
            if (field.getName().equals("id")) {
                // Handling here just in case advanced filter is used for non modules which has ID field already
                filterField = getIdFilterField(field);
            } else if (field.getName().equals("siteId")) {
                filterField = new FilterFieldContext(FieldFactory.getSiteField(module), false);
            } else if (field.getName().equals("moduleState")) {
                filterField = new FilterFieldContext(field, createStateFieldFilter(module));
            } else {
                filterField = new FilterFieldContext(field);
            }

            if(filterField.getLookupModule() != null && StringUtils.isNotEmpty(filterField.getLookupModule().getName())) {
                handleSpecialOperators(filterField);
            }
            filterFieldContextList.add(filterField);
        }

        Collections.sort(filterFieldContextList, Comparator.comparing(FilterFieldContext::isMain).reversed()
                .thenComparing(Comparator.comparing(FilterFieldContext::getDisplayName))
        );

        context.put(FacilioConstants.ContextNames.FIELDS, filterFieldContextList);

        return false;
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
                case FacilioConstants.ContextNames.VENDORS:
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
}
