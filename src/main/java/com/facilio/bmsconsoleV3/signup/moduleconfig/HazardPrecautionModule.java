package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class HazardPrecautionModule extends BaseModuleConfig{
    public HazardPrecautionModule(){
        setModuleName(FacilioConstants.ContextNames.HAZARD_PRECAUTION);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> hazardPrecaution = new ArrayList<FacilioView>();
        hazardPrecaution.add(getAssociatedHazardPrecautionView().setOrder(order++));
        hazardPrecaution.add(getAssociatedPrecautionView().setOrder(order++));
        hazardPrecaution.add(getAllHazardPrecautionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.HAZARD_PRECAUTION);
        groupDetails.put("views", hazardPrecaution);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAssociatedHazardPrecautionView() {

        FacilioView allView = new FacilioView();
        allView.setName("associatedhazards");
        allView.setDisplayName("Associated Hazards");

        return allView;
    }

    private static FacilioView getAssociatedPrecautionView() {

        FacilioView allView = new FacilioView();
        allView.setName("associatedprecautions");
        allView.setDisplayName("Associated Precautions");
        return allView;
    }

    private static FacilioView getAllHazardPrecautionView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Hazuard Precaution");

        return allView;
    }

}

