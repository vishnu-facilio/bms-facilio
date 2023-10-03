package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class InductionTemplateModule extends BaseModuleConfig{
    public InductionTemplateModule(){
        setModuleName(FacilioConstants.Induction.INDUCTION_TEMPLATE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inductionTemplate = new ArrayList<FacilioView>();
        inductionTemplate.add(getAllInductionTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Induction.INDUCTION_TEMPLATE);
        groupDetails.put("views", inductionTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInductionTemplateViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Templates.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Induction Templates");
        allView.setModuleName(FacilioConstants.Induction.INDUCTION_TEMPLATE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
