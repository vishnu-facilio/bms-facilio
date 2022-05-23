package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ShiftRotationModule extends BaseModuleConfig{
    public ShiftRotationModule(){
        setModuleName(FacilioConstants.ContextNames.SHIFT_ROTATION);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> shiftRotation = new ArrayList<FacilioView>();
        shiftRotation.add(getAllShiftRotationView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SHIFT_ROTATION);
        groupDetails.put("views", shiftRotation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllShiftRotationView() {
        FacilioField name = new FacilioField();
        name.setName("schedularName");
        name.setDataType(FieldType.STRING);
        name.setColumnName("SCHEDULAR_NAME");
        name.setModule(ModuleFactory.getShiftRotationModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Shift Rotation");

        return allView;
    }
}

