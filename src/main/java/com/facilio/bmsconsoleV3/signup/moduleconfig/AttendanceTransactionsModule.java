package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class AttendanceTransactionsModule extends BaseModuleConfig{
    public AttendanceTransactionsModule(){
        setModuleName(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> attendanceTransaction = new ArrayList<FacilioView>();
        attendanceTransaction.add(getAllAttendanceTransactionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS);
        groupDetails.put("views", attendanceTransaction);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAttendanceTransactionView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("transactionTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("TRANSACTION_TIME");
        createdTime.setModule(ModuleFactory.getAttendanceTransactionModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Attendance Transactions");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, true)));

        return allView;
    }
}
