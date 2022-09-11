package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class AssetHazardModule extends BaseModuleConfig{
    public AssetHazardModule(){
        setModuleName(FacilioConstants.ContextNames.ASSET_HAZARD);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> assetHazard = new ArrayList<FacilioView>();
        assetHazard.add(getAllAssetHazardsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ASSET_HAZARD);
        groupDetails.put("views", assetHazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAssetHazardsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Asset Hazards");

        return allView;
    }
}

