package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class FailureClassModule extends BaseModuleConfig {
    public FailureClassModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FAILURE_CLASS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> failureClass = new ArrayList<FacilioView>();
        failureClass.add(getAllFailureClass().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FAILURE_CLASS);
        groupDetails.put("views", failureClass);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFailureClass() {

        FacilioModule failureClassModule = ModuleFactory.getFailureClassModule();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setColumnName("ID");
        id.setDataType(FieldType.NUMBER);
        id.setModule(failureClassModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Failure Class");
        allView.setModuleName(failureClassModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(id, false)));

        return allView;
    }
}
