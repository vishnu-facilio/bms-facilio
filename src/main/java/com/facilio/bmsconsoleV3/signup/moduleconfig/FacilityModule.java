package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class FacilityModule extends BaseModuleConfig{
    public FacilityModule(){
        setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> facility = new ArrayList<FacilioView>();
        facility.add(getAllFacilityView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        groupDetails.put("views", facility);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFacilityView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Facility");
        allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
