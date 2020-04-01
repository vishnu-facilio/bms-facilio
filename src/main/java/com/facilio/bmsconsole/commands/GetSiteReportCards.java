package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GetSiteReportCards extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long campusId = (long) context.get(FacilioConstants.ContextNames.ID);

		if (campusId > 0) {
			List<String> fetchReportMeta = (List<String>) context.get(FacilioConstants.ContextNames.REPORT_CARDS_META);
			JSONObject reports = new JSONObject();
			JSONArray reportCards = new JSONArray();
			if (fetchReportMeta == null || (CollectionUtils.isNotEmpty(fetchReportMeta) && fetchReportMeta.contains("spaceCount"))) {
				reports.put("independent_spaces", SpaceAPI.getIndependentSpacesCount(campusId));
				reports.put("allSpaces", getAllSpaces(campusId, SpaceType.SPACE.getIntVal()));
				reports.put("buildings", getAllSpaces(campusId, SpaceType.BUILDING.getIntVal()));
			}

			if (fetchReportMeta == null || (CollectionUtils.isNotEmpty(fetchReportMeta) && fetchReportMeta.contains("moduleCount"))) {
				JSONObject woCount = new JSONObject();
				woCount.put("type", "count");
				woCount.put("name", "work_orders");
				woCount.put("label", "Work Orders");
				woCount.put("data", SpaceAPI.getWorkOrdersCount(campusId));

				JSONObject faCount = new JSONObject();
				faCount.put("type", "count");
				faCount.put("name", "fire_alarms");
				faCount.put("label", "Alarms");
				faCount.put("data", SpaceAPI.getV2AlarmCount(campusId));

				JSONObject assetCount = new JSONObject();
				assetCount.put("type", "count");
				assetCount.put("name", "assets");
				assetCount.put("label", "Assets");
				assetCount.put("data", SpaceAPI.getAssetsCount(campusId));


				reportCards.add(woCount);
				reportCards.add(faCount);
				reportCards.add(assetCount);
			}


			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		} else {
			throw new IllegalArgumentException("Invalid Campus ID : " + campusId);
		}
		return false;
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
