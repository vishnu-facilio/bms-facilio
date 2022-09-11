package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class AttendanceModule extends BaseModuleConfig{
    public AttendanceModule(){
        setModuleName(FacilioConstants.ContextNames.ATTENDANCE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> attendance = new ArrayList<FacilioView>();
        attendance.add(getAllAttendanceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ATTENDANCE);
        groupDetails.put("views", attendance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAttendanceView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getAttendanceModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Attendance");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return allView;
    }
}
