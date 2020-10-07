package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ModuleNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;

public class GetDbUserFilterToWidgetMapping extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDbTimeLineFilterToWidgetMapping.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<Long, List<Long>> widgetUserFiltersMap = new HashMap<>();

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

					long widgetId = widget.getId();
					long widgetModuleId =-1;
					try {
					 widgetModuleId = DashboardFilterUtil.getModuleIdFromWidget(widget);
					}
					catch(Exception e)
					{
						LOGGER.log(Level.SEVERE,"Error occured  finding module for widget , ID="+widgetId+" Skipping   db userfilters",e);
						continue;
					}

					JSONObject widgetSettings = widget.getWidgetSettings();
					boolean isFilterExclude = (boolean) widgetSettings.get("excludeDbFilters");
					if (isFilterExclude == true || widgetModuleId == -1) {// widgetModuleId=-1 implies widget has no
																			// module associated with it , like TEXT,WEB
																			// widget, reading widgets etc
						continue;
					}

					FacilioModule widgetModule = modBean.getModule(widgetModuleId);
					widgetModule.setFields(modBean.getAllFields(widgetModule.getName()));
					boolean isCustomScript = DashboardFilterUtil.isCustomScriptWidget(widget);

					for (DashboardUserFilterContext filter : userFilters) {
						// if custom script is enabled for widget, put all filters in filterMap
						if (isCustomScript) {
							if (!widgetUserFiltersMap.containsKey(widgetId)) {
								widgetUserFiltersMap.put(widgetId, new ArrayList<Long>());
							}
							widgetUserFiltersMap.get(widgetId).add(filter.getId());
							// CANNOT identify field for lookup filter for custom widget

						}

						// for all other widget except custom script, map only the filters that apply to
						// them

						else {
							// Is filter TYPE enum(field obj present) or type lookup (module obj ) present
							FacilioModule moduleForFilter = filter.getModule();
							FacilioField fieldForFilter = filter.getField();

							if (moduleForFilter != null)// enum filter
							{	moduleForFilter.setFields(modBean.getAllFields(moduleForFilter.getName()));
								FacilioField filterApplicableField = getFilterApplicableField(moduleForFilter,
										widgetModule);
								if (filterApplicableField != null) {
									// add WIDGET->Widget-Field to apply filter in filter obj
									filter.getWidgetFieldMap().put(widgetId, filterApplicableField);

									// add filter to list of applicable filters in widget
									this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);

								}
							} else if (fieldForFilter != null) {
								Boolean isFilterApplicableForWidget = this
										.isEnumFilterApplicableToWidget(filter.getField().getModule(), widgetModule);

								if (isFilterApplicableForWidget) {
									filter.getWidgetFieldMap().put(widgetId, fieldForFilter);
									this.addToWidgetUserFiltersMap(widgetId, filter.getId(), widgetUserFiltersMap);
								}
							}

						}

					}

				}
			}

			dbFilter.setWidgetUserFiltersMap(widgetUserFiltersMap);

		}

		return false;
	}

	// enum db user filters apply to only widgets of the same module
	// must check if widget module is either same as filter module or one of its
	// children
	// Ex , ticketCategory field has module='ticket' but report_chart corresponding
	// to workorders has module='workorder'
	public FacilioField getFilterApplicableField(FacilioModule filterModule, FacilioModule widgetModule) {
		
		List<FacilioField> filterApplicableFields = widgetModule.getFields().stream().filter((FacilioField field) -> {
			if (field.getDataTypeEnum() == FieldType.LOOKUP) {
				LookupField lookupField = (LookupField) field;
				if (lookupField.getLookupModule().equals(filterModule)) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());

		if (filterApplicableFields != null && filterApplicableFields.size() > 0) {
			return filterApplicableFields.get(0);
		}
		return null;
	}

	public boolean isEnumFilterApplicableToWidget(FacilioModule filterModule, FacilioModule widgetModule)
			throws Exception {

		long widgetModuleId = widgetModule.getModuleId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<FacilioModule> filterChildModules = modBean.getChildModules(filterModule);

		List<Long> filterChildModuleIds = new ArrayList<Long>();

		if (filterChildModules != null) {
			filterChildModuleIds = filterChildModules.stream().map((FacilioModule module) -> {
				return module.getModuleId();
			}).collect(Collectors.toList());
		}

		if (widgetModuleId == filterModule.getModuleId() || filterChildModuleIds.contains(widgetModuleId)) {
			return true;
		} else {
			return false;
		}
	}

	public void addToWidgetUserFiltersMap(long widgetId, long filterId, Map<Long, List<Long>> widgetUserFiltersMap) {
		if (!widgetUserFiltersMap.containsKey(widgetId)) {
			widgetUserFiltersMap.put(widgetId, new ArrayList<Long>());
		}
		widgetUserFiltersMap.get(widgetId).add(filterId);
	}

}
