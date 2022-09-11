package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class HazardModule extends BaseModuleConfig{
    public HazardModule(){
        setModuleName(FacilioConstants.ContextNames.HAZARD);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> hazard = new ArrayList<FacilioView>();
        hazard.add(getAllHazardModuleView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.HAZARD);
        groupDetails.put("views", hazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHazardModuleView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Hazards");

        return allView;
    }
}

