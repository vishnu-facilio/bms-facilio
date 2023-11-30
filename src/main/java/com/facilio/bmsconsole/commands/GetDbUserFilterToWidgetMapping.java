package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.cards.util.CardLayout;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldType;
import com.facilio.report.context.ReportYAxisContext;
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
	private List<String> supportedModules = Arrays.asList("asset","building","site","assetcategory");
	private List<String> supportedMeterModules = Arrays.asList("meter","building","site","utilitytype");

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
				for (DashboardUserFilterContext filter : userFilters) {
					List<DashboardFieldMappingContext> customMappingV2 = DashboardFilterUtil.getFilterMappingsForFilterId(filter.getId());
					if(customMappingV2 != null && customMappingV2.size() > 0){
						for(DashboardFieldMappingContext filterMapping: customMappingV2){
							filter.getFilterFieldMap().put(filterMapping.getModuleId(), modBean.getField(filterMapping.getFieldId()));
						}
					}
				}

				for (DashboardWidgetContext widget : widgets) {
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
					if(widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.CARD)){
						WidgetCardContext cardContext = (WidgetCardContext) widget;
						if(cardContext.getCardLayout().equals(CardLayout.COMBO_CARD.getName())) {
							if(cardContext.getChildCards() != null && cardContext.getChildCards().size() > 0){
								for(WidgetCardContext childCard : cardContext.getChildCards()){
									childCard.setType("card");
									widgetFieldMapping(childCard , userFilters, widgetUserFiltersMap, DashboardFilterUtil.getModuleIdFromWidget(childCard));
								}
							}
						}else{
							widgetFieldMapping(widget , userFilters, widgetUserFiltersMap, widgetModuleId);
						}

					}else{
						widgetFieldMapping(widget , userFilters, widgetUserFiltersMap, widgetModuleId);
					}
				}
			}

			dbFilter.setWidgetUserFiltersMap(widgetUserFiltersMap);
			dbFilter.setCustomScriptWidgets(customScriptWidgets);

		}

		return false;
	}

   public void widgetFieldMapping(DashboardWidgetContext widget, List<DashboardUserFilterContext> userFilters, Map<Long, List<Long>> widgetUserFiltersMap, long widgetModuleId) throws Exception {
	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	   JSONObject widgetSettings = widget.getWidgetSettings();
	   Boolean isFilterExclude = (widgetSettings == null) ? false : widgetSettings.containsKey("excludeDbFilters") ?   (Boolean) widgetSettings.get("excludeDbFilters") : false;

	   long widgetId = widget.getId();
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
					   V2ReportContext reportContext = V2AnalyticsOldUtil.getV2Report(chartWidget.getNewReportId());
					   List<V2MeasuresContext> measures = reportContext.getMeasures();
					   if(filter.getFieldId() < 0 && filter.getModuleName() != null) {
						   FacilioModule energyModule = modBean.getModule(report.getModuleId());
						   if(energyModule != null && energyModule.getName().equals("energydata")) {
							   Map<String, DashboardReadingWidgetFilterContext> customMapping = DashboardFilterUtil.getReadingFilterMappingsForFilterId(filter.getId(),widgetId);
							   List<String> allModules = new ArrayList<>();
							   Map<String, FacilioField> mappingFields = new HashMap<>();
							   Map<String, FacilioField> excludedMappingFields = new HashMap<>();
							   for(V2MeasuresContext measure: measures) {
								   if(!allModules.contains(measure.getModuleName())) {
									   allModules.add(measure.getModuleName());
									   FacilioModule parentModule = modBean.getModule(measure.getModuleName());
									   parentModule.setFields(modBean.getAllFields(measure.getModuleName()));
									   FacilioModule filterModule = modBean.getModule(filter.getModuleName());
									   FacilioField mappingField = null;
									   FacilioField excludedMappingField = null;
									   if(customMapping.containsKey(parentModule.getName())){
										   DashboardReadingWidgetFilterContext customMap = customMapping.get(parentModule.getName());
										   if(customMap.getWidgetFieldId() == null) {
											   excludedMappingField = DashboardFilterUtil.getApplicableField(filterModule, parentModule, energyModule);
										   }else{
											   mappingField = modBean.getField(customMap.getWidgetFieldId(), parentModule.getName());
										   }
									   }else{
										   mappingField = DashboardFilterUtil.getApplicableField(filterModule, parentModule, energyModule);
									   }
									   if(mappingField != null) {
										   mappingFields.put(parentModule.getName(),mappingField);
									   }
									   if(excludedMappingField != null) {
										   excludedMappingFields.put(parentModule.getName(), excludedMappingField);
									   }
								   }
							   }
							   filter.getReadingWidgetModuleMap().put(widgetId,allModules);
							   if(mappingFields != null && mappingFields.size() > 0) {
								   filter.getReadingWidgetFieldMap().put(widgetId,mappingFields);
							   }
							   if(excludedMappingFields != null && excludedMappingFields.size() > 0) {
								   filter.getExcludedReadingWidgetFieldMap().put(widgetId,excludedMappingFields);
							   }
						   }
					   }else if(filter.getFieldId() > 0) {
						   List<String> allModules = new ArrayList<>();
						   Map<String, FacilioField> mappingFields = new HashMap<>();
						   for(V2MeasuresContext measure: measures) {
							   if(!allModules.contains(measure.getModuleName())) {
								   allModules.add(measure.getModuleName());
								   FacilioModule parentModule = modBean.getModule(measure.getModuleName());
								   FacilioField mappingField = modBean.getField(filter.getFieldId());
								   if(mappingField != null) {
									   mappingFields.put(parentModule.getName(),mappingField);
								   }
							   }
						   }
						   filter.getReadingWidgetModuleMap().put(widgetId,allModules);
						   filter.getReadingWidgetFieldMap().put(widgetId,mappingFields);
					   }
				   }
			   }
			   else if(widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD) {
				   WidgetCardContext cardWidget = (WidgetCardContext) widget;
				   if(cardWidget.getCardLayout().equals("v2_reading_card")) {
					   for(DashboardUserFilterContext filter : userFilters) {
						   JSONObject cardParams = cardWidget.getCardParams();
						   String moduleName = (String) cardParams.get("type");
						   if(filter.getFieldId() < 0 && filter.getModuleName() != null) {
							   Map<String, DashboardReadingWidgetFilterContext> customMapping = DashboardFilterUtil.getReadingFilterMappingsForFilterId(filter.getId(),widgetId);
							   FacilioField field = modBean.getField((Long) cardParams.get("fieldId"));
							   FacilioModule baseModule = field.getModule();
							   filter.getReadingWidgetModuleMap().put(widgetId,Collections.singletonList(moduleName));
							   FacilioModule parentModule = modBean.getModule(moduleName);
							   parentModule.setFields(modBean.getAllFields(moduleName));
							   FacilioModule filterModule = modBean.getModule(filter.getModuleName());
							   FacilioField mappingField = null;
							   FacilioField excludedMappingField = null;
							   if(customMapping.containsKey(parentModule.getName())){
								   DashboardReadingWidgetFilterContext customMap = customMapping.get(parentModule.getName());
								   if(customMap.getWidgetFieldId() == null){
									   excludedMappingField = DashboardFilterUtil.getApplicableField(filterModule, parentModule, baseModule);
								   }else{
									   mappingField = modBean.getField(customMap.getWidgetFieldId(), parentModule.getName());
								   }
							   }else{
								   mappingField = DashboardFilterUtil.getApplicableField(filterModule, parentModule, baseModule);
							   }
							   if(mappingField != null) {
								   filter.getReadingWidgetFieldMap().put(widgetId,Collections.singletonMap(moduleName,mappingField));
							   }
							   if(excludedMappingField != null) {
								   filter.getExcludedReadingWidgetFieldMap().put(widgetId,Collections.singletonMap(moduleName,excludedMappingField));
							   }
						   }
						   else if(filter.getFieldId() > 0) {
							   filter.getReadingWidgetModuleMap().put(widgetId,Collections.singletonList(moduleName));
							   filter.getReadingWidgetFieldMap().put(widgetId,Collections.singletonMap(moduleName,modBean.getField(filter.getFieldId())));
						   }
					   }
				   }
			   }
		   }
		   catch(Exception e)
		   {
			   LOGGER.log(Level.SEVERE,"Error occured  finding module for widget , ID="+widgetId+" Skipping   db userfilters",e);
		   }
		   return;
	   }

	   FacilioModule widgetModule = modBean.getModule(widgetModuleId);
	   widgetModule.setFields(modBean.getAllFields(widgetModule.getName()));


	   for (DashboardUserFilterContext filter : userFilters) {


		   // for all other widget except custom script, map only the filters that apply to
		   // them

			Map<Long, FacilioField> customModulevsFieldMapping = new HashMap<>();
			List<DashboardFieldMappingContext> customMappingV2 = DashboardFilterUtil.getFilterMappingsForFilterId(filter.getId());
			if(customMappingV2 != null && customMappingV2.size() > 0){
				for(DashboardFieldMappingContext filterMapping: customMappingV2){
					customModulevsFieldMapping.put(filterMapping.getModuleId(), modBean.getField(filterMapping.getFieldId()));
				}
			}
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
					if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2) && customModulevsFieldMapping != null && customModulevsFieldMapping.containsKey(widgetModule.getModuleId())){
						filterApplicableField = customModulevsFieldMapping.get(widgetModule.getModuleId());
					}
					else{
						filterApplicableField =DashboardFilterUtil.getFilterApplicableField(moduleForFilter,widgetModule, fieldId != null && fieldId > 0 ? fieldId : null);
					}
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

			   } else {
				   filter.getWidgetModuleMap().put(widgetId, widgetModule != null ? widgetModule.getName() : null);
				   filter.getWidgetExcludeFieldMap().put(widgetId,DashboardFilterUtil.getFilterApplicableField(moduleForFilter,widgetModule, null));
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

	
	

	public void addToWidgetUserFiltersMap(long widgetId, long filterId, Map<Long, List<Long>> widgetUserFiltersMap) {
		if (!widgetUserFiltersMap.containsKey(widgetId)) {
			widgetUserFiltersMap.put(widgetId, new ArrayList<Long>());
		}
		widgetUserFiltersMap.get(widgetId).add(filterId);
	}

}
