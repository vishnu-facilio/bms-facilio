package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportGroupByField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class HandleGroupByDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		
		List<Map<String, Object>> dataFormatted = new ArrayList<>();
		
		List<ReportDataPointContext> dataPoints = report.getDataPoints();
		
		String xAlias = getxAlias(report);
		
		boolean shouldIterate = false;
		for (ReportDataPointContext dataPointContext : dataPoints) {
			Collection<Map<String, Object>> dataList = (Collection<Map<String, Object>>) data.get(FacilioConstants.ContextNames.DATA_KEY);
			if (dataList != null) {
				for (Map<String, Object> map : dataList) {
					Map<String, Object> object = null;
					for (Map<String, Object> d : dataFormatted) {
						if (d.get(xAlias).equals(map.get(xAlias))) {
							object = d;
							break;
						}
					}
					if (object == null) {
						object = new HashMap<>();
						object.put(xAlias, map.get(xAlias));
						dataFormatted.add(object);
					}
					
					Map<String, Object> lastMap = object;
					if (dataPointContext.getGroupByFields() != null) {
						for (int i =0 ; i < dataPointContext.getGroupByFields().size(); i++) {
							ReportGroupByField groupByField = dataPointContext.getGroupByFields().get(i);
							List<Map<String, Object>> groupByList = (List<Map<String, Object>>) lastMap.get(groupByField.getAlias());
							if (groupByList == null) {
								groupByList = new ArrayList<>();
								lastMap.put(groupByField.getAlias(), groupByList);
							}
							Map<String, Object> m = null;
							if (shouldIterate) {
								for (Map<String, Object> it : groupByList) {
									if (it.get(groupByField.getAlias()).equals(map.get(groupByField.getAlias()))) {
										m = it;
										break;
									}
								}
							}
							if (m == null) {
								m = new HashMap<>();
								groupByList.add(m);
							}
							m.put(groupByField.getAlias(), map.get(groupByField.getAlias()));
							
							lastMap = m;
							
							groupByField.getAlias();
						}
					}
					for (String key: dataPointContext.getAliases().values()) {
						lastMap.put(key, map.get(key));
					}
				}
			}
			shouldIterate = true;
		}
		data.put("data", dataFormatted);
//		context.put(FacilioConstants.ContextNames.REPORT_DATA, dataFormated);
		
		return false;
	}
	
	private String getxAlias(ReportContext report) {
		return report.getxAlias() == null ? FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS : report.getxAlias();
	}

}
