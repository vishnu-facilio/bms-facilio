package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class GetUnmodelledInstancesForControllerCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		Boolean configured = (Boolean) context.get(FacilioConstants.ContextNames.CONFIGURE);
		Boolean fetchMapped = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_MAPPED);
		List<Map<String, Object>> instances = TimeSeriesAPI.getUnmodeledInstancesForController(controllerId, configured, fetchMapped);
		if (fetchMapped != null && fetchMapped && instances != null) {
			Set<Long> assetIds = instances.stream().map(inst -> (Long) inst.get("assetId")).collect(Collectors.toSet());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, assetIds);
		}
		context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instances);
		return false;
	}

}
