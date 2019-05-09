package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GatePassAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private GatePassContext gatePass;

	public GatePassContext getGatePass() {
		return gatePass;
	}

	public void setGatePass(GatePassContext gatePass) {
		this.gatePass = gatePass;
	}

	private List<GatePassLineItemsContext> lineItems;

	public List<GatePassLineItemsContext> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GatePassLineItemsContext> lineItems) {
		this.lineItems = lineItems;
	}

	private List<Long> transactionsId;

	public void setTransactionsId(List<Long> transactionsId) {
		this.transactionsId = transactionsId;
	}

	public List<Long> getTransactionsId() {
		return transactionsId;
	}

	private int approvedState;

	public int getApprovedState() {
		return approvedState;
	}

	public void setApprovedState(int approvedState) {
		this.approvedState = approvedState;
	}
	
	private long gatePassId;
	public long getGatePassId() {
		return gatePassId;
	}
	public void setGatePassId(long gatePassId) {
		this.gatePassId = gatePassId;
	}
	
	private List<Long> gatePassIds;
	
	public List<Long> getGatePassIds() {
		return gatePassIds;
	}

	public void setGatePassIds(List<Long> gatePassIds) {
		this.gatePassIds = gatePassIds;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private List<GatePassContext> gatePassList;

	public List<GatePassContext> getItemTypesList() {
		return gatePassList;
	}

	public void setItemTypesList(List<ItemTypesContext> itemTypesList) {
		this.gatePassList = gatePassList;
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
	
	private Long gatePassCount;

	public Long getGatePassCount() {
		return gatePassCount;
	}

	public void setGatePassCount(Long itemTypesCount) {
		this.gatePassCount = itemTypesCount;
	}
	
	private List<PurchaseOrderLineItemContext> poLineItems;
	
	public List<PurchaseOrderLineItemContext> getPoLineItems() {
		return poLineItems;
	}

	public void setPoLineItems(List<PurchaseOrderLineItemContext> poLineItems) {
		this.poLineItems = poLineItems;
	}
	
	private List<WorkorderItemContext> woItems;
	private List<WorkorderToolsContext> woTools;
	
	public List<WorkorderItemContext> getWoItems() {
		return woItems;
	}

	public void setWoItems(List<WorkorderItemContext> woItems) {
		this.woItems = woItems;
	}

	public List<WorkorderToolsContext> getWoTools() {
		return woTools;
	}

	public void setWoTools(List<WorkorderToolsContext> woTools) {
		this.woTools = woTools;
	}

	public String addGatePass() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, gatePass);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, transactionsId);
		context.put(FacilioConstants.ContextNames.TOOL_TRANSACTION_APPORVED_STATE, approvedState);
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);

		Chain c = TransactionChainFactory.getAddGatePassChain();
		c.execute(context);
		setGatePass((GatePassContext) context.get(FacilioConstants.ContextNames.GATE_PASS));
		setResult(FacilioConstants.ContextNames.GATE_PASS, gatePass);
		return SUCCESS;
	}
	
	public String addOrUpdateGatePass() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, gatePass);
		
		Chain c = TransactionChainFactory.getAddOrUpdateGatePassChain();
		c.execute(context);
		setGatePass((GatePassContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.GATE_PASS, gatePass);
		return SUCCESS;
	}
	
	public String gatePassList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Gate_Pass.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "Gate_Pass.name");
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

		Chain gatePassListChain = ReadOnlyChainFactory.getGatePassList();
		gatePassListChain.execute(context);
		if (getCount()) {
			setGatePassCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", gatePassCount);
		} else {
			gatePassList = (List<GatePassContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (gatePassList == null) {
				gatePassList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.GATE_PASS, gatePassList);
		}
		return SUCCESS;
	}
	
	public String gatePassCount() throws Exception {
		gatePassList();
		setResult(FacilioConstants.ContextNames.GATE_PASS_COUNT, gatePass);
		return SUCCESS;
	}
	
	public String gatePassDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getGatePassId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchGatePassDetails();
		inventryDetailsChain.execute(context);

		setGatePass((GatePassContext) context.get(FacilioConstants.ContextNames.GATE_PASS));
		setResult(FacilioConstants.ContextNames.GATE_PASS, gatePass);
		return SUCCESS;
	}
	
	public String deleteGatePass() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, gatePassId != -1 ? Collections.singletonList(gatePassId) : gatePassIds);
		Chain chain = TransactionChainFactory.getGatePassDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, gatePassId != -1 ? Collections.singletonList(gatePassId) : gatePassIds);
		return SUCCESS;
	}
	
	public String usePoLineItemsForGatepass() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, gatePassId != -1 ? Collections.singletonList(gatePassId) : gatePassIds);
		Chain chain = TransactionChainFactory.getGatePassDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, gatePassId != -1 ? Collections.singletonList(gatePassId) : gatePassIds);
		return SUCCESS;
	}
	
	
}
