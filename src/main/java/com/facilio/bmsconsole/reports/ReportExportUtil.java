package com.facilio.bmsconsole.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;

public class ReportExportUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getDataInExportFormat(JSONArray reportData, ReportContext reportContext, Long baseLineComparisionDiff) throws Exception {
		
		List<String> headers = new ArrayList<>();
		
		List<Map<String, Object>> records = new ArrayList<>();
		
		String xAxisLabel = reportContext.getxAxisLabel();
		headers.add(xAxisLabel);
		
		for (int i = 0, len = reportData.size() ; i < len; i++ ) {
			Map<String, Object> data = new HashMap<>();
			if (data.get("label") == null) {
				continue;
			}

			Map<String, Object> fields = new HashMap<>();
			records.add(fields);

			Object rowLabel = data.get("label");
			if (baseLineComparisionDiff != null && rowLabel instanceof Long) {
				rowLabel = (Long)rowLabel + baseLineComparisionDiff;
			}

			int operationIdDate = -1;
			if (reportContext.getDateFilter() != null) {
				operationIdDate = reportContext.getDateFilter().getOperatorId();
			}
			
//			formatLabel(rowLabel, reportContext.getxAxisField(), null, operationIdDate);
			fields.put(xAxisLabel, rowLabel);

			Object value = data.get("value");
			if (value instanceof ArrayList) {
				((ArrayList)value).forEach(col -> {
					String subLabel = ((HashMap)col).get("label").toString();
					if (!headers.contains(subLabel)) {
						headers.add(subLabel);
					}
					fields.put(subLabel,((HashMap)col).get("value").toString());
				});
				fields.put("isArray", true);
			}
			else {
				String y1AxisLabel = reportContext.getY1AxisLabel();
				headers.add(reportContext.getY1AxisLabel());
				fields.put(y1AxisLabel, value);
			}
		}
		/*
			          row.orig_label = row.label
			          if (diff && typeof (row.label) === 'number') {
			            row.label = row.label + diff
			          }
			          row.orgLabel = row.label

			          row.label = this.formatLabel(row.label, reportContext.xAxisField, reportOptions.xaxis, operationIdDate)
			          if (Array.isArray(row.value)) {
			            for (let j = 0; j < row.value.length; j++) {
			              let col = row.value[j]
			              col.label = this.formatLabel(col.label, reportContext.groupByField, reportOptions.groupby, operationIdDate)
			              col.formatted_value = this.formatValue(col.value, reportOptions[yAxis])
			            }
			          }
			          else {
			            row.formatted_value = this.formatValue(row.value, reportOptions[yAxis])
			          }
			        }
			      }
			      */
	}
	
	public static Map<String, Object> getTabularReportData (JSONArray reportData, ReportContext reportContext, List<ReportColumnContext> reportColumns) throws Exception {
		List<String> headers = new ArrayList<String>();
		List<List> formattedData = new ArrayList<>();
		
		Map<Long, Object> rows = new HashMap<>();
		for(ReportColumnContext column: reportColumns) {
			if (column.getReportId() == reportContext.getId()) {
				headers.add(column.getReport().getxAxisLabel());
			}
			else {
				headers.add(column.getReport().getName());
			}
		}
		
		for (int i = 0, len = reportData.size() ; i < len; i++ ) {
			List row = (ArrayList) reportData.get(i);
			List<Object> newRow = new ArrayList<>();
			for (int j = 0, colCount = row.size(); j < colCount; j++) {
				ReportColumnContext column = reportColumns.get(j);
				if (row.get(j) == null) {
					newRow.add("");
				}
				else {
					ReportFieldContext field;
					AggregateOperator operator;
					if (column.getReportId() == reportContext.getId()) {
						field = column.getReport().getxAxisField();
						operator = column.getReport().getXAxisAggregateOpperator();
					}
					else {
						field = column.getReport().getY1AxisField();
						operator = column.getReport().getY1AxisAggregateOpperator();
					}
					Object value = formatValue(row.get(j), field, operator);
					newRow.add(value);
				}
			}
			formattedData.add(newRow);
		}
		
		Map<String, Object> table = new HashMap();
		table.put("headers", headers);
		table.put("records", formattedData);
		
		return table;
	}
	
	private static Object formatValue (Object value, ReportFieldContext field, AggregateOperator operator) throws Exception {
		if (field != null && field.getField().getDataTypeEnum() != null) {
			switch (field.getField().getDataTypeEnum()) {
				case DATE_TIME: {
					if (operator == DateAggregateOperator.MONTHANDYEAR) {
						return DateTimeUtil.getFormattedTime((long)value, "MMMM yyyy");
					}
					return DateTimeUtil.getFormattedTime((long)value);
				}
			}
		}
		return value;
	}
	
	private static String formatLabel(Object label,FacilioField axis,Object axisOptions,int operatorId) {
		if (label == null) {
	        return "Unknown";
	    }
		if (axis == null) {
			return label.toString();
		}
		
		return null;
	}
}
