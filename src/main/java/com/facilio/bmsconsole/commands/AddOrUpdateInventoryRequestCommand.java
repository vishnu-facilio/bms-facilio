package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateInventoryRequestCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (inventoryRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(inventoryRequestContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			// setting current user to requestedFor
			if(inventoryRequestContext.getRequestedFor() == null) {
		 	  inventoryRequestContext.setRequestedFor(AccountUtil.getCurrentUser());
			}
			if (inventoryRequestContext.getId() > 0) {
				updateRecord(inventoryRequestContext, module, fields);
				
				DeleteRecordBuilder<InventoryRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<InventoryRequestLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", String.valueOf(inventoryRequestContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				if(inventoryRequestContext.getRequestedTime() == -1) {
					inventoryRequestContext.setRequestedTime(System.currentTimeMillis());
				}
				
				inventoryRequestContext.setStatus(InventoryRequestContext.Status.REQUESTED);
				addRecord(true, Collections.singletonList(inventoryRequestContext), module, fields);
			}
			
			updateLineItems(inventoryRequestContext);
			addRecord(false, inventoryRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, inventoryRequestContext);
		}
		return false;
	}
	
	
	private void updateLineItems(InventoryRequestContext inventoryRequestContext) throws Exception{
		List<InventoryRequestLineItemContext> rotatingItems = new ArrayList<InventoryRequestLineItemContext>();
		
		for (InventoryRequestLineItemContext lineItemContext : inventoryRequestContext.getLineItems()) {
			lineItemContext.setInventoryRequestId(inventoryRequestContext.getId());
			lineItemContext.setIssuedQuantity(lineItemContext.getQuantity());
			if(inventoryRequestContext.getParentId() > 0) {
				lineItemContext.setParentId(inventoryRequestContext.getParentId());
			}
			if(inventoryRequestContext.getStoreRoom() != null &&  inventoryRequestContext.getStoreRoom().getId() > 0) {
				lineItemContext.setStoreRoom(inventoryRequestContext.getStoreRoom());
			}
			if(lineItemContext.getAssetIds() != null && lineItemContext.getAssetIds().size() > 0) {
				AssetContext lineItemAsset = new AssetContext();
				lineItemAsset.setId(lineItemContext.getAssetIds().get(0));
				lineItemContext.setAsset(lineItemAsset);
				lineItemContext.setQuantity(1);
				lineItemContext.setIssuedQuantity(1);
				for(int i=1; i<lineItemContext.getAssetIds().size(); i++) {
					 InventoryRequestLineItemContext lineItem = new InventoryRequestLineItemContext();
					 AssetContext asset = new AssetContext();
					 asset.setId(lineItemContext.getAssetIds().get(i));
					 lineItem.setAsset(asset);
					 lineItem.setInventoryType(lineItemContext.getInventoryType());
					 if(lineItemContext.getInventoryType() == InventoryType.ITEM.getValue()) {
						 lineItem.setItemType(lineItemContext.getItemType());
					 }
					 else if(lineItemContext.getInventoryType() == InventoryType.TOOL.getValue()) {
						 lineItem.setToolType(lineItemContext.getToolType());
					 }
					 
					 lineItem.setQuantity(1);
					 lineItem.setIssuedQuantity(1);
						
					 lineItem.setInventoryRequestId(inventoryRequestContext.getId());
					 if(inventoryRequestContext.getParentId() > 0) {
						 lineItem.setParentId(inventoryRequestContext.getParentId());
					 }
					 if(inventoryRequestContext.getStoreRoom() != null &&  inventoryRequestContext.getStoreRoom().getId() > 0) {
						 lineItem.setStoreRoom(inventoryRequestContext.getStoreRoom());
					 }
					
					 rotatingItems.add(lineItem); 
				}
			}
			
		}
		inventoryRequestContext.getLineItems().addAll(rotatingItems);
	}
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	
}
