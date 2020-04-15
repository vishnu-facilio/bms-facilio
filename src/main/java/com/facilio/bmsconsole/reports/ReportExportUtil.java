package com.facilio.bmsconsole.reports;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.pdf.PdfUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportTemplateContext;

public class ReportExportUtil {
	
	private static Logger log = LogManager.getLogger(ReportExportUtil.class.getName());
	static Map<String, String> moduleVsRoute = new HashMap<>();
	static {
		Map<String, String> moduleVsRoute = new HashMap<>();
		moduleVsRoute.put(ContextNames.ASSET, "at");
		moduleVsRoute.put(ContextNames.ASSET_BREAKDOWN, "at");
		moduleVsRoute.put(ContextNames.ALARM, "fa");
		moduleVsRoute.put(ContextNames.ALARM_OCCURRENCE, "fa");
		moduleVsRoute.put(ContextNames.READING_ALARM_OCCURRENCE, "fa");
		moduleVsRoute.put(ContextNames.ML_ALARM_OCCURRENCE, "fa");
		moduleVsRoute.put(ContextNames.VIOLATION_ALARM_OCCURRENCE, "fa");
		moduleVsRoute.put(ContextNames.BASE_EVENT, "fa");
		moduleVsRoute.put(ContextNames.TENANT, "home");
		moduleVsRoute.put(ContextNames.VENDORS, "home");
		moduleVsRoute.put(ContextNames.CONTACT, "home");
		moduleVsRoute.put(ContextNames.VISITOR_LOGGING, "vi");
		moduleVsRoute.put(ContextNames.VISITOR, "vi");
		moduleVsRoute.put(ContextNames.WATCHLIST, "vi");
	}
	
	
	public static String exportPdf(FacilioModule module, FileFormat fileFormat, ReportContext report, Boolean isS3Url, String fileName, Context context) throws Exception {
		long reportId = report.getId();
		StringBuilder url = new StringBuilder(FacilioProperties.getConfig("clientapp.url")).append("/app/");
		String moduleName = module.getName();
		
		if (report.getTypeEnum() == ReportType.READING_REPORT) {
			url.append("em");
		}
		else if (module.isCustom()) {
			url.append("ca");
		}
		else if (moduleVsRoute.containsKey(moduleName)) {
			url.append(moduleVsRoute.get(moduleName));
		}
		else {
			url.append("wo");
		}
		
		if (reportId > 0) {
			url.append("/reports/newview/").append(reportId);			
		}
		url.append("?print=true");
		if(fileFormat == FileFormat.IMAGE) {
			url.append("&showOnlyImage=true");
		}
		
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
		Map<String, Object> params = (Map<String, Object>) context.get("exportParams");
		if (params != null) {
			for(Map.Entry<String, Object> param: params.entrySet()) {
				url.append("&").append(param.getKey()).append("=").append(param.getValue());
			}
		}
		
		if (report.getTypeEnum() == ReportType.READING_REPORT) {
			ReportTemplateContext reportTemplate=report.getReportTemplate();
			if(reportTemplate != null) {
				url.append("&template=").append(FieldUtil.getAsJSON(reportTemplate));
			}
		}
		
		log.debug("pdf url --- "+ url);
		
		Map<String, Object> renderParams = (Map<String, Object>) context.get("renderParams");
		JSONObject additionalInfo = new JSONObject(); 
		if (renderParams != null) {
			additionalInfo = new JSONObject();
			additionalInfo.putAll(renderParams);
		}
		
		return PdfUtil.exportUrlAsPdf(url.toString(), isS3Url, fileName, additionalInfo, fileFormat);
	}
	
}
