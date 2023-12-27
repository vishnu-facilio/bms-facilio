package com.facilio.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiCurrencyConstants {
    public static final List<String> MULTI_CURRENCY_ENABLED_MODULES = Collections.unmodifiableList(getMultiCurrencyEnabledModuleNames());
    public static final List<String> MULTI_CURRENCY_CUSTOM_FIELD_ADDITION_MODULES = Collections.unmodifiableList(getMultiCurrencyCustomFieldAdditionModules());

    private static List<String> getMultiCurrencyCustomFieldAdditionModules() {
        List<String> moduleNames = new ArrayList<>();
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_ORDER);
        moduleNames.add(FacilioConstants.ContextNames.QUOTE);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_REQUEST);
        moduleNames.add(FacilioConstants.ContextNames.WORK_ORDER);
        moduleNames.add(FacilioConstants.ContextNames.ASSET);
        return moduleNames;
    }

    private static List<String> getMultiCurrencyEnabledModuleNames() {
        List<String> moduleNames = new ArrayList<>();
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_ORDER);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        moduleNames.add(FacilioConstants.ContextNames.QUOTE);
        moduleNames.add(FacilioConstants.ContextNames.QUOTE_LINE_ITEMS);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_REQUEST);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
        moduleNames.add(FacilioConstants.ContextNames.WORK_ORDER);
        moduleNames.add(FacilioConstants.ContextNames.TICKET);
        moduleNames.add(FacilioConstants.ContextNames.RESOURCE);
        moduleNames.add(FacilioConstants.ContextNames.ASSET);
        moduleNames.add(FacilioConstants.ContextNames.AHU);
        moduleNames.add(FacilioConstants.ContextNames.CHILLER_CONDENSER_PUMP);
        moduleNames.add(FacilioConstants.ContextNames.CHILLER);
        moduleNames.add(FacilioConstants.ContextNames.CHILLER_PRIMARY_PUMP);
        moduleNames.add(FacilioConstants.ContextNames.CHILLER_SECONDARY_PUMP);
        moduleNames.add(FacilioConstants.ContextNames.CONTROLLER_ASSET);
        moduleNames.add(FacilioConstants.ContextNames.COOLING_TOWER);
        moduleNames.add(FacilioConstants.ContextNames.CUSTOM_KIOSK);
        moduleNames.add(FacilioConstants.ModuleNames.DEVICES);
        moduleNames.add(FacilioConstants.ContextNames.ENERGY_METER);
        moduleNames.add(FacilioConstants.ContextNames.FCU);
        moduleNames.add(FacilioConstants.ContextNames.HEAT_PUMP);
        moduleNames.add(FacilioConstants.ContextNames.UTILITY_METER);
        moduleNames.add(FacilioConstants.ContextNames.WATER_METER);
        moduleNames.add(FacilioConstants.ContextNames.WORKORDER_COST);
        moduleNames.add(FacilioConstants.ContextNames.WORKORDER_ITEMS);
        moduleNames.add(FacilioConstants.ContextNames.WORKORDER_TOOLS);
        moduleNames.add(FacilioConstants.ContextNames.WO_SERVICE);
        moduleNames.add(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
        moduleNames.add(FacilioConstants.ContextNames.WO_PLANNED_TOOLS);
        moduleNames.add(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
        moduleNames.add(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
        moduleNames.add(FacilioConstants.ContextNames.WO_LABOUR);
        moduleNames.add(FacilioConstants.ContextNames.Budget.BUDGET);
        moduleNames.add(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
        moduleNames.add(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
        moduleNames.add(FacilioConstants.ContextNames.TRANSACTION);
        moduleNames.add(FacilioConstants.ContextNames.ITEM);
        moduleNames.add(FacilioConstants.ContextNames.ITEM_TYPES);
        moduleNames.add(FacilioConstants.ContextNames.TOOL);
        moduleNames.add(FacilioConstants.ContextNames.TOOL_TYPES);
        moduleNames.add(FacilioConstants.ContextNames.SERVICE);
        moduleNames.add(FacilioConstants.ContextNames.SERVICE_VENDOR);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASED_ITEM);
        moduleNames.add(FacilioConstants.ContextNames.PURCHASED_TOOL);
        moduleNames.add(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        moduleNames.add(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        return moduleNames;
    }
}
