package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.operators.CommonOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetSiteReportCards extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long campusId = (long) context.get(FacilioConstants.ContextNames.ID);

		if(campusId > 0) {
			
			JSONObject reports = new JSONObject();
				reports.put("independent_spaces", getIndependentSpaces(campusId));
			reports.put("allSpaces", getAllSpaces(campusId, SpaceType.SPACE.getIntVal()));
			reports.put("buildings", getAllSpaces(campusId, SpaceType.BUILDING.getIntVal()));
			
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			woCount.put("data", SpaceAPI.getWorkOrdersCount(campusId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			faCount.put("data", SpaceAPI.getFireAlarmsCount(campusId));
			
			JSONObject assetCount = new JSONObject();
			assetCount.put("type", "count");
			assetCount.put("name", "assets");
			assetCount.put("label", "Assets");
			assetCount.put("data", SpaceAPI.getAssetsCount(campusId));
			
			JSONObject energyUsage= new JSONObject();
			energyUsage.put("type", "count");
			energyUsage.put("name", "energy");
			energyUsage.put("label", "ENERGY CONSUMED");
			energyUsage.put("data", "--");
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(assetCount);
			reportCards.add(energyUsage);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Campus ID : "+campusId);
		}

		return false;
	}

	private long getIndependentSpaces (long siteId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.select(new HashSet<>())
				.module(module)
				.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(SpaceType.SPACE.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space1"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space2"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space3"), CommonOperators.IS_EMPTY))
				;

		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (CollectionUtils.isNotEmpty(props)) {
			count = ((Number) props.get(0).get("id")).longValue();
		}
		return count;
	}
	
	private long getAllSpaces (long siteId, long spaceType) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.select(new HashSet<>())
				.module(module)
				.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(spaceType), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (CollectionUtils.isNotEmpty(props)) {
			count = ((Number) props.get(0).get("id")).longValue();
		}
		return count;
		
	}
}
