package com.facilio.bmsconsole.commands.anomaly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

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
				
				count.put(month, metric.get("alarm"));
				mttc.put(month, metric.get("duration"));
			}
		}
		if (!rows.isEmpty()) {
			List<Map<String, Object>> projections = (List<Map<String, Object>>) context.get("projections");
			if (CollectionUtils.isNotEmpty(projections)) {
				for(Map<String, Object> projection: projections) {
					String month = getMonth((long) projection.get("ttime"));
					if (projection.get("deviation") != null) {
						deviation.put(month, projection.get("deviation"));
					}
					if (projection.get("wastage") != null) {
						wastage.put(month, projection.get("wastage"));
					}
				}
			}
			
			JSONObject obj = (JSONObject) context.get("energyCdd");
			List<Map<String, Object>> energyReadings =  (List<Map<String, Object>>) obj.get("energy");
			if (CollectionUtils.isNotEmpty(energyReadings)) {
				for(Map<String, Object> energy: energyReadings) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("energy", energy.get("totalEnergyConsumptionDelta"));
					
					String month = getMonth((long) energy.get("ttime"));
					energyByCdd.put(month, jsonObj);
				}
			}
			List<Map<String, Object>> cddReadings =  (List<Map<String, Object>>) obj.get("cdd");
			if (CollectionUtils.isNotEmpty(cddReadings)) {
				for(Map<String, Object> cddReading: cddReadings) {
					String month = getMonth((long) cddReading.get("ttime"));
					if (energyByCdd.get(month) != null) {
						JSONObject jsonObj = (JSONObject) energyByCdd.get(month);
						Double energy = (Double) jsonObj.get("energy");
						Double cdd = (Double) cddReading.get("cdd");
						if (cdd > 0) {
							jsonObj.put("value", FacilioUtil.decimalClientFormat(energy/cdd));
						}
					}
				}
			}
		}
		
		context.put(ContextNames.RESULT, result);
		return false;
	}
	
	private static String getMonth(long time) {
		return DateTimeUtil.getFormattedTime(time, "MMMM");
	}

}
