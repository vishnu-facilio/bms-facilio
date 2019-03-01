package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventoryTransactionsContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateInventoryStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InventoryCostContext> inventoryCost = (List<InventoryCostContext>) context
				.get(FacilioConstants.ContextNames.INVENTORY_COST);
		if (inventoryCost != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_TRANSACTIONS);
			List<FacilioField> fields = modBean
					.getAllFields(FacilioConstants.ContextNames.INVENTORY_TRANSACTIONS);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

//			SelectRecordsBuilder<InventoryTransactionsContext> inventoryselectBuilder = new SelectRecordsBuilder<InventoryTransactionsContext>()
//					.select(fields).table(module.getTableName()).moduleName(module.getName())
//					.beanClass(InventoryTransactionsContext.class)
//					.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"), String.valueOf(TransactionState.ADDITION), EnumOperators.IS));
		
			List<InventoryTransactionsContext> inventoryTransaction = new ArrayList<>();
			for (InventoryCostContext ic : inventoryCost) {
				InventoryTransactionsContext transaction = new InventoryTransactionsContext();
				transaction.setTransactionState(TransactionState.ADDITION.getValue());
				transaction.setInventoryCost(ic);
				transaction.setInventory(ic.getInventory());
				transaction.setQuantity(ic.getQuantity());
				transaction.setParentId(ic.getId());
				transaction.setIsReturnable(false);
				transaction.setTransactionType(TransactionType.STOCK.getValue());
				inventoryTransaction.add(transaction);
			}
			
			InsertRecordBuilder<InventoryTransactionsContext> readingBuilder = new InsertRecordBuilder<InventoryTransactionsContext>()
					.module(module)
					.fields(fields)
					.addRecords(inventoryTransaction);
			readingBuilder.save();
		}
		return false;
	}

}
