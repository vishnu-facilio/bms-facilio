package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class FormatForTimeDuration extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(FormatForTimeDuration.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		ReportMode mode =  (ReportMode) context.get(FacilioConstants.ContextNames.REPORT_MODE);
		if(mode == null){
			String chartState = report.getChartState();
			if(chartState != null){
				JSONObject chartObj = (JSONObject)new JSONParser().parse(chartState);
				String modeValue = ((JSONObject)chartObj.get("common")).get("mode").toString();
				mode = ReportMode.valueOf(Integer.valueOf(modeValue));
			}
		}
		if(mode!=null && mode.equals(ReportMode.TIME_DURATION)){
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			
			if ((!((ArrayList<Object>)reportData.get("data")).isEmpty())) {
				Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
				
				ArrayList<Map> enumData = new ArrayList<>();

				for (ReportDataPointContext dp : report.getDataPoints()) {
					switch (dp.getyAxis().getDataTypeEnum()) {
						case BOOLEAN:
						case ENUM:
								setEnumData(dp.getAliases().get("actual"), reportAggrData, enumData);
							break;
						default:
							break;
					}
				}
				reportData.put(FacilioConstants.ContextNames.DATA_KEY, enumData);
			}
		}
		long orgId = AccountUtil.getCurrentOrg().getId();
		if(orgId == 6l) {
			LOGGER.info("FormatForTimeDuration is" + context);
		}
		return false;
	}
	
	private void setEnumData(String alias, Map<String, Object> reportAggrData, ArrayList<Map> enumData){
		Map<String, Object> enumObj = (Map<String, Object>)reportAggrData.get(alias+".duration");
		for (int i = 0; i < enumObj.size(); i++) {
			Map<String, Object> data = new HashMap<>();
			data.put("X", i);
			data.put(alias, enumObj.get(i));
			enumData.add(data);
		}
	}
}
