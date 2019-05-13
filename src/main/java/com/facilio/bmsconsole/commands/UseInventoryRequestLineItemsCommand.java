package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UseInventoryRequestLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InventoryRequestLineItemContext> lineItems = (List<InventoryRequestLineItemContext>) context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		Integer inventoryType = (Integer) context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		List<WorkorderItemContext> woItemList = new ArrayList<WorkorderItemContext>();
		List<WorkorderToolsContext> woToolList = new ArrayList<WorkorderToolsContext>();
		StringJoiner lineItemIds = new StringJoiner(",");
		
		for(InventoryRequestLineItemContext lineItem : lineItems) {
			if(inventoryType == InventoryType.ITEM.getValue())	{
				woItemList.add(lineItem.constructWorkOrderItemContext());
			}
			else if(inventoryType == InventoryType.TOOL.getValue()) {
				woToolList.add(lineItem.constructWorkOrderToolContext());
			}
			if(lineItem.getParentId() != -1) {
				lineItemIds.add(String.valueOf(lineItem.getId()));
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inventoryType == InventoryType.ITEM.getValue() ? woItemList : woToolList);
		
		return false;
	}
	
	
}
