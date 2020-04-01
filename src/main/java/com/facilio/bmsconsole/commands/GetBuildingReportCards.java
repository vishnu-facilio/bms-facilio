package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
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

public class GetBuildingReportCards extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long buildingId = (long) context.get(FacilioConstants.ContextNames.ID);

		if (buildingId > 0) {
			List<String> fetchReportMeta = (List<String>) context.get(FacilioConstants.ContextNames.REPORT_CARDS_META);
			JSONObject reports = new JSONObject();
			JSONArray reportCards = new JSONArray();
			if (fetchReportMeta == null || (CollectionUtils.isNotEmpty(fetchReportMeta) && fetchReportMeta.contains("spaceCount"))) {
				reports.put("spaces", getBuildingsAllSpacesCount(buildingId));
				reports.put("independent_spaces", SpaceAPI.getIndependentSpacesCount(buildingId));
				reports.put("floors", SpaceAPI.getBuildingsFloorsCount(buildingId));
			}

			if (fetchReportMeta == null || (CollectionUtils.isNotEmpty(fetchReportMeta) && fetchReportMeta.contains("moduleCount"))) {
				JSONObject woCount = new JSONObject();
				woCount.put("type", "count");
				woCount.put("name", "work_orders");
				woCount.put("label", "Work Orders");
				woCount.put("data", SpaceAPI.getWorkOrdersCount(buildingId));

				JSONObject faCount = new JSONObject();
				faCount.put("type", "count");
				faCount.put("name", "fire_alarms");
				faCount.put("label", "Alarms");
				faCount.put("data", SpaceAPI.getV2AlarmCount(buildingId));

				JSONObject assetCount = new JSONObject();
				assetCount.put("type", "count");
				assetCount.put("name", "assets");
				assetCount.put("label", "Assets");
				assetCount.put("data", SpaceAPI.getAssetsCount(buildingId));

				reportCards.add(woCount);
				reportCards.add(faCount);
				reportCards.add(assetCount);
			}
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Building ID : "+buildingId);
		}

		return false;
	}

	private long getBuildingsAllSpacesCount(long buildingId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.select(new HashSet<>())
				.module(module)
				.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(BaseSpaceContext.SpaceType.SPACE.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), String.valueOf(buildingId), NumberOperators.EQUALS))
				;

		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (CollectionUtils.isNotEmpty(props)) {
			count = ((Number) props.get(0).get("id")).longValue();
		}
		return count;

	}
}
