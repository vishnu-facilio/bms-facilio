package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolTransactionRemainingQuantityRollupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		TransactionState transactionState = (TransactionState) context
				.get(FacilioConstants.ContextNames.TRANSACTION_STATE);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionsFieldsMap = FieldFactory.getAsMap(toolTransactionsFields);
		if (toolTransactions != null && !toolTransactions.isEmpty()) {
			if (toolTransactions.get(0).getTransactionStateEnum() == TransactionState.RETURN) {
				for (ToolTransactionContext transaction : toolTransactions) {
					ToolTransactionContext toolTransaction = getToolTransaction(transaction.getParentTransactionId(),
							toolTransactionsModule, toolTransactionsFields, toolTransactionsFieldsMap);
					double totalReturnQuantity = getTotalReturnQuantity(transaction.getParentTransactionId(), toolTransactionsModule,
							toolTransactionsFieldsMap);
					double totalRemainingQuantity = toolTransaction.getQuantity() - totalReturnQuantity;
					toolTransaction.setRemainingQuantity(totalRemainingQuantity);

					UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
							.module(toolTransactionsModule).fields(toolTransactionsFields)
							.andCondition(CriteriaAPI.getIdCondition(transaction.getParentTransactionId(), toolTransactionsModule));
					updateBuilder.update(toolTransaction);
				}
			}
		}
		return false;
	}

	private double getTotalReturnQuantity(long parentTransactionId, FacilioModule module,
			Map<String, FacilioField> fieldsMap) throws Exception {

		if (parentTransactionId <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalRemainingQuantity", "sum(QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ToolTransactionContext> builder = new SelectRecordsBuilder<ToolTransactionContext>()
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

	private ToolTransactionContext getToolTransaction(long parentTransactionId, FacilioModule module,
			List<FacilioField> fields, Map<String, FacilioField> fieldsMap) throws Exception {

		if (parentTransactionId <= 0) {
			return null;
		}

		SelectRecordsBuilder<ToolTransactionContext> builder = new SelectRecordsBuilder<ToolTransactionContext>()
				.select(fields).moduleName(module.getName()).beanClass(ToolTransactionContext.class)
				.andCondition(CriteriaAPI.getIdCondition(parentTransactionId, module));

		List<ToolTransactionContext> toolTransactions = builder.get();
		if (toolTransactions != null && !toolTransactions.isEmpty()) {
			return toolTransactions.get(0);
		}
		return null;

	}

}
