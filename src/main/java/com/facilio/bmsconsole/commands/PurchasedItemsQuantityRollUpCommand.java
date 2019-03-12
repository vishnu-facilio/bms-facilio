package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ItemContext;
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

public class PurchasedItemsQuantityRollUpCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(PurchasedItemsQuantityRollUpCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> itemTransactionsFieldMap = FieldFactory.getAsMap(itemTransactionsFields);

		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField totalQuantityConsumedField = new FacilioField();
		totalQuantityConsumedField.setName("totalQuantityConsumed");
		totalQuantityConsumedField.setColumnName("sum(" + itemTransactionsFieldMap.get("quantityConsumed") + ")");
		fields.add(totalQuantityConsumedField);
		
		List<? extends ItemTransactionsContext> itemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		Set<Long> uniquepurchasedItemsIds = new HashSet<Long>();
		Set<Long> uniqueItemIds = new HashSet<Long>();
		int totalQuantityConsumed = 0;
		if (itemTransactions != null && !itemTransactions.isEmpty()) {
			for (ItemTransactionsContext consumable : itemTransactions) {
				uniquepurchasedItemsIds.add(consumable.getPurchasedItem().getId());
				uniqueItemIds.add(consumable.getItem().getId());
			}
		}
		FacilioModule purchasedItemsModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		Map<String, FacilioField> purchasedItemFieldMap = FieldFactory.getAsMap(purchasedItemFields);
		for (Long id : uniquepurchasedItemsIds) {
			Double totalConsumed = getTotalQuantityConsumed(id);
			PurchasedItemContext inventoryCost = new PurchasedItemContext();
			SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
					.select(purchasedItemFields).table(purchasedItemsModule.getTableName())
					.moduleName(purchasedItemsModule.getName()).beanClass(PurchasedItemContext.class)
					.andCondition(CriteriaAPI.getIdCondition(id, purchasedItemsModule));
			List<PurchasedItemContext> purchasedItems = selectBuilder.get();
			if (purchasedItems != null && !purchasedItems.isEmpty()) {
				inventoryCost = purchasedItems.get(0);
				inventoryCost.setCurrentQuantity(totalConsumed);
				UpdateRecordBuilder<PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<PurchasedItemContext>()
						.module(purchasedItemsModule).fields(modBean.getAllFields(purchasedItemsModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(id, purchasedItemsModule));
				LOGGER.info("totalCost"+ inventoryCost.getCurrentQuantity());
				updateBuilder.update(inventoryCost);
			}

		}
		
		List<Long> inventoryIds = new ArrayList<Long>();
		inventoryIds.addAll(uniqueItemIds);
		context.put(FacilioConstants.ContextNames.ITEM_IDS, inventoryIds);
		return false;
	}

	public static Double getTotalQuantityConsumed(long inventoryCostId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule consumableModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> consumableFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> consumableFieldMap = FieldFactory.getAsMap(consumableFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(consumableModule.getTableName())
				.andCustomWhere(consumableModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(consumableModule),
						String.valueOf(consumableModule.getModuleId()), NumberOperators.EQUALS));

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)", FieldType.DECIMAL));
		fields.add(FieldFactory.getField("issues", "sum(case WHEN TRANSACTION_STATE = 2 THEN QUANTITY ELSE 0 END)", FieldType.DECIMAL));
		fields.add(FieldFactory.getField("returns", "sum(case WHEN TRANSACTION_STATE = 3 THEN QUANTITY ELSE 0 END)", FieldType.DECIMAL));
		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get("purchasedItem"),
				String.valueOf(inventoryCostId), PickListOperators.IS));

		
		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0, issues = 0, returns = 0;
			addition = rs.get(0).get("addition")!=null ?(Double)  rs.get(0).get("addition") : 0;
			issues=  rs.get(0).get("issues")!=null ? (Double) rs.get(0).get("issues") : 0;
			returns = rs.get(0).get("returns")!=null ? (Double) rs.get(0).get("returns") : 0;
			LOGGER.info(addition + " " + issues + " " + returns );
			return ((addition+returns) - issues);
		}
		return 0d;
	}

}