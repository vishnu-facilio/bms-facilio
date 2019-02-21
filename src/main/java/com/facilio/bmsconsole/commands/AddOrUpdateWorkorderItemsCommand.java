package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.facilio.bmsconsole.modules.FieldFactory;
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
		List<WorkorderItemContext> workorderItemslist = new ArrayList<>();
		List<WorkorderItemContext> itemToBeAdded = new ArrayList<>();
		if (workorderitems != null) {
			long parentId = workorderitems.get(0).getParentId();
			for (WorkorderItemContext workorderitem : workorderitems) {
				InventryContext inventry = getInventory(workorderitem.getInventory().getId());
				List<InventoryCostContext> inventoryCosts = new ArrayList<>();

				if (inventry.getCostType() == 1) {
					inventoryCosts = getInventoryCostList(inventry.getId(), " asc");
				} else if (inventry.getCostType() == 2) {
					inventoryCosts = getInventoryCostList(inventry.getId(), " desc");
				}

				if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
					InventoryCostContext invenCost = inventoryCosts.get(0);
					if (workorderitem.getQuantityConsumed() <= invenCost.getCurrentQuantity()) {
						double costOccured = 0;
						if (invenCost.getUnitcost() >= 0) {
							costOccured = invenCost.getUnitcost() * workorderitem.getQuantityConsumed();
						}
						workorderitem.setInventoryCost(invenCost);
						workorderitem.setCost(costOccured);
						workorderitem.setParentId(parentId);
						workorderitem.setModifiedTime(System.currentTimeMillis());
						if (workorderitem.getId() <= 0) {
							// Insert
							workorderItemslist.add(workorderitem);
							itemToBeAdded.add(workorderitem);
						} else {
							// update
							workorderItemslist.add(workorderitem);
							updateWorkorderParts(workorderItemsModule, workorderItemFields, workorderitem);
						}
						break;
					} else {
						double requiredQuantity = workorderitem.getQuantityConsumed();
						for (InventoryCostContext icosts : inventoryCosts) {
							WorkorderItemContext item = new WorkorderItemContext();
							double costOccured = 0;
							double quantityUsedForTheCost = 0;
							if (requiredQuantity <= icosts.getCurrentQuantity()) {
								quantityUsedForTheCost = requiredQuantity;
							} else {
								quantityUsedForTheCost = icosts.getCurrentQuantity();
							}
							if (icosts.getUnitcost() >= 0) {
								costOccured = icosts.getUnitcost() * quantityUsedForTheCost;
							}
							item.setCost(costOccured);
							item.setModifiedTime(System.currentTimeMillis());
							item.setInventoryCost(icosts);
							item.setQuantityConsumed(quantityUsedForTheCost);
							item.setInventory(inventry);
							item.setParentId(parentId);
							requiredQuantity -= quantityUsedForTheCost;
							if (workorderitem.getId() <= 0) {
								// Insert
								workorderItemslist.add(item);
								itemToBeAdded.add(item);
							} else {
								// update
								workorderItemslist.add(item);
								updateWorkorderParts(workorderItemsModule, workorderItemFields, item);
							}
							if (requiredQuantity <= 0) {
								break;
							}
						}
					}
				}
			}
			if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
				addWorkorderParts(workorderItemsModule, workorderItemFields, itemToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderitems.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.INVENTORY_ID, workorderitems.get(0).getInventory().getId());
			context.put(FacilioConstants.ContextNames.INVENTORY_IDS,
					Collections.singletonList(workorderitems.get(0).getInventory().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItemslist);
		}
		return false;
	}

	private void addWorkorderParts(FacilioModule module, List<FacilioField> fields, List<WorkorderItemContext> parts)
			throws Exception {
		InsertRecordBuilder<WorkorderItemContext> readingBuilder = new InsertRecordBuilder<WorkorderItemContext>()
				.module(module).fields(fields).addRecords(parts);
		readingBuilder.save();
	}

	private void updateWorkorderParts(FacilioModule module, List<FacilioField> fields, WorkorderItemContext part)
			throws Exception {

		UpdateRecordBuilder<WorkorderItemContext> updateBuilder = new UpdateRecordBuilder<WorkorderItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	public static InventryContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);

		SelectRecordsBuilder<InventryContext> selectBuilder = new SelectRecordsBuilder<InventryContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(InventryContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id);

		List<InventryContext> inventories = selectBuilder.get();

		if (inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}

	public static InventoryCostContext getInventoryCost(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);

		SelectRecordsBuilder<InventoryCostContext> selectBuilder = new SelectRecordsBuilder<InventoryCostContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(InventoryCostContext.class).andCustomWhere(module.getTableName() + ".INVENTORY_ID = ?", id);

		List<InventoryCostContext> inventoryCosts = selectBuilder.get();

		if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
			return inventoryCosts.get(0);
		}
		return null;
	}

	public static List<InventoryCostContext> getInventoryCostList(long id, String orderByType) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField moduleField = FieldFactory.getModuleIdField();
		SelectRecordsBuilder<InventoryCostContext> selectBuilder = new SelectRecordsBuilder<InventoryCostContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(InventoryCostContext.class).andCustomWhere(module.getTableName() + ".INVENTORY_ID = ?", id)
				.orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

		List<InventoryCostContext> inventoryCosts = selectBuilder.get();

		if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
			return inventoryCosts;
		}
		return null;
	}
}
