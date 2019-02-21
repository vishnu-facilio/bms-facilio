package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConsumableContext;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class InventoryCostQuantityRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule consumableModule = modBean.getModule(FacilioConstants.ContextNames.CONSUMABLES);
		List<FacilioField> consumableFields = modBean.getAllFields(FacilioConstants.ContextNames.CONSUMABLES);
		Map<String, FacilioField> consumableFieldMap = FieldFactory.getAsMap(consumableFields);

		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField totalQuantityConsumedField = new FacilioField();
		totalQuantityConsumedField.setName("totalQuantityConsumed");
		totalQuantityConsumedField.setColumnName("sum(" + consumableFieldMap.get("quantityConsumed") + ")");
		fields.add(totalQuantityConsumedField);
		
		List<? extends ConsumableContext> consumables = (List<ConsumableContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		Set<Long> uniqueCostIds = new HashSet<Long>();
		Set<Long> uniqueInventoryIds = new HashSet<Long>();
		int totalQuantityConsumed = 0;
		if (consumables != null && !consumables.isEmpty()) {
			for (ConsumableContext consumable : consumables) {
				uniqueCostIds.add(consumable.getInventoryCost().getId());
				uniqueInventoryIds.add(consumable.getInventory().getId());
			}
		}
		FacilioModule inventoryCostModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_COST);
		List<FacilioField> inventoryCostFields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_COST);
		Map<String, FacilioField> inventoryrCostsFieldMap = FieldFactory.getAsMap(inventoryCostFields);
		for (Long id : uniqueCostIds) {
			Double totalConsumed = getTotalQuantityConsumed(id);
			InventoryCostContext inventoryCost = new InventoryCostContext();
			SelectRecordsBuilder<InventoryCostContext> selectBuilder = new SelectRecordsBuilder<InventoryCostContext>()
					.select(inventoryCostFields).table(inventoryCostModule.getTableName())
					.moduleName(inventoryCostModule.getName()).beanClass(InventoryCostContext.class)
					.andCondition(CriteriaAPI.getIdCondition(id, inventoryCostModule));
			List<InventoryCostContext> inventoryCosts = selectBuilder.get();
			if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
				inventoryCost = inventoryCosts.get(0);
				Double quantity = inventoryCost.getQuantity();
				inventoryCost.setCurrentQuantity(quantity - totalConsumed);
				UpdateRecordBuilder<InventoryCostContext> updateBuilder = new UpdateRecordBuilder<InventoryCostContext>()
						.module(inventoryCostModule).fields(modBean.getAllFields(inventoryCostModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(id, inventoryCostModule));

				updateBuilder.update(inventoryCost);
			}

		}
		
		List<Long> inventoryIds = new ArrayList<Long>();
		inventoryIds.addAll(uniqueInventoryIds);
		context.put(FacilioConstants.ContextNames.INVENTORY_IDS, inventoryIds);
		return false;
	}

	public static Double getTotalQuantityConsumed(long inventoryCostId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule consumableModule = modBean.getModule(FacilioConstants.ContextNames.CONSUMABLES);
		List<FacilioField> consumableFields = modBean.getAllFields(FacilioConstants.ContextNames.CONSUMABLES);
		Map<String, FacilioField> consumableFieldMap = FieldFactory.getAsMap(consumableFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(consumableModule.getTableName())
				.andCustomWhere(consumableModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(consumableModule),
						String.valueOf(consumableModule.getModuleId()), NumberOperators.EQUALS));

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("value", "sum(QUANTITY_CONSUMED)", FieldType.DECIMAL));
		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get("inventoryCost"),
				String.valueOf(inventoryCostId), PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			return (Double) rs.get(0).get("value");
		}
		return 0d;
	}

}