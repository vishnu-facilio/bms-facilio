package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TermsAndConditionModule extends BaseModuleConfig{
    public TermsAndConditionModule(){
        setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> termsAndCondition = new ArrayList<FacilioView>();
        termsAndCondition.add(getAllTermsAndConditionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        groupDetails.put("views", termsAndCondition);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTermsAndConditionView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getTermsAndConditionModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All T&C(s)");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

        return allView;
    }
}
