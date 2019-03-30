package com.facilio.bmsconsole.reports;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.ReportContext.ReportChartType;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import java.util.*;
import java.util.stream.Collectors;

public class ReportExportUtil {
	
	private static final String UNKNOWN = "Unknown";

	private static Logger log = LogManager.getLogger(ReportExportUtil.class.getName());

	public static Map<String, Object> getDataInExportFormat(JSONArray reportData, ReportContext reportContext, Long baseLineComparisionDiff,ReportSpaceFilterContext reportSpaceFilterContext, JSONArray dateFilter) throws Exception {
		List<Map<String, Object>> reportDatas = new ArrayList<>();
		
		Map<String, Object> rdata = new HashMap<>();
		rdata.put("reportData", reportData);
		rdata.put("reportContext", reportContext);
		rdata.put("baseLineComparisionDiff", baseLineComparisionDiff);
		rdata.put("title", reportContext.getY1AxisField().getFieldLabel());
		reportDatas.add(rdata);
		
		boolean isMulti = reportContext.getReportChartType() != null && reportContext.getReportChartType() == ReportChartType.TIMESERIES;
		if (isMulti) {
			fetchMultiData(reportContext, reportDatas, reportSpaceFilterContext, dateFilter);
		}
		
		return getExportData(reportDatas, isMulti);
	}
	
