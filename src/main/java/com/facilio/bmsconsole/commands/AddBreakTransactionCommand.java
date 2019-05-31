package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType;
import com.facilio.bmsconsole.context.BreakContext.BreakType;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.bmsconsole.util.AttendanceApi;
import com.facilio.constants.FacilioConstants;

public class AddBreakTransactionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		BreakTransactionContext breakTransaction = (BreakTransactionContext) context.get(FacilioConstants.ContextNames.BREAK_TRANSACTION);
		if (breakTransaction != null) {
			long lastBreakStartTime = -1;
			AttendanceContext attendance = new AttendanceContext();
			attendance = AttendanceApi.getAttendanceForId(breakTransaction.getAttendance().getId());
			lastBreakStartTime = attendance.getLastBreakStartTime();
			AttendanceTransactionContext attendanceTransaction = new AttendanceTransactionContext();
			attendanceTransaction.setAttendance(breakTransaction.getAttendance());
			if (breakTransaction.getBreakStartTime() > 0) {
				attendance.setLastBreakStartTime(breakTransaction.getBreakStartTime());
				attendanceTransaction.setTransactionTime(breakTransaction.getBreakStartTime());
				attendanceTransaction.setTransactionType(TransactionType.BREAKSTART);
			} else if(breakTransaction.getBreakStopTime() > 0) {
				long totalBreakDur = breakTransaction.getBreakStopTime() - lastBreakStartTime;
				if(attendance.getTotalPaidBreakHrs() > 0) {
					totalBreakDur += attendance.getTotalPaidBreakHrs();
				}
				attendance.setTotalPaidBreakHrs(totalBreakDur);
				attendance.setLastBreakStartTime(-1);
				attendanceTransaction.setTransactionTime(breakTransaction.getBreakStopTime());
				attendanceTransaction.setTransactionType(TransactionType.BREAKSTOP);
			}
			breakTransaction.setAttendanceTransaction(attendanceTransaction);
//			if(breakTransaction.getBreakId().getBreakTypeEnum() == BreakType.PAID) {
//				
//			}
			AttendanceApi.addAttendanceTransaction(attendanceTransaction);
			AttendanceApi.updateAttendance(attendance);
			breakTransaction.setAttendanceTransaction(attendanceTransaction);
			context.put(FacilioConstants.ContextNames.RECORD, breakTransaction);
		}
		return false;
	}

}
