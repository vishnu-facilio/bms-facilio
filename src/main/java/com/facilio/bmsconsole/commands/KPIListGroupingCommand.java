package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.constants.FacilioConstants.ContextNames;

public class KPIListGroupingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String, Object>> kpis = (List<Map<String, Object>>) context.get(ContextNames.KPI_LIST);
		if (CollectionUtils.isEmpty(kpis)) {
			return false;
		}
		
		String groupBy = (String) context.get("groupBy");
		
		List<Map<String, Object>> records = new ArrayList<>();
		
		long prevId = -1;
		List<Map<String, Object>> data = null;
		
		for(Map<String, Object> kpi: kpis) {
			
			long id;
			String name;
			if (groupBy.equals(ContextNames.KPI)) {
				name = (String) kpi.get("name");
				id = (long) kpi.get("id");
			}
			else {
				name = (String) kpi.get("resourceName");
				id = (long) kpi.get("resourceId");
			}
			
			if (prevId != id) {
				Map<String, Object> row = new HashMap<>();
				row.put("name", name);
				row.put("id", id);
				
				data = new ArrayList<>();
				row.put("data", data);
				records.add(row);
			}
			
			prevId = id;
			data.add(kpi);
		}
		
		context.put(ContextNames.RESULT, records);
		
		return false;
	}

}
