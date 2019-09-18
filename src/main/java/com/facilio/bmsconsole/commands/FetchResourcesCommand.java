package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;

public class FetchResourcesCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportData != null && !reportData.isEmpty()) {
			Collection<Map<String, Object>> csvData = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			JSONArray resourceAliases = (JSONArray) report.getFromReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES);
			
			if (resourceAliases != null && !resourceAliases.isEmpty()) {
				List<Long> resourceIds = new ArrayList<>();
				
				for (Map<String, Object> data : csvData) {
					resourceAliases.forEach(alias -> {
						Long id = (Long) data.get(alias);
						if (id != null) {
							resourceIds.add(id);
						}
					});
				}
				
				if (!resourceIds.isEmpty()) {
					Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
					for (Map<String, Object> data : csvData) {
						resourceAliases.forEach(alias -> {
							Long id = (Long) data.get(alias);
							if (id != null) {
								data.put((String) alias, resourceMap.get(id).getName());
							}
						});
					}
				}
			}
		}
		return false;
	}

}
