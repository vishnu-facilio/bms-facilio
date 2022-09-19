package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class EnergyDataReadingModule extends BaseModuleConfig{
    public EnergyDataReadingModule(){
        setModuleName(FacilioConstants.ContextNames.ENERGY_DATA_READING);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> energyDataReading = new ArrayList<FacilioView>();
        energyDataReading.add(getReportView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ENERGY_DATA_READING);
        groupDetails.put("views", energyDataReading);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getReportView() {

        FacilioView reportView = new FacilioView();
        reportView.setName("report");
        reportView.setHidden(true);

        return reportView;
    }
}
