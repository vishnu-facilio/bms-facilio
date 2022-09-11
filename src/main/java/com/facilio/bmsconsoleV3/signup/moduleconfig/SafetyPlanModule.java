package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class SafetyPlanModule extends BaseModuleConfig{
    public SafetyPlanModule(){
        setModuleName(FacilioConstants.ContextNames.SAFETY_PLAN);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> safetyPlan = new ArrayList<FacilioView>();
        safetyPlan.add(getAllSafetyPlansView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SAFETY_PLAN);
        groupDetails.put("views", safetyPlan);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSafetyPlansView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Safety Plans");

        return allView;
    }
}

