package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class VisitorModule extends BaseModuleConfig{
    public VisitorModule(){
        setModuleName(FacilioConstants.ContextNames.VISITOR);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> visitor = new ArrayList<FacilioView>();
        visitor.add(getAllVisitorsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR);
        groupDetails.put("views", visitor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVisitorsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Visitors");

        return allView;
    }
}
