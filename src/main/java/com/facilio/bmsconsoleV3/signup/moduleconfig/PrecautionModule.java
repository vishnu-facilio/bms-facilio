package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class PrecautionModule extends BaseModuleConfig{
    public PrecautionModule(){
        setModuleName(FacilioConstants.ContextNames.PRECAUTION);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> precaution = new ArrayList<FacilioView>();
        precaution.add(getAllPrecautionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PRECAUTION);
        groupDetails.put("views", precaution);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPrecautionView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Precautions");

        return allView;
    }
}

