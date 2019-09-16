package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.pdf.PdfUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.time.DateTimeUtil;

public class GetExportModuleReportFileCommand extends FacilioCommand {

private static final String ALIAS = "alias";
	
	private ReportContext report;
//	private ReportMode mode;
//	private Map<String, Object> dataMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Boolean isS3Url = (Boolean) context.get("isS3Url");
		if (isS3Url == null) {
			isS3Url = false;
		}
		
		report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String fileUrl = null;
		String fileName = "Report Data";
		if (StringUtils.isNotEmpty(report.getName())) {
			fileName = report.getName();
		}
		fileName += " - " + DateTimeUtil.getFormattedTime(System.currentTimeMillis(), "dd-MM-yyyy HH-mm");
		if(fileFormat != FileFormat.PDF && fileFormat != FileFormat.IMAGE) {
			List<String> headers = new ArrayList<>();
			
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			List<Map<String, Object>> records = getTableData(reportData, headers);

			Map<String, Object> table = new HashMap<String, Object>();
			table.put("headers", headers);
			table.put("records", records);
			fileUrl = ExportUtil.exportData(fileFormat, fileName, table, isS3Url);
		}
		else {
			StringBuilder url = getClientUrl(report.getDataPoints().get(0).getxAxis().getModule().getName(), report.getId(), fileFormat);
			String chartType = (String) context.get("chartType");
			if (chartType != null) {
				url.append("&charttype=").append(chartType);
			}
			Map<String, Object> params = (Map<String, Object>) context.get("exportParams");
			if (params != null) {
				for(Map.Entry<String, Object> param: params.entrySet()) {
					url.append("&").append(param.getKey()).append("=").append(param.getValue());
				}
			}
			
			fileUrl = PdfUtil.exportUrlAsPdf(url.toString(), isS3Url, fileName, fileFormat);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		context.put(FacilioConstants.ContextNames.FILE_NAME, fileName);
		
		return false;
	}
	
