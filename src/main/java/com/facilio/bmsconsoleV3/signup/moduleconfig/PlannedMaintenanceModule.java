package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PlannedMaintenanceModule extends BaseModuleConfig{
    public PlannedMaintenanceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> plannedMaintenance = new ArrayList<FacilioView>();
        plannedMaintenance.add(getAllPlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getActivePlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getInActivePlannedMaintenanceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        groupDetails.put("views", plannedMaintenance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPlannedMaintenanceView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Planned Maintenance");

        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getInActivePlannedMaintenanceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getInActivePlannedMaintenanceCondition());
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("inactive");
        allView.setDisplayName("Unpublished");
        allView.setCriteria(criteria);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getActivePlannedMaintenanceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActivePlannedMaintenanceCondition());
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("active");
        allView.setDisplayName("Published");
        allView.setCriteria(criteria);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    public static Condition getActivePlannedMaintenanceCondition() {
        FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField booleanField = new FacilioField();
        booleanField.setName("isActive");
        booleanField.setColumnName("IS_ACTIVE");
        booleanField.setDataType(FieldType.BOOLEAN);
        booleanField.setModule(module);

        Condition open = new Condition();
        open.setField(booleanField);
        open.setOperator(BooleanOperators.IS);
        open.setValue("true");

        return open;
    }

    public static Condition getInActivePlannedMaintenanceCondition() {
        FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField booleanField = new FacilioField();
        booleanField.setName("isActive");
        booleanField.setColumnName("IS_ACTIVE");
        booleanField.setDataType(FieldType.BOOLEAN);
        booleanField.setModule(module);

        Condition open = new Condition();
        open.setField(booleanField);
        open.setOperator(BooleanOperators.IS);
        open.setValue("false");

        return open;
    }
}

