package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CostAssetsContext;
import com.facilio.bmsconsole.context.CostContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchCostDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		CostContext cost = (CostContext) context.get(FacilioConstants.ContextNames.COST);
		CostAssetsContext asset = (CostAssetsContext) context.get(FacilioConstants.ContextNames.COST_ASSET);
		long firstBillTime = (long) context.get(FacilioConstants.ContextNames.COST_FIRST_BILL_TIME);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField utilityField = cost.getUtilityEnum().getReadingField();
		List<FacilioField> fields = modBean.getAllFields(utilityField.getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentId = fieldMap.get("parentId");
		FacilioField ttime = fieldMap.get("ttime");
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(utilityField);
		selectFields.add(ttime);
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																	.select(selectFields)
																	.module(utilityField.getModule())
																	.beanClass(ReadingContext.class)
																	.andCondition(CriteriaAPI.getCondition(parentId, String.valueOf(asset.getAssetId()), PickListOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(ttime, firstBillTime+", "+range.getEndTime(), DateOperators.BETWEEN))
																	.orderBy("ttime")
																	;
		
		List<ReadingContext> readings = selectBuilder.get();
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		return false;
	}

}
