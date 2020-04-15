package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;

public class GetCommissioningDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(ContextNames.ID);
		CommissioningLogContext log = CommissioningApi.commissioniongDetails(id);
		setHeaders(log);
		if (log.getPublishedTime() == -1) {
			setPoints(log);
		}

		JSONArray points = log.getPoints();
		if (points != null) {
			Set<Long> resourceIds = new HashSet<>();
			Set<Long> fieldIds = new HashSet<>(); 
			JSONArray finalPoints  = new JSONArray();
			List<String> headers = log.getHeaders().stream().map(header -> (String)header.get("name")).collect(Collectors.toList());
			Map<Integer, String> unitMap = new HashMap<>();
			for(int i = 0, size = points.size(); i < size; i++) {
				Map<String, Object> point = (Map<String, Object>) points.get(i);
				if (point.get("resourceId") != null) {
					resourceIds.add((long) point.get("resourceId"));
				}
				if (point.get("fieldId") != null) {
					fieldIds.add((long) point.get("fieldId"));
				}
				if (point.get("unit") != null && !unitMap.containsKey(point.get("unit"))) {
					int unitId = Integer.parseInt(point.get("unit").toString());
					if (unitId > 0) {
						Unit unit = Unit.valueOf(unitId);
						unitMap.put(unitId, unit.getSymbol());
					}
					else {
						point.put("unit", null); // since unit is set as 0 by default in points table even if there is no data
					}
				}
				// TODO fetch only those points from db instead of filtering
				// Points fetched from db will have orgId. Filtering unwanted values here
				finalPoints.add(getPointObj(point, headers));
			}
			
			log.setPoints(finalPoints);
			setResources(resourceIds, context);
			setFields(fieldIds, context);
			context.put(ContextNames.UNIT , unitMap);
		}

		context.put(ContextNames.LOG, log);
		return false;
	}

	@SuppressWarnings("unchecked")
	private void setPoints(CommissioningLogContext log) throws Exception {
		FacilioModule module = ModuleFactory.getPointModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
		GetPointRequest getPointRequest = new GetPointRequest()
				.filterConfigurePoints()
				.withControllerIds(log.getControllerIds())
				.ofType(log.getControllerTypeEnum())
//				.initBuilder(null)
				.orderBy(fieldMap.get(AgentConstants.NAME).getCompleteColumnName())
				.limit(-1);
				;
		if (CollectionUtils.isNotEmpty(log.getPoints())) {
			List<Long> ids = (List<Long>) log.getPoints().stream().map(point -> (long) ((Map<String, Object>)point).get("id") )
					.collect(Collectors.toList());
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), ids, NumberOperators.NOT_EQUALS));
			getPointRequest.withCriteria(criteria);
		}
		List<Map<String, Object>> points = getPointRequest.getPointsData();
		if (CollectionUtils.isNotEmpty(points)) {
			JSONArray pointsJson = log.getPoints() != null ? log.getPoints() : new JSONArray();
			pointsJson.addAll(points);
			log.setPoints(pointsJson);
		}
	}
	
	private Map<String, Object> getPointObj(Map<String, Object> point, List<String> headers) {
		if (point.containsKey("orgId")) {
			Map<String, Object> filteredPoint = new HashMap<>();
			for(String header: headers) {
				if (point.containsKey(header)) {
					filteredPoint.put(header, point.get(header));
				}
			}
			filteredPoint.put("id", point.get("id"));
			return filteredPoint;
		}
		
		return point;
	}

	private void setResources(Set<Long> resourceIds, Context context) throws Exception {
		if (!resourceIds.isEmpty()) {
			FacilioChain chain = FacilioChainFactory.getPickListChain();
			Context picklistContext = chain.getContext();
			picklistContext.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.RESOURCE);
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getIdCondition(resourceIds, null));
			picklistContext.put(ContextNames.FILTER_CRITERIA, criteria);
			chain.execute();
			context.put(ContextNames.RESOURCE_LIST, picklistContext.get(ContextNames.PICKLIST));
		}
	}

	private void setFields(Set<Long> fieldIds, Context context) throws Exception {
		if (!fieldIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getFields(fieldIds);
			if (fields != null) {
				Map<Long, Map<String, Object>> fieldMap = new HashMap<>();
				for(FacilioField field: fields) {
					Map<String, Object> fieldDetail = new HashMap<>();
					fieldDetail.put("name", field.getDisplayName());
					if (field instanceof NumberField) {
						fieldDetail.put("metric", ((NumberField) field).getMetric());
					}
					fieldMap.put(field.getId(), fieldDetail);
				}
				context.put(ContextNames.FIELDS, fieldMap);
			}
		}
	}

	private void setHeaders(CommissioningLogContext log) {
		List<Map<String, Object>> headers = new ArrayList<>();

		Map<String, Object> header = new HashMap<>();
		header.put("name", AgentConstants.NAME);
		headers.add(header);

		header = new HashMap<>();
		header.put("name", AgentConstants.DEVICE_NAME);
		headers.add(header);

		List<Map<String, Object>> typeBasedHeaders = getTypeBasedHeaders(log.getControllerTypeEnum());
		headers.addAll(typeBasedHeaders);

		header = new HashMap<>();
		header.put("name", AgentConstants.ASSET_CATEGORY_ID);
		header.put("editable", true);
		headers.add(header);

		header = new HashMap<>();
		header.put("name", AgentConstants.RESOURCE_ID);
		header.put("editable", true);
		headers.add(header);

		header = new HashMap<>();
		header.put("name", AgentConstants.FIELD_ID);
		header.put("editable", true);
		headers.add(header);

		header = new HashMap<>();
		header.put("name", AgentConstants.UNIT);
		header.put("editable", true);
		headers.add(header);

		header = new HashMap<>();
		header.put("name", "enumInputValues");
		headers.add(header);

		log.setHeaders(headers);

	}

	private List<Map<String, Object>> getTypeBasedHeaders(FacilioControllerType type) {
		List<Map<String, Object>> typeHeaders = new ArrayList<>();
		switch(type) {
			case NIAGARA:
				Map<String, Object> header = new HashMap<>();
				header.put("name", AgentConstants.PATH);
				typeHeaders.add(header);
				break;
				
			case BACNET_IP:
			case BACNET_MSTP:
				header = new HashMap<>();
				header.put("name", AgentConstants.INSTANCE_NUMBER);
				typeHeaders.add(header);
				
				header = new HashMap<>();
				header.put("name", AgentConstants.INSTANCE_TYPE);
				typeHeaders.add(header);
				break;
			
		}
		return typeHeaders;
	}

}
