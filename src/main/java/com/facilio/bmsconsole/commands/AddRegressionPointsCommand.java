package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AnovaResultContext;
import com.facilio.bmsconsole.context.RegressionContext;
import com.facilio.bmsconsole.context.RegressionContext.DataConditions;
import com.facilio.bmsconsole.context.RegressionPointContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;

public class AddRegressionPointsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(AddRegressionPointsCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<RegressionContext> regressionConfig = (List<RegressionContext>) context.get(FacilioConstants.ContextNames.REGRESSION_CONFIG);
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		Boolean isMultiple;
				
		Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
		if(regressionConfig == null && reportContext != null && reportContext.getReportState() != null) {
			List<Map<String,Object>> regressionList = (List<Map<String, Object>>)reportContext.getReportState().get(StringConstants.REGRESSION_CONFIG);
			if(regressionList != null) {
				regressionConfig = FieldUtil.getAsBeanListFromMapList(regressionList, RegressionContext.class);
			}
		}
		
		Map<String, Map<String, Object>> regressionResults = new HashMap<String, Map<String,Object>>();
		if(regressionConfig != null && data!= null && !regressionConfig.isEmpty() && data.size() != 0) {
			isMultiple = regressionConfig.get(0).getIsMultiple();
			
			if(data.size() != 0) {
				for(RegressionContext rc: regressionConfig) {
					DataConditions dataConditions = checkDataForAuthenticity(new ArrayList(data), rc.getxAxis(), rc.getyAxis(), isMultiple);
					
					switch(dataConditions) {
					case NOT_ENOUGH_DATA:{
						rc.setErrorStateENUM(DataConditions.NOT_ENOUGH_DATA);
						rc.setErrorState(DataConditions.NOT_ENOUGH_DATA.getValue());
						break;
					}
					case SINGULAR_MATRIX:{
						rc.setErrorStateENUM(DataConditions.SINGULAR_MATRIX);
						rc.setErrorState(DataConditions.SINGULAR_MATRIX.getValue());
						break;
					}
					case DATA_AUTHENTICATED:{
						rc.setErrorStateENUM(DataConditions.DATA_AUTHENTICATED);
						rc.setErrorState(DataConditions.DATA_AUTHENTICATED.getValue());
						break;
					}
					}
					
					String groupAlias = getRegressionPointAlias(rc);
					rc.setGroupAlias(groupAlias);
					
					if(rc.getErrorStateENUM() == dataConditions.DATA_AUTHENTICATED) {
						isMultiple = rc.getIsMultiple();
						data = cleanData(new ArrayList(data), rc.getxAxis(), rc.getyAxis());
						
						Map<String, Object> regressionResult = prepareData(rc, new ArrayList(data), isMultiple);
						
						if(regressionResult.isEmpty()) {
							return false;
						}
						
						
						String expressionString = getExpressionString(regressionResult, reportContext, rc.getxAxis());
						ReportDataPointContext regressionPoint = getRegressionDataPoint(groupAlias, expressionString);
						reportContext.getDataPoints().add(regressionPoint);
						Map<String, Object> coefficientMap = getCoefficientMap(regressionResult, rc.getxAxis());
						computeRegressionData(groupAlias, (ArrayList)data, coefficientMap);
						regressionResult.put(StringConstants.COEFFICIENT_MAP, coefficientMap);
						
						computeTstatAndP(regressionResult, rc.getxAxis(), reportContext.getDataPoints());
						computeANOVAResultMetrics(regressionResult, (ArrayList)data, groupAlias, rc.getyAxis().getAlias());
						
						regressionResults.put(groupAlias, regressionResult);
					}
					
					
				}
				
				reportData.put(FacilioConstants.ContextNames.REGRESSION_RESULT, regressionResults);
			}
			
		}
		if(regressionConfig != null && !regressionConfig.isEmpty()) {
			if(reportContext.getReportState() != null) {
				reportContext.getReportState().put(FacilioConstants.ContextNames.REGRESSION_CONFIG, regressionConfig);
			}
			else {
				JSONObject reportState = new JSONObject();
				reportState.put(FacilioConstants.ContextNames.REGRESSION_CONFIG, regressionConfig);
				reportContext.setReportState(reportState);
			}
		}
		long orgId = AccountUtil.getCurrentOrg().getId();
		if(orgId == 6l) {
			LOGGER.info("AddRegressionPointsCommand is" + context);
		}
		return false;
	}
	
	private DataConditions checkDataForAuthenticity(ArrayList<Map<String, Object>> data, List<RegressionPointContext> idpVariables, RegressionPointContext depVariable, boolean isMultiple) {
		DataConditions finalCondition = DataConditions.DATA_AUTHENTICATED;
		
		if(isMultiple == false) {
			if(checkDataForNull(data, idpVariables, depVariable)) {
				finalCondition = DataConditions.NOT_ENOUGH_DATA;
			}
		}
		else {
			data = cleanData(data, idpVariables, depVariable);
			
			LOGGER.severe("Data for multiple regression report!");
			LOGGER.severe(data.toString());
			
			if(data == null || data.size() == 0) {
				return DataConditions.NOT_ENOUGH_DATA;
			}
			
			if(data.size() < idpVariables.size() + 1) {
				finalCondition = DataConditions.NOT_ENOUGH_DATA;
			}
			
			if(isDataMatrixNonSingular(data, idpVariables) == false) {
				finalCondition = DataConditions.SINGULAR_MATRIX;
			}
		}
		
		
		return finalCondition;
	}
	
	private boolean checkDataForNull(ArrayList<Map<String, Object>> data, List<RegressionPointContext> idpVariables, RegressionPointContext depVariable) {
		List<Map<String, Object>> finalData = new ArrayList<Map<String,Object>>();
		
		finalData = cleanData(data, idpVariables, depVariable);
		
		if(finalData.size() == 0 || (finalData.size() < idpVariables.size() + 1)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isDataMatrixNonSingular(ArrayList<Map<String, Object>> data, List<RegressionPointContext> idpVariables) {
		boolean isNonSingularMatrix = true;
		
		Integer min  = Math.min(data.size(), idpVariables.size());
		
		double [][] x = new double [data.size()][idpVariables.size()];
		
		for(int i = 0; i< data.size(); i++) {
			Map<String, Object> record = data.get(i);
			for(int j = 0; j< idpVariables.size(); j++) {
				x[i][j] = Double.valueOf((String)record.get(idpVariables.get(j).getAlias()));
			}
		}
		
		RealMatrix m = MatrixUtils.createRealMatrix(x);
		 SingularValueDecomposition singleDc = new SingularValueDecomposition(m);
		 
		 Integer rank = singleDc.getRank();
		 LOGGER.severe("Matrix rank:" + rank.toString());
		 if(rank < min) {
			 isNonSingularMatrix = false;
		 }
		 
		 LOGGER.severe("Is Matrix non singular: " + isNonSingularMatrix);
		 return isNonSingularMatrix;
		
	}
	private void computeRegressionData(String groupAlias, ArrayList<Map<String,Object>> data, Map<String,Object> coefficientMap) {
		
		ArrayList<String> coefficientKeys = new ArrayList(coefficientMap.keySet());
		for(Map<String, Object> record: data) {
			double value = 0.0;
			for(String key: coefficientKeys) {
				if(key != StringConstants.CONSTANT) {
					value = value + (Double)coefficientMap.get(key) * Double.valueOf((String)record.get(key));
				}
			}
			value = value + (Double)coefficientMap.get(StringConstants.CONSTANT);
			record.put(groupAlias, String.valueOf(value));
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
			cMap.put(StringConstants.CONSTANT, coefficients[0]);
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
		yAxisContext.setLabel("RegressionModel " + alias);
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
				expressionString.append(coefficients[1] + independentPoints.get(0).getAlias());
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
//		Double rSquared = (Double) regressionResult.get(StringConstants.RSQUARED);
//		expressionString = expressionString.append("( R2 = " + rSquared + " )");
		return expressionString.toString();
	}
	
	
	private String getDataPointName(List<ReportDataPointContext> dataPoints, RegressionPointContext xPoint, boolean isFieldName) {
		for(ReportDataPointContext dataPoint: dataPoints) {
			ArrayList<Object> temp = (ArrayList) dataPoint.getMetaData().get("parentIds");
			if(temp.size() != 0) {
				Long parentId = Long.valueOf(String.valueOf(temp.get(0)));
				
				if(dataPoint.getyAxis().getFieldId() == xPoint.getReadingId() && parentId == xPoint.getParentId()) {
					if(isFieldName) {
						return dataPoint.getyAxis().getFieldName();
					}
					else {
						return dataPoint.getName();
					}
					
				}
				else {
					continue;
				}
			}
		}
		return null;
	}
	
	
	private ArrayList<Map<String, Object>> cleanData(ArrayList<Map<String, Object>> data, List<RegressionPointContext> idpVariables, RegressionPointContext depVariable) {
		ArrayList<Map<String, Object>> finalData = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> entry: data) {
			boolean isMarked = false;
			if(entry.get(depVariable.getAlias())== null || entry.get(depVariable.getAlias()) == "") {
				isMarked = true;
			}
			
			if(isMarked == false) {
				for(int j =0 ;j<idpVariables.size(); j++ ) {
					RegressionPointContext idp = idpVariables.get(j);
					if(entry.get(idp.getAlias()) == null || entry.get(idp.getAlias()) == "") {
						isMarked = true;
						break;
					}
				}
			}
			
			if(!isMarked) {
				finalData.add(entry);
			}
		}
		
		LOGGER.severe("Final Cleaned Data");
		LOGGER.severe(finalData.toString());
		return finalData;
	}
	
	private Map<String, Object> prepareData(RegressionContext regressionContext, ArrayList<Map<String, Object>> data, boolean isMultiple){
		Map<String, Object> results = new HashMap<String, Object>();
		OLSMultipleLinearRegression olsInstance = new OLSMultipleLinearRegression();
		
		List<RegressionPointContext> xPoints = regressionContext.getxAxis();
		RegressionPointContext yPoint = regressionContext.getyAxis();
		
		double[][] xData = new double[data.size()][xPoints.size()];
		double[] yData = new double[data.size()];
		
		
		for(int i = 0;i < data.size();i ++) {
			Map<String, Object> entry = data.get(i);
			LOGGER.info("Record yValue");
			LOGGER.log(Level.INFO, "ENTRY YPOINT ##### "+ yPoint.getAlias());
			LOGGER.log(Level.INFO, "ENTRY MAP ##### "+ entry);
			LOGGER.log(Level.INFO, "ENTRY GET VALUE ##### "+ entry.get(yPoint.getAlias()));
			
			yData[i] = Double.valueOf((String)entry.get(yPoint.getAlias()));
			
			if(isMultiple) {		
				for(int j=0; j < regressionContext.getxAxis().size(); j++) {
					RegressionPointContext xRecord = regressionContext.getxAxis().get(j);
					xData[i][j] = Double.valueOf((String)entry.get(xRecord.getAlias()));
					
				}
			}
			else {
				// single regression code
				
				RegressionPointContext xRecord = regressionContext.getxAxis().get(0);
				xData[i][0] = Double.valueOf((String) entry.get(xRecord.getAlias()));
				
			}
			
		}
		
		try{
			olsInstance.newSampleData(yData, xData);
		}catch(MathIllegalArgumentException e) {
			return results;
		}
		
		
		results.put(StringConstants.COEFFICIENTS, olsInstance.estimateRegressionParameters());
		results.put(StringConstants.RESIDUALS, olsInstance.estimateResiduals());
		results.put(StringConstants.RSQUARED, olsInstance.calculateRSquared());
		results.put(StringConstants.ADJUSTED_R_SQUARED, olsInstance.calculateAdjustedRSquared());
		results.put(StringConstants.STANDARD_ERROR, olsInstance.estimateRegressionStandardError());
		results.put(StringConstants.STANDARD_PARAMETER_ERRORS, olsInstance.estimateRegressionParametersStandardErrors());
		results.put(StringConstants.OBSERVATIONS, data.size());
		
		results = formatData(results);
		return results;
	}
	
	private Map<String, Object> formatData(Map<String, Object> regressionResult) {
		DecimalFormat formatter = new DecimalFormat("0.00");
		DecimalFormat coeffFormat = new DecimalFormat("0.0000");
		
		Map<String, Object> temp = new HashMap<String, Object>();
		
		temp.put(StringConstants.RSQUARED, Double.valueOf(formatter.format(regressionResult.get(StringConstants.RSQUARED))));
		temp.put(StringConstants.ADJUSTED_R_SQUARED, Double.valueOf(formatter.format(regressionResult.get(StringConstants.ADJUSTED_R_SQUARED))));
		temp.put(StringConstants.STANDARD_ERROR, Double.valueOf(formatter.format(regressionResult.get(StringConstants.STANDARD_ERROR))));
		
		double [] arrayStore = (double []) regressionResult.get(StringConstants.COEFFICIENTS);
		
		for(int i = 0; i< arrayStore.length; i++) {
			arrayStore[i] = Double.valueOf(coeffFormat.format(arrayStore[i]));
		}
		
		temp.put(StringConstants.COEFFICIENTS, arrayStore);
		
		arrayStore = (double []) regressionResult.get(StringConstants.RESIDUALS);
		for(int i = 0; i< arrayStore.length; i++) {
			arrayStore[i] = Double.valueOf(formatter.format(arrayStore[i]));
		}
		
		temp.put(StringConstants.RESIDUALS, arrayStore);
		
		arrayStore = (double[]) regressionResult.get(StringConstants.STANDARD_PARAMETER_ERRORS);
		for(int i = 0; i< arrayStore.length; i++) {
			arrayStore[i] = Double.valueOf(formatter.format(arrayStore[i]));
		}
		
		temp.put(StringConstants.STANDARD_PARAMETER_ERRORS, arrayStore);
		temp.put(StringConstants.OBSERVATIONS, regressionResult.get(StringConstants.OBSERVATIONS));
		
		
		return temp;
		
	}
	
	private void computeTstatAndP (Map<String, Object> results, List<RegressionPointContext> idpPoints, List<ReportDataPointContext> reportDataPoints) {
		Map<String, Map<String, Object>> metricRecords = new LinkedHashMap<String, Map<String, Object>>();
		
		double [] coefficients = (double[]) results.get(StringConstants.COEFFICIENTS);
		double [] errors = (double []) results.get(StringConstants.STANDARD_PARAMETER_ERRORS);
		double [] residuals = (double[]) results.get(StringConstants.RESIDUALS);
		
		int degreesOfFreedom = residuals.length - coefficients.length;
		TDistribution tDistribution = new TDistribution(degreesOfFreedom);
		
		
		DecimalFormat formatter = new DecimalFormat("0.00");
		DecimalFormat pFormat = new DecimalFormat("0.0000");
		double tStat;
		double pValue;
		
		double coefficient = Double.valueOf(formatter.format(coefficients[0]));
		double standardError = Double.valueOf(formatter.format(errors[0])); 
		tStat = Double.valueOf(formatter.format(coefficient/ standardError));
		pValue = Double.valueOf(pFormat.format(tDistribution.cumulativeProbability(-FastMath.abs(tStat)) * 2));
		
		Map<String, Object> newRecord = new LinkedHashMap<String, Object>();
		newRecord.put(StringConstants.COEFFICIENT, coefficient);
		newRecord.put(StringConstants.STANDARD_ERROR, standardError);
		newRecord.put(StringConstants.T_VALUE, tStat);
		newRecord.put(StringConstants.P_VALUE, pValue);
		
		metricRecords.put(StringConstants.INTERCEPT, newRecord);
		
		for(int i =1; i < coefficients.length; i++) {
			newRecord = new LinkedHashMap<String, Object>();
			coefficient = Double.valueOf(formatter.format(coefficients[i]));
			standardError = Double.valueOf(formatter.format(errors[i]));
			tStat = Double.valueOf(formatter.format(coefficient/ standardError));
			pValue = Double.valueOf(pFormat.format(tDistribution.cumulativeProbability(-FastMath.abs(tStat)) * 2));
			
			newRecord.put(StringConstants.COEFFICIENT, coefficient);
			newRecord.put(StringConstants.STANDARD_ERROR, standardError);
			newRecord.put(StringConstants.T_VALUE, tStat);
			newRecord.put(StringConstants.P_VALUE, pValue);
			
			String name = getDataPointName(reportDataPoints, idpPoints.get(i - 1), false);
			
			metricRecords.put(name, newRecord);
		}
		
		results.put(StringConstants.REGRESSION_METRICS, metricRecords);
	}
	
	private String getRegressionPointAlias (RegressionContext regressionContext) {
		StringBuilder groupAlias = new StringBuilder();
		
		
		for(RegressionPointContext xContext: regressionContext.getxAxis()) {
			groupAlias.append(xContext.getAlias());
			groupAlias.append("_");
		}
		groupAlias.append(regressionContext.getyAxis().getAlias());
		
		groupAlias.append("regr");
		return groupAlias.toString();
	}
	
	public double computeSumOfSquareErrors(List<Map<String, Object>> data, String pointAlias, String yAlias ) {
		// SSE
		DecimalFormat formatter = new DecimalFormat("0.00");
		double sumOfSquareErrors = 0.0;
		double diff = 0.0;
		for(Map<String, Object>d : data) {
			diff = Double.valueOf((String)d.get(yAlias)) - Double.valueOf((String)d.get(pointAlias));
			sumOfSquareErrors = sumOfSquareErrors + Math.pow(diff, 2);
		}
		
		return Double.valueOf(formatter.format(sumOfSquareErrors));
	}
	
	public double computeSumOfSqaureTotal(List<Map<String, Object>> data, String yAlias) {
		// SST
		DecimalFormat formatter = new DecimalFormat("0.00");
		double sumOfSquareTotal = 0.0;
		double mean  = computeMean(data, yAlias);
		double diff = 0.0;
		for(Map<String, Object> d: data) {
			diff = Double.valueOf((String)d.get(yAlias))- mean;
			sumOfSquareTotal = sumOfSquareTotal + Math.pow(diff, 2);
		}
		
		return Double.valueOf(formatter.format(sumOfSquareTotal));
	}
	
	public double computeSumOfSquareRegression(List<Map<String, Object>> data, String pointAlias, String yAlias) {
		// SSR
		DecimalFormat formatter = new DecimalFormat("0.00");
		double sumOfSquareRegression = 0.0;
		double mean  = computeMean(data, yAlias);
		double diff = 0.0;
		
		for(Map<String, Object> d: data) {
			diff = Double.valueOf((String) d.get(pointAlias)) - mean;
			sumOfSquareRegression = sumOfSquareRegression + Math.pow(diff, 2);
		}
		return Double.valueOf(formatter.format(sumOfSquareRegression));
	}
	
	public double computeMean (List<Map<String, Object>> data, String alias) {
		DecimalFormat formatter = new DecimalFormat("0.00");
		double sum = 0.0;
		int count = 0;
		for(Map<String, Object>d : data) {
			if(d.get(alias) != null && d.get(alias) != "") {
				sum  = sum + Double.valueOf((String)d.get(alias));
				count++;
			}
		}
		return Double.valueOf(formatter.format(sum / count));
	}
	
	public double computeMeanSquareError(List<Map<String, Object>> data, int sampleLength, int numberOfCoeff, String pointAlias, String yAlias) {
		DecimalFormat formatter = new DecimalFormat("0.00");
		double sse = computeSumOfSquareErrors(data, pointAlias, yAlias);
		return Double.valueOf(formatter.format(sse / (sampleLength - numberOfCoeff)));
	}
	
	public double computeMeanSquareRegression(List<Map<String, Object>> data, int numberOfCoeff, String pointAlias, String yAlias) {
		DecimalFormat formatter = new DecimalFormat("0.00");
		double ssr = computeSumOfSquareRegression(data, pointAlias, yAlias);
		return Double.valueOf(formatter.format(ssr / (numberOfCoeff - 1)));
	}
	
	public void computeANOVAResultMetrics(Map<String, Object> regressionResult, ArrayList<Map<String, Object>> data, String pointAlias, String yAlias) {
		
		DecimalFormat formatter = new DecimalFormat("0.00");
		
		List<AnovaResultContext> anovaResults = new ArrayList<AnovaResultContext>();
		int observations = (int)regressionResult.get(StringConstants.OBSERVATIONS);
		double [] coeffcients = (double []) regressionResult.get(StringConstants.COEFFICIENTS);
		
		AnovaResultContext regression = new AnovaResultContext();
		AnovaResultContext residuals = new AnovaResultContext();
		AnovaResultContext total = new AnovaResultContext();
		
		regression.setDegreeOfFreedom(coeffcients.length - 1);
		regression.setSumOfSquare(computeSumOfSquareRegression(data, pointAlias, yAlias));
		regression.setMeanSumOfSquare(computeMeanSquareRegression(data, coeffcients.length, pointAlias, yAlias));
		regression.setResultVariable(StringConstants.REGRESSION);
		regression.setfStat(Double.valueOf(formatter.format(computeMeanSquareRegression(data, coeffcients.length, pointAlias, yAlias) / computeMeanSquareError(data, data.size(), coeffcients.length, pointAlias, yAlias))));
		
		
		residuals.setResultVariable(StringConstants.RESIDUAL);
		residuals.setDegreeOfFreedom(data.size() - coeffcients.length);
		residuals.setSumOfSquare(computeSumOfSquareErrors(data, pointAlias, yAlias));
		residuals.setMeanSumOfSquare(computeMeanSquareError(data, data.size(), coeffcients.length, pointAlias, yAlias));
		
		total.setResultVariable("total");
		total.setDegreeOfFreedom(data.size() - 1);
		total.setSumOfSquare(computeSumOfSqaureTotal(data, yAlias));
		
		anovaResults.add(regression);
		anovaResults.add(residuals);
		anovaResults.add(total);
		
		regressionResult.put("anovaMetrics", anovaResults);
	}
	
	public class StringConstants{
		final static String COEFFICIENTS = "coefficients";
		final static String RESIDUALS = "residuals";
		final static String RSQUARED = "rsquared";
		final static String ADJUSTED_R_SQUARED= "adjustedrSquared";
		final static String STANDARD_ERROR = "standardError";
		final static String STANDARD_PARAMETER_ERRORS = "standardParameterErrors";
		final static String OBSERVATIONS = "observations";
		final static String CONSTANT = "constant";
		final static String REGRESSION_MODEL = "Regression Model";
		final static String INTERCEPT = "Intercept";
		final static String T_VALUE = "tValue";
		final static String P_VALUE = "pValue";
		final static String COEFFICIENT = "coefficient";
		final static String REGRESSION_METRICS = "regressionMetrics";
		final static String RESIDUAL = "residual";
		final static String REGRESSION = "regression";
		final static String COEFFICIENT_MAP = "coefficientMap";
		final static String REGRESSION_CONFIG="regressionConfig";
	}
	
}
