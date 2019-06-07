package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.AggregateOperator.DateAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.pdf.PdfUtil;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetExportReportDataCommand implements Command {
	
	private static final String SERIES_X_HEADER = "Data Point";
	private static final String ACTUAL_HEADER = "Actual";
	
	private static ReportContext report;

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		Boolean isS3Url = (Boolean) context.get("isS3Url");
		if (isS3Url == null) {
			isS3Url = false;
		}
		
		report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Set<Object> xValues = (Set<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
		Map<String, Map<String, Map<Object, Object>>> reportData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportMode mode = (ReportMode)context.get(FacilioConstants.ContextNames.REPORT_MODE);
		
		if (mode == null) {
			String chartStateString = report.getChartState();
			JSONParser parser = new JSONParser();
			Map<String, Object> chartState = (Map<String, Object>) parser.parse(chartStateString);
			if (chartState != null && chartState.containsKey("common")) {
				Map<String, Object> common = (Map<String, Object>) chartState.get("common");
				mode = ReportMode.valueOf((int)(long) common.get("mode"));
			}
		}
		
		
		Map<Long, ReportBaseLineContext> baseLineMap = null;
		
		List<String> currentHeaderKeys = new ArrayList<>();
		new LinkedHashMap<>();
	    if (mode == ReportMode.SERIES) {
	    	  currentHeaderKeys.add(SERIES_X_HEADER);
	    	  currentHeaderKeys.addAll(reportData.values().stream().filter(val -> val != null && !val.isEmpty()).findFirst().get().keySet());
	    }
	    else {
	    		String header;
	    		switch(mode) {
	    			case SITE:
	    				header = "Site";
	    				break;
	    			case BUILDING:
	    				header = "Building";
	    				break;
	    			case RESOURCE:
	    				header = "Asset";
	    				break;
	    			default:
	    				header = report.getDataPoints().get(0).getxAxis().getField().getName();
	    		}
	    		currentHeaderKeys.add(header);
	    		if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
	    			baseLineMap = report.getBaseLines().stream().collect(Collectors.toMap(ReportBaseLineContext::getBaseLineId, Function.identity()));
	    		}
	    		
	    		reportData.forEach((key, data) -> {
	    			currentHeaderKeys.add(key);
	    			if (report.getBaseLines() != null) {
	    				currentHeaderKeys.addAll(report.getBaseLines().stream().map(bl -> String.valueOf(bl.getBaseLineId()) + "__baseline__" + key).collect(Collectors.toList()));
	    			}
	    		});
	    		
	    }
	    
	    Map<String, Object> tableState = null;
	    String tabularState = report.getTabularState() != null ? report.getTabularState() : (String) context.get(FacilioConstants.ContextNames.TABULAR_STATE);
		if (tabularState != null) {
			JSONParser parser = new JSONParser();
    			tableState = (Map<String, Object>) parser.parse(report.getTabularState());
		}
		
		List<Map<String, Object>> columns;
		if (tableState == null) {
			tableState = new HashMap<>();
			columns = new ArrayList<>();
			setTableStateColumns(columns, currentHeaderKeys);
		}
		else {
			List<Map<String, Object>> stateColumns = (List<Map<String, Object>>) tableState.get("columns");
			columns = stateColumns.stream().filter(col -> currentHeaderKeys.contains(col.get("name"))).collect(Collectors.toList());
			if (columns.size() != stateColumns.size()) {
				List<String> existingHeaders = stateColumns.stream().map(col -> col.get("name").toString()).collect(Collectors.toList());
				List<String> remainingHeaders = currentHeaderKeys.stream().filter(header -> !existingHeaders.contains(header)).collect(Collectors.toList());
				setTableStateColumns(columns, remainingHeaders);
			}
		}
		
		Map<String, ReportDataPointContext> dataMap = report.getDataPoints().stream().collect(Collectors.toMap(ReportDataPointContext::getName, Function.identity()));
		
		List<String> headers = getTableHeaders(columns, report, baseLineMap, dataMap, mode);
		List<Map<String, Object>> records = getTableData(columns, reportData, xValues, baseLineMap, dataMap, mode);
		
		Map<String, Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("records", records);
		
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String fileUrl = null;
		if(fileFormat != FileFormat.PDF && fileFormat != FileFormat.IMAGE) {
			fileUrl = ExportUtil.exportData(fileFormat, "Report Data", table, isS3Url);
		}
		else {
			StringBuilder url = getClientUrl(report.getDataPoints().get(0).getxAxis().getModule().getName(), report.getId(), fileFormat)
					.append("?print=true");
			if(report.getDateRange() != null) {
				JSONObject dateRange = new JSONObject();
				dateRange.put("startTime", report.getDateRange().getStartTime());
				dateRange.put("endTime", report.getDateRange().getEndTime());
				dateRange.put("operatorId", report.getDateOperator());
				dateRange.put("value", report.getDateValue());
				url.append("&daterange=").append(ReportsUtil.encodeURIComponent(dateRange.toJSONString()));
			}
			String chartType = (String) context.get("chartType");
			if (chartType != null) {
				url.append("&charttype=").append(chartType);
			}
			
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(), url.toString(), fileFormat);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}
	
	private void setTableStateColumns (List<Map<String, Object>> columns, List<String> headerKeys) {
		headerKeys.forEach(header -> {
			Map<String, Object> column = new HashMap<>();
			column.put("name", header);
			if (header.contains("__baseline__")) {
				column.put("baseLine", true);
				column.put("baseLineId", Long.parseLong(header.substring(0, header.indexOf("__baseline__"))));
				column.put("dp", header.substring(header.indexOf("__baseline__") + 12));
			}
			else if (header.equalsIgnoreCase(ACTUAL_HEADER)) {
				column.put("displayName", ACTUAL_HEADER);
			}
			columns.add(column);
		});
	}
	
	private List<String> getTableHeaders(List<Map<String, Object>> columns, com.facilio.report.context.ReportContext report, Map<Long, ReportBaseLineContext> baseLineMap, Map<String, ReportDataPointContext> dataMap, ReportMode mode) throws Exception {
		List<String> headers = new ArrayList<>();
		if (mode == ReportMode.SERIES) {
			columns.forEach(col -> {
				String name = col.get("displayName") != null ? (String) col.get("displayName") : (String) col.get("name");
				headers.add(name);
				col.put("header", name);
			});
		}
		else {
			
			for(int i = 0, size = columns.size(); i < size; i++) {
				Map<String, Object> col = columns.get(i);
				if (i == 0) {
					String name;
					switch(mode) {
						case SITE:
						case BUILDING:
						case RESOURCE:
							name = ((String) col.get("name"));
							break;
						default:
							name = report.getDataPoints().get(0).getxAxis().getField().getDisplayName();
								
					}
					headers.add(name);
					col.put("header", name);
					continue;
				}
				boolean isBaseLine = col.containsKey("baseLine") && ((boolean)col.get("baseLine"));
				String colName = isBaseLine ? (String) col.get("dp") : ((String) col.get("name"));
				if ((String) col.get("displayName") != null) {
					headers.add((String) col.get("displayName"));
					col.put("header", col.get("displayName"));
				}
				else {
					ReportDataPointContext dataPoint = dataMap.get(colName);
					String unit = null;
					FacilioField field = dataPoint.getyAxis().getField();
					if (field instanceof NumberField) {
						unit = ((NumberField)field).getUnit();
					}
					StringBuilder builder = new StringBuilder(dataPoint.getName());
					builder.append((unit != null && !unit.isEmpty() ? " (" + unit + ")" : ""));
					if (isBaseLine) {
						builder.append(" - ").append(baseLineMap.get(col.get("baseLineId")).getBaseLine().getName());
					}
					String header = builder.toString();
					headers.add(header);
					col.put("header", header);
				}
			}
		}
		return headers;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> getTableData(List<Map<String, Object>> columns, Map<String, Map<String, Map<Object, Object>>> reportData, Set<Object> xValues, Map<Long, ReportBaseLineContext> baseLineMap,Map<String, ReportDataPointContext> dataMap, ReportMode mode) {
		List<Map<String, Object>> records = new ArrayList<>();
		if (mode == ReportMode.SERIES) {
			reportData.forEach((key, data) -> {
				Map<String, Object> newRow = new HashMap<>();
				columns.forEach(col -> {
					String columnName = (String) col.get("name");
					if (columnName.equals(SERIES_X_HEADER)) {
						newRow.put((String) col.get("header"), key);
					}
					else if (reportData.get(key).containsKey(columnName)){
						ReportDataPointContext dataPoint = dataMap.get(key);
						Object val = reportData.get(key).get(columnName).keySet().stream().findFirst().get();
						FacilioField field = null;
						try {
							field = dataPoint != null ? dataPoint.getyAxis().getField() : null;
						} catch (Exception e) {
							e.printStackTrace();
						}
						String unit = null;
						if (field instanceof NumberField) {
							unit = ((NumberField)field).getUnit();
						}
						if (reportData.get(key).containsKey(columnName) && reportData.get(key).get(columnName).containsKey(val)) {
							newRow.put((String) col.get("header"), reportData.get(key).get(columnName).get(val) + (unit != null ? " " + unit : ""));
						}
					}
				});
				records.add(newRow);
			});
		}
		else {
			
			String format = "EEEE, MMMM dd, yyyy hh:mm a";
			
			AggregateOperator aggr = report.getxAggrEnum();
			if (aggr != null && aggr instanceof DateAggregateOperator) {
				String dateFormat = ((DateAggregateOperator)aggr).getFormat();
				format = dateFormat != null ? dateFormat : format;
			}
			
			for(Object xValue: xValues) {
				String firstCol;
				switch (mode) {
				case TIMESERIES:
				case TIME_CONSOLIDATED:
				case TIME_SPLIT:
					firstCol =  DateTimeUtil.getFormattedTime((long)xValue, format);
					break;
				default:
					firstCol = (String) xValue;
					break;
				}
				Map<String, Object> newRow = new HashMap<>();
				newRow.put((String) columns.get(0).get("header"), firstCol);
				for(Map<String, Object> column : columns) {
					String name, bl;
					if (column.containsKey("baseLine") && ((boolean)column.get("baseLine"))) {
						name = (String) column.get("dp");
						ReportBaseLineContext baseContext =  baseLineMap.get(column.get("baseLineId"));
						bl = baseContext.getBaseLine().getName();
					}
					else {
						name = (String) column.get("name");
						bl = ACTUAL_HEADER.toLowerCase();
					}
					if (reportData.get(name) != null && reportData.get(name).containsKey(bl) && reportData.get(name).get(bl).containsKey(xValue) && reportData.get(name).get(bl).get(xValue) != null) {
						Object value = reportData.get(name).get(bl).get(xValue);
						ReportDataPointContext dataPoint = dataMap.get(name);
						if (dataPoint.getyAxis().getEnumMap() != null && dataPoint.getyAxis().getEnumMap().containsKey(value)) {
							value =dataPoint.getyAxis().getEnumMap().get(value);
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
		return url;
	}
	
	private String addAnalyticsConfigAndGetUrl(FileFormat fileFormat) {
		StringBuilder url = new StringBuilder();
		if(fileFormat == FileFormat.IMAGE) {
			url.append("/show");
		}
		return url.toString();
	}
}
