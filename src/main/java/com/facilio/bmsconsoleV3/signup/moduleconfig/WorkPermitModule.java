package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkPermitModule extends BaseModuleConfig{
    public WorkPermitModule(){
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workPermit = new ArrayList<FacilioView>();
        workPermit.add(getAllWorkPermitView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        groupDetails.put("views", workPermit);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkPermitView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Permit");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
