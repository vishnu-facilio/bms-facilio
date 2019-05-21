package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurchaseOrderTotalCostRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context
				.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseOrder != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
			
			//get total cost from line items
			double totalCost = getTotalCost(purchaseOrder.getId());
			double totalQuantity = getTotalQuantity(purchaseOrder.getId());
			purchaseOrder.setTotalCost(totalCost);
			purchaseOrder.setTotalQuantity(totalQuantity);
			//update total cost for purchase order
			UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
					.module(module).fields(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(purchaseOrder.getId(), module));
			updateBuilder.update(purchaseOrder);
		}
		return false;
	}

	private double getTotalCost(long id) throws Exception {
		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.select(field).moduleName(lineModule.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation()
				;

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalItemsCost") != null) {
				return (double) rs.get(0).get("totalItemsCost");
			}
			return 0d;
		}
		return 0d;
	}
	
	private double getTotalQuantity(long id) throws Exception {
		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItems", "sum(QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.select(field).moduleName(lineModule.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation()
				;

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalItems") != null) {
				return (double) rs.get(0).get("totalItems");
			}
			return 0d;
		}
		return 0d;
	}

}