package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AttendanceAction extends ModuleAction{

	private static final long serialVersionUID = 1L;
	
	private AttendanceContext attendance;
	public AttendanceContext getAttendance() {
		return attendance;
	}
	public void setAttendance(AttendanceContext attendance) {
		this.attendance = attendance;
	}
	
	private long attendanceId;
	public long getAttendanceId() {
		return attendanceId;
	}
	public void setAttendanceId(long attendanceId) {
		this.attendanceId = attendanceId;
	}
	
	private AttendanceTransactionContext attendanceTransaction;
	public AttendanceTransactionContext getAttendanceTransaction() {
		return attendanceTransaction;
	}
	public void setAttendanceTransaction(AttendanceTransactionContext attendanceTransaction) {
		this.attendanceTransaction = attendanceTransaction;
	}
	
	private long attendanceTransactionId;
	public long getAttendanceTransactionId() {
		return attendanceTransactionId;
	}
	public void setAttendanceTransactionId(long attendanceTransactionId) {
		this.attendanceTransactionId = attendanceTransactionId;
	}
	
	public String addAttendanceTransaction() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, attendanceTransaction);
		Chain addItem = TransactionChainFactory.getAddAttendanceTransactionChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS, attendanceTransaction);
		return SUCCESS;
	}


}
