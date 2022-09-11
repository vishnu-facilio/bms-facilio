package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class WorkOrderHazardModule extends BaseModuleConfig{
    public WorkOrderHazardModule(){
        setModuleName(FacilioConstants.ContextNames.WORKORDER_HAZARD);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workorderHazard = new ArrayList<FacilioView>();
        workorderHazard.add(getAllWorkOrderHazardsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WORKORDER_HAZARD);
        groupDetails.put("views", workorderHazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderHazardsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Workorder Hazards");

        return allView;
    }
}
