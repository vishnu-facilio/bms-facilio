package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class GetExportReportDataCommand implements Command {
	
	private static final String SERIES_X_HEADER = "Data Point";
	private static final String ACTUAL_HEADER = "Actual";

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Set<Object> xValues = (Set<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
		Map<String, Map<String, Map<Object, Object>>> reportData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportMode mode = (ReportMode)context.get(FacilioConstants.ContextNames.REPORT_MODE);
		
		Map<Long, ReportBaseLineContext> baseLineMap = null;
		
		List<String> currentHeaderKeys = new ArrayList<>();
		Map<String, Map<String, Object>> rows = new LinkedHashMap<>();
	    if (mode == ReportMode.SERIES) {
	    	  currentHeaderKeys.add(SERIES_X_HEADER);
	    	  currentHeaderKeys.addAll(reportData.values().stream().findFirst().get().keySet());
	    }
	    else {
	    		currentHeaderKeys.add(report.getDataPoints().get(0).getxAxis().getField().getName());
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
		if (report.getTabularState() != null) {
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
				setTableStateColumns(stateColumns, remainingHeaders);
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
			fileUrl = ExportUtil.exportData(fileFormat, "Report Data", table);
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
					String name = report.getDataPoints().get(0).getxAxis().getField().getDisplayName();
					headers.add(name);
					col.put("header", name);
					continue;
				}
				boolean isBaseLine = col.containsKey("baseLine") && ((boolean)col.get("baseLine"));
				String colName = isBaseLine ? (String) col.get("dp") : ((String) col.get("name"));
				if ((String) col.get("displayName") != null) {
					headers.add((String) col.get("displayName"));
					col.put("header", (String) col.get("displayName"));
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
						ReportDataPointContext dataPoint = dataMap.get(columnName);
						Object val = reportData.get(key).get(columnName).keySet().stream().findFirst().get();
						FacilioField field = null;
						try {
							field = dataPoint != null ? dataPoint.getyAxis().getField() : null;
						} catch (Exception e) {
							e.printStackTrace();
						}
						String unit = "";
						if (field instanceof NumberField) {
							unit = ((NumberField)field).getUnit();
						}
						if (reportData.get(key).containsKey(columnName) && reportData.get(key).get(columnName).containsKey(val)) {
							newRow.put((String) col.get("header"), reportData.get(key).get(columnName).get(val) + " " + unit);
						}
					}
				});
				records.add(newRow);
			});
		}
		else {
			for(Object xObj: xValues) {
				long x = (long)xObj;
				Map<String, Object> newRow = new HashMap<>();
				for(int i = 0, size = columns.size(); i < size; i++) {
					Map<String, Object> column = columns.get(i);
					if (i == 0) {
						String  format = "MMMM dd, yyyy hh:mm a";
						newRow.put((String) column.get("header"), DateTimeUtil.getFormattedTime(x, format));
						continue;
					}
					String name, bl;
					long xTime;
					if (column.containsKey("baseLine") && ((boolean)column.get("baseLine"))) {
						name = (String) column.get("dp");
						ReportBaseLineContext baseContext =  baseLineMap.get(column.get("baseLineId"));
						bl = baseContext.getBaseLine().getName();
						xTime = x - baseContext.getDiff();
					}
					else {
						name = (String) column.get("name");
						bl = ACTUAL_HEADER.toLowerCase();
						xTime = x;
					}
					if (reportData.get(name) != null && reportData.get(name).containsKey(bl) && reportData.get(name).get(bl).containsKey(xTime) && reportData.get(name).get(bl).get(xTime) != null) {
						newRow.put((String) column.get("header"), reportData.get(name).get(bl).get(xTime));
					}
				}
				records.add(newRow);
			};
		}
		return records;
	}

}