	private List<Map<String, Object>> setDataMapAndGetColumns () {
		List<String> currentHeaderKeys = new ArrayList<>();
		currentHeaderKeys.add(report.getxAlias() != null ? report.getxAlias() : "X");
		List<Map<String, Object>> columns = null;

//		report.getDataPoints().forEach(dp -> {
//			String alias = dp.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA);
//			currentHeaderKeys.add(alias);
//			dataMap.put(alias, dp);
//			if (report.getBaseLines() != null && dp.getTypeEnum() != DataPointType.DERIVATION) {
//				report.getBaseLines().stream().forEach(bl -> {
//					String blAlias = dp.getAliases().get(bl.getBaseLine().getName());
//					currentHeaderKeys.add(blAlias);
//					
//					Map<String, Object> blMap = new HashMap<>();
//					blMap.put("dpAlias", alias);
//					blMap.put("baseline", bl);
//					dataMap.put(blAlias, blMap);
//				});
//			}
//		});
//	    
//		if (tableState == null) {
//			columns = new ArrayList<>();
//			setTableStateColumns(columns, currentHeaderKeys);
//		}
//		else {
//			List<Map<String, Object>> stateColumns = (List<Map<String, Object>>) tableState.get("columns");
//			columns = stateColumns.stream().filter(col -> currentHeaderKeys.contains(col.get(ALIAS))).collect(Collectors.toList());
//			String xColAlias = currentHeaderKeys.get(0);
//			List<String> existingHeaders = stateColumns.stream().map(col -> col.get(ALIAS).toString()).collect(Collectors.toList());
//			if (!stateColumns.get(0).get(ALIAS).equals(xColAlias)) {
//				int xColIdx = existingHeaders.indexOf(currentHeaderKeys.get(0));
//				if (xColIdx != -1) {
//					Map<String, Object> xCol = stateColumns.remove(xColIdx);
//					stateColumns.add(0, xCol);
//				}
//				else {
//					setTableStateColumns(columns, Collections.singletonList(xColAlias), true);
//				}
//			}
//			if (columns.size() != stateColumns.size()) {
//				List<String> remainingHeaders = currentHeaderKeys.stream().filter(header -> !existingHeaders.contains(header)).collect(Collectors.toList());
//				setTableStateColumns(columns, remainingHeaders);
//			}
//		}
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
	
//	private List<String> getTableHeaders(List<Map<String, Object>> columns) throws Exception {
//		List<String> headers = new ArrayList<>();
//		for(int i = 0, size = columns.size(); i < size; i++) {
//			Map<String, Object> col = columns.get(i);
//			if (i == 0) {
//				String name;
//				switch(mode) {
//					case SITE:
//						name = "Site";
//	    					break;
//					case BUILDING:
//						name = "Building";
//	    					break;
//					case RESOURCE:
//						name = "Asset";
//	    					break;
//					default:
//						name = report.getDataPoints().get(0).getxAxis().getLabel();
//							
//				}
//				headers.add(name);
//				col.put("header", name);
//				continue;
//			}
//			if ((String) col.get("displayName") != null) {
//				headers.add((String) col.get("displayName"));
//				col.put("header", col.get("displayName"));
//			}
//			else {
////				ReportDataPointContext dataPoint;
////				ReportBaseLineContext baseLine = null;
////				Object pointObj = dataMap.get(col.get(ALIAS));
////				if (pointObj instanceof ReportDataPointContext) {
////					dataPoint = (ReportDataPointContext) pointObj;
////				}
////				else {
////					String alias = (String) ((Map<String, Object>) pointObj).get("dpAlias");
////					dataPoint = (ReportDataPointContext) dataMap.get(alias);
////					baseLine = (ReportBaseLineContext) ((Map<String, Object>) pointObj).get("baseline");
////				}
////				
////				StringBuilder builder = new StringBuilder(dataPoint.getName());
////				if (baseLine != null) {
////					builder.append(" - ").append(baseLine.getBaseLine().getName());
////				}
////				String unit = dataPoint.getyAxis().getUnitStr();
////				builder.append((unit != null && !unit.isEmpty() ? " (" + unit + ")" : ""));
////				if ( (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)){
////					if (report.getxAggrEnum() != null && report.getxAggrEnum() != CommonAggregateOperator.ACTUAL) {
////						if (col.containsKey("enumMode") && (int)col.get("enumMode") > 0 ) {
////							EnumMode enumMode = EnumMode.valueOf((int)col.get("enumMode"));
////							col.put("enumModeEnum", enumMode);
////							if (enumMode == EnumMode.GRAPH) {
////								col.put("enumModeEnum", EnumMode.DURATION);
////							}
////							else if (enumMode == EnumMode.PERCENT){
////								builder.append(" (%)");
////							}
////						}
////						else {
////							col.put("enumModeEnum", EnumMode.DURATION);
////						}
////					}
////				}
////				String header = builder.toString();
////				headers.add(header);
////				col.put("header", header);
//			}
//		}
//		return headers;
//	}
	
	private List<Map<String, Object>> getTableData(JSONObject reportData, List<String> columns) throws Exception {
		List<Map<String, Object>> records = new ArrayList<>();
		String format = "EEEE, MMMM dd, yyyy hh:mm a";
		
		String xAlias = report.getxAlias() != null ? report.getxAlias() : "X";
		
		ReportDataPointContext dataPoint = report.getDataPoints().get(0);
		FacilioModule module = report.getModule();
		ReportFieldContext xAxisReportField = dataPoint.getxAxis();
		List<ReportGroupByField> groupByFields = dataPoint.getGroupByFields();
		
		AggregateOperator xAggr = report.getxAggrEnum();
		if (xAggr != null && xAggr instanceof DateAggregateOperator) {
			format = ((DateAggregateOperator) xAggr).getFormat();
		}
		
		columns.add(handleXAxisLabel(module, xAxisReportField));

		Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
		if (CollectionUtils.isNotEmpty(data)) {
			for (Map<String, Object> row : data) {
				Map<String, Object> newRow = new HashMap<>();
				records.add(newRow);
				
				Object value = row.get(xAlias);
				if (value == null) {
					continue;
				}
				
				newRow.put(handleXAxisLabel(module, xAxisReportField), handleData(xAxisReportField, value, format));
				
				if (CollectionUtils.isNotEmpty(groupByFields)) {
					for (ReportGroupByField groupByField : groupByFields) {
						String groupByAlias = groupByField.getAlias();
						List<Map<String, Object>> list = (List<Map<String, Object>>) row.get(groupByAlias);
						for (Map<String, Object> map : list) {
							Object groupByValue = map.get(groupByAlias);
							groupByValue = handleData(groupByField, groupByValue, format);
							if (groupByValue == null) {
								continue;
							}
							addColumn(columns, String.valueOf(groupByValue));
							
							ReportYAxisContext getyAxis = dataPoint.getyAxis();
							String yAlias = dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA);
							newRow.put(String.valueOf(groupByValue), handleData(getyAxis, map.get(yAlias), format));
						}
					}
				} else {
					ReportYAxisContext getyAxis = dataPoint.getyAxis();
					String yAlias = dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA);
					String yAxisLable = handleYAxisLabel(module, getyAxis);
					addColumn(columns, yAxisLable);
					newRow.put(yAxisLable, handleData(getyAxis, row.get(yAlias), format));
				}
			}
			
			if (CollectionUtils.isNotEmpty(groupByFields)) {
				for (Map<String, Object> r : records) {
					Collection<String> disjunction = CollectionUtils.disjunction(columns, r.keySet());
					for (String s : disjunction) {
						r.put(s, 0);
					}
				}
			}
		}
		return records;
	}
	
	private String handleXAxisLabel(FacilioModule facilioModule, ReportFieldContext reportField) {
		if (reportField.getFieldName().equals("siteId")) {
			return "Site";
		} else if (reportField.getFieldName().equals("actualWorkStart")) {
			return "Start Time";
		} else if (reportField.getFieldName().equals("actualWorkDuration")) {
			return "Work Duration";
		} else if (reportField.getField() instanceof LookupField) {
			FacilioModule lookupModule = ((LookupField) reportField.getField()).getLookupModule();
			if (lookupModule.getName().equals("resource") || lookupModule.getName().equals("basespace")) {
				return report.getxAggrEnum().getStringValue();
			}
		}
		return reportField.getLabel();
	}
	
	private String handleYAxisLabel(FacilioModule facilioModule, ReportYAxisContext getyAxis) {
		if (getyAxis.getFieldName().toLowerCase().equals("id")) {
			String displayName = facilioModule.getDisplayName();
			return "Number of " + displayName;
		}
		return getyAxis.getLabel();
	}

	private void addColumn(List<String> columns, String groupByField) {
		if (!columns.contains(groupByField)) {
			columns.add(groupByField);
		}
	}

	private Object handleData(ReportFieldContext reportField, Object value, String format) {
		// TODO Auto-generated method stub
		FacilioField field = reportField.getField();
		switch (field.getDataTypeEnum()) {
		case LOOKUP:
			value = reportField.getLookupMap().get(value);
			break;
			
		case ENUM:
		case BOOLEAN:
			value = reportField.getEnumMap().get(value);
			break;
			
		case DATE:
		case DATE_TIME:
			value = DateTimeUtil.getFormattedTime((long)value, format);
			break;
			
		case NUMBER:
			if (field.getName().equals("siteId")) {
				value = reportField.getLookupMap().get(value);
			}
			break;

		default:
			break;
		}
		return value;
	}

