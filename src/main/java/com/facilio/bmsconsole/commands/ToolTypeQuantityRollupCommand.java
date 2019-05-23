package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolTypeQuantityRollupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> toolTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_TYPES_IDS);
		if (toolTypesIds != null && !toolTypesIds.isEmpty()) {
			FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

			FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
			List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
			Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);

			FacilioModule transactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
			List<FacilioField> transactionFields = modBean
					.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
			Map<String, FacilioField> transactionFieldMap = FieldFactory.getAsMap(transactionFields);

			long lastPurchasedDate = -1, lastIssuedDate = -1;

			if (toolTypesIds != null && !toolTypesIds.isEmpty()) {
				for (Long id : toolTypesIds) {

					SelectRecordsBuilder<ToolContext> builder = new SelectRecordsBuilder<ToolContext>()
							.select(toolFields).moduleName(toolModule.getName())
							.andCondition(CriteriaAPI.getCondition(toolFieldMap.get("toolType"), String.valueOf(id),
									NumberOperators.EQUALS))
							.beanClass(ToolContext.class).orderBy("LAST_PURCHASED_DATE DESC");

					List<ToolContext> tools = builder.get();
					long storeRoomId = -1;
					ToolContext tool;
					if (tools != null && !tools.isEmpty()) {
						tool = tools.get(0);
						storeRoomId = tool.getStoreRoom().getId();
						lastPurchasedDate = tool.getLastPurchasedDate();
					}

					SelectRecordsBuilder<ToolTransactionContext> issuetransactionsbuilder = new SelectRecordsBuilder<ToolTransactionContext>()
							.select(transactionFields).moduleName(transactionModule.getName())
							.andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("toolType"),
									String.valueOf(id), NumberOperators.EQUALS))
							.andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("transactionState"),
									String.valueOf(2), NumberOperators.EQUALS))
							.beanClass(ToolTransactionContext.class).orderBy("CREATED_TIME DESC");

					List<ToolTransactionContext> transactions = issuetransactionsbuilder.get();
					ToolTransactionContext transaction;
					if (transactions != null && !transactions.isEmpty()) {
						transaction = transactions.get(0);
						lastIssuedDate = transaction.getSysCreatedTime();
					}

					double quantity = getTotalQuantity(id, toolModule, toolFieldMap);
					ToolTypesContext toolType = new ToolTypesContext();
					toolType.setId(id);
					toolType.setCurrentQuantity(quantity);
					toolType.setLastPurchasedDate(lastPurchasedDate);
					toolType.setLastIssuedDate(lastIssuedDate);

					UpdateRecordBuilder<ToolTypesContext> updateBuilder = new UpdateRecordBuilder<ToolTypesContext>()
							.module(toolTypesModule).fields(modBean.getAllFields(toolTypesModule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(id, toolTypesModule));

					updateBuilder.update(toolType);
					StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
				}
			}
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
		}
		return false;
	}

	public static double getTotalQuantity(long id, FacilioModule toolModule, Map<String, FacilioField> toolFieldMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalQuantity", "sum(CURRENT_QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>()
				.select(field).moduleName(toolModule.getName()).andCondition(CriteriaAPI
						.getCondition(toolFieldMap.get("toolType"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalQuantity") != null) {
				return (double) rs.get(0).get("totalQuantity");
			}
			return 0d;
		}
		return 0d;
	}

//	private void updateStoreRoomLastPurchasedDate(long storeRoomId, long lastPurchasedDate) throws Exception {
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
//		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
//		StoreRoomContext storeRoom = new StoreRoomContext();
//		storeRoom.setId(storeRoomId);
//		storeRoom.setLastPurchasedDate(lastPurchasedDate);
//		UpdateRecordBuilder<StoreRoomContext> updateBuilder = new UpdateRecordBuilder<StoreRoomContext>().module(module)
//				.fields(fields).andCondition(CriteriaAPI.getIdCondition(storeRoomId, module));
//		updateBuilder.update(storeRoom);
//	}

}
