package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class InspectionPriorityModule extends BaseModuleConfig{
    public InspectionPriorityModule(){
        setModuleName(FacilioConstants.Inspection.INSPECTION_PRIORITY);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllInspectionPriorityViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Inspection.INSPECTION_PRIORITY);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInspectionPriorityViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inspection Priority");
        allView.setModuleName(FacilioConstants.Inspection.INSPECTION_PRIORITY);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
