package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryTransactionsContext;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.StockedToolsReturnTrackingContext;
import com.facilio.bmsconsole.context.StockedToolsTransactionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
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

public class StockedToolsReturnQuantityRollupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule stockedToolsTransactionModule = modBean
				.getModule(FacilioConstants.ContextNames.STOCKED_TOOLS_TRANSACTIONS);
		List<FacilioField> stockedToolsTransactionFields = modBean
				.getAllFields(FacilioConstants.ContextNames.STOCKED_TOOLS_TRANSACTIONS);
		Map<String, FacilioField> stockedToolsTransactionFieldMap = FieldFactory
				.getAsMap(stockedToolsTransactionFields);

		List<FacilioField> fields = new ArrayList<FacilioField>();
		List<? extends StockedToolsTransactionContext> stockedToolsTransactions = (List<StockedToolsTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		Set<Long> uniqueStockedToolsIds = new HashSet<Long>();
		int totalQuantityConsumed = 0;
		if (stockedToolsTransactions != null && !stockedToolsTransactions.isEmpty()) {
			for (StockedToolsTransactionContext stockedToolTransaction : stockedToolsTransactions) {
				double totalReturnQuantity = getTotalReturnQuantity(stockedToolTransaction.getId());

				uniqueStockedToolsIds.add(stockedToolTransaction.getStockedTool().getId());
//				stockedToolTransaction.setReturnQuantity(totalReturnQuantity);
				UpdateRecordBuilder<StockedToolsTransactionContext> updateBuilder = new UpdateRecordBuilder<StockedToolsTransactionContext>()
						.module(stockedToolsTransactionModule)
						.fields(modBean.getAllFields(stockedToolsTransactionModule.getName())).andCondition(CriteriaAPI
								.getIdCondition(stockedToolTransaction.getId(), stockedToolsTransactionModule));

				updateBuilder.update(stockedToolTransaction);

			}
			List<Long> stockedToolsIds = new ArrayList<Long>();
			stockedToolsIds.addAll(uniqueStockedToolsIds);
			context.put(FacilioConstants.ContextNames.STOCKED_TOOLS_IDS, stockedToolsIds);
		}

		return false;
	}

	public static Double getTotalReturnQuantity(long id) throws Exception {

		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STOCKED_TOOLS_RETURN_TRACKING);

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("value", "sum(RETURN_QUANTITY)", FieldType.DECIMAL));
		SelectRecordsBuilder<StockedToolsReturnTrackingContext> selectBuilder = new SelectRecordsBuilder<StockedToolsReturnTrackingContext>()
				.select(field).table(module.getTableName()).moduleName(module.getName())
				.beanClass(StockedToolsReturnTrackingContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		List<Map<String, Object>> rs = selectBuilder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("value") != null) {
				return (Double) rs.get(0).get("value");
			}
			return 0d;
		}
		return 0d;
	}
}
