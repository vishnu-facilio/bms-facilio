package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PoLineItemsSerialNumberContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoLineItemSerialNumberAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private List<PoLineItemsSerialNumberContext> poLineItemSerialNumbers;
	public List<PoLineItemsSerialNumberContext> getPoLineItemSerialNumeber() {
		return poLineItemSerialNumbers;
	}
	public void setPoLineItemSerialNumeber(List<PoLineItemsSerialNumberContext> poLineItemSerialNumeber) {
		this.poLineItemSerialNumbers = poLineItemSerialNumeber;
	}
	
	private PoLineItemsSerialNumberContext poLineItemSerialNumber;
	public PoLineItemsSerialNumberContext getPoLineItemSerialNumber() {
		return poLineItemSerialNumber;
	}
	public void setPoLineItemSerialNumber(PoLineItemsSerialNumberContext poLineItemSerialNumber) {
		this.poLineItemSerialNumber = poLineItemSerialNumber;
	}
	
	private List<Long> recordIds;
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> workorderItemsId) {
		this.recordIds = workorderItemsId;
	}
	
	public String addLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, poLineItemSerialNumber);
		context.put(FacilioConstants.ContextNames.SERIAL_NUMBERS, poLineItemSerialNumber.getSerialNumbers());
		context.put(FacilioConstants.ContextNames.ASSETS, poLineItemSerialNumber.getAssets());
		Chain addInventry = TransactionChainFactory.getAddPoLineItemSerialNumbersChain();
		addInventry.execute(context);
		poLineItemSerialNumbers = (List<PoLineItemsSerialNumberContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS, poLineItemSerialNumbers);
		return SUCCESS;
	}
	
	public String updateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, poLineItemSerialNumber);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(poLineItemSerialNumber.getId()));
		Chain addInventry = TransactionChainFactory.getUpdatePoLineItemSerialNumbersChain();
		addInventry.execute(context);
		poLineItemSerialNumber = (PoLineItemsSerialNumberContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS, poLineItemSerialNumber);
		return SUCCESS;
	}
	
	public String deletelineitems() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);

		Chain deleteInventoryChain = TransactionChainFactory.getDeletePoLineItemSerialNumbersChain();
		deleteInventoryChain.execute(context);
		setRecordIds((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("recordIds", recordIds);
		return SUCCESS;
	}
	
	public String lineitemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "PO_Line_Item_Serial_Numbers.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "poLineItemSerialNumbers.lineItem");
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
	
		Chain itemsListChain = ReadOnlyChainFactory.getPoLineItemsSerialNumberList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemsCount);
		} else {
			poLineItemSerialNumbers = (List<PoLineItemsSerialNumberContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (poLineItemSerialNumbers == null) {
				poLineItemSerialNumbers = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS, poLineItemSerialNumbers);
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

	private Long itemsCount;

	public Long getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}

}
