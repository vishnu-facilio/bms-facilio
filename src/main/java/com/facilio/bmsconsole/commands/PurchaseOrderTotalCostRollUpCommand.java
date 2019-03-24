package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
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
			
			purchaseOrder.setTotalCost(totalCost);
			
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

}