package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.context.BreakContext.BreakType;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.bmsconsole.context.BreakTransactionContext.TransactionType;
import com.facilio.bmsconsole.util.AttendanceApi;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class AddOrUpdateBreakTransactionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		BreakTransactionContext breakTransactionRecord = (BreakTransactionContext) context
				.get(FacilioConstants.ContextNames.RECORD);
		if (breakTransactionRecord != null) {
			long userId = (long) context.get(FacilioConstants.ContextNames.USER_ID);
			if (userId <= 0) {
				userId = AccountUtil.getCurrentUser().getOuid();
			}
			long day = -1;
			if (breakTransactionRecord.getTransactionTypeEnum() == TransactionType.BREAKSTART) {
				day = DateTimeUtil.getDayStartTimeOf(breakTransactionRecord.getStartTime());
			} else if (breakTransactionRecord.getTransactionTypeEnum() == TransactionType.BREAKSTOP) {
				day = DateTimeUtil.getDayStartTimeOf(breakTransactionRecord.getStopTime());
			}
			long lastBreakStartTime = -1;
			BreakTransactionContext existingBreakTransaction;
			AttendanceContext attendance = new AttendanceContext();
			attendance = AttendanceApi.getAttendance(userId, day);
			if (attendance == null) {
				throw new IllegalArgumentException("Kindly first check in");
			}
			lastBreakStartTime = attendance.getLastBreakStartTime();
			AttendanceTransactionContext attendanceTransaction = new AttendanceTransactionContext();
			attendanceTransaction.setAttendance(breakTransactionRecord.getAttendance());
			attendanceTransaction.setTransactionTime(breakTransactionRecord.getStartTime());
			if (breakTransactionRecord.getTransactionTypeEnum() == TransactionType.BREAKSTART) {
				if (attendance.getCheckOutTime() > 0) {
					throw new IllegalArgumentException("Kindly Check in first");
				}
				if (attendance.getLastBreakStartTime() > 0) {
					throw new IllegalArgumentException("The User has already checked in break");
				}
				attendance.setLastBreakStartTime(breakTransactionRecord.getStartTime());
				attendance.setLastBreakId(breakTransactionRecord.getBreakId());
				attendanceTransaction.setTransactionType(
						com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType.BREAKSTART);
			} else if (breakTransactionRecord.getTransactionTypeEnum() == TransactionType.BREAKSTOP) {
				if (attendance.getLastBreakStartTime() < 0) {
					throw new IllegalArgumentException("The User has already checked out break");
				}
				if (breakTransactionRecord.getBreakId() != null) {
					BreakContext breakContext = ShiftAPI.getBreak(breakTransactionRecord.getBreakId().getId());
					if (breakContext.getBreakTypeEnum() == BreakType.PAID) {
						long totalBreakDur = breakTransactionRecord.getStartTime() - lastBreakStartTime;
						if (attendance.getTotalPaidBreakHrs() > 0) {
							totalBreakDur += attendance.getTotalPaidBreakHrs();
						}
						attendance.setTotalPaidBreakHrs(totalBreakDur);
					} else if (breakContext.getBreakTypeEnum() == BreakType.UNPAID) {
						long totalBreakDur = breakTransactionRecord.getStartTime() - lastBreakStartTime;
						if (attendance.getTotalUnpaidBreakHrs() > 0) {
							totalBreakDur += attendance.getTotalUnpaidBreakHrs();
						}
						attendance.setTotalUnpaidBreakHrs(totalBreakDur);
					}
				}
				attendance.setLastBreakStartTime(-99);
				attendance.setLastBreakId(null);
				attendanceTransaction.setTransactionType(
						com.facilio.bmsconsole.context.AttendanceTransactionContext.TransactionType.BREAKSTOP);
			}
			// if(breakTransaction.getBreakId().getBreakTypeEnum() ==
			// BreakType.PAID) {
			//
			// }
			attendanceTransaction.setSourceType(breakTransactionRecord.getSourceType());
			attendanceTransaction.setAttendance(attendance);
			long attendanceTransactionId = AttendanceApi.addAttendanceTransaction(attendanceTransaction);
			attendanceTransaction.setId(attendanceTransactionId);
			AttendanceApi.updateAttendance(attendance);
			breakTransactionRecord.setAttendance(attendance);
			if (breakTransactionRecord.getStopTime() > 0) {
				existingBreakTransaction = AttendanceApi.getBreakTransaction(lastBreakStartTime);
				if (existingBreakTransaction != null) {
					existingBreakTransaction.setStopTime(breakTransactionRecord.getStopTime());
					existingBreakTransaction.setTimeTaken(breakTransactionRecord.getStopTime() - existingBreakTransaction.getStartTime());
					AttendanceApi.updateBreakTransaction(existingBreakTransaction);
				}
			} else {
				AttendanceApi.addBreakTransaction(breakTransactionRecord);
			}
		}
		return false;
	}

}
