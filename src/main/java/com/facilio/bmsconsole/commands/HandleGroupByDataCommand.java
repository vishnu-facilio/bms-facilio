package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportGroupByField;
import com.mysql.fabric.xmlrpc.base.Array;

public class HandleGroupByDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		
		List<Map<String, Object>> dataFormatted = new ArrayList<>();
		
		List<ReportDataPointContext> dataPoints = report.getDataPoints();
		
//		ReportDataPointContext dataPointContext = dataPoints.get(0);
		boolean shouldIterate = false;
		for (ReportDataPointContext dataPointContext : dataPoints) {
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) data.get("data");
			if (dataList != null) {
				for (Map<String, Object> map : dataList) {
					Map<String, Object> object = null;
					for (Map<String, Object> d : dataFormatted) {
						if (d.get(report.getxAlias()).equals(map.get(report.getxAlias()))) {
							object = d;
							break;
						}
					}
					if (object == null) {
						object = new HashMap<>();
						object.put(report.getxAlias(), map.get(report.getxAlias()));
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
					lastMap.put(dataPointContext.getAliases().get("actual"), map.get(dataPointContext.getAliases().get("actual")));
				}
			}
			shouldIterate = true;
		}
		data.put("data", dataFormatted);
//		context.put(FacilioConstants.ContextNames.REPORT_DATA, dataFormated);
		
		return false;
	}

}
