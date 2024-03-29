package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ToolTransactionsAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private List<ToolTransactionContext> toolTransaction;
	public List<ToolTransactionContext> getToolTransaction() {
		return toolTransaction;
	}
	public void setToolTransaction(List<ToolTransactionContext> toolTransactions) {
		this.toolTransaction = toolTransactions;
	}
	
	private List<Long> toolTransactionsId;
	public List<Long> getToolTransactionsId() {
		return toolTransactionsId;
	}
	
	public void setToolTransactionsId(List<Long> toolTransactionsId) {
		this.toolTransactionsId = toolTransactionsId;
	}
	
	private int approvedState;
	public int getApprovedState() {
		return approvedState;
	}
	public void setApprovedState(int approvedState) {
		this.approvedState = approvedState;
	}
	
	private int transactionType;

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	public String addOrUpdateToolTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		FacilioChain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateToolTransactionsChain();
		addWorkorderPartChain.execute(context);
		setToolTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolTransactionsId", toolTransactionsId);
		return SUCCESS;
	} 
	
	public String approveOrRejectToolTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, toolTransactionsId);
		context.put(FacilioConstants.ContextNames.TOOL_TRANSACTION_APPORVED_STATE, approvedState);
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
		
		FacilioChain addWorkorderPartChain;
		addWorkorderPartChain = TransactionChainFactory.getApproveRejectWorkorderToolsChain();
//		if (transactionType == 3) {
//			addWorkorderPartChain = TransactionChainFactory.getApproveRejectManualToolsChain();
//		} else {
//			addWorkorderPartChain = TransactionChainFactory.getApproveRejectWorkorderToolsChain();
//		}
		addWorkorderPartChain.execute(context);
		setToolTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolTransactionsId", toolTransactionsId);
		return SUCCESS;
	}
	
	public String deleteToolTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, toolTransactionsId);

		FacilioChain deleteInventoryChain = TransactionChainFactory.getDeleteToolTransactChain();
		deleteInventoryChain.execute(context);
		setToolTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolTransactionsId", toolTransactionsId);
		return SUCCESS;
	}
	
	public String toolsTransactionsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Tool_transactions.ID desc");
		context.put(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID, siteId);
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "toolTransactions.tool");
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

		if(getShowToolsForReturn()) {
			context.put(FacilioConstants.ContextNames.SHOW_TOOLS_FOR_RETURN, showToolsForReturn);
		}
		else if(getShowToolsForIssue()) {
			context.put(FacilioConstants.ContextNames.SHOW_TOOLS_FOR_ISSUE, showToolsForIssue);
		}
		FacilioChain itemsListChain = ReadOnlyChainFactory.getToolTransactionsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemsCount);
		} else {
			toolTransaction = (List<ToolTransactionContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (toolTransaction == null) {
				toolTransaction = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, toolTransaction);
		}
		return SUCCESS;
	}
	
	public String showToolTransactionListForReturn() throws Exception {		
		toolsTransactionsList();
		return SUCCESS;		
	}
	
	public String toolsTransactionsCount() throws Exception {
		toolsTransactionsList();
		setResult(FacilioConstants.ContextNames.COUNT, itemsCount);
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
	
	private Long itemsCount;
	public Long getItemsCount() {
		return itemsCount;
	}
	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}
	
	private List<Long> purchasedTools;
	public List<Long> getPurchasedTools() {
		return purchasedTools;
	}
	public void setPurchasedTools(List<Long> purchasedItems) {
		this.purchasedTools = purchasedItems;
	}
	
	private Boolean showToolsForReturn;
	public Boolean getShowToolsForReturn() {
		if (showToolsForReturn == null) {
			return false;
		}
		return showToolsForReturn;
	}
	public void setShowToolsForReturn(Boolean showToolsForReturn) {
		this.showToolsForReturn = showToolsForReturn;
	}
	

	private Boolean showItemsForReturn;

	public Boolean getShowItemsForReturn() {
		if (showItemsForReturn == null) {
			return false;
		}
		return showItemsForReturn;
	}

	public void setShowItemsForReturn(Boolean showToolsForReturn) {
		this.showItemsForReturn = showToolsForReturn;
	}
	private Boolean showToolsForIssue;

	public Boolean getShowToolsForIssue() {
		if (showToolsForIssue == null) {
			return false;
		}
		return showToolsForIssue;
	}

	public void setShowToolsForIssue(Boolean showToolsForIssue) {
		this.showToolsForIssue = showToolsForIssue;
	}
	
	public String showToolTransactionListForIssue() throws Exception {
		toolsTransactionsList();
		return SUCCESS;
	}
	
	
}
