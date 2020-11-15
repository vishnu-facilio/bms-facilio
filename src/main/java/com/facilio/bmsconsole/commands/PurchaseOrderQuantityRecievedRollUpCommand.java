package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

public class PurchaseOrderQuantityRecievedRollUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Set<Long> receivableIds = (Set<Long>) context.get(FacilioConstants.ContextNames.RECEIVABLE_ID);

		if (receivableIds != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RECEIVABLE);
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

			FacilioModule receiptsmodule = modBean.getModule(FacilioConstants.ContextNames.RECEIPTS);
			List<FacilioField> receiptsfields = modBean.getAllFields(FacilioConstants.ContextNames.RECEIPTS);
			Map<String, FacilioField> receiptsfieldsMap = FieldFactory.getAsMap(receiptsfields);

			FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
			List<FacilioField> pofields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);

			Map<Long, Double> poIdVsQty = new HashMap<>();
			List<Long> poIds = new ArrayList<>();
			List<V3PurchaseOrderContext> receivedPOs = new ArrayList<>();
			SelectRecordsBuilder<ReceivableContext> builder = new SelectRecordsBuilder<ReceivableContext>()
					.module(module).select(fields).beanClass(ReceivableContext.class)
					.andCondition(CriteriaAPI.getIdCondition(receivableIds, module));

			List<ReceivableContext> receivableContexts = builder.get();
			for (ReceivableContext receivables : receivableContexts) {
				double quantity = getTotalReceivedQuantity(receivables.getId(), receiptsmodule, receiptsfieldsMap);
				poIdVsQty.put(receivables.getPoId().getId(), quantity);
				poIds.add(receivables.getPoId().getId());
			}
			for (Map.Entry<Long, Double> entry : poIdVsQty.entrySet()) {
				SelectRecordsBuilder<V3PurchaseOrderContext> poBuilder = new SelectRecordsBuilder<V3PurchaseOrderContext>()
						.module(pomodule).select(pofields).beanClass(V3PurchaseOrderContext.class)
						.andCondition(CriteriaAPI.getIdCondition(entry.getKey(), pomodule));
				List<V3PurchaseOrderContext> purchaseOrderlist = poBuilder.get();
				ReceivableContext receivable = new ReceivableContext();
				if (purchaseOrderlist != null && !purchaseOrderlist.isEmpty()) {
					for (V3PurchaseOrderContext po : purchaseOrderlist) {
						Map<String, Object> map = FieldUtil.getAsProperties(po);
						receivable.setPoId(po);
						po.setQuantityReceived(entry.getValue());
						if (entry.getValue() < po.getTotalQuantity()) {
							po.setStatus(Status.PARTIALLY_RECEIVED.getValue());
							po.setReceivableStatus(V3PurchaseOrderContext.ReceivableStatus.PARTIALLY_RECEIVED.getIndex());
							receivable.setStatus(com.facilio.bmsconsole.context.ReceivableContext.Status.PARTIALLY_RECEIVED);
						} else if (entry.getValue() >= po.getTotalQuantity()) {
							po.setStatus(Status.RECEIVED.getValue());
							receivable.setStatus(com.facilio.bmsconsole.context.ReceivableContext.Status.RECEIVED);
							po.setReceivableStatus(V3PurchaseOrderContext.ReceivableStatus.RECEIVED.getIndex());
							receivedPOs.add(po);
						}
						updatePurchaseOrder(po, pomodule, pofields);
						updateReceivables(receivable, module, fields, fieldsMap);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.PURCHASE_ORDERS, receivedPOs);
		}
		return false;
	}

	public static double getTotalReceivedQuantity(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("received", "sum(case WHEN STATUS = 1 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		field.add(FieldFactory.getField("returns", "sum(case WHEN STATUS = 2 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));

		// SelectRecordsBuilder<ReceiptContext> builder = new
		// SelectRecordsBuilder<ReceiptContext>()
		// .select(field).moduleName(module.getName()).andCondition(CriteriaAPI
		// .getCondition(fieldsMap.get("receivableId"), String.valueOf(id),
		// NumberOperators.EQUALS))
		// .setAggregation();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.andCustomWhere(module.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.select(field).andCondition(CriteriaAPI.getCondition(fieldsMap.get("receivableId"), String.valueOf(id),
						NumberOperators.EQUALS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double received = 0, returns = 0;
			received = rs.get(0).get("received") != null ? (double) rs.get(0).get("received") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			
			return (received - returns);
		}
		return 0d;
	}
	
	private void updatePurchaseOrder(V3PurchaseOrderContext po, FacilioModule pomodule, List<FacilioField> pofields) throws Exception{

		Map<String, Object> map = FieldUtil.getAsProperties(po);
		JSONObject json = new JSONObject();
		json.putAll(map);

		FacilioChain patchChain = ChainUtil.getBulkPatchChain(FacilioConstants.ContextNames.PURCHASE_ORDER);
		FacilioContext patchContext = patchChain.getContext();
		V3Config v3Config = ChainUtil.getV3Config(FacilioConstants.ContextNames.PURCHASE_ORDER);
		Class beanClass = ChainUtil.getBeanClass(v3Config, pomodule);

		Constants.setModuleName(patchContext, FacilioConstants.ContextNames.PURCHASE_ORDER);
		Constants.setBulkRawInput(patchContext, Collections.singletonList(json));
		patchContext.put(Constants.BEAN_CLASS, beanClass);
		patchContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		patchContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
		patchChain.execute();
	}
	
	private void updateReceivables(ReceivableContext receivable, FacilioModule module, List<FacilioField> fields, Map<String, FacilioField> fieldsMap) throws Exception{
		UpdateRecordBuilder<ReceivableContext> updateBuilder = new UpdateRecordBuilder<ReceivableContext>()
				.module(module).fields(fields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(receivable.getPoId().getId()), NumberOperators.EQUALS));
		updateBuilder.update(receivable);
	}

}
