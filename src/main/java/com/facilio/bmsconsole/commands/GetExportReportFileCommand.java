package com.facilio.bmsconsole.commands;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.CalculateAggregationCommand.EnumVal;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.pdf.PdfUtil;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class GetExportReportFileCommand implements Command {
	
	private static final String SERIES_X_HEADER = "Data Point";
	private static final String ALIAS = "alias";
	
	private ReportContext report;
	private ReportMode mode;
	private Map<String, Object> dataMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String fileUrl = null;
		if(fileFormat != FileFormat.PDF && fileFormat != FileFormat.IMAGE) {
			
			mode = (ReportMode)context.get(FacilioConstants.ContextNames.REPORT_MODE);
			if (mode == null) {
				String chartStateString = report.getChartState();
				JSONParser parser = new JSONParser();
				Map<String, Object> chartState = (Map<String, Object>) parser.parse(chartStateString);
				if (chartState != null && chartState.containsKey("common")) {
					Map<String, Object> common = (Map<String, Object>) chartState.get("common");
					mode = ReportMode.valueOf((int)(long) common.get("mode"));
				}
			}
			
		    Map<String, Object> tableState = null;
		    String tabularState = (String) context.get(FacilioConstants.ContextNames.TABULAR_STATE);
		    if (tabularState == null) {
		    		tabularState = report.getTabularState();
		    }
			if (tabularState != null) {
				JSONParser parser = new JSONParser();
	    			tableState = (Map<String, Object>) parser.parse(report.getTabularState());
			}
			
			List<Map<String, Object>> columns = setDataMapAndGetColumns(tableState);
			List<String> headers = getTableHeaders(columns);
			
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			List<Map<String, Object>> records = getTableData(columns, reportData);

			Map<String, Object> table = new HashMap<String, Object>();
			table.put("headers", headers);
			table.put("records", records);
			fileUrl = ExportUtil.exportData(fileFormat, "Report Data", table);
		}
		else {
			StringBuilder url = getClientUrl(report.getDataPoints().get(0).getxAxis().getField().getModule().getName(), report.getId(), fileFormat);			
			String chartType = (String) context.get("chartType");
			if (chartType != null) {
				url.append("&charttype=").append(chartType);
			}
			
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(), url.toString(), fileFormat);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}
	
	private List<Map<String, Object>> setDataMapAndGetColumns (Map<String, Object> tableState) {
		List<String> currentHeaderKeys = new ArrayList<>();
		currentHeaderKeys.add(report.getxAlias());
		
		report.getDataPoints().forEach(dp -> {
			String alias = dp.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA);
			currentHeaderKeys.add(alias);
			dataMap.put(alias, dp);
			if (report.getBaseLines() != null) {
				report.getBaseLines().stream().forEach(bl -> {
					String blAlias = dp.getAliases().get(bl.getBaseLine().getName());
					currentHeaderKeys.add(blAlias);
					
					Map<String, Object> blMap = new HashMap<>();
					blMap.put("dpAlias", alias);
					blMap.put("baseline", bl);
					dataMap.put(blAlias, blMap);
				});
			}
		});
	    
		List<Map<String, Object>> columns;
		if (tableState == null) {
			columns = new ArrayList<>();
			setTableStateColumns(columns, currentHeaderKeys);
		}
		else {
			List<Map<String, Object>> stateColumns = (List<Map<String, Object>>) tableState.get("columns");
			columns = stateColumns.stream().filter(col -> currentHeaderKeys.contains(col.get(ALIAS))).collect(Collectors.toList());
			String xColAlias = currentHeaderKeys.get(0);
			List<String> existingHeaders = stateColumns.stream().map(col -> col.get(ALIAS).toString()).collect(Collectors.toList());
			if (!stateColumns.get(0).get(ALIAS).equals(xColAlias)) {
				int xColIdx = existingHeaders.indexOf(currentHeaderKeys.get(0));
				if (xColIdx != -1) {
					Map<String, Object> xCol = stateColumns.remove(xColIdx);
					stateColumns.add(0, xCol);
				}
				else {
					setTableStateColumns(columns, Collections.singletonList(xColAlias), true);
				}
			}
			if (columns.size() != stateColumns.size()) {
				List<String> remainingHeaders = currentHeaderKeys.stream().filter(header -> !existingHeaders.contains(header)).collect(Collectors.toList());
				setTableStateColumns(columns, remainingHeaders);
			}
		}
		return columns;
	}
	
	private void setTableStateColumns (List<Map<String, Object>> columns, List<String> headerKeys, Boolean...isXval) {
		headerKeys.forEach(header -> {
			Map<String, Object> column = new HashMap<>();
			column.put(ALIAS, header);
			if (isXval != null && isXval.length > 0 && isXval[0]) {
				columns.add(0, column);
			}
			else {
				columns.add(column);
			}
		});
	}
	
	private List<String> getTableHeaders(List<Map<String, Object>> columns) throws Exception {
		List<String> headers = new ArrayList<>();
		for(int i = 0, size = columns.size(); i < size; i++) {
			Map<String, Object> col = columns.get(i);
			if (i == 0) {
				String name;
				switch(mode) {
					case SITE:
						name = "Site";
	    					break;
					case BUILDING:
						name = "Building";
	    					break;
					case RESOURCE:
						name = "Asset";
	    					break;
					default:
						name = report.getDataPoints().get(0).getxAxis().getLabel();
							
				}
				headers.add(name);
				col.put("header", name);
				continue;
			}
			if ((String) col.get("displayName") != null) {
				headers.add((String) col.get("displayName"));
				col.put("header", col.get("displayName"));
			}
			else {
				ReportDataPointContext dataPoint;
				ReportBaseLineContext baseLine = null;
				Object pointObj = dataMap.get(col.get(ALIAS));
				if (pointObj instanceof ReportDataPointContext) {
					dataPoint = (ReportDataPointContext) pointObj;
				}
				else {
					String alias = (String) ((Map<String, Object>) pointObj).get("dpAlias");
					dataPoint = (ReportDataPointContext) dataMap.get(alias);
					baseLine = (ReportBaseLineContext) ((Map<String, Object>) pointObj).get("baseline");
				}
				
				StringBuilder builder = new StringBuilder(dataPoint.getName());
				if (baseLine != null) {
					builder.append(" - ").append(baseLine.getBaseLine().getName());
				}
				String unit = dataPoint.getyAxis().getUnitStr();
				builder.append((unit != null && !unit.isEmpty() ? " (" + unit + ")" : ""));
				if ( (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)){
					if (report.getxAggrEnum() != null && report.getxAggrEnum() != CommonAggregateOperator.ACTUAL) {
						if (col.containsKey("enumMode") && (int)col.get("enumMode") > 0 ) {
							EnumMode enumMode = EnumMode.valueOf((int)col.get("enumMode"));
							col.put("enumModeEnum", enumMode);
							if (enumMode == EnumMode.GRAPH) {
								col.put("enumModeEnum", EnumMode.DURATION);
							}
							else if (enumMode == EnumMode.PERCENT){
								builder.append(" (%)");
							}
						}
						else {
							col.put("enumModeEnum", EnumMode.DURATION);
						}
					}
				}
				String header = builder.toString();
				headers.add(header);
				col.put("header", header);
			}
		}
		return headers;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getTableData(List<Map<String, Object>> columns, JSONObject reportData) {
		List<Map<String, Object>> records = new ArrayList<>();
		FieldType xDataType = report.getDataPoints().get(0).getxAxis().getDataTypeEnum();
		boolean isTimeSeries = false;
		if (xDataType == FieldType.DATE || xDataType == FieldType.DATE_TIME) {
			isTimeSeries = true;
		}
		String format = "EEEE, MMMM dd, yyyy hh:mm a";
		
		AggregateOperator aggr = report.getxAggrEnum();
		if (isTimeSeries && aggr != null && aggr instanceof DateAggregateOperator) {
			String dateFormat = ((DateAggregateOperator)aggr).getFormat();
			format = dateFormat != null ? dateFormat : format;
		}
		
		List<Map<String, Object>> data = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
		if (data != null && !data.isEmpty()) {
			for(Map<String, Object> row: data) {
				Map<String, Object> newRow = new HashMap<>();
				for(int i = 0; i < columns.size(); i++) {
					Map<String, Object> column = columns.get(i);
					Object value = row.get(column.get(ALIAS));
					if (value != null) {
						if (i == 0) {
							if (isTimeSeries) {
								value = DateTimeUtil.getFormattedTime((long)value, format);
							}
							newRow.put((String) columns.get(0).get("header"), value);
							continue;
						}
						
						ReportDataPointContext dataPoint;
						Object pointObj = dataMap.get(column.get(ALIAS));
						if (pointObj instanceof ReportDataPointContext) {
							dataPoint = (ReportDataPointContext) pointObj;
						}
						else {
							String alias = (String) ((Map<String, Object>) pointObj).get("dpAlias");
							dataPoint = (ReportDataPointContext) dataMap.get(alias);
						}
						switch(dataPoint.getyAxis().getDataTypeEnum()) {
							case BOOLEAN:
							case ENUM:
								EnumVal enumVal = (EnumVal) value;
								value = null;
								if (column.get("enumModeEnum") == null) {	// High-res
									List<SimpleEntry<Long, Integer>> timeline = enumVal.getTimeline();
									if (timeline != null && !timeline.isEmpty()) {
										value = dataPoint.getyAxis().getEnumMap().get(timeline.get(0).getValue());
									}
								}
								else if (column.get("enumModeEnum") == EnumMode.PERCENT) {
									Map<Integer, Long> duration = enumVal.getDuration();
									if (duration != null) {
										long total = duration.values().stream().reduce(0L, (prev, key) -> prev + key);
										StringBuilder percent = new StringBuilder(); 
										for (Entry<Integer, Long> entry : duration.entrySet()) {
											percent.append(dataPoint.getyAxis().getEnumMap().get(entry.getKey()))
												.append(": ")
												.append(entry.getValue() / total * 100)
												.append("\n");
										}
										value = percent.toString();
									}
								}
								else {	// DURATION
									Map<Integer, Long> duration = enumVal.getDuration();
									if (duration != null) {
										StringBuilder durationVal = new StringBuilder();
										for (Entry<Integer, Long> entry : duration.entrySet()) {
											durationVal.append(dataPoint.getyAxis().getEnumMap().get(entry.getKey()))
											.append(": ")
											.append(entry.getValue())
											.append("\n");
										}
										value = durationVal.toString();
									}
								}
								break;
						}
						newRow.put((String) column.get("header"), value);
					}
				}
				records.add(newRow);
			}
		}
		return records;
	}
	
	private StringBuilder getClientUrl(String moduleName, Long reportId, FileFormat fileFormat) {
		moduleName = FacilioConstants.ContextNames.ENERGY_DATA_READING;	// Temp
		StringBuilder url = new StringBuilder(AwsUtil.getConfig("clientapp.url")).append("/app/");
		if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			url.append("wo");
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ALARM)) {
			url.append("fa");
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
			url.append("em");
		}
		if (reportId > 0) {
			url.append("/reports/newview/").append(reportId);			
		}
		else {
			url.append(addAnalyticsConfigAndGetUrl(fileFormat));
		}
		if(fileFormat == FileFormat.IMAGE) {
			url.append("/show");
		}
		url.append("?print=true");
		
		if(report.getDateRange() != null) {
			JSONObject dateRange = new JSONObject();
			dateRange.put("startTime", report.getDateRange().getStartTime());
			dateRange.put("endTime", report.getDateRange().getEndTime());
			dateRange.put("operatorId", report.getDateOperator());
			dateRange.put("value", report.getDateValue());
			url.append("&daterange=").append(ReportsUtil.encodeURIComponent(dateRange.toJSONString()));
		}
		
		return url;
	}
	
	private String addAnalyticsConfigAndGetUrl(FileFormat fileFormat) {
		StringBuilder url = new StringBuilder();
		if(fileFormat == FileFormat.IMAGE) {
			url.append("/show");
		}
		return url.toString();
	}
	
	private enum EnumMode {
		DURATION,
		PERCENT,
		GRAPH;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static EnumMode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
