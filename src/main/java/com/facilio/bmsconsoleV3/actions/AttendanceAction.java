package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttendanceAction extends V3Action {

    private AttendanceSettings.WorkingHoursMode mode;
    private Long duration;
    private Long rangeFrom;
    private Long rangeTo;
    private Long date;
    private Long peopleID;
    private List<Long> employees;
    public String list() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAttendanceListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Attendance.RANGE_FROM, rangeFrom);
        context.put(FacilioConstants.Attendance.RANGE_TO, rangeTo);
        context.put(FacilioConstants.Attendance.PEOPLE_ID, peopleID);
        chain.execute();

        setData("attendance", context.get("attendance"));
        setData(FacilioConstants.Shift.SHIFTS, context.get(FacilioConstants.Shift.SHIFTS));
        return SUCCESS;
    }

    public String transactionList() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAttendanceTransactionListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Attendance.DATE, date);
        context.put(FacilioConstants.Attendance.PEOPLE_ID, peopleID);
        chain.execute();

        setData("attendanceTransaction", context.get("attendanceTransaction"));
        return SUCCESS;
    }
    public String transitionList() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAttendanceTransitionListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Attendance.PEOPLE_ID, peopleID);
        chain.execute();

        setData("attendanceTransitions", context.get("attendanceTransitions"));
        return SUCCESS;
    }

    public String settings() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAttendanceSettingsUpdateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Attendance.MODE, mode);
        context.put(FacilioConstants.Attendance.DURATION, duration);
        chain.execute();

        setData("attendanceSettings", context.get("attendanceSettings"));
        return SUCCESS;
    }

    public String getSettings() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAttendanceSettingsChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setData("attendanceSettings", context.get("attendanceSettings"));
        return SUCCESS;
    }


}
