package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateToolStockTransactionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
		ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);

		if (toolIds != null && !toolIds.isEmpty()) {
			long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
			FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
			List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

			SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>()
					.select(Toolfields).table(Toolmodule.getTableName()).moduleName(Toolmodule.getName())
					.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(toolIds, Toolmodule));

			List<ToolContext> tools = toolselectBuilder.get();
			ToolContext tool = (ToolContext) context.get(FacilioConstants.ContextNames.RECORD);
			// if (tools != null && !tools.isEmpty()) {
			// tool = tools.get(0);
			// }

			FacilioModule ToolTypemodule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
			List<FacilioField> ToolTypefields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

			SelectRecordsBuilder<ToolTypesContext> toolTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
					.select(ToolTypefields).table(ToolTypemodule.getTableName()).moduleName(ToolTypemodule.getName())
					.beanClass(ToolTypesContext.class)
					.andCondition(CriteriaAPI.getIdCondition(toolTypeId, ToolTypemodule));

			List<ToolTypesContext> toolTypes = toolTypesselectBuilder.get();
			ToolTypesContext toolType = null;
			if (toolTypes != null && !toolTypes.isEmpty()) {
				toolType = toolTypes.get(0);
			}

			if (toolType == null) {
				throw new IllegalArgumentException("No such tool found");
			}

			List<ToolTransactionContext> toolTransaction = new ArrayList<>();

			if (toolType.isRotating()) {

				List<PurchasedToolContext> pts = (List<PurchasedToolContext>) context
						.get(FacilioConstants.ContextNames.PURCHASED_TOOL);

				if (pts != null && !pts.isEmpty()) {
					for (PurchasedToolContext pt : pts) {
						ToolTransactionContext transaction = new ToolTransactionContext();
						transaction.setPurchasedTool(pt);
						transaction.setTool(pt.getTool());
						transaction.setQuantity(1);
						transaction.setParentId(pt.getId());
						transaction.setIsReturnable(false);
						if(shipment == null) {
							transaction.setTransactionType(TransactionType.STOCK.getValue());
							transaction.setTransactionState(TransactionState.ADDITION.getValue());
						}
						else {
							transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
							transaction.setTransactionState(TransactionState.ADDITION.getValue());
							transaction.setShipment(shipment.getId());
						}

						transaction.setToolType(toolType);
						transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);

						SelectRecordsBuilder<ToolTransactionContext> transactionsselectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
								.select(fields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(ToolTransactionContext.class)
								.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
										String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.VALUE_IS))
								.andCondition(CriteriaAPI.getCondition(fieldMap.get("purchasedTool"),
										String.valueOf(pt.getId()), PickListOperators.IS));
						List<ToolTransactionContext> transactions = transactionsselectBuilder.get();
						if (transactions != null && !transactions.isEmpty()) {
							ToolTransactionContext it = transactions.get(0);
							it.setQuantity(1);
							UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
									.module(module).fields(modBean.getAllFields(module.getName()))
									.andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
							updateBuilder.update(it);
						} else {
							toolTransaction.add(transaction);
						}
					}
				}
			} else {
				ToolTransactionContext transaction = new ToolTransactionContext();
				transaction.setTool(tool);
				transaction.setQuantity(tool.getQuantity());
				transaction.setParentId(tool.getId());
				transaction.setIsReturnable(false);
				transaction.setToolType(toolType);
				transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);

				if(shipment == null) {
					transaction.setTransactionType(TransactionType.STOCK.getValue());
					transaction.setTransactionState(TransactionState.ADDITION.getValue());
				}
				else {
					transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
					transaction.setTransactionState(TransactionState.ADDITION.getValue());
					transaction.setShipment(shipment.getId());
				}

				SelectRecordsBuilder<ToolTransactionContext> transactionsselectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
						.select(fields).table(module.getTableName()).moduleName(module.getName())
						.beanClass(ToolTransactionContext.class)
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
								String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.VALUE_IS));
				List<ToolTransactionContext> transactions = transactionsselectBuilder.get();
				if (transactions != null && !transactions.isEmpty()) {
					ToolTransactionContext it = transactions.get(0);
					it.setQuantity(1);
					UpdateRecordBuilder<ToolTransactionContext> updateBuilder = new UpdateRecordBuilder<ToolTransactionContext>()
							.module(module).fields(modBean.getAllFields(module.getName()))
							.andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
					updateBuilder.update(it);
				} else {
					toolTransaction.add(transaction);
				}
			}
			InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
					.module(module).fields(fields).addRecords(toolTransaction);
			readingBuilder.save();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
		} else {
			ToolContext tool = (ToolContext) context.get(FacilioConstants.ContextNames.TOOL);
			AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.ROTATING_ASSET);
			if (tool != null) {
				ToolContext t = ToolsApi.getTool(tool.getId());
				double q = t.getQuantity() >= 0 ? t.getQuantity() : 0;
				q += 1;
				t.setQuantity(q);
				ToolTransactionContext transaction = new ToolTransactionContext();
				transaction.setTool(t);
				transaction.setQuantity(1);
				transaction.setParentId(t.getId());
				transaction.setIsReturnable(false);
				transaction.setToolType(t.getToolType());
				transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
				transaction.setAsset(asset);
				if(shipment == null) {
					transaction.setTransactionType(TransactionType.STOCK.getValue());
					transaction.setTransactionState(TransactionState.ADDITION.getValue());
				}
				else {
					transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
					transaction.setTransactionState(TransactionState.ADDITION.getValue());
					transaction.setShipment(shipment.getId());
				}

				updateToolQty(t);
				InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
						.module(module).fields(fields).addRecord(transaction);
				readingBuilder.save();
				context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(transaction));
			}
		}
		return false;
	}

	private void updateToolQty(ToolContext tool) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);

		UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>().module(module)
				.fields(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);
	}

}
