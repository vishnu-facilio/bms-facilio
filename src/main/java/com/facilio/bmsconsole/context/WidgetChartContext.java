package com.facilio.bmsconsole.context;

import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import com.facilio.accounts.util.AccountUtil;
import org.json.simple.JSONObject;
import com.facilio.report.util.ReportUtil;
@Getter @Setter
public class WidgetChartContext extends DashboardWidgetContext {

	/**
	 * 
	 */
	private static final Logger LOGGER = Logger.getLogger(WidgetChartContext.class.getName());
	private static final long serialVersionUID = 1L;
	Long reportId;
	Long newReportId;
	public Long getNewReportId() {
		return newReportId;
	}
	public void setNewReportId(Long newReportId) {
		this.newReportId = newReportId;
	}
	Integer chartType;
	
	public Integer getChartType() {
		return chartType;
	}
	public void setChartType(Integer chartType) {
		this.chartType = chartType;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	Long dateFilterId;
	public Long getDateFilterId() {
		return dateFilterId;
	}
	public void setDateFilterId(Long dateFilter) {
		this.dateFilterId = dateFilter;
	}
	private String reportTemplate;
	
	public String getReportTemplate() {
		return reportTemplate;
	}
	public void setReportTemplate(String reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String reportType;
	private String reportName;
	private  String newReportName;

	@Override
	public JSONObject widgetJsonObject(boolean optimize) {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("id", getId());
		resultJson.put("link_name", getLinkName());
		resultJson.put("type", getWidgetType().getName());
		resultJson.put("widgetSettings",getWidgetSettings());
		resultJson.put("helpText",getHelpText());
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", getLayoutHeight());
		layoutJson.put("width", getLayoutWidth());
		layoutJson.put("x", getxPosition());
		layoutJson.put("y", getyPosition());
		layoutJson.put("position", getLayoutPosition());
		
		resultJson.put("layout", layoutJson);
		
		JSONObject mlayoutJson = new JSONObject();
		mlayoutJson.put("height", getmLayoutHeight());
		mlayoutJson.put("width", getmLayoutWidth());
		mlayoutJson.put("x", getmXPosition());
		mlayoutJson.put("y", getmYPosition());
		mlayoutJson.put("position", getmLayoutPosition());
		
		resultJson.put("mLayout", mlayoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getHeaderText());
		// Temprovery 
		if(getHeaderSubText() != null && getHeaderSubText().equals("{today}")) {
			headerJson.put("subtitle", "today");
		}
		else {
			headerJson.put("subtitle", getHeaderSubText());
		}
		
		headerJson.put("export", isHeaderIsExport());
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
		if(chartType != null && chartType > 0) {
			dataOptionsJson.put("chartType", ReportContext.ReportChartType.getWidgetChartType(chartType).getName());
		}
		dataOptionsJson.put("dataurl", "/dashboard/getData?reportId="+getReportId());
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("reportId", getReportId());
		dataOptionsJson.put("newReportId", getNewReportId());
		try {
			if(getNewReportId() != null && getNewReportId() > 0) {
//				if(optimize == false) {
//					dataOptionsJson.put("newReport", ReportUtil.getReport(getNewReportId(), true));
//				}
				dataOptionsJson.put("moduleName", ReportUtil.getReportModuleName(getNewReportId()));
				dataOptionsJson.put("reportType", ReportUtil.getReportType(getNewReportId()));
				dataOptionsJson.put("reportTemplate", getReportTemplate());
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		dataOptionsJson.put("chartTypeInt", chartType);
		dataOptionsJson.put("dateFilter", dateFilterId);
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		resultJson.put("customActions", getCustomActions());

		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
//		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
	@Override
	public JSONObject widgetMobileJsonObject(boolean optimize, int index) {

		JSONObject widgetJson = new JSONObject();
		widgetJson.put("label", getWidgetName());
		widgetJson.put("id", getId());
		widgetJson.put("reportId", getNewReportId());

		widgetJson.put("link_name", getLinkName());
		widgetJson.put("type", getWidgetType().getName());
		widgetJson.put("helpText",getHelpText());
		widgetJson.put("title", getHeaderText());
		widgetJson.put("sequence", index);
		widgetJson.put("web_url", "dashboard/"+ AccountUtil.getCurrentApp().getLinkName() + "/widget/" + getId());
		try {
			if(getNewReportId() != null && getNewReportId() > 0) {
				widgetJson.put("reportType", ReportUtil.getReportType(getNewReportId()));
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		};
		return widgetJson;
	}
}
