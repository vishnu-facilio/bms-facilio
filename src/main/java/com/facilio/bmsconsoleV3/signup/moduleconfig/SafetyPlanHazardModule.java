package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class SafetyPlanHazardModule extends BaseModuleConfig{
    public SafetyPlanHazardModule(){
        setModuleName(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> safetyPlanHazard = new ArrayList<FacilioView>();
        safetyPlanHazard.add(getAllSafetyPlanHazardsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SAFETYPLAN_HAZARD);
        groupDetails.put("views", safetyPlanHazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSafetyPlanHazardsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Safety Plan Hazards");

        return allView;
    }
}
