package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
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

public class WarrantyContractTotalCostRollUpCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WarrantyContractContext warrantyContract = (WarrantyContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(warrantyContract!=null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
			double totalCost = getTotalCost(warrantyContract.getId());
			warrantyContract.setTotalCost(totalCost);
			
			UpdateRecordBuilder<WarrantyContractContext> updateBuilder = new UpdateRecordBuilder<WarrantyContractContext>()
					.module(module).fields(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(warrantyContract.getId(), module));
			updateBuilder.update(warrantyContract);
		}
		return false;
	}
	
	private double getTotalCost(long id) throws Exception {
		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
		List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<PurchaseContractLineItemContext> builder = new SelectRecordsBuilder<PurchaseContractLineItemContext>()
				.select(field).moduleName(lineModule.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("warrantyContractId"), String.valueOf(id), NumberOperators.EQUALS))
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
