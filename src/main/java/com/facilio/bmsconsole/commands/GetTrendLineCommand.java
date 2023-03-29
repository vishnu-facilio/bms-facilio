package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class GetTrendLineCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetTrendLineCommand.class.getName());
	private JSONArray dataPointAlias = new JSONArray();
	private String xAxis = "X";
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long orgId = AccountUtil.getCurrentOrg().getOrgId();
			String pythonAI = FacilioProperties.getPythonAI();
			String trendLineAIUrl = pythonAI+"/trendline";
			
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			
			ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
			JSONObject trendLineObj = new JSONObject();
			
			String trendLine = (String) context.get("trendLine");
			if(trendLine != null) {
				trendLineObj = (JSONObject)new JSONParser().parse(trendLine);
			}else{
				String chartState = report.getChartState();
				if(chartState != null){
					JSONObject chartObj = (JSONObject)new JSONParser().parse(chartState);
					trendLineObj = (JSONObject)chartObj.get("trendLine");
				}
			}
			
			if(trendLineObj != null && !trendLineObj.isEmpty() && trendLineObj.containsKey("enable") && (boolean) trendLineObj.get("enable")){
				
				List<ReportDataPointContext> trendLineDataPoints = getDataPoints(report.getDataPoints(), ((JSONArray)trendLineObj.get("selectedPoints")));
				
				JSONObject reportData = (JSONObject)context.get(FacilioConstants.ContextNames.REPORT_DATA);
				if(!((ArrayList<Object>)reportData.get("data")).isEmpty()){
					JSONObject body = new JSONObject();
					body.put("trendLineObj", trendLineObj);
					body.put("reportData", reportData);
					body.put("xaxis", xAxis);
					body.put("yaxis", dataPointAlias);
					body.put("orgId", orgId.toString());
					LOGGER.info("trendline request raised");
					String response = AwsUtil.doHttpPost(trendLineAIUrl, headers, null, body.toJSONString());
					LOGGER.info("trendline response----->"+response);
					if(!response.isEmpty()){
						JSONObject responseObj = (JSONObject) new JSONParser().parse(response);
						if(responseObj != null && !responseObj.isEmpty()){
							JSONObject resultObj = (JSONObject) responseObj.get("result");
							if(resultObj != null && !resultObj.isEmpty()){
								context.put(FacilioConstants.ContextNames.REPORT_DATA, (JSONObject) resultObj.get("reportData"));
								report.setTrendLineDataPoints(trendLineDataPoints);
							}
						}
					}
				}
		}
			if(orgId == 6l) {
				LOGGER.info("GetTrendLineCommand is" + context);
			}
		return false;
	}
	
	private List<ReportDataPointContext> getDataPoints(List<ReportDataPointContext> dataPoints, JSONArray selectedPoints){
		List<ReportDataPointContext> trendLinePoints = new ArrayList<>();
		for(ReportDataPointContext dataPoint : dataPoints){
			String alias = dataPoint.getAliases().get("actual");
			if(dataPoint.isxDataPoint()){
				xAxis = alias;
			}
			if((selectedPoints.isEmpty() && !dataPoint.isxDataPoint()) || selectedPoints.contains(alias)){
				ReportDataPointContext trendLinePoint = new ReportDataPointContext();
				
				this.dataPointAlias.add(alias);
				alias+="_TrendLine";
				Map<String, String> aliases = new HashMap<>();
				aliases.put("actual", alias);
				
				trendLinePoint.setAliases(aliases);
				trendLinePoint.setName(alias);
				trendLinePoint.setType(ReportDataPointContext.DataPointType.TRENDLINE);
				trendLinePoint.setxAxis(dataPoint.getxAxis());
				trendLinePoint.setyAxis(dataPoint.getyAxis());
				trendLinePoint.setCriteria(dataPoint.getCriteria());
				trendLinePoint.setGroupByFields(dataPoint.getGroupByFields());
				trendLinePoint.setDateField(dataPoint.getDateField());
				trendLinePoint.setMetaData(dataPoint.getMetaData());
				trendLinePoints.add(trendLinePoint);
			}
		}
		return trendLinePoints;
	}
}