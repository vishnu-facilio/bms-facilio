package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchAssetDowntimeMetricsCommand implements Command {

	FacilioModule module;
	long assetId;
	
	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		assetId = (long) context.get(ContextNames.ASSET_ID);
		module = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
		Map<String, Object> metrics = new HashMap<>();
		
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BREAKDOWN);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<AssetBreakdownContext> builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_MONTH)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("timeBetweenFailure"), CommonOperators.IS_NOT_EMPTY))
				.aggregate(NumberAggregateOperator.AVERAGE, fieldMap.get("timeBetweenFailure"))
				;
		
		List<Map<String, Object>> props = builder.getAsProps();
		metrics.put("mtbf", CollectionUtils.isNotEmpty(props) ? props.get(0).get("timeBetweenFailure") : 0);
		
		builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_YEAR_UPTO_LAST_MONTH)
				.aggregate(NumberAggregateOperator.AVERAGE, fieldMap.get("timeBetweenFailure"))
				;
		
		props = builder.getAsProps();
		metrics.put("mtbfTillLastMonth", CollectionUtils.isNotEmpty(props) ? props.get(0).get("timeBetweenFailure") : 0);
		
		builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_MONTH)
				.aggregate(NumberAggregateOperator.AVERAGE, fieldMap.get("duration"))
				;
		
		props = builder.getAsProps();
		metrics.put("mttr", CollectionUtils.isNotEmpty(props) ? props.get(0).get("duration") : 0);
		
		builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_YEAR_UPTO_LAST_MONTH)
				.aggregate(NumberAggregateOperator.AVERAGE, fieldMap.get("duration"))
				;
		
		props = builder.getAsProps();
		metrics.put("mttrTillLastMonth", CollectionUtils.isNotEmpty(props) ? props.get(0).get("duration") : 0);
		
		
		
		builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_MONTH)
				.aggregate(NumberAggregateOperator.SUM, fieldMap.get("duration"))
				;
		
		props = builder.getAsProps();
		metrics.put("downtime", CollectionUtils.isNotEmpty(props) ? props.get(0).get("duration") : 0);
		
		builder = getAssetBreakdownBuilder(fieldMap, DateOperators.CURRENT_YEAR_UPTO_LAST_MONTH)
				.aggregate(NumberAggregateOperator.SUM, fieldMap.get("duration"))
				;
		
		props = builder.getAsProps();
		metrics.put("downtimeTillLastMonth", CollectionUtils.isNotEmpty(props) ? props.get(0).get("duration") : 0);
		
		context.put(ContextNames.RESULT, metrics);
		
		return false;
	}
	
	private SelectRecordsBuilder<AssetBreakdownContext> getAssetBreakdownBuilder(Map<String, FacilioField> fieldMap, DateOperators operator) {
		SelectRecordsBuilder<AssetBreakdownContext> builder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fromtime"), operator))
				;
		return builder;
	}

}
