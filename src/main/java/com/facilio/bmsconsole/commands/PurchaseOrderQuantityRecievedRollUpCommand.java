package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class PurchaseOrderQuantityRecievedRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
			Map<String, FacilioField> pofieldsMap = FieldFactory.getAsMap(pofields);

			Map<Long, Double> poIdVsQty = new HashMap<>();
			List<Long> poIds = new ArrayList<>();
			List<PurchaseOrderContext> receivedPOs = new ArrayList<>();
			SelectRecordsBuilder<ReceivableContext> builder = new SelectRecordsBuilder<ReceivableContext>()
					.module(module).select(fields).beanClass(ReceivableContext.class)
					.andCondition(CriteriaAPI.getIdCondition(receivableIds, module));

			List<ReceivableContext> receivableContexts = builder.get();
			for (ReceivableContext receivables : receivableContexts) {
				double quantity = getTotalReceivedQuantity(receivables.getId(), receiptsmodule, receiptsfieldsMap);
				poIdVsQty.put(receivables.getPoId(), quantity);
				poIds.add(receivables.getPoId());
			}
			for (Map.Entry<Long, Double> entry : poIdVsQty.entrySet()) {
				SelectRecordsBuilder<PurchaseOrderContext> poBuilder = new SelectRecordsBuilder<PurchaseOrderContext>()
						.module(pomodule).select(pofields).beanClass(PurchaseOrderContext.class)
						.andCondition(CriteriaAPI.getIdCondition(entry.getKey(), pomodule));
				List<PurchaseOrderContext> purchaseOrderlist = poBuilder.get();
				
				if (purchaseOrderlist != null && !purchaseOrderlist.isEmpty()) {
					for (PurchaseOrderContext po : purchaseOrderlist) {
						if (entry.getValue() < po.getTotalQuantity()) {
							po.setStatus(Status.PARTIALLY_RECEIVED);
						} else if (entry.getValue() == po.getTotalQuantity()) {
							po.setStatus(Status.RECEIVED);
							receivedPOs.add(po);
						}
						
						UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
								.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
								.andCondition(CriteriaAPI.getIdCondition(po.getId(), pomodule));
						updateBuilder.update(po);
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

}
