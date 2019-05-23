package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetInventoryRequestLineItemsForStoreRoomIdCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Long storeRoomId = (Long) context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
		Integer status = (Integer) context.get(FacilioConstants.ContextNames.STATUS);
		
		List<ItemContext> itemList = ItemsApi.getItemsForStore(storeRoomId);
		List<ToolContext> toolList = ToolsApi.getToolsForStore(storeRoomId);
		
		String itemsIdString = null;
		StringJoiner joinerItem = new StringJoiner(",");
		if(CollectionUtils.isNotEmpty(itemList)) {
			for(ItemContext item : itemList) {
				joinerItem.add(String.valueOf(item.getId()));
			}
			itemsIdString = joinerItem.toString();
		}
		
		String toolsIdString = null;
		StringJoiner joinerTool = new StringJoiner(",");
		if(CollectionUtils.isNotEmpty(toolList)) {
			for(ToolContext tool : toolList) {
				joinerTool.add(String.valueOf(tool.getId()));
			}
			toolsIdString = joinerTool.toString();
		}
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		SelectRecordsBuilder<InventoryRequestContext> builder = new SelectRecordsBuilder<InventoryRequestContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", "", CommonOperators.IS_NOT_EMPTY))
		;
				

		if(status != null) {
			builder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(status), EnumOperators.IS));
		}
        
		List<InventoryRequestContext> records = builder.get();
		StringJoiner idString = new StringJoiner(",");
		for(InventoryRequestContext request : records)	{
			idString.add(String.valueOf(request.getId()));
		}
		List<InventoryRequestLineItemContext> lineItems = InventoryRequestAPI.getLineItemsForInventoryRequest(idString.toString(), itemsIdString, toolsIdString);
		context.put(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		return false;
	}		

	
}
