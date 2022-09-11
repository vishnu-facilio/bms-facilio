package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ShiftModule extends BaseModuleConfig{
    public ShiftModule(){
        setModuleName(FacilioConstants.ContextNames.SHIFT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> shift = new ArrayList<FacilioView>();
        shift.add(getAllShiftView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SHIFT);
        groupDetails.put("views", shift);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllShiftView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(ModuleFactory.getShiftModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Shift(s)");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

        return allView;
    }
}

