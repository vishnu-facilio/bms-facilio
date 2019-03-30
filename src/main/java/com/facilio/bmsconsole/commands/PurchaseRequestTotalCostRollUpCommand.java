package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurchaseRequestTotalCostRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context
				.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);

			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
			List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
			
			//get total cost from line items
			double totalCost = getTotalCost(purchaseRequestContext.getId());
			
			purchaseRequestContext.setTotalCost(totalCost);
			
			//update total cost for purchase request
			UpdateRecordBuilder<PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<PurchaseRequestContext>()
					.module(module).fields(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(purchaseRequestContext.getId(), module));
			updateBuilder.update(purchaseRequestContext);
		}
		return false;
	}

	private double getTotalCost(long id) throws Exception {
		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
		List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<PurchaseRequestLineItemContext>()
				.select(field).moduleName(lineModule.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("prid"), String.valueOf(id), NumberOperators.EQUALS))
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
