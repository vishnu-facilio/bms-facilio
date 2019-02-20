package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAddInventoryCostCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule inventoryCostModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> inventoryCostFields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);
//		Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(inventoryCostFields);
		long inventoryId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<InventoryCostContext> inventoryCost = (List<InventoryCostContext>) context.get(FacilioConstants.ContextNames.INVENTORY_COST);
		
		FacilioModule inventoryModule = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
		List<FacilioField> inventoryFields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);
		Map<String, FacilioField> inventoryFieldMap = FieldFactory.getAsMap(inventoryCostFields);

		SelectRecordsBuilder<InventryContext> inventoryselectBuilder = new SelectRecordsBuilder<InventryContext>()
				.select(inventoryFields).table(inventoryModule.getTableName()).moduleName(inventoryModule.getName())
				.beanClass(InventryContext.class).andCondition(CriteriaAPI.getIdCondition(inventoryId, inventoryModule));
		
		List<InventryContext> inventory = inventoryselectBuilder.get();
		InventryContext inven = new InventryContext();
		if (inventory != null && !inventory.isEmpty()) {
			inven = inventory.get(0);
		}
		List<InventoryCostContext> icToBeAdded = new ArrayList<>();
		if(inventoryCost!=null && !inventory.isEmpty()) {
			for(InventoryCostContext ic : inventoryCost ){
				ic.setModifiedTime(System.currentTimeMillis());
				ic.setInventory(inven);
				double quantity = ic.getQuantity();
				ic.setCurrentQuantity(quantity);
				
				if (ic.getId() <= 0) {
					//Insert
					ic.setTtime(System.currentTimeMillis());
					ic.setCostDate(System.currentTimeMillis());
					icToBeAdded.add(ic);
				}
				else {
					updateInventorycost(inventoryCostModule, inventoryCostFields, ic);
				}				
			}
			if(icToBeAdded!=null && !icToBeAdded.isEmpty()) {
				addInventorycost(inventoryCostModule, inventoryCostFields, icToBeAdded);
			}
		}
		context.put(FacilioConstants.ContextNames.INVENTORY_COST, inventoryCost);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inventoryCost);
		context.put(FacilioConstants.ContextNames.INVENTORY_ID, inventoryId);
		context.put(FacilioConstants.ContextNames.INVENTORY_IDS, Collections.singletonList(inventoryId));
		return false;
	}
	
	private void addInventorycost(FacilioModule module, List<FacilioField> fields, List<InventoryCostContext> parts) throws Exception {
		InsertRecordBuilder<InventoryCostContext> readingBuilder = new InsertRecordBuilder<InventoryCostContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(parts);
		readingBuilder.save();
	}
	
	private void updateInventorycost(FacilioModule module, List<FacilioField> fields, InventoryCostContext part) throws Exception {

		UpdateRecordBuilder<InventoryCostContext> updateBuilder = new UpdateRecordBuilder<InventoryCostContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println( Thread.currentThread().getName()+"Exiting updateCosts in  AddorUpdateCommand#######  ");

	}

}
