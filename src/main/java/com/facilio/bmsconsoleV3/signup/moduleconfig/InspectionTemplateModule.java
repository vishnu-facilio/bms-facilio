package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class InspectionTemplateModule extends BaseModuleConfig{
    public InspectionTemplateModule(){
        setModuleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inspectionTemplate = new ArrayList<FacilioView>();
        inspectionTemplate.add(getAllInspectionTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        groupDetails.put("views", inspectionTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInspectionTemplateViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Templates.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inspection Templates");
        allView.setModuleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
