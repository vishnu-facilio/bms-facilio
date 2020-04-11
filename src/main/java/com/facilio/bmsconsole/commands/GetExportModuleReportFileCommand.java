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

import com.facilio.bmsconsole.reports.ReportExportUtil;
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
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
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
			StringBuilder url = ReportExportUtil.getClientUrl(module, fileFormat, report, context);
			fileUrl = PdfUtil.exportUrlAsPdf(url.toString(), isS3Url, fileName, fileFormat);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		context.put(FacilioConstants.ContextNames.FILE_NAME, fileName);
		
		return false;
	}
	
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

}
