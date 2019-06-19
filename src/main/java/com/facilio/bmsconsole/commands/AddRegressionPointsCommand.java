package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.RegressionContext;
import com.facilio.bmsconsole.context.RegressionPointContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;

public class AddRegressionPointsCommand implements Command{

	private static final Logger LOGGER = LogManager.getLogger(AddRegressionPointsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<RegressionContext> regressionConfig = (List<RegressionContext>) context.get(FacilioConstants.ContextNames.REGRESSION_CONFIG);
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		boolean isMultiple = true;
		
		Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
		
		Map<String, Map<String, Object>> regressionResults = new HashMap<String, Map<String,Object>>();
		if(regressionConfig != null && data!= null && !regressionConfig.isEmpty() && !data.isEmpty()) {
			data = cleanData(data);
			
			for(RegressionContext rc : regressionConfig) {
				Map<String, Object> regressionResult = prepareData(rc, new ArrayList(data), isMultiple);
				String groupAlias = getRegressionPointAlias(rc);
				String expressionString = getExpressionString(regressionResult, reportContext, rc.getxAxisContext());
				ReportDataPointContext regressionPoint = getRegressionDataPoint(groupAlias, expressionString);
				reportContext.getDataPoints().add(regressionPoint);
				Map<String, Object> coefficientMap = getCoefficientMap(regressionResult, rc.getxAxisContext());
				computeRegressionData(groupAlias, (ArrayList)data, coefficientMap);
				regressionResults.put(groupAlias, regressionResult);
				
			}
			
			reportData.put(FacilioConstants.ContextNames.REGRESSION_RESULT, regressionResults);
			
		}
		else {
			throw new Exception("Error in regressionConfiguration.");
		}
		
		
		return true;
	}
	
	private void computeRegressionData(String groupAlias, ArrayList<Map<String,Object>> data, Map<String,Object> coefficientMap) {
		
		ArrayList<String> coefficientKeys = new ArrayList(coefficientMap.keySet());
		for(Map<String, Object> record: data) {
			double value = 0.0;
			for(String key: coefficientKeys) {
				if(key != "constant") {
					value = value + (Double)coefficientMap.get(key) * Double.valueOf((String)record.get(key));
				}
			}
			value = value + (Double)coefficientMap.get("constant");
			record.put(groupAlias, value);
		}
	}
	
	private Map<String, Object> getCoefficientMap(Map<String, Object> regressionResult, List<RegressionPointContext> xPoints){
		Map<String, Object> cMap = new LinkedHashMap<String, Object>();
		double [] coefficients = (double [])regressionResult.get("coefficients");
		
		if(coefficients.length != 0) {
			for(RegressionPointContext xPoint: xPoints) {
				int index = xPoints.indexOf(xPoint) + 1;
				cMap.put(xPoint.getAlias(), coefficients[index]);
			}
			cMap.put("constant", coefficients[0]);
		}
		
		return cMap;
	}
	private ReportDataPointContext getRegressionDataPoint(String alias, String expressionString) {
		ReportDataPointContext rd = new ReportDataPointContext();
		Map<String, String> aliases = new HashMap<String, String>();
		aliases.put("actual", alias);
		rd.setAliases(aliases);
		rd.setName("Regression Model" + alias);
		FacilioField xField = FieldFactory.getField("ttime", "TimeStamp", "TTIME", null, FieldType.DATE_TIME);
		ReportFieldContext xFieldContext = new ReportFieldContext();
		xFieldContext.setField(xField.getModule(), xField);
		rd.setxAxis(xFieldContext);
		ReportYAxisContext yAxisContext = new ReportYAxisContext();
		yAxisContext.setDataType(3);
		yAxisContext.setLabel("RegressionModel" + alias);
		yAxisContext.setUnitStr("");
		rd.setyAxis(yAxisContext);
		rd.setType(ReportDataPointContext.DataPointType.DERIVATION);
		rd.setExpressionString(expressionString);
		
		return rd;
	}
	
