package com.facilio.bmsconsoleV3.util;

//import com.facilio.bmsconsoleV3.context.attendance.Attendance;
//import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
//import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
//import com.facilio.bmsconsoleV3.context.shift.Break;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
//
//class AttendanceAPITest {
//
//     static final Long HOUR_IN_MS = 3600000L;
//
////    @Test
////    void template() throws Exception {
////
////        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
////
////        List<AttendanceTransaction> txns = new ArrayList<>();
////
////        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
////
////        assertEquals(attendance.getCheckInTime(), 1);
////        assertEquals(attendance.getCheckOutTime(), 1);
////        assertEquals(attendance.getWorkingHours(), 1);
////        assertEquals(attendance.getStatus(), Attendance.Status.PRESENT);
////        assertEquals(attendance.getTotalPaidBreakHrs(), 1);
////        assertEquals(attendance.getTotalUnpaidBreakHrs(), 1);
////    }
//
//    @Test
//    void testForWorkingHoursWithSingleSlot() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 1 * HOUR_IN_MS);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getCheckInTime(), 1);
//        assertEquals(attendance.getCheckOutTime(), 2);
//        assertEquals(attendance.getWorkingHours(), 1);
//        assertEquals(attendance.getStatus(), Attendance.Status.PRESENT);
//        assertEquals(attendance.getTotalPaidBreakHrs(), 0);
//        assertEquals(attendance.getTotalUnpaidBreakHrs(), 0);
//
//    }
//
//    @Test
//    void testForWorkingHoursWithMultipleSlots() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
//
//        Break br1 = new Break(Break.Type.UNPAID, 1L);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(3L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(4L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getWorkingHours(), 2L);
//    }
//
//    @Test
//    void testForPaidBreakWithSingleSlot() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
//
//        Break br1 = new Break(Break.Type.PAID, 1L);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(3L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(4L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getWorkingHours(), 3L);
//        assertEquals(attendance.getTotalPaidBreakHrs(), 1L);
//    }
//
//    @Test
//    void testForPaidBreakWithMultipleSlots() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
//
//        Break br1 = new Break(Break.Type.PAID, 1L);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(3L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(4L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(5L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(6L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getWorkingHours(), 5L);
//        assertEquals(attendance.getTotalPaidBreakHrs(), 2L);
//    }
//
//    @Test
//    void testForUnPaidBreakWithSingleSlot() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
//
//        Break br1 = new Break(Break.Type.UNPAID, 1L);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(3L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(4L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getWorkingHours(), 2L);
//        assertEquals(attendance.getTotalUnpaidBreakHrs(), 1L);
//    }
//
//    @Test
//    void testForUnPaidBreakWithMultipleSlots() throws Exception {
//
//        AttendanceSettings settings = new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 3 * HOUR_IN_MS);
//
//        Break br1 = new Break(Break.Type.UNPAID, 1L);
//
//        List<AttendanceTransaction> txns = new ArrayList<>();
//        txns.add(new AttendanceTransaction(1L, AttendanceTransaction.Type.CHECK_IN));
//        txns.add(new AttendanceTransaction(2L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(3L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(4L, AttendanceTransaction.Type.BREAK, br1));
//        txns.add(new AttendanceTransaction(5L, AttendanceTransaction.Type.RESUME_WORK));
//        txns.add(new AttendanceTransaction(6L, AttendanceTransaction.Type.CHECK_OUT));
//
//        Attendance attendance = AttendanceAPI.reduceAttendanceTransactions(settings, txns);
//
//        assertEquals(attendance.getWorkingHours(), 3L);
//        assertEquals(attendance.getTotalUnpaidBreakHrs(), 2L);
//    }
//}