package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.timeseries.TimeSeriesAPI;

public class GetUnmodelledInstancesForControllerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		Boolean configured = (Boolean) context.get(FacilioConstants.ContextNames.CONFIGURE);
		Boolean fetchMapped = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_MAPPED);
		Boolean isSubscribed = (Boolean) context.get(FacilioConstants.ContextNames.SUBSCRIBE);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		Boolean isCount = (Boolean) context.get(FacilioConstants.ContextNames.COUNT);
		
		boolean fetchDetails = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_AS_MAP, false); // temp variable to get only asset and field names
		if (isCount == null) {
			isCount = false;
		}
		String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		List<Map<String, Object>> instances = TimeSeriesAPI.getPointsInstancesForController(controllerId, configured, fetchMapped, pagination, isSubscribed, isCount, searchText, false);
		if (fetchDetails) {
			Set<Long> resourceIds = new HashSet<>();
			Set<Long> fieldIds = new HashSet<>(); 
			for(Map<String, Object> instance: instances) {
				if (instance.get("resourceId") != null) {
					resourceIds.add((long) instance.get("resourceId"));
				}
				if (instance.get("fieldId") != null) {
					fieldIds.add((long) instance.get("fieldId"));
				}
			}
			if (!resourceIds.isEmpty()) {
				FacilioChain chain = FacilioChainFactory.getPickListChain();
				Context picklistContext = chain.getContext();
				picklistContext.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.RESOURCE);
				chain.execute();
				context.put(ContextNames.RESOURCE_LIST, picklistContext.get(ContextNames.PICKLIST));
			}
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
						if (field instanceof EnumField) {
//							fieldDetail.put("enumMap", ((EnumField) field).getEnumMap());
							fieldDetail.put("values", ((EnumField) field).getVisibleOptions());
						}
						fieldMap.put(field.getId(), fieldDetail);
					}
					context.put(ContextNames.FIELDS, fieldMap);
				}
			}
		}
		else if (fetchMapped != null && fetchMapped && instances != null && !isCount) {
			Set<Long> assetIds = instances.stream().map(inst -> (Long) inst.get("resourceId")).collect(Collectors.toSet());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, assetIds);
		}
			
		context.put(FacilioConstants.ContextNames.PAGINATION, null);
		context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instances);
		return false;
	}
}
