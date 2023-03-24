package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AttendanceContext;
import com.facilio.bmsconsole.context.AttendanceStateContext;
import com.facilio.bmsconsole.context.AttendanceTransactionContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

@Deprecated
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
	
	private List<AttendanceContext> attendanceList;
	public List<AttendanceContext> getAttendanceList() {
		return attendanceList;
	}
	public void setAttendanceList(List<AttendanceContext> attendanceList) {
		this.attendanceList = attendanceList;
	}
	
	private List<AttendanceTransactionContext> attendanceTranactionList;
	public List<AttendanceTransactionContext> getAttendanceTranactionList() {
		return attendanceTranactionList;
	}
	public void setAttendanceTranactionList(List<AttendanceTransactionContext> attendanceTranactionList) {
		this.attendanceTranactionList = attendanceTranactionList;
	}
	
	public String addAttendanceTransaction() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, attendanceTransaction);
		FacilioChain addItem = TransactionChainFactory.getAddAttendanceTransactionChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS, attendanceTransaction);
		return SUCCESS;
	}

	public String attendanceList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Attendance.DAY asc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "attendance.user");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		if (getCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		FacilioChain itemsListChain = ReadOnlyChainFactory.getAttendanceList();
		itemsListChain.execute(context);
		if (getCount()) {
			setAttendanceCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", attendanceCount);
		} else {
			attendanceList = (List<AttendanceContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if (attendanceList == null) {
				attendanceList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ATTENDANCE, attendanceList);
		}
		return SUCCESS;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	private Boolean count;

	public Boolean getCount() {
		if (count == null) {
			return false;
		}
		return count;
	}

	public void setCount(Boolean count) {
		this.count = count;
	}
	
	private Long attendanceCount;
	public Long getAttendanceCount() {
		return attendanceCount;
	}
	public void setAttendanceCount(Long attendanceCount) {
		this.attendanceCount = attendanceCount;
	}
	
	public String attendanceTransactionList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Attendance_Transactions.TRANSACTION_TIME asc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "attendanceTransaction.attendance");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		if (getCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		FacilioChain itemsListChain = ReadOnlyChainFactory.getAttendanceTransactionsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setAttendanceCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", attendanceCount);
		} else {
			attendanceTranactionList = (List<AttendanceTransactionContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if (attendanceTranactionList == null) {
				attendanceTranactionList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS, attendanceTranactionList);
		}
		return SUCCESS;
	}
	
	private long userId;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private long time;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	private List<AttendanceStateContext> attendanceTransitions;
	public List<AttendanceStateContext> getAttendanceTransitions() {
		return attendanceTransitions;
	}
	public void setAttendanceTransitions(List<AttendanceStateContext> attendanceTransitions) {
		this.attendanceTransitions = attendanceTransitions;
	}
	
	private ShiftContext shift;
	public ShiftContext getShift() {
		return shift;
	}
	public void setShift(ShiftContext shift) {
		this.shift = shift;
	}
	public String showStateForAttendance() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.USER_ID, getUserId());
		context.put(FacilioConstants.ContextNames.TIMESTAMP, getTime());
		FacilioChain chain = TransactionChainFactory.getAttendanceTransitionState();
		chain.execute(context);
		attendanceTransitions = (List<AttendanceStateContext>) context.get(FacilioConstants.ContextNames.RECORD);
		shift =  (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
		setResult(FacilioConstants.ContextNames.SHIFT, shift);
		setResult(FacilioConstants.ContextNames.RECORD, attendanceTransitions);
		return SUCCESS;
	}
	
	public String getAttendanceDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, attendanceId);
		
		FacilioChain chain = ReadOnlyChainFactory.getAttendanceDetailsChain();
		chain.execute(context);
		
		AttendanceContext attendance = (AttendanceContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.ATTENDANCE, attendance);
		
		return SUCCESS;
	}
}
