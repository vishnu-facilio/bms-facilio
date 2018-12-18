package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;

public class FetchResourcesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportData != null && !reportData.isEmpty()) {
			List<Map<String, Object>> csvData = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			Set<String> resourceAliases = (Set<String>) report.getFromReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES);
			
			if (resourceAliases != null && !resourceAliases.isEmpty()) {
				List<Long> resourceIds = new ArrayList<>();
				
				for (Map<String, Object> data : csvData) {
					for (String alias : resourceAliases) {
						Long id = (Long) data.get(alias);
						if (id != null) {
							resourceIds.add(id);
						}
					}
				}
				
				if (!resourceIds.isEmpty()) {
					Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
					for (Map<String, Object> data : csvData) {
						for (String alias : resourceAliases) {
							Long id = (Long) data.get(alias);
							if (id != null) {
								data.put(alias, resourceMap.get(id).getName());
							}
						}
					}
				}
			}
		}
		return false;
	}

}
