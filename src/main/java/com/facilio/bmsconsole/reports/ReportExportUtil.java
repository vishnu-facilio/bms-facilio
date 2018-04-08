package com.facilio.bmsconsole.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ReportExportUtil {
	
	private static final String UNKNOWN = "Unknown"; 

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getDataInExportFormat(JSONArray reportData1, ReportContext reportContext, Long baseLineComparisionDiff) throws Exception {
		
		List<String> headers = new ArrayList<>();
		Map<String, List<Long>> modVsIds = new HashMap<String, List<Long>>();
		Map<String, FacilioField> headerFields = new HashMap<String, FacilioField>();
		List<String> subLabels = new ArrayList();
		boolean isGroup = false;
		
		List<Map<String, Object>> records = new ArrayList<>();
		
		String xAxisLabel = reportContext.getxAxisLabel();
		headers.add(xAxisLabel);
		
		FacilioField xAxisField = reportContext.getxAxisField().getField();
		headerFields.put(xAxisLabel, xAxisField);
		
		List<Map<String, Object>> reportData = (List<Map<String, Object>>) reportData1.stream().filter(data -> ((Map) data).get("label") != null).collect(Collectors.toList());
		
		for (int i = 0, len = reportData.size() ; i < len; i++ ) {
			Map<String, Object> data = (Map<String, Object>) reportData.get(i);

			Map<String, Object> fields = new HashMap<>();
			records.add(fields);

			Object rowLabel = data.get("label");
			if (baseLineComparisionDiff != null && rowLabel instanceof Long) {
				rowLabel = (Long)rowLabel + baseLineComparisionDiff;
			}

			rowLabel = formatLabel(rowLabel, xAxisField, modVsIds, reportContext.getXAxisAggregateOpperator(), reportContext);
			fields.put(xAxisLabel, rowLabel);
			
			String unit = reportContext.getY1AxisUnit();
			FieldType valueFieldType = reportContext.getY1AxisField() != null && reportContext.getY1AxisField().getField().getDataTypeEnum() != null ? 
					reportContext.getY1AxisField().getField().getDataTypeEnum() : null;
			
			Object value = data.get("value");
			if (value instanceof List) {
				((List)value).forEach(col -> {
					Object label = ((HashMap)col).get("label");
					String subLabel;
					if (label == null) {
						subLabel = UNKNOWN;
					}
					else {
						subLabel = ((HashMap)col).get("label").toString();
					}
					if (!subLabels.contains(subLabel)) {
						subLabels.add(subLabel);
					}
					Object fieldVal = ((HashMap)col).get("value").toString();
					fieldVal = formatValue(fieldVal, valueFieldType, reportContext.getY1AxisAggregateOpperator(), reportContext);
					fields.put(subLabel,fieldVal);
				});
				isGroup = true;
			}
			else {
				String y1AxisLabel = reportContext.getY1AxisLabel();
				if (!headers.contains(y1AxisLabel)) {
					headers.add(y1AxisLabel);
				}
				value = formatValue(value, valueFieldType, reportContext.getY1AxisAggregateOpperator(), reportContext);
				fields.put(y1AxisLabel, value);
			}
		}
		
		if (isGroup) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField serviceField = null;
			for (int i = 0, len = records.size() ; i < len; i++ ) {
				Map<String, Object> record = records.get(i);
				for(int j = 0, sublen=subLabels.size() ; j < sublen; j++) {
					String sublabel = subLabels.get(j);
					if (i == 0) {
						if (reportContext.getGroupByField() != null) {
							FacilioField subField = reportContext.getGroupByField().getField();
							sublabel = formatLabel(sublabel, subField, modVsIds, null, reportContext);
							if (subField.getColumnName().equals("PARENT_METER_ID") || subField.getColumnName().equals("BUILDING_ID")) {
								if (subField.getDisplayName().equals("Building") || subField.getDisplayName().equals("Parent")) {
						        }
						        else if (subField.getDisplayName().equals("Service")) {
						        		if (serviceField == null) {
						        			serviceField =  bean.getField("purpose", FacilioConstants.ContextNames.ENERGY_METER);
						        		}
						        		subField = serviceField;
						        }
							}
							if (!headers.contains(sublabel)) {
								headers.add(sublabel);
								headerFields.put(sublabel, subField);
							}
						}
					}
					if (!record.containsKey(sublabel)) {
						record.put(sublabel, 0L);
					}
				}
			}
			
		}
		
		Map<String, Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("headerFields", headerFields);
		table.put("records", records);
		table.put("modVsIds", modVsIds);
		
		return table;
	}
	
	public static Map<String, Object> getTabularReportData (JSONArray reportData, ReportContext reportContext, List<ReportColumnContext> reportColumns) throws Exception {
		List<String> headers = new ArrayList<String>();
		List<List<Object>> formattedData = new ArrayList<>();
		
		for(ReportColumnContext column: reportColumns) {
			if (column.getReportId() == reportContext.getId()) {
				headers.add(column.getReport().getxAxisLabel());
			}
			else {
				headers.add(column.getReport().getName());
			}
		}
		
		for (int i = 0, len = reportData.size() ; i < len; i++ ) {
			List<Object> row = (ArrayList) reportData.get(i);
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
					Object value = formatValue(row.get(j), field.getField().getDataTypeEnum(), operator, null);
					newRow.add(value);
				}
			}
			formattedData.add(newRow);
		}
		
		Map<String, Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("records", formattedData);
		
		return table;
	}
	
	private static Object formatValue (Object value, FieldType dataType, AggregateOperator operator, ReportContext reportContext) {
		if (dataType != null) {
			switch (dataType) {
				case DATE:
				case DATE_TIME: {
					String  format = "EEEE, MMMM dd, yyyy hh:mm a";
					if (!reportContext.getIsHighResolutionReport()) {
						String dateFormat = ((DateAggregateOperator)operator).getFormat();
						format = dateFormat != null ? dateFormat : format;
					}
					return DateTimeUtil.getFormattedTime((long)value, format);
				}
				case DECIMAL: {
					return ReportsUtil.roundOff(Double.parseDouble(value.toString()), 2);
				}
				default:
					break;
			}
		}
		return value;
	}
	
	private static String formatLabel(Object label,FacilioField field, Map<String, List<Long>> modVsIds, AggregateOperator operator, ReportContext reportContext) {
		if (label == null || label.equals(UNKNOWN)) {
	        return UNKNOWN;
	    }
		
		if (field == null) {
			return label.toString();
		}
		
		FieldType dataType = field.getDataTypeEnum();
		if (dataType == FieldType.DATE ||dataType == FieldType.DATE_TIME) {
			try {
				return formatValue(label, dataType, operator, reportContext).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (field.getColumnName().equals("PARENT_METER_ID") || field.getColumnName().equals("BUILDING_ID")) {
			if (field.getDisplayName().equals("Building") || field.getDisplayName().equals("Parent")) {
				
	        }
	        else if (field.getDisplayName().equals("Service")) {
	        		try {
	        			if (!(label instanceof Long)) {
	    					label = Long.parseLong(label.toString());
	    				}
	    	        		String moduleName = FacilioConstants.ContextNames.ENERGY_METER_PURPOSE;
	    	        		List<Long> ids= modVsIds.get(moduleName);
	    	        		if(ids==null) {
	    	    				ids=new ArrayList<Long>();
	    	    				modVsIds.put(moduleName, ids);
	    	    			}
	    	        		ids.add((Long) label);
	        		}
	        		catch (NumberFormatException e) {
	        			return label.toString();
	        		}
	        }
		}
		
		if (dataType==FieldType.LOOKUP) {
			if (!(label instanceof Long)) {
				label = Long.parseLong(label.toString());
			}
			LookupField lookupField= (LookupField)field;
			String moduleName=lookupField.getSpecialType();
			if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
				moduleName= lookupField.getLookupModule().getName();
			}
			List<Long> ids= modVsIds.get(moduleName);
			if(ids==null) {
				ids=new ArrayList<Long>();
				modVsIds.put(moduleName, ids);
			}
			ids.add((Long) label);
		}
		
		return label.toString();
	}
	
}
