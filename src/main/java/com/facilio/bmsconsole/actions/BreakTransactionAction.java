package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class BreakTransactionAction extends ModuleAction{

	private static final long serialVersionUID = 1L;
	private BreakTransactionContext breakTransaction;
	public BreakTransactionContext getBreakTransaction() {
		return breakTransaction;
	}
	public void setBreakTransaction(BreakTransactionContext breakTransaction) {
		this.breakTransaction = breakTransaction;
	}
	
	private long userId = -1;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private List<BreakTransactionContext> breakTransactions;
	public List<BreakTransactionContext> getBreakTransactions() {
		return breakTransactions;
	}
	public void setBreakTransactions(List<BreakTransactionContext> breakTransactions) {
		this.breakTransactions = breakTransactions;
	}
	
	public String addBreakTransaction() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, breakTransaction);
		context.put(FacilioConstants.ContextNames.USER_ID, userId);
		Chain addItem = TransactionChainFactory.getAddBreakTransactionChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.BREAK_TRANSACTION, breakTransaction);
		return SUCCESS;
	}
	
	public String breakTransactionList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Break_Transaction.START_TIME asc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "breakTransaction.attendance");
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

		Chain itemsListChain = ReadOnlyChainFactory.getBreakTransactionsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setBreakTransactionCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", breakTransactionCount);
		} else {
			breakTransactions = (List<BreakTransactionContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if (breakTransactions == null) {
				breakTransactions = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.BREAK_TRANSACTION, breakTransactions);
		}
		return SUCCESS;
	}

	private Long breakTransactionCount;
	public Long getBreakTransactionCount() {
		return breakTransactionCount;
	}
	public void setBreakTransactionCount(Long attendanceCount) {
		this.breakTransactionCount = attendanceCount;
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
}
