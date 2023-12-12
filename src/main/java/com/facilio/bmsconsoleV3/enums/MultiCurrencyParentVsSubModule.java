package com.facilio.bmsconsoleV3.enums;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum MultiCurrencyParentVsSubModule {
    PURCHASE_REQUEST(FacilioConstants.ContextNames.PURCHASE_REQUEST, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS, FieldFactory.getField("purchaseRequest", "PR_ID", FieldType.LOOKUP)));
    }}),
    PURCHASE_ORDER(FacilioConstants.ContextNames.PURCHASE_ORDER, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, FieldFactory.getField("purchaseOrder", "PO_ID", FieldType.LOOKUP)));
    }}),
    WORK_ORDER(FacilioConstants.ContextNames.WORK_ORDER, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.WORKORDER_COST, FieldFactory.getField("parentId", "PARENT_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WORKORDER_ITEMS, FieldFactory.getField("workorder", "WO_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WORKORDER_TOOLS, FieldFactory.getField("workorder", "WO_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WO_SERVICE, FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER)));
        add(new SubModule(FacilioConstants.ContextNames.WO_LABOUR, FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER)));
        add(new SubModule(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, FieldFactory.getField("workOrder", "WORKORDER_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, FieldFactory.getField("workOrder", "WORKORDER_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, FieldFactory.getField("workOrder", "WORKORDER_ID", FieldType.LOOKUP)));
        add(new SubModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN, FieldFactory.getField("parent", "PARENT_ID", FieldType.LOOKUP)));
    }}),
    QUOTE(FacilioConstants.ContextNames.QUOTE, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.QUOTE_LINE_ITEMS, FieldFactory.getField("quote", "QUOTATION_ID", FieldType.LOOKUP)));
    }}),
    INVOICE(FacilioConstants.ContextNames.INVOICE, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.INVOICE_LINE_ITEMS, FieldFactory.getField("invoice", "INVOICE_ID", FieldType.LOOKUP)));
    }}),
    BUDGET(FacilioConstants.ContextNames.Budget.BUDGET, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT, FieldFactory.getField("budget", "BUDGET_ID", FieldType.LOOKUP)));
    }}),
    BUDGET_AMOUNT(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT, new ArrayList<SubModule>(){{
        add(new SubModule(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT, FieldFactory.getField("budgetAmount", "AMOUNT_ID", FieldType.LOOKUP)));
    }})
    ;

    MultiCurrencyParentVsSubModule(String moduleName, ArrayList<SubModule> subModulesList) {
        this.moduleName = moduleName;
        this.subModulesList = subModulesList;
    }
    private final String moduleName;
    private final List<SubModule> subModulesList;

    public static final Map<String, List<SubModule>> PARENT_MODULE_VS_SUB_MODULES = Collections.unmodifiableMap(initMap());
    public static final Map<String, FacilioField> SUBMODULE_VS_PARENT_MODULE_FIELD = Collections.unmodifiableMap(initSubModuleVsParentModuleFieldMap());
    public static final Map<String, String> SUBMODULE_VS_PARENT_MODULE = Collections.unmodifiableMap(initSubModuleVsParentModuleMap());
    private static Map<String, FacilioField> initSubModuleVsParentModuleFieldMap() {
        return PARENT_MODULE_VS_SUB_MODULES.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(SubModule::getModuleName, SubModule::getParentModuleField));
    }

    private static Map<String, String> initSubModuleVsParentModuleMap() {
        Map<String, String> subModuleVsParentModuleMap = new HashMap<>();
        for (MultiCurrencyParentVsSubModule value : values()) {
            for (SubModule subModule : value.getSubModulesList()) {
                subModuleVsParentModuleMap.put(subModule.getModuleName(), value.moduleName);
            }
        }
        return subModuleVsParentModuleMap;
    }
    private static Map<String, List<SubModule>> initMap() {
        Map<String, List<SubModule>> parentVsSubModulesMap = new HashMap<>();
        for (MultiCurrencyParentVsSubModule value : values()) {
            parentVsSubModulesMap.put(value.moduleName, value.getSubModulesList());
        }
        return parentVsSubModulesMap;
    }

    @Getter
    public static class SubModule {
        private final String moduleName;
        private final FacilioField parentModuleField;
        public SubModule(String subModuleName, FacilioField parentModuleField) {
            this.moduleName = subModuleName;
            this.parentModuleField = parentModuleField;
        }
    }
}
