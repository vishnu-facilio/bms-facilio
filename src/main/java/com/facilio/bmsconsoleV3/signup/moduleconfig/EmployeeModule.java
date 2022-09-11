package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class EmployeeModule extends BaseModuleConfig{
    public EmployeeModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.EMPLOYEE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> employee = new ArrayList<FacilioView>();
        employee.add(getAllHiddenEmployees().setOrder(order++));
        employee.add(getAllEmployees().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.EMPLOYEE);
        groupDetails.put("views", employee);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);

        allView.setHidden(true);
        return allView;
    }

    private static FacilioView getAllEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-employees");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

}
