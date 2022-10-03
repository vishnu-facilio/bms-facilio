package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class BaseSpaceHazardsModule extends BaseModuleConfig{
    public BaseSpaceHazardsModule(){
        setModuleName(FacilioConstants.ContextNames.BASESPACE_HAZARD);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> baseSpaceHazard = new ArrayList<FacilioView>();
        baseSpaceHazard.add(getAllBaseSpaceHazardView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ASSET_HAZARD);
        groupDetails.put("views", baseSpaceHazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBaseSpaceHazardView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Space Hazards");

        return allView;
    }
}
