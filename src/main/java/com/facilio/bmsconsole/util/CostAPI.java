package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.AdditionalCostContext;
import com.facilio.bmsconsole.context.CostAssetsContext;
import com.facilio.bmsconsole.context.CostContext;
import com.facilio.bmsconsole.context.CostSlabContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostAPI {
	public static List<CostContext> getAllCosts() throws Exception {
		FacilioModule module = ModuleFactory.getCostsModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getCostFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		return getCostsFromProps(selectBuilder.get());
	}
	
	private static List<CostContext> getCostsFromProps (List<Map<String, Object>> props) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<CostContext> costs = new ArrayList<>();
			List<Long> costIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				CostContext cost = FieldUtil.getAsBeanFromMap(prop, CostContext.class);
				costs.add(cost);
				costIds.add(cost.getId());
			}
			if (!costs.isEmpty()) {
				Map<Long, List<CostSlabContext>> slabs = getCostChildren(CostSlabContext.class, ModuleFactory.getCostSlabsModule(), FieldFactory.getCostSlabFields(), costIds, "COST_ID, MAX_UNIT, START_RANGE");
				Map<Long, List<AdditionalCostContext>> additionalCosts = getCostChildren(AdditionalCostContext.class, ModuleFactory.getAdditionalCostModule(), FieldFactory.getAdditionalCostFields(), costIds, null);
				Map<Long, List<CostAssetsContext>> assets = getCostChildren(CostAssetsContext.class, ModuleFactory.getCostAssetsModule(), FieldFactory.getCostAssetsFields(), costIds, null);
				
				for (CostContext cost : costs) {
					cost.setSlabs(slabs.get(cost.getId()));
					cost.setAdditionalCosts(additionalCosts.get(cost.getId()));
					cost.setAssets(assets.get(cost.getId()));
				}
			}
			return costs;
		}
		return null;
	}
	
	private static <T> Map<Long, List<T>> getCostChildren( Class<T> classObj, FacilioModule module, List<FacilioField> fields, List<Long> costIds, String orderBy) throws Exception {
		FacilioField costField = FieldFactory.getAsMap(fields).get("costId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(costField, costIds, PickListOperators.IS))
														;
		if (orderBy != null) {
			selectBuilder.orderBy(orderBy);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, List<T>> costWiseChildren = new HashMap<>();
			for (Map<String, Object> prop : props) {
				long costId = (long) prop.get("costId");
				List<T> children = costWiseChildren.get(costId);
				if (children == null) {
					children = new ArrayList<>();
					costWiseChildren.put(costId, children);
				}
				children.add(FieldUtil.getAsBeanFromMap(prop, classObj));
			}
			return costWiseChildren;
		}
		return null;
	}
	
	private static void addDefaultCostReadingFields(List<FacilioField> fields) {
		fields.add(FieldFactory.getField("totalCost", "Total Cost", "TOTAL_COST", null, FieldType.DECIMAL));
		fields.add(FieldFactory.getField("slabCost", "Slab Cost", "SLAB_COST", null, FieldType.NUMBER));
	}
	
	public static ZonedDateTime getBillStartTime(CostAssetsContext asset, ZonedDateTime zdt) {
		long firstBillStartTime = asset.getFirstBillTime();
		ZonedDateTime firstBillZdt = DateTimeUtil.getDateTime(firstBillStartTime);
		
		Period diff = Period.between(firstBillZdt.toLocalDate(), zdt.toLocalDate());
		int months = diff.getMonths();
		zdt = zdt.minusMonths(months % asset.getNoOfBillMonths());
		
		if (zdt.getDayOfMonth() < asset.getBillStartDay()) {
			zdt = zdt.minusMonths(1);
		}
		zdt = zdt.withDayOfMonth(asset.getBillStartDay());
	
		return DateTimeUtil.getDayStartZDT(zdt);
	}
}
