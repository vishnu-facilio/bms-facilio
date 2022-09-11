package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class OccupantModule extends BaseModuleConfig{
    public OccupantModule(){
        setModuleName(FacilioConstants.ContextNames.OCCUPANT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> occupant = new ArrayList<FacilioView>();
        occupant.add(getAllOccupantsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.OCCUPANT);
        groupDetails.put("views", occupant);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllOccupantsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Occupants");

        return allView;
    }
}
