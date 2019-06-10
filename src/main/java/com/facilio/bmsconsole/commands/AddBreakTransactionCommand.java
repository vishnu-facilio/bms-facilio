package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.BreakContext.BreakType;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.bmsconsole.context.BreakTransactionContext.TransactionType;
import com.facilio.bmsconsole.util.AttendanceApi;
import com.facilio.constants.FacilioConstants;

public class AddBreakTransactionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		BreakTransactionContext breakTransaction = (BreakTransactionContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (breakTransaction != null) {
			long lastBreakStartTime = -1;
			AttendanceContext attendance = new AttendanceContext();
			attendance = AttendanceApi.getAttendanceForId(breakTransaction.getAttendance().getId());
			if(attendance == null) {
				throw new IllegalArgumentException("Kindly first check in");
			}
			lastBreakStartTime = attendance.getLastBreakStartTime();
			AttendanceTransactionContext attendanceTransaction = new AttendanceTransactionContext();
			attendanceTransaction.setAttendance(breakTransaction.getAttendance());
			attendanceTransaction.setTransactionTime(breakTransaction.getStartTime());
			if (breakTransaction.getTransactionTypeEnum() == TransactionType.BREAKSTART) {
				if(attendance.getCheckOutTime() > 0) {
					throw new IllegalArgumentException("Kindly Check in first");
				}
				if (attendance.getLastBreakStartTime() > 0) {
					throw new IllegalArgumentException("The User has already checked in break");
				}
				attendance.setLastBreakStartTime(breakTransaction.getStartTime());
				attendance.setLastBreakId(breakTransaction.getBreakId());
				attendanceTransaction.setTransactionType(com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType.BREAKSTART);
			} else if(breakTransaction.getTransactionTypeEnum() == TransactionType.BREAKSTOP) {
				if (attendance.getLastBreakStartTime() < 0) {
					throw new IllegalArgumentException("The User has already checked out break");
				}
				long totalBreakDur = breakTransaction.getStartTime() - lastBreakStartTime;
				if(attendance.getTotalPaidBreakHrs() > 0) {
					totalBreakDur += attendance.getTotalPaidBreakHrs();
				}
				attendance.setTotalPaidBreakHrs(totalBreakDur);
				attendance.setLastBreakStartTime(-99);
				attendance.setLastBreakId(null);
				attendanceTransaction.setTransactionType(com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType.BREAKSTOP);
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
