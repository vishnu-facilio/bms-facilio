package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class PreventiveMaintenanceModule extends BaseModuleConfig{
    public PreventiveMaintenanceModule(){
        setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
    }

    @Override
    protected void addTriggers() throws Exception {
        return;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> preventiveMaintanance = new ArrayList<FacilioView>();
        preventiveMaintanance.add(getStatusPreventiveWorkOrders("active", "Active", true).setOrder(order++));
        preventiveMaintanance.add(getTypePreventiveWorkOrders("preventive", "Preventive", "Preventive").setOrder(order++));
        preventiveMaintanance.add(getTypePreventiveWorkOrders("corrective", "Corrective", "Corrective").setOrder(order++));
        preventiveMaintanance.add(getTypePreventiveWorkOrders("rounds", "Rounds", "Rounds").setOrder(order++));
        preventiveMaintanance.add(getTypePreventiveWorkOrders("breakdown", "Breakdown", "Breakdown").setOrder(order++));
        preventiveMaintanance.add(getTypePreventiveWorkOrders("compliance", "Compliance", "Compliance").setOrder(order++));
        preventiveMaintanance.add(getAllPreventiveWorkOrders().setOrder(order++));
        preventiveMaintanance.add(getStatusPreventiveWorkOrders("inactive", "Inactive", false).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        groupDetails.put("views", preventiveMaintanance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getStatusPreventiveWorkOrders(String name, String displayName, boolean status) {

        Condition statusCondition = getPreventiveStatusCondition(status);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusCondition);

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getTypePreventiveWorkOrders(String name, String displayName, String type) {

        List<FacilioField> templateFields = FieldFactory.getWorkOrderTemplateFields();
        Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(templateFields);
        LookupField typeIdField = (LookupField) fieldProps.get("typeId");

        FacilioModule typeModule = ModuleFactory.getTicketTypeModule();
        FacilioField nameField = FieldFactory.getField("name", "NAME", typeModule, FieldType.STRING);
        Condition nameCondition = CriteriaAPI.getCondition(nameField, type, StringOperators.IS);
        Criteria crit = new Criteria();
        crit.addAndCondition(nameCondition);

        Condition typeCondition = CriteriaAPI.getCondition(typeIdField, crit, LookupOperator.LOOKUP);
        Condition statusCondition = getPreventiveStatusCondition(true);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(typeCondition);
        criteria.addAndCondition(statusCondition);

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getAllPreventiveWorkOrders() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    public static Condition getPreventiveStatusCondition(boolean status) {
        List<FacilioField> preventiveFields = FieldFactory.getPreventiveMaintenanceFields();
        Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(preventiveFields);
        FacilioField statusField = fieldProps.get("status");

        Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);

        return statusCondition;
    }
}
