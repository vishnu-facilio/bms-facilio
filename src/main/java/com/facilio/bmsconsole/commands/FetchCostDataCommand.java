package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CostAssetsContext;
import com.facilio.bmsconsole.context.CostContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.time.DateRange;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchCostDataCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(FetchCostDataCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		CostContext cost = (CostContext) context.get(FacilioConstants.ContextNames.COST);
		CostAssetsContext asset = (CostAssetsContext) context.get(FacilioConstants.ContextNames.COST_ASSET);
		long firstBillTime = (long) context.get(FacilioConstants.ContextNames.COST_FIRST_BILL_TIME);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		LOGGER.debug("Firstbill Time : "+firstBillTime);
		LOGGER.debug("Range : "+range);
		
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
		
		LOGGER.debug("Cost data size : "+readings.size());
		
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		return false;
	}

}
