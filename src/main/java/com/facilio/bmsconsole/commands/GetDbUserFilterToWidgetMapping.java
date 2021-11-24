package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.ReportUtil;


public class GetDbUserFilterToWidgetMapping extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDbTimeLineFilterToWidgetMapping.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<Long, List<Long>> widgetUserFiltersMap = new HashMap<>();
		List<Long> customScriptWidgets=new ArrayList<>();

		DashboardFilterContext dbFilter = (DashboardFilterContext) context
				.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);
		if (dbFilter != null) {

			List<DashboardUserFilterContext> userFilters = dbFilter.getDashboardUserFilters();

			DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);

			DashboardTabContext dashboardTab = (DashboardTabContext) context
					.get(FacilioConstants.ContextNames.DASHBOARD_TAB);

			List<DashboardWidgetContext> widgets = new ArrayList<>();
			if (dashboard != null) {
				widgets = dashboard.getDashboardWidgets();
			} else if (dashboardTab != null) {
				widgets = dashboardTab.getDashboardWidgets();
			} else {
				throw new Exception("NO dashboard or dashboard tab");
			}

			// for each widget , find which filters are applicable to it .

			if (widgets != null && userFilters != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				for (DashboardWidgetContext widget : widgets) {

					JSONObject widgetSettings = widget.getWidgetSettings();
					Boolean isFilterExclude = (widgetSettings == null) ? false : (Boolean) widgetSettings.get("excludeDbFilters");
					if (isFilterExclude != null && isFilterExclude == true) {
						continue;
					}

					long widgetId = widget.getId();

					boolean isCustomScript = DashboardFilterUtil.isCustomScriptWidget(widget);
					// if custom script is enabled for widget, add widget to customScriptWidget list and skip futher processing

					if (isCustomScript) {												
						customScriptWidgets.add(widgetId);
							
								continue;
					}
					
					long widgetModuleId =-1;
					try {
					 widgetModuleId = DashboardFilterUtil.getModuleIdFromWidget(widget);
					}
					catch(Exception e)
					{
						LOGGER.log(Level.SEVERE,"Error occured  finding module for widget , ID="+widgetId+" Skipping   db userfilters",e);
						continue;
					}
					if (widgetModuleId == -1) {// widgetModuleId=-1 implies widget has no
																			// module associated with it , like TEXT,WEB
																			// widget, reading widgets etc

			
						try {
							if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CHART) {


								WidgetChartContext chartWidget = (WidgetChartContext) widget;
								chartWidget.getNewReportId();
								ReportContext report = ReportUtil.getReport(chartWidget.getNewReportId(), true);
								

								for (DashboardUserFilterContext filter : userFilters) {
									Boolean isUserFilterApplicableReadingWidget = DashboardFilterUtil.isUserFilterApplicableReadingWidget(report, filter);
									if (isUserFilterApplicableReadingWidget) {
										FacilioField widgetField = new FacilioField();
										widgetField.setName(filter.getModuleName());
								
										filter.getWidgetFieldMap().put(widgetId, widgetField);

										this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
									}
								}
								}
							

							}
						catch(Exception e)
						{
							LOGGER.log(Level.SEVERE,"Error occured  finding module for widget , ID="+widgetId+" Skipping   db userfilters",e);
							continue;
						}

						
							
						
				
						continue;
					}

					FacilioModule widgetModule = modBean.getModule(widgetModuleId);
					widgetModule.setFields(modBean.getAllFields(widgetModule.getName()));
					

					for (DashboardUserFilterContext filter : userFilters) {
						

						// for all other widget except custom script, map only the filters that apply to
						// them

						
							Map<Long,FacilioField> customAppliesToMapping= DashboardFilterUtil.getUserFilterToWidgetColumnMapping(filter.getId());
							// Is filter TYPE enum(field obj present) or type lookup (module obj ) present
							FacilioModule moduleForFilter = filter.getModule();
							FacilioField fieldForFilter = filter.getField();
									
							if (moduleForFilter != null)// enum filter
							{	moduleForFilter.setFields(modBean.getAllFields(moduleForFilter.getName()));
								
								FacilioField filterApplicableField; 
							
								//when there are more than one fields which have the same lookup -> users module filter-> workorder module,requestedBy,createdBy etc
								//take from custom mapping else select first option	
								if(customAppliesToMapping!=null&&customAppliesToMapping.containsKey(widgetId))
								{
									filterApplicableField=customAppliesToMapping.get(widgetId);
								}
								else
								{
								 filterApplicableField =DashboardFilterUtil.getFilterApplicableField(moduleForFilter,
										widgetModule);
								 
								}
							
								if (filterApplicableField != null) {
									// add WIDGET->Widget-Field to apply filtwidgetUserFiltersMaper in filter obj
									filter.getWidgetFieldMap().put(widgetId, filterApplicableField);

									// add filter to list of applicable filters in widget
									this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);

								}
								
							} else if (fieldForFilter != null) {
								Boolean isFilterApplicableForWidget = 
										DashboardFilterUtil.isEnumFilterApplicableToWidget(filter.getField().getModule(), widgetModule);

								if (isFilterApplicableForWidget) {
									filter.getWidgetFieldMap().put(widgetId, fieldForFilter);
									this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
								}
							}

						

					}

				}
			}

			dbFilter.setWidgetUserFiltersMap(widgetUserFiltersMap);
			dbFilter.setCustomScriptWidgets(customScriptWidgets);

		}

		return false;
	}



	
	

	public void addToWidgetUserFiltersMap(long widgetId, long filterId, Map<Long, List<Long>> widgetUserFiltersMap) {
		if (!widgetUserFiltersMap.containsKey(widgetId)) {
			widgetUserFiltersMap.put(widgetId, new ArrayList<Long>());
		}
		widgetUserFiltersMap.get(widgetId).add(filterId);
	}

}
