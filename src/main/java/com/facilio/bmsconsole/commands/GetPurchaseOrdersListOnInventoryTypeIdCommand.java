package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetPurchaseOrdersListOnInventoryTypeIdCommand  extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		int inventoryType = (Integer) context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		long id = (Long) context.get(FacilioConstants.ContextNames.ID);
		long storeRoomId = (Long) context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule purchaseOrderModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
			FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			List<FacilioField> poFields = modBean.getAllFields(purchaseOrderModule.getName());
			
			
			SelectRecordsBuilder<PurchaseOrderContext> builder = new SelectRecordsBuilder<PurchaseOrderContext>()
					.moduleName(purchaseOrderModule.getName())
					.select(poFields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(purchaseOrderModule.getName()))
					.innerJoin(lineItemModule.getTableName())
					.on(purchaseOrderModule.getTableName()+".ID = "+lineItemModule.getTableName()+".PO_ID")
					.andCondition(CriteriaAPI.getCondition("INVENTORY_TYPE", "inventoryType", String.valueOf(inventoryType), NumberOperators.EQUALS))
					;
			if(storeRoomId != 0) {
				builder.andCondition(CriteriaAPI.getCondition("STOREROOM", "storeRoom", String.valueOf(storeRoomId), NumberOperators.EQUALS));
			}
			if(inventoryType == InventoryType.ITEM.ordinal()+1) {
				builder.andCondition(CriteriaAPI.getCondition("ITEM_TYPE", "itemType", String.valueOf(id), NumberOperators.EQUALS));
			}
			else if(inventoryType == InventoryType.TOOL.ordinal()+1) {
				builder.andCondition(CriteriaAPI.getCondition("TOOL_TYPE", "toolType", String.valueOf(id), NumberOperators.EQUALS));
			}
			List<PurchaseOrderContext> list = builder.get();
			context.put(FacilioConstants.ContextNames.PURCHASE_ORDERS, list);
					
		return false;
	}

}