	private static Map<String, Object> getExportData(List<Map<String, Object>> reportDatas, boolean isMulti) throws Exception {
		List<String> headers = new ArrayList<>();
		Map<String, FacilioField> headerFields = new HashMap<String, FacilioField>();
		List<Map<String, Object>> records = new ArrayList<>();
		Map<String, List<Long>> modVsIds = new HashMap<String, List<Long>>();
		
		Map<String, Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("headerFields", headerFields);
		table.put("records", records);
		table.put("modVsIds", modVsIds);
		
		String xAxisLabel = null;
		Map<String, Map<String, Object>> rows = new LinkedHashMap<>();
		
		for(Map<String, Object> data: reportDatas) {
			JSONArray reportData = (JSONArray) data.get("reportData");
			ReportContext reportContext = (ReportContext) data.get("reportContext");
			Long baseLineComparisionDiff = null;
			if (data.containsKey("baseLineComparisionDiff")) {
				baseLineComparisionDiff = (Long) data.get("baseLineComparisionDiff");
			}
			
			if (xAxisLabel == null) {
				xAxisLabel = reportContext.getxAxisLabel();
				FacilioField xAxisField = reportContext.getxAxisField().getField();
				headers.add(xAxisLabel);
				headerFields.put(xAxisLabel, xAxisField);
			}
			else {
				xAxisLabel = reportContext.getxAxisLabel();
			}
			
			if (isMulti) {
				String header = (String) data.get("title");
				headers.add(header);
				 
				records = new ArrayList<>();
				table.put("records", records);
				 
				setReportData(reportData, reportContext, baseLineComparisionDiff, table, false);
				
				for (int j = 0, len = records.size() ; j < len; j++ ) {
					Map<String, Object> record = records.get(j);
					String rowLabel = record.get(xAxisLabel).toString();
					Map<String, Object> row;
					if (!rows.containsKey(rowLabel)) {
						rows.put(rowLabel, record);
					}
					
					row = rows.get(rowLabel);
					String y1AxisLabel = reportContext.getY1AxisLabel();
					Object value = record.get(y1AxisLabel);
					if (!y1AxisLabel.equals(header)) {
						row.remove(y1AxisLabel);
					}
					row.put(header, value);
				}
			}
			else {
				setReportData(reportData, reportContext, baseLineComparisionDiff, table, true);
			}
		}
		
		if (isMulti) {
			records = rows.values().stream()
					.collect(Collectors.toList());
			table.put("records", records);
		}
		
		if (records != null && !records.isEmpty()) {
			try {
				records.sort((m1,m2) -> {
					Long d1 = Long.parseLong(m1.get("rawLabel").toString());
					Long d2 = Long.parseLong(m2.get("rawLabel").toString());
					if(d1 == d2){
				         return 0;
				    }
					return d1 < d2 ? -1 : 1;
				});
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
			}
		}
		
		return table;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setReportData(JSONArray reportData1, ReportContext reportContext, Long baseLineComparisionDiff, Map<String, Object> table, boolean setHeader) throws Exception {
		List<String> headers = (List<String>) table.get("headers");
		Map<String, FacilioField> headerFields = (Map<String, FacilioField>) table.get("headerFields");
		List<Map<String, Object>> records = (List<Map<String, Object>>) table.get("records");
		Map<String, List<Long>> modVsIds = (Map<String, List<Long>>) table.get("modVsIds");
		
		List<String> subLabels = new ArrayList();
		boolean isGroup = false;
		
		List<Map<String, Object>> reportData = (List<Map<String, Object>>) reportData1.stream().filter(data -> ((Map) data).get("label") != null).collect(Collectors.toList());
		
		for (int i = 0, len = reportData.size() ; i < len; i++ ) {
			Map<String, Object> data = reportData.get(i);

			Map<String, Object> fields = new HashMap<>();
			records.add(fields);

			Object rowLabel = data.get("label");
			if (baseLineComparisionDiff != null && rowLabel instanceof Long) {
				rowLabel = (Long)rowLabel + baseLineComparisionDiff;
			}
			fields.put("rawLabel", rowLabel);

			String xAxisLabel = reportContext.getxAxisLabel();
			FacilioField xAxisField = reportContext.getxAxisField().getField();
			rowLabel = formatLabel(rowLabel, xAxisField, modVsIds, reportContext.getXAxisAggregateOpperator(), reportContext);
			fields.put(xAxisLabel, rowLabel);
			
			reportContext.getY1AxisUnit();
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
				if (setHeader && !headers.contains(y1AxisLabel)) {
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
							if (setHeader && !headers.contains(sublabel)) {
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
	}
	
	private static void fetchMultiData(ReportContext reportContext, List<Map<String, Object>> reportDatas, ReportSpaceFilterContext reportSpaceFilterContext, JSONArray dateFilter) throws Exception {
		if ((reportContext.getComparingReportContexts() != null && !reportContext.getComparingReportContexts().isEmpty()) || (reportContext.getBaseLineContexts() != null && !reportContext.getBaseLineContexts().isEmpty())) {
			List<Map<String, Object>> reportParams = new ArrayList<>();
			
			if (reportContext.getBaseLineContexts() != null) {
				for(BaseLineContext baseContext: reportContext.getBaseLineContexts()) {
					Map<String, Object> params = new HashMap<>();
					params.put("reportId", reportContext.getId());
					params.put("baseLineId", baseContext.getId());
					params.put("baseLineName", baseContext.getName());
					if(reportSpaceFilterContext != null) {
						ReportSpaceFilterContext spaceContext = new ReportSpaceFilterContext();
						spaceContext.setBuildingId(reportSpaceFilterContext.getBuildingId());
						params.put("reportSpaceFilterContext", spaceContext);
					}
					reportParams.add(params);
					
					if (reportContext.getComparingReportContexts() != null) {
						for(ReportContext compContext: reportContext.getComparingReportContexts()) {
	                      if (compContext.getExcludeBaseline()) {
	                        continue;
	                      }
	                      Map<String, Object> params2 = new HashMap<>();
	                      params2.put("reportId", compContext.getId());
	                      params2.put("baseLineId", baseContext.getId());
	                      params2.put("baseLineName", baseContext.getName());
	                      reportParams.add(params2);
	                    }
	                  }
				}
			}
			if (reportContext.getComparingReportContexts() != null) {
				for(ReportContext compContext: reportContext.getComparingReportContexts()) {
					Map<String, Object> params = new HashMap<>();
					params.put("reportId", compContext.getId());
					reportParams.add(params);
				}
//				reportdIds.addAll(reportContext.getComparingReportContexts().stream().map(ReportContext::getId).collect(Collectors.toList()));
			}
			
			for(int i = 0; i < reportParams.size(); i++) {
				Map<String, Object> params = reportParams.get(i);
				DashboardAction action = new DashboardAction();
				action.setReportId((Long) params.get("reportId"));
				if (params.containsKey("baseLineId")) {
					action.setBaseLineId((long) params.get("baseLineId"));
				}
				if (params.containsKey("reportSpaceFilterContext")) {
					action.setReportSpaceFilterContext((ReportSpaceFilterContext) params.get("reportSpaceFilterContext"));
				}
				if (dateFilter != null) {
					action.setDateFilter(dateFilter);
				}
				action.getData();
				
				Map<String, Object> rdata = new HashMap<>();
				rdata.put("reportData", action.getReportData());
				rdata.put("reportContext", action.getReportContext());
				rdata.put("baseLineComparisionDiff", action.getBaseLineComparisionDiff());
				String title = action.getReportContext().getName();
				if (params.containsKey("baseLineName")) {
					title = (String) params.get("baseLineName");
				}
				rdata.put("title", title);
				reportDatas.add(rdata);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
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
			List<Object> row = (ArrayList<Object>) reportData.get(i);
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
					Object value = formatValue(row.get(j), field.getField().getDataTypeEnum(), operator, reportContext);
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
	
	public static Map<String, Object> getAnalyticsData(List<Map<String, Object>> exportDataList, Map<String, Object> analyticsConfig) throws Exception {
		JSONArray dateFilter = null;
		if (analyticsConfig.containsKey("dateFilter")) {
			dateFilter = (JSONArray) analyticsConfig.get("dateFilter");
		}
		boolean excludeWeekends = false;
		if(analyticsConfig.containsKey("excludeWeekends")) {
			excludeWeekends = (boolean) analyticsConfig.get("excludeWeekends");
		}
		boolean cost = false;
		if(analyticsConfig.containsKey("cost")) {
			cost = (boolean) analyticsConfig.get("cost");
		}
		
		List<Map<String, Object>> reportDatas = new ArrayList<>();
		FacilioModule module = null;
		for(Map<String, Object> data: exportDataList) {
			DashboardAction action = new DashboardAction();
			action.setDateFilter(dateFilter);
			action.setParentId((long) data.get("parentId"));
			action.setReadingFieldId((long) data.get("readingFieldId"));
			int xAggr = Integer.parseInt(data.get("xAggr").toString());
			action.setxAggr(xAggr);
			int yAggr = Integer.parseInt(data.get("yAggr").toString());
			action.setyAggr(yAggr);
			if (data.containsKey("baseLineId")) {
				action.setBaseLineId((long) data.get("baseLineId"));
			}
			action.setExcludeWeekends(excludeWeekends);
			action.setCost(cost);
			if (data.containsKey("derivation")) {
				action.setDerivation(FieldUtil.getAsBeanFromMap((Map<String, Object>)data.get("derivation"), DerivationContext.class));
			}
			
			action.getReadingReportData();
			Map<String, Object> exportData = new HashMap<>();
			exportData.put("reportData", action.getReportData());
			exportData.put("reportContext", action.getReportContext());
			exportData.put("baseLineComparisionDiff", action.getBaseLineComparisionDiff());
			exportData.put("title", data.get("title"));
			reportDatas.add(exportData);
			
			if (module == null) {
				module = action.getReportModule();
			}
		}
		
		Map<String, Object> table = getExportData(reportDatas, true);
		table.put("module", module);
		return table;
	}
	
	private static Object formatValue (Object value, FieldType dataType, AggregateOperator operator, ReportContext reportContext) {
		if (dataType != null && value != null) {
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
		else if (dataType == FieldType.DECIMAL || dataType == FieldType.NUMBER) {
			value = 0;
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
				log.info("Exception occurred ", e);
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
