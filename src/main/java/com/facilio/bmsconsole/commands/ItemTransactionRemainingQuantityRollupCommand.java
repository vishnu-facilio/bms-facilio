package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
public class ItemTransactionRemainingQuantityRollupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		TransactionState transactionState = (TransactionState) context
				.get(FacilioConstants.ContextNames.TRANSACTION_STATE);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		EventType event = (EventType)context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		List<ItemTransactionsContext> itemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> itemTransactionsFieldsMap = FieldFactory.getAsMap(itemTransactionsFields);
		if (itemTransactions != null && !itemTransactions.isEmpty()) {
			if (itemTransactions.get(0).getTransactionStateEnum() == TransactionState.RETURN) {
				for (ItemTransactionsContext transaction : itemTransactions) {
					ItemTransactionsContext itemTransaction = getItemTransaction(transaction.getParentTransactionId(),
							itemTransactionsModule, itemTransactionsFields, itemTransactionsFieldsMap);
					double totalReturnQuantity = getTotalReturnQuantity(transaction.getParentTransactionId(),
							itemTransactionsModule, itemTransactionsFieldsMap, transactionState);
					double totalRemainingQuantity = itemTransaction.getQuantity() - totalReturnQuantity;
					itemTransaction.setRemainingQuantity(totalRemainingQuantity);

					UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
							.module(itemTransactionsModule).fields(itemTransactionsFields).andCondition(CriteriaAPI
									.getIdCondition(transaction.getParentTransactionId(), itemTransactionsModule));
					updateBuilder.update(itemTransaction);
				}
			}
			else if (itemTransactions.get(0).getTransactionStateEnum() == TransactionState.USE && itemTransactions.get(0).getParentTransactionId() > 0) {
				for (ItemTransactionsContext transaction : itemTransactions) {
					ItemTransactionsContext itemTransaction = getItemTransaction(transaction.getParentTransactionId(),
							itemTransactionsModule, itemTransactionsFields, itemTransactionsFieldsMap);
					double totalRemainingQuantity = 0;
					totalRemainingQuantity = itemTransaction.getQuantity() - getTotalRemainingQuantityQuantity(transaction.getParentTransactionId(),
								itemTransactionsModule, itemTransactionsFieldsMap, TransactionState.USE);
					
					itemTransaction.setRemainingQuantity(totalRemainingQuantity);

					UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
							.module(itemTransactionsModule).fields(itemTransactionsFields).andCondition(CriteriaAPI
									.getIdCondition(transaction.getParentTransactionId(), itemTransactionsModule));
					updateBuilder.update(itemTransaction);
				}
			}
		}
		return false;
	}

	private double getTotalReturnQuantity(long parentTransactionId, FacilioModule module,
			Map<String, FacilioField> fieldsMap, TransactionState transactionState) throws Exception {

		if (parentTransactionId <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalRemainingQuantity", "sum(QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemTransactionsContext> builder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(field).moduleName(module.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentTransactionId"),
						String.valueOf(parentTransactionId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("transactionState"), String.valueOf(3),
						NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalRemainingQuantity") != null) {
				return (double) rs.get(0).get("totalRemainingQuantity");
			}
			return 0d;
		}
		return 0d;
	}
	
	private double getTotalRemainingQuantityQuantity(long parentTransactionId, FacilioModule module,
			Map<String, FacilioField> fieldsMap, TransactionState transactionState) throws Exception {

		if (parentTransactionId <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalRemainingQuantity", "sum(QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemTransactionsContext> builder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(field).moduleName(module.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentTransactionId"),
						String.valueOf(parentTransactionId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("transactionState"), String.valueOf(4),
						NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalRemainingQuantity") != null) {
				return (double) rs.get(0).get("totalRemainingQuantity");
			}
			return 0d;
		}
		return 0d;
	}
	
	private ItemTransactionsContext getItemTransaction(long parentTransactionId, FacilioModule module,
			List<FacilioField> fields, Map<String, FacilioField> fieldsMap) throws Exception {

		if (parentTransactionId <= 0) {
			return null;
		}

		SelectRecordsBuilder<ItemTransactionsContext> builder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(fields).moduleName(module.getName()).beanClass(ItemTransactionsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(parentTransactionId, module));

		List<ItemTransactionsContext> itemTransactions = builder.get();
		if (itemTransactions != null && !itemTransactions.isEmpty()) {
			return itemTransactions.get(0);
		}
		return null;

	}

}
