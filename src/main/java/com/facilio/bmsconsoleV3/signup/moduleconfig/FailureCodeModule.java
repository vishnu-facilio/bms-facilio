package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class FailureCodeModule extends BaseModuleConfig {
    public FailureCodeModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FAILURE_CODE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> failureCode = new ArrayList<FacilioView>();
        failureCode.add(getAllFailureCodes().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FAILURE_CODE);
        groupDetails.put("views", failureCode);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFailureCodes() {

        FacilioModule failureCodeModule = ModuleFactory.getFailureCodeModule();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setColumnName("ID");
        id.setDataType(FieldType.NUMBER);
        id.setModule(failureCodeModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Failure Codes");
        allView.setModuleName(failureCodeModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(id, false)));

        return allView;
    }
}
