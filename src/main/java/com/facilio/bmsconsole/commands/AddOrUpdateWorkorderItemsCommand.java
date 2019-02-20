package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderItemsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderItemsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
		List<FacilioField> workorderItemFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_ITEMS);
		List<WorkorderItemContext> workorderitems = (List<WorkorderItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderItemContext> itemToBeAdded = new ArrayList<>();
		if (workorderitems != null) {
			for (WorkorderItemContext workorderitem : workorderitems) {
				InventryContext inventry = getInventory(workorderitem.getInventory().getId());
				InventoryCostContext inventoryCost = getInventoryCost(inventry.getId());
				double costOccured = 0;
				if (inventoryCost.getUnitcost() >= 0) {
					costOccured = inventoryCost.getUnitcost() * workorderitem.getQuantityConsumed();
				}
				workorderitem.setCost(costOccured);
				workorderitem.setModifiedTime(System.currentTimeMillis());
				if (workorderitem.getId() <= 0) {
					//Insert
					itemToBeAdded.add(workorderitem);
				}
				else {
					updateWorkorderParts(workorderItemsModule, workorderItemFields, workorderitem);
				}
			}
			if(itemToBeAdded!=null && !itemToBeAdded.isEmpty()) {
				addWorkorderParts(workorderItemsModule, workorderItemFields, itemToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderitems.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.INVENTORY_ID, workorderitems.get(0).getInventory().getId());
			context.put(FacilioConstants.ContextNames.INVENTORY_IDS, Collections.singletonList(workorderitems.get(0).getInventory().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderitems);
		}
		return false;
	}

	private void addWorkorderParts(FacilioModule module, List<FacilioField> fields, List<WorkorderItemContext> parts) throws Exception {
		InsertRecordBuilder<WorkorderItemContext> readingBuilder = new InsertRecordBuilder<WorkorderItemContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(parts);
		readingBuilder.save();
	}
	
	private void updateWorkorderParts(FacilioModule module, List<FacilioField> fields, WorkorderItemContext part) throws Exception {

		UpdateRecordBuilder<WorkorderItemContext> updateBuilder = new UpdateRecordBuilder<WorkorderItemContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println( Thread.currentThread().getName()+"Exiting updateReadings in  AddorUpdateCommand#######  ");

	}
	public static InventryContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);
		
		SelectRecordsBuilder<InventryContext> selectBuilder = new SelectRecordsBuilder<InventryContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(InventryContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<InventryContext> inventories = selectBuilder.get();
		
		if(inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}
	
	public static InventoryCostContext getInventoryCost(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);
		
		SelectRecordsBuilder<InventoryCostContext> selectBuilder = new SelectRecordsBuilder<InventoryCostContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(InventoryCostContext.class)
																	.andCustomWhere(module.getTableName()+".INVENTORY_ID = ?", id);
		
		List<InventoryCostContext> inventoryCosts = selectBuilder.get();
		
		if(inventoryCosts != null && !inventoryCosts.isEmpty()) {
			return inventoryCosts.get(0);
		}
		return null;
	}
}
