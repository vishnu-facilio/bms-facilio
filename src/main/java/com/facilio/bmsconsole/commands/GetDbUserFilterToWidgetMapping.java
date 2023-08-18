package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldType;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
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
					Boolean isFilterExclude = (widgetSettings == null) ? false : widgetSettings.containsKey("excludeDbFilters") ?   (Boolean) widgetSettings.get("excludeDbFilters") : false;

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
									else if(filter.getFieldId() != -1 && filter.getField() != null && filter.getField().getDataTypeEnum() == FieldType.DATE_TIME && report != null && report.getModuleId() != -1){
										FacilioModule energyModule = modBean.getModule(report.getModuleId());
										if(energyModule != null && energyModule.getName().equals("energydata")) {
											FacilioField ttimeField = modBean.getField("ttime", energyModule.getName());
											filter.getWidgetFieldMap().put(widgetId, ttimeField);
											this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
										}
									}
									if(filter.getModule().getName().equals("asset")){
										FacilioModule energyModule = modBean.getModule(report.getModuleId());
										if(energyModule != null && energyModule.getName().equals("energydata")) {
											List<ReportDataPointContext> dpContexts = new ArrayList<>();
											List<DashboardReadingWidgetFilterContext> mappings = DashboardFilterUtil.getReadingFilterMappingsForFilterId(filter.getWidget_id(), widgetId);
											for(ReportDataPointContext dp: report.getDataPoints()){
												if(mappings.stream().anyMatch(mapping -> mapping.getDataPointAlias().equals(dp.getAliases().get("actual")))){
													dpContexts.add(dp);
												}
											}
											if(dpContexts != null && dpContexts.size() > 0){
												filter.getReadingWidgetFieldMap().put(widgetId,dpContexts);
											}
										}
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
									Long fieldId = DashboardFilterUtil.getFieldForMappingOnWidget(filter.getDashboardUserFilterJson(), widgetModule != null ? widgetModule.getName() : null);
								 filterApplicableField =DashboardFilterUtil.getFilterApplicableField(moduleForFilter,
										widgetModule, fieldId != null && fieldId > 0 ? fieldId : null);
								 
								}
							
								if (filterApplicableField != null) {
									filter.getWidgetModuleMap().put(widgetId, widgetModule != null ? widgetModule.getName() : null);
									if (isFilterExclude != null && isFilterExclude == true) {
										filter.getWidgetExcludeFieldMap().put(widgetId, filterApplicableField);
									}
									else {
										// add WIDGET->Widget-Field to apply filtwidgetUserFiltersMaper in filter obj
										filter.getWidgetFieldMap().put(widgetId, filterApplicableField);

										// add filter to list of applicable filters in widget
										this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
									}

								}
								
							} else if (fieldForFilter != null) {
								Boolean isFilterApplicableForWidget =
										DashboardFilterUtil.isEnumFilterApplicableToWidget(filter.getField().getModule(), widgetModule);
								if(customAppliesToMapping!=null && customAppliesToMapping.containsKey(widgetId))
								{
									FacilioField filterApplicableField=customAppliesToMapping.get(widgetId);
									filter.getWidgetFieldMap().put(widgetId, filterApplicableField);
									this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
								}
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
