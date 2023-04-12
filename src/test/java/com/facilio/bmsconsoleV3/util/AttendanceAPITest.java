package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.shift.Break;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceAPITest {

    @Test
    void testNoBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(5000, attendance.getCheckOutTime());
        assertEquals(4000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(0, attendance.getTotalPaidBreakHrs());
        assertEquals(0,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testPartialPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.PAID, 2L);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(3000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(5000, attendance.getCheckOutTime());
        assertEquals(4000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(1000, attendance.getTotalPaidBreakHrs());
        assertEquals(0,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testExactPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.PAID, 2L);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(4000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(5000, attendance.getCheckOutTime());
        assertEquals(4000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(2000, attendance.getTotalPaidBreakHrs());
        assertEquals(0,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testExcessPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.PAID, 2L);
        br1.setId(1);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(6000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(6000, attendance.getCheckOutTime());
        assertEquals(4000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(2000, attendance.getTotalPaidBreakHrs());
        assertEquals(0,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testMultiPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.PAID, 2L);
        br1.setId(1);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(3000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(4000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(6000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(6000, attendance.getCheckOutTime());
        assertEquals(5000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(2000, attendance.getTotalPaidBreakHrs());
        assertEquals(0,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testPartialUnPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.UNPAID, 2L);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(3000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(5000, attendance.getCheckOutTime());
        assertEquals(3000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(0, attendance.getTotalPaidBreakHrs());
        assertEquals(1000,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testExactUnPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.UNPAID, 2L);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(4000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(5000, attendance.getCheckOutTime());
        assertEquals(2000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(0, attendance.getTotalPaidBreakHrs());
        assertEquals(2000,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testExcessUnPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.UNPAID, 2L);
        br1.setId(1);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(6000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(6000, attendance.getCheckOutTime());
        assertEquals(2000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(0, attendance.getTotalPaidBreakHrs());
        assertEquals(2000,attendance.getTotalUnpaidBreakHrs());
    }

    @Test
    void testMultiUnPaidBreakUsage() throws Exception {

        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1000);

        Break br1 = new Break(Break.Type.UNPAID, 2L);
        br1.setId(1);

        List<AttendanceTransaction> txns = new ArrayList<>();
        txns.add(new AttendanceTransaction(1000L, AttendanceTransaction.Type.CHECK_IN));
        txns.add(new AttendanceTransaction(2000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(3000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(4000L, AttendanceTransaction.Type.BREAK, br1));
        txns.add(new AttendanceTransaction(5000L, AttendanceTransaction.Type.RESUME_WORK));
        txns.add(new AttendanceTransaction(6000L, AttendanceTransaction.Type.CHECK_OUT));

        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);

        assertEquals(1000,attendance.getCheckInTime());
        assertEquals(6000, attendance.getCheckOutTime());
        assertEquals(3000, attendance.getWorkingHours());
        assertEquals(Attendance.Status.PRESENT, attendance.getStatus());
        assertEquals(0, attendance.getTotalPaidBreakHrs());
        assertEquals(2000,attendance.getTotalUnpaidBreakHrs());
    }
}