	private String getExpressionString(Map<String, Object> regressionResult, ReportContext reportContext, List<RegressionPointContext> independentPoints) {
		StringBuilder expressionString = new StringBuilder();
		expressionString.append("y = ");
		if(independentPoints.size() == 1) {
			
			// single regression
			double [] coefficients = (double [])regressionResult.get("coefficients");
			if(coefficients != null && coefficients.length != 0) {
				expressionString.append(coefficients[1] + 'x');
				if(coefficients[0] < 0) {
					expressionString.append(coefficients[0]);
				}
				else {
					expressionString.append(" + " + coefficients[0]);
				}
			}
		}
		else {
			double [] coefficients = (double [])regressionResult.get("coefficients");
			
			for(RegressionPointContext xPoint: independentPoints) {
				//String name = getDataPointName(reportContext.getDataPoints(), xPoint);
				String name = xPoint.getAlias();
				if(name != null) {
					double c = coefficients[independentPoints.indexOf(xPoint) + 1];
					if(independentPoints.indexOf(xPoint) != 0) {
						if(c > 0) {
							expressionString.append(" + " + c + name);
						}
						else {
							expressionString.append(c + name);
						}
					}
					else {
						expressionString.append(c);
						expressionString.append(name);
					}
				}
				
			}
			if(coefficients[0] > 1) {
				expressionString.append(" + " + coefficients[0]);
			}
			else {
				expressionString.append(coefficients[0]);
			}
		}
		return expressionString.toString();
	}
	
	
	private String getDataPointName(List<ReportDataPointContext> dataPoints, RegressionPointContext xPoint) {
		for(ReportDataPointContext dataPoint: dataPoints) {
			ArrayList<Long> temp = (ArrayList) dataPoint.getMetaData().get("parentIds");
			if(temp.size() != 0) {
				Long parentId = temp.get(0);
				if(dataPoint.getyAxis().getFieldId() == xPoint.getReadingId() && parentId == xPoint.getParentId()) {
					return dataPoint.getName();
				}
				else {
					continue;
				}
			}
		}
		return null;
	}
	
	
	private Collection<Map<String, Object>> cleanData(Collection<Map<String, Object>> data) {
		Collection<Map<String, Object>> finalData = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> entry: data) {
			boolean isMarked = false;
			for(String key: entry.keySet()) {
				if(entry.get(key) == null || entry.get(key) == "") {
					isMarked = true;
					break;
				}
			}
			if(!isMarked) {
				finalData.add(entry);
			}
		}
		
		return finalData;
	}
	
	private Map<String, Object> prepareData(RegressionContext regressionContext, ArrayList<Map<String, Object>> data, boolean isMultiple){
		
		
		OLSMultipleLinearRegression olsInstance = new OLSMultipleLinearRegression();
		
		List<RegressionPointContext> xPoints = regressionContext.getxAxisContext();
		RegressionPointContext yPoint = regressionContext.getyAxisContext();
		
		double[][] xData = new double[data.size()][xPoints.size()];
		double[] yData = new double[data.size()];
		
		
		for(int i = 0;i < data.size();i ++) {
			Map<String, Object> entry = data.get(i);
			yData[i] = Double.valueOf((String)entry.get(yPoint.getAlias()));
			
			if(isMultiple) {		
				for(int j=0; j < regressionContext.getxAxisContext().size(); j++) {
					RegressionPointContext xRecord = regressionContext.getxAxisContext().get(j);
					xData[i][j] = Double.valueOf((String)entry.get(xRecord.getAlias()));
					
				}
			}
			else {
				// single regression code
				
				RegressionPointContext xRecord = regressionContext.getxAxisContext().get(0);
				xData[i][0] = Double.valueOf((String) entry.get(xRecord.getAlias()));
				
			}
			
		}
		
		olsInstance.newSampleData(yData, xData);
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("coefficients", olsInstance.estimateRegressionParameters());
		results.put("residuals", olsInstance.estimateResiduals());
		results.put("rsqaured", olsInstance.calculateRSquared());
		results.put("adjustedrSquared", olsInstance.calculateAdjustedRSquared());
		results.put("standardError", olsInstance.estimateRegressionStandardError());
		return results;
	}
	
	private String getRegressionPointAlias (RegressionContext regressionContext) {
		StringBuilder groupAlias = new StringBuilder();
		
		groupAlias.append(regressionContext.getyAxisContext().getAlias());
		for(RegressionPointContext xContext: regressionContext.getxAxisContext()) {
			groupAlias.append("_");
			groupAlias.append(xContext.getAlias());
		}
		
		groupAlias.append("regr");
		return groupAlias.toString();
	}
	
}