//	@SuppressWarnings("unchecked")
//	private List<Map<String, Object>> getTableData(List<Map<String, Object>> columns, JSONObject reportData, boolean b) {
//		List<Map<String, Object>> records = new ArrayList<>();
//		FieldType xDataType = report.getDataPoints().get(0).getxAxis().getDataTypeEnum();
//		boolean isTimeSeries = false;
//		if (xDataType == FieldType.DATE || xDataType == FieldType.DATE_TIME) {
//			isTimeSeries = true;
//		}
//		String format = "EEEE, MMMM dd, yyyy hh:mm a";
//		
//		AggregateOperator aggr = report.getxAggrEnum();
//		if (isTimeSeries && aggr != null && aggr instanceof DateAggregateOperator) {
//			String dateFormat = ((DateAggregateOperator)aggr).getFormat();
//			format = dateFormat != null ? dateFormat : format;
//		}
//		
//		List<Map<String, Object>> data = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
//		if (data != null && !data.isEmpty()) {
//			for(Map<String, Object> row: data) {
//				Map<String, Object> newRow = new HashMap<>();
//				for(int i = 0; i < columns.size(); i++) {
//					Map<String, Object> column = columns.get(i);
//					Object value = row.get(column.get(ALIAS));
//					if (value != null) {
//						if (i == 0) {
//							if (isTimeSeries) {
//								value = DateTimeUtil.getFormattedTime((long)value, format);
//							}
//							newRow.put((String) columns.get(0).get("header"), value);
//							continue;
//						}
//						
//						ReportDataPointContext dataPoint;
//						Object pointObj = dataMap.get(column.get(ALIAS));
//						if (pointObj instanceof ReportDataPointContext) {
//							dataPoint = (ReportDataPointContext) pointObj;
//						}
//						else {
//							String alias = (String) ((Map<String, Object>) pointObj).get("dpAlias");
//							dataPoint = (ReportDataPointContext) dataMap.get(alias);
//						}
//						switch(dataPoint.getyAxis().getDataTypeEnum()) {
//							case BOOLEAN:
//							case ENUM:
//								EnumVal enumVal = (EnumVal) value;
//								value = null;
//								if (column.get("enumModeEnum") == null) {	// High-res
//									List<SimpleEntry<Long, Integer>> timeline = enumVal.getTimeline();
//									if (timeline != null && !timeline.isEmpty()) {
//										value = dataPoint.getyAxis().getEnumMap().get(timeline.get(0).getValue());
//									}
//								}
//								else if (column.get("enumModeEnum") == EnumMode.PERCENT) {
//									Map<Integer, Long> duration = enumVal.getDuration();
//									if (duration != null) {
//										long total = duration.values().stream().reduce(0L, (prev, key) -> prev + key);
//										StringBuilder percent = new StringBuilder(); 
//										for (Entry<Integer, Long> entry : duration.entrySet()) {
//											percent.append(dataPoint.getyAxis().getEnumMap().get(entry.getKey()))
//												.append(": ")
//												.append(ReportsUtil.roundOff(entry.getValue() / total * 100, 2))
//												.append("\n");
//										}
//										value = percent.toString();
//									}
//								}
//								else {	// DURATION
//									Map<Integer, Long> duration = enumVal.getDuration();
//									if (duration != null) {
//										StringBuilder durationVal = new StringBuilder();
//										for (Entry<Integer, Long> entry : duration.entrySet()) {
//											durationVal.append(dataPoint.getyAxis().getEnumMap().get(entry.getKey()))
//											.append(": ")
//											.append(entry.getValue())
//											.append("\n");
//										}
//										value = durationVal.toString();
//									}
//								}
//								break;
//						}
//						newRow.put((String) column.get("header"), value);
//					}
//				}
//				records.add(newRow);
//			}
//		}
//		return records;
//	}
	
	private StringBuilder getClientUrl(String moduleName, Long reportId, FileFormat fileFormat) {
		// moduleName = FacilioConstants.ContextNames.ENERGY_DATA_READING;	// Temp
		StringBuilder url = new StringBuilder(FacilioProperties.getConfig("clientapp.url")).append("/app/");
		if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			url.append("wo");
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ALARM)) {
			url.append("fa");
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
			url.append("at");
		}
		else {
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
