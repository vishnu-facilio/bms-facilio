package com.facilio.bmsconsole.commands.anomaly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateTimeUtil;

public class FormatAnomalyMetricsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject result = new JSONObject();

		JSONObject count = new JSONObject();
		result.put("noofanomalies", count);
		
		JSONObject mttc = new JSONObject();
		result.put("mttc", mttc);
		
		JSONObject deviation = new JSONObject();
		result.put("deviation", deviation);
		
		JSONObject wastage = new JSONObject();
		result.put("wastage", wastage);
		
		JSONObject energyByCdd = new JSONObject();
		result.put("energyByCdd", energyByCdd);
		
		List<Map<String, Object>> metrics = (List<Map<String, Object>>) context.get("metrics");
		
		List<String> rows = new ArrayList<>();
		result.put("time", rows);
		
		if (CollectionUtils.isNotEmpty(metrics)) {
			for(Map<String, Object> metric: metrics) {
				String month = getMonth((long) metric.get("createdTime"));
				rows.add(month);
				
				count.put(month, metric.get("id"));
				mttc.put(month, metric.get("duration"));
			}
		}
		if (!rows.isEmpty()) {
			List<ReadingContext> projections = (List<ReadingContext>) context.get("projections");
			if (CollectionUtils.isNotEmpty(projections)) {
				for(ReadingContext projection: projections) {
					String month = getMonth((long) projection.getTtime());
					Map<String, Object> data = projection.getData();
					if (data.containsKey("deviation")) {
						deviation.put(month, data.get("deviation"));
					}
					if (data.containsKey("wastage")) {
						deviation.put(month, data.get("wastage"));
					}
				}
			}
		}
		
		
		/*JSONObject energyCdd = (JSONObject) context.get("energyCdd");
		if (energyCdd != null) {
			List<ReadingContext> energyReadings = (List<ReadingContext>) energyCdd.get("energy");
			if (CollectionUtils.isNotEmpty(energyReadings)) {
				for(ReadingContext energy: energyReadings) {
					String month = DateTimeUtil.getFormattedTime((long) energy.getTtime(), "M");
					Map<String, Object> data = energy.getData();
					if (data.containsKey("energy")) {
						energy.put(month, data.get("energy"));
					}
					if (data.containsKey("wastage")) {
						deviation.put(month, data.get("wastage"));
					}
				}
			}
			String month = DateTimeUtil.getFormattedTime((long) energy.getTtime(), "M");
			Map<String, Object> data = projection.getData();
			if (data.containsKey("deviation")) {
				deviation.put(month, data.get("deviation"));
			}
			if (data.containsKey("wastage")) {
				deviation.put(month, data.get("wastage"));
			}
		} */
		
		context.put(ContextNames.RESULT, result);
		return false;
	}
	
	private static String getMonth(long time) {
		return DateTimeUtil.getFormattedTime(time, "MMMM");
	}

}
