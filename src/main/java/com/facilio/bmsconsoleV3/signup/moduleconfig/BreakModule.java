package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class BreakModule extends BaseModuleConfig{
    public BreakModule(){
        setModuleName(FacilioConstants.ContextNames.BREAK);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> breakModule = new ArrayList<FacilioView>();
        breakModule.add(getAllBreakView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BREAK);
        groupDetails.put("views", breakModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBreakView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getBreakModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Break");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

        return allView;
    }
}

