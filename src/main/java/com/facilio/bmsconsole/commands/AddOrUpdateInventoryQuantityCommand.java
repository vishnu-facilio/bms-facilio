package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateInventoryQuantityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule inventoryCostModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> inventoryCostFields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);
		Map<String, FacilioField> inventoryrCostsFieldMap = FieldFactory.getAsMap(inventoryCostFields);
		// long inventoryId = (long)
		// context.get(FacilioConstants.ContextNames.INVENTORY_ID);
		List<Long> inventoryIds = (List<Long>) context.get(FacilioConstants.ContextNames.INVENTORY_IDS);

		if (inventoryIds != null && !inventoryIds.isEmpty()) {
			for (long inventoryId : inventoryIds) {
				SelectRecordsBuilder<InventoryCostContext> selectBuilder = new SelectRecordsBuilder<InventoryCostContext>()
						.select(inventoryCostFields).table(inventoryCostModule.getTableName())
						.moduleName(inventoryCostModule.getName()).beanClass(InventoryCostContext.class)
						.andCondition(CriteriaAPI.getCondition(inventoryrCostsFieldMap.get("inventory"),
								String.valueOf(inventoryId), PickListOperators.IS));

				List<InventoryCostContext> inventoryCosts = selectBuilder.get();
				int quantity = 0;
				if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
					for (InventoryCostContext invCost : inventoryCosts) {
						quantity += invCost.getCurrentQuantity();
					}
				}
				FacilioModule inventoryModule = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
				List<FacilioField> inventoryFields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);
				Map<String, FacilioField> inventoryFieldMap = FieldFactory.getAsMap(inventoryFields);

				SelectRecordsBuilder<InventryContext> inventoryselectBuilder = new SelectRecordsBuilder<InventryContext>()
						.select(inventoryFields).table(inventoryModule.getTableName())
						.moduleName(inventoryModule.getName()).beanClass(InventryContext.class)
						.andCondition(CriteriaAPI.getIdCondition(inventoryId, inventoryModule));

				List<InventryContext> inventory = inventoryselectBuilder.get();
				InventryContext inven = new InventryContext();
				if (inventory != null && !inventory.isEmpty()) {
					inven = inventory.get(0);
					inven.setQuantity(quantity);
				}

				UpdateRecordBuilder<InventryContext> updateBuilder = new UpdateRecordBuilder<InventryContext>()
						.module(inventoryModule).fields(modBean.getAllFields(inventoryModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(inven.getId(), inventoryModule));

				updateBuilder.update(inven);
			}
		}

		return false;
	}

}
