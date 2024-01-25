package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GetDbTimeLineFilterToWidgetMapping;
import com.facilio.bmsconsole.context.WidgetCardContext.ScriptMode;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.cards.util.CardLayout;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.BaseCriteriaAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;

public class DashboardFilterUtil {
    private static final Logger LOGGER = Logger.getLogger(DashboardFilterUtil.class.getName());

    public static final List<String> BUILDING_IS_MODULES=new ArrayList<String>(Arrays.asList("site","building","floor","space"));
	public static final List<String> T_TIME_ONLY_CARD_LAYOUTS = initCardLayouts();
	

	static List<String> initCardLayouts() {

		return Collections.unmodifiableList(new ArrayList<String>(
				Arrays.asList(CardLayout.READINGCARD_LAYOUT_1.getName(), CardLayout.READINGCARD_LAYOUT_2.getName(),
						CardLayout.READINGCARD_LAYOUT_3.getName(), CardLayout.READINGCARD_LAYOUT_4.getName(),
						CardLayout.READINGCARD_LAYOUT_5.getName(), CardLayout.READINGCARD_LAYOUT_6.getName(),
						CardLayout.ENERGYCARD_LAYOUT_1.getName(), CardLayout.ENERGYCOST_LAYOUT_1.getName(),
						CardLayout.CARBONCARD_LAYOUT_1.getName(), CardLayout.GAUGE_LAYOUT_1.getName(),
						CardLayout.GAUGE_LAYOUT_2.getName(), CardLayout.GAUGE_LAYOUT_3.getName(),
						CardLayout.GAUGE_LAYOUT_4.getName(), CardLayout.GAUGE_LAYOUT_5.getName(),
						CardLayout.GAUGE_LAYOUT_6.getName(),						
						CardLayout.PMREADINGS_LAYOUT_1.getName(), CardLayout.TABLE_LAYOUT_1.getName()

				)));
	}
	public static boolean isUserFilterApplicableReadingWidget(ReportContext report, DashboardUserFilterContext filter) {

	    JSONParser parser = new JSONParser();
	    try {
	        JSONObject chartStateObject = (JSONObject) parser.parse(report.getChartState());

	        JSONObject common = (JSONObject) chartStateObject.get("common");
			JSONObject reportTemplate = (JSONObject) chartStateObject.get("reportTemplate");

			

	        if (common != null) {
	        	Long mode = (Long) common.get("mode");
	        		        	
	            if (mode != null && mode == 1 && reportTemplate != null) {
					// dashboard filter suppoprt for report template widgets
					if (reportTemplate.get("categoryId") != null && filter.getModuleName() != null && filter.getModuleName().equals("asset")) {
						return true;
					}
				}
				else {
					JSONObject filters = (JSONObject) common.get("filters");

	            if (filters != null) {
	                JSONObject filterState = (JSONObject) filters.get("filterState");
	                String FilterModuleName = filter.getModuleName();

	                if (filterState != null) {
	                    String liveFilterModuleName = (String) filterState.get("liveFilterField");
	                    if (FilterModuleName != null && liveFilterModuleName.equals(FilterModuleName)) {
	                        return true;
	                    }
	                }

	            }
				}

	        }

	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Error occured  while parsing chart state of this report, ID=" + report.getId() + " Skipping   db userfilters", e);

	    }

	    return false;

	}
	public static DashboardFilterContext getDashboardFilter(Long dashboardId, Long dashboardTabId) throws Exception {
		FacilioModule module = ModuleFactory.getDashboardFilterModule();
		Condition idCondition;

		if (dashboardId != null && dashboardId > 0) {
			idCondition = CriteriaAPI.getCondition("DASHBOARD_ID", "dashboardId", "" + dashboardId,
					NumberOperators.EQUALS);
		} else if (dashboardTabId != null && dashboardTabId > 0) {
			idCondition = CriteriaAPI.getCondition("DASHBOARD_TAB_ID", "dashboardTabId", "" + dashboardTabId,
					NumberOperators.EQUALS);

		} else {
			throw new IllegalArgumentException("No Dashboard or Tab Specified");
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(FieldFactory.getDashboardFilterFields()).andCondition(idCondition);

		List<Map<String, Object>> records = builder.get();
		if (records != null && !records.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(records.get(0), DashboardFilterContext.class);
		}
		return null;

	}

	public static List<DashboardUserFilterContext> getDashboardUserFilters(Long dashboardFilterId) throws Exception {
		FacilioModule module = ModuleFactory.getDashboardUserFilterModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(FieldFactory.getDashboardUserFilterFields()).andCondition(CriteriaAPI.getCondition(
						"DASHBOARD_FILTER_ID", "dashboardFilterId", "" + dashboardFilterId, NumberOperators.EQUALS));

		List<Map<String, Object>> records = builder.get();
		if (records != null && !records.isEmpty()) {
			List<DashboardUserFilterContext> dashboardUserFilters = FieldUtil.getAsBeanListFromMapList(records,
					DashboardUserFilterContext.class);
			
			//sort based on order
			dashboardUserFilters.sort(new Comparator<DashboardUserFilterContext>() {

				@Override
				public int compare(DashboardUserFilterContext f1, DashboardUserFilterContext f2) {

					return f1.getFilterOrder()-f2.getFilterOrder();	
				}
				
			});
			
			
			for (DashboardUserFilterContext filter : dashboardUserFilters) {
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				if(filter.getFieldId()>0)
				{
				filter.setField(modBean.getField(filter.getFieldId()));
				}
				if(filter.getModuleName()!=null)
				{
					filter.setModule(modBean.getModule(filter.getModuleName()));
					filter.getModule().setFields(modBean.getAllFields(filter.getModuleName()));
				}
				
				if(filter.getCriteriaId()>0)
				{
					filter.setCriteria(CriteriaAPI.getCriteria(filter.getCriteriaId()));
					if(LookupSpecialTypeUtil.isSpecialType(filter.getModuleName())){
						List list = LookupSpecialTypeUtil.getObjects(filter.getModuleName(), filter.getCriteria());
						if(list!=null)
						{
							List<String> recordIds = new ArrayList<>();
							for (Object record : list) {
								Map<String, Object> props = FieldUtil.getAsProperties(record);
								recordIds.add(String.valueOf(props.get("id")));
							}
							filter.setSelectedOptionsRecordIds(recordIds);
						}
					}
					else{
						SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder=new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
								.module(filter.getModule())
								.select(Collections.singletonList(FieldFactory.getIdField(filter.getModule())))
								.andCriteria(filter.getCriteria())
								.beanClass(ModuleBaseWithCustomFields.class);

						List<ModuleBaseWithCustomFields>  optionRecords= selectRecordsBuilder.get();

						if(optionRecords!=null)
						{
							List<String> recordIds=optionRecords.stream().map(record->String.valueOf(record.getId())).collect(Collectors.toList());
							filter.setSelectedOptionsRecordIds(recordIds);
						}
					}
				}


				if(filter.getWidget_id() != null)
				{
					DashboardWidgetContext filter_widget = DashboardUtil.getWidget(filter.getWidget_id());
					if(filter_widget != null){
						filter.setLink_name(filter_widget.getLinkName());
					}
				}
				
				//filter.setWidgetFieldMap(getUserFilterToWidgetColumnMapping(filter.getId()));

			}
			
			//set cross cascading rels
			try {
				for (DashboardUserFilterContext filter : dashboardUserFilters) {
						if(filter.getShowOnlyRelevantValues()!=null&&filter.getShowOnlyRelevantValues()==true)
						{
						filter.setCascadingFilters(DashboardFilterUtil.findCascadingFilterRel(filter,dashboardUserFilters));
						}
				}
				
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE,"Exception finding cascading filter relation",e);
				
			}
			
			return dashboardUserFilters;
		}
		return null;
	}

	public static Long insertDashboardUserFilterRel(DashboardUserFilterContext dashboardUserFilterRel)
			throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardUserFilterModule().getTableName())
				.fields(FieldFactory.getDashboardUserFilterFields());

		return builder.insert(FieldUtil.getAsProperties(dashboardUserFilterRel));
	}

	public static void updateDashboardUserFilerRel(DashboardUserFilterContext dashboardUserFilterRel) throws Exception {
		FacilioModule module = ModuleFactory.getDashboardUserFilterModule();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(FieldFactory.getDashboardUserFilterFields())
				.andCondition(CriteriaAPI.getIdCondition(dashboardUserFilterRel.getId(), module));

		builder.update(FieldUtil.getAsProperties(dashboardUserFilterRel));
	}

	public static void deleteDashboardUserFilterRel(List<Long> toRemove, List<Long> deleted_widget_id) throws Exception {
		FacilioModule module = ModuleFactory.getDashboardUserFilterModule();

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(toRemove, module));
		builder.delete();

		if(deleted_widget_id != null && deleted_widget_id.size() > 0) {
			FacilioModule widgetModule = ModuleFactory.getWidgetModule();

			GenericDeleteRecordBuilder widget_delete_builder = new GenericDeleteRecordBuilder().table(widgetModule.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(deleted_widget_id, widgetModule));
			widget_delete_builder.delete();
		}

	}
	public static boolean isCustomScriptWidget(DashboardWidgetContext widget)
	{
		if(widget.getWidgetType()==WidgetType.CARD)
		{
			WidgetCardContext cardContext=(WidgetCardContext)widget;
			if(cardContext.getScriptMode()==ScriptMode.CUSTOM_SCRIPT)
			{
				return true;
			}
			
				//hack ,treat gauge_layout_7 similar to custom script.as its multi module and default widget-filter map doesn't support multi module
			if(cardContext.getCardLayout().equals(CardLayout.GAUGE_LAYOUT_7.getName()))
			{
				return true;
			}

			//hack ,treat connected app widget similar to custom script.as we can develop any widget using javascript sdk
			if(cardContext.getCardLayout().equals(CardLayout.WEB_LAYOUT_1.getName()))
			{
				JSONObject cardParams = cardContext.getCardParams();
				if (cardParams != null) {
					String type = (String) cardParams.get("type");

					if ("conncetdapp".equals(type) || "connectedapp".equals(type)) {
						return true;
					}
				}
			}
		}
		
		return false;
		
	}
	public static long getModuleIdFromWidget(DashboardWidgetContext widget) throws Exception
	{
		//only modular reports and Module KPI cards and lists  have modules associated with them 
		long moduleId=-1L;
		
		 if(widget.getWidgetType().equals(WidgetType.CHART))
		 {
			 WidgetChartContext widgetChart=(WidgetChartContext)widget;
			 
			 
			 ReportContext report=ReportUtil.getReport(widgetChart.getNewReportId(), true);
			 
			 				 
			 if(report.getTypeEnum()==ReportType.WORKORDER_REPORT || report.getTypeEnum()==ReportType.PIVOT_REPORT)
			 {				 
				 moduleId=report.getModuleId();
			 }
			 				 				 
		 }
		 else if(widget.getWidgetType().equals(WidgetType.LIST_VIEW))
		 {
			 WidgetListViewContext listWidget = (WidgetListViewContext)widget;
			 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			 
			 FacilioModule listModule=modBean.getModule(listWidget.getModuleName());
			 moduleId=listModule.getModuleId();
			 
			 
		 }
		 else if(widget.getWidgetType().equals(WidgetType.CARD))
		 {
			 WidgetCardContext newCardWidget = (WidgetCardContext) widget;
			 if (newCardWidget.getCardLayout().equals(CardLayout.KPICARD_LAYOUT_1.getName()) || newCardWidget.getCardLayout().equals(CardLayout.KPICARD_LAYOUT_2.getName())) {
					JSONObject cardParams = newCardWidget.getCardParams();
//				
					String kpiType = (String) cardParams.get("kpiType");
					if (kpiType.equalsIgnoreCase("module")) {

						
							JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
							long kpiId = (long) kpiObj.get("kpiId");
							KPIContext kpi = KPIUtil.getKPI(kpiId,false);
							moduleId=kpi.getModuleId();
						
					}

				}else if(newCardWidget.getCardLayout().equals("v2_module_card")) {
				 JSONObject cardParams = newCardWidget.getCardParams();
				 if(cardParams.get("reportId") != null && (Long) cardParams.get("reportId") > 0){
					 ReportContext report = ReportUtil.getReport((Long) cardParams.get("reportId"));
					 moduleId = report.getModuleId();
				 }
			 }
		 }
		 else if(widget.getWidgetType().equals(WidgetType.FILTER)){
			 DashboardUserFilterContext userFilterContext = DashboardFilterUtil.getDashboardUserFiltersForWidgetId(widget.getId());
			 if(userFilterContext.getModuleName() != null){
				 ModuleBean modBean = Constants.getModBean();
				 moduleId = modBean.getModule(userFilterContext.getModuleName()).getModuleId();
			 }
		 }
		 
		 return moduleId;
		 
	}
	public static FacilioField getApplicableField(FacilioModule filterModule, FacilioModule parentModule, FacilioModule energyModule) throws Exception {
		FacilioField mappingField = new FacilioField();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(filterModule.getName().equals(parentModule.getName()) || (filterModule.getExtendModule() != null && filterModule.getExtendModule().getName().equals(parentModule.getName()))){
			mappingField = modBean.getField("parentId", energyModule.getName());
		} else if((parentModule.getName().equals(FacilioConstants.ContextNames.METER_MOD_NAME) || parentModule.getName().equals(FacilioConstants.ContextNames.ASSET)) && filterModule.getName().equals(FacilioConstants.ContextNames.SITE)) {
			mappingField = modBean.getField("siteId", parentModule.getName());
		}
		else {
			mappingField = DashboardFilterUtil.getFilterApplicableField(filterModule,parentModule,null);
		}
		return mappingField;
	}
	public static  Map<Long,FacilioField> getUserFilterToWidgetColumnMapping(long userFilterId) throws Exception
	{
		FacilioModule module = ModuleFactory.getDashboardUserFilterWidgetFieldMappingModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(FieldFactory.getDashboardUserFilterWidgetFieldMappingFields()).andCondition(CriteriaAPI.getCondition(
						"USER_FILTER_ID", "userFilterId", "" + userFilterId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> records = builder.get();
		
		
		Map<Long ,FacilioField> widgetIdWidgetFieldMap=new HashMap<>();
		if (records != null && !records.isEmpty()) {
			List<DashboardUserFilterWidgetFieldMappingContext> userFilterWidgetFieldRels=FieldUtil.getAsBeanListFromMapList(records,DashboardUserFilterWidgetFieldMappingContext.class);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for (DashboardUserFilterWidgetFieldMappingContext userFilterWidgetFieldRel : userFilterWidgetFieldRels) {
				
				widgetIdWidgetFieldMap.put(userFilterWidgetFieldRel.getWidgetId(),modBean.getField(userFilterWidgetFieldRel.getWidgetFieldId()));
			}
		
				return widgetIdWidgetFieldMap;
		}
		
		
		return null;
	}

	public static FacilioField getFilterApplicableField(FacilioModule filterModule, FacilioModule widgetModule, Long fieldId){
		
		//see if module's lookup fields refrer to the filter's lookupmodule.
		List<FacilioField> filterApplicableFields = widgetModule.getFields().stream().filter((FacilioField field) -> {
			if (field.getDataTypeEnum() == FieldType.LOOKUP) {
				LookupField lookupField = (LookupField) field;
				if (lookupField.getLookupModule().equals(filterModule)) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
			
//		 else traverse fields again and check if field's lookupModule is a parent of filter's lookupmodule
		//moduleId=-1 ,special type modules, cannot inherit or be extended , so skip parent comparison
		if(filterApplicableFields.size()==0&&filterModule.getModuleId()!=-1)
		{
			filterApplicableFields=widgetModule.getFields().stream().filter((FacilioField field) -> {
				if (field.getDataTypeEnum() == FieldType.LOOKUP) {
					LookupField lookupField = (LookupField) field;
					
						try {
							if(filterModule.getExtendedModuleIds().contains(lookupField.getLookupModule().getModuleId()))
							{
								return true;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							LOGGER.log(Level.SEVERE, "Exception checking extended modules for lookup filter relation");
							e.printStackTrace();
						}
//				
				}
				return false;
			}).collect(Collectors.toList());
				
		}
    
		
		if (filterApplicableFields != null && filterApplicableFields.size() > 0) {
			
			if(fieldId != null && fieldId > 0)
			{
				for(FacilioField field : filterApplicableFields){
					if(field != null && field.getFieldId() == fieldId){
						return field;
					}
				}
			}
			return filterApplicableFields.get(0);
		}
		return null;
	 }

//enum db user filters apply to only widgets of the same module
		// must check if widget module is either same as filter module or one of its
		// children
		// Ex , ticketCategory field has module='ticket' but report_chart corresponding
		// to workorders has module='workorder'
	public static  boolean isEnumFilterApplicableToWidget(FacilioModule filterModule, FacilioModule widgetModule)
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

public static Map<Long,FacilioField>  findCascadingFilterRel(DashboardUserFilterContext currentFilter,List<DashboardUserFilterContext> otherFilters) throws Exception
{
	if(currentFilter.getModule()==null)
	{
		return null;//filter rel only applicable for lookup (module) type filters
	}
	
	
	
	Map<Long,FacilioField> cascadingRelMap=new HashMap<Long, FacilioField>();
	//return a map of ->otherFilterId:applicableToField in currentFilter
	for(DashboardUserFilterContext otherFilter:otherFilters)
	{
		if(currentFilter.getId()!=otherFilter.getId())//dont find filter rel for itself
		{
			
			
			//a ENUM filter can cascade onto a module filter but not vice versa
			//Example Filter1->Workorders , Filter 2,Ticket->Source , 2 can filter out options fetched in 1 , but not the reverse as 2 isnt a module to support filtering its records
			if(otherFilter.getField()!=null&&DashboardFilterUtil.isEnumFilterApplicableToWidget(otherFilter.getField().getModule(), currentFilter.getModule()))//the other filter is of type ENUM
			{
				cascadingRelMap.put(otherFilter.getFieldId(), otherFilter.getField());
			}
			else if(otherFilter.getModule()!=null) {//both current and other are lookup ie module filters,find which col in current relates to other
				Long fieldId = DashboardFilterUtil.getFieldForMappingOnWidget(otherFilter.getDashboardUserFilterJson(), currentFilter.getModule() != null? currentFilter.getModule().getName() : null);
				FacilioField applicableField=DashboardFilterUtil.getFilterApplicableField(otherFilter.getModule(), currentFilter.getModule(), fieldId != null && fieldId > 0 ? fieldId : null);
				if(applicableField!=null)
				{
					cascadingRelMap.put(otherFilter.getId(), applicableField);
					
				}
			}
			
		
		}
	}
	if(cascadingRelMap.size()==0)
	{
		return null;
	}
	
	return cascadingRelMap;
}

public static Criteria getUserFilterCriteriaForModule(DashboardCustomScriptFilter customscriptFilters,
		FacilioModule widgetModule) throws Exception

{
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	widgetModule.setFields(modBean.getAllFields(widgetModule.getName()));
	Boolean useBuildingIsOperator = false;
	Criteria criteria = new Criteria();
	if (customscriptFilters != null && customscriptFilters.getFilterMeta() != null) {

//	Map<FacilioField, List<Long>> fieldValueMap=new HashMap<>();

		// find which field in module applies to which dashboard user filter
		for (DashboardUserFilterContext userFilter : customscriptFilters.getFilterMeta()) {
			FacilioField fieldForFilter = null;
			if (userFilter.getModuleName() != null)// LOOKUP filter
			{
				Long fieldId = DashboardFilterUtil.getFieldForMappingOnWidget(userFilter.getDashboardUserFilterJson(), widgetModule != null ? widgetModule.getName() : null);
				fieldForFilter = getFilterApplicableField(userFilter.getModule(), widgetModule, fieldId != null && fieldId > 0 ? fieldId : null);

			} else if (userFilter.getFieldId() > 0)// ENUM type filter
			{
				FacilioField filterEnumField = userFilter.getField();
				if (isEnumFilterApplicableToWidget(filterEnumField.getModule(), widgetModule)) {
					fieldForFilter = filterEnumField;
				}
			}

			// found field , its either the field in filter itself.enum case , or LOOKUP
			// field referencing same module LOOKUP filter case
			// get corresponding dropdown and generate condition
			if (fieldForFilter != null) {

				String moduleName = userFilter.getModuleName();

				if (moduleName != null && BUILDING_IS_MODULES.contains(moduleName)) {
					useBuildingIsOperator = true;
				}
					
				Map<String,List<String>> userFilterValueMap=customscriptFilters.getFilterValues();				
				List<String> valuesForFilter = userFilterValueMap.get(Long.toString((userFilter.getId())));
				
				
				
				if (valuesForFilter != null && valuesForFilter.size() > 0 &&  valuesForFilter.get(0) != null && !valuesForFilter.get(0).equals("")
						&& (!valuesForFilter.get(0).equals("all") && !valuesForFilter.get(0).equals("others"))) {
					// skip adding any criteria for filter when it's dropdown is 'all'
					// to . generate valuelist for userFilter.optionType==SOME ,all and others cases
					List<Long> valueList = valuesForFilter.stream().map((String value) -> {
						return Long.parseLong(value);
					}).collect(Collectors.toList());
//				fieldValueMap.put(fieldForFilter, valueList);

					Condition condition = CriteriaAPI.getCondition(fieldForFilter, valueList,
							useBuildingIsOperator ? BuildingOperator.BUILDING_IS : PickListOperators.IS);
					criteria.addAndCondition(condition);

				}
//			TO DO : Handle multiple filters applying to same field case, site,building-> apply to RESOURCE field BUILDING IS[SITEID,BUILDINGID]

			}
		}

	}
	if (criteria.isEmpty()) {
		return null;
	} else {
		return criteria;
	}
}

	public static Long getFieldForMappingOnWidget(String dashboardFilterJson, String module)throws Exception
	{
		try {
			if (dashboardFilterJson != null && dashboardFilterJson.contains("widget_field_mapping") && module != null) {
				JSONParser parser = new JSONParser();
				JSONObject dashboard_filter_obj = (JSONObject) parser.parse(dashboardFilterJson);
				if (dashboard_filter_obj != null && dashboard_filter_obj.containsKey("widget_field_mapping")) {
					JSONArray jsonArray = (JSONArray) parser.parse(dashboard_filter_obj.get("widget_field_mapping").toString());
					if (jsonArray != null && jsonArray.size() > 0) {
						int len = jsonArray.size();
						for (int i = 0; i < len; i++) {
							JSONObject temp = (JSONObject) jsonArray.get(i);
							if (temp != null && temp.containsKey("moduleName") && temp.get("moduleName").equals(module)) {
								return (Long) temp.get("fieldId");
							}
						}
					}
				}
			}
		}catch (Exception e)
		{
			LOGGER.log(Level.SEVERE,"Exception finding widget mapping field",e);
		}
		return null;
	}
	public static List<Long> getFilterMappingIdForFilterId(long id) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(ModuleFactory.getDashboardFieldMappingModule().getTableName()).select(FieldFactory.getDashboardFieldMappingsFields())
				.andCondition(CriteriaAPI.getCondition("DASHBOARD_USER_FILTER_ID","dashboardUserFilterId", String.valueOf(id),NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		List<Long> filterMappingIds = new ArrayList<>();
		for(Map<String, Object> prop : props){
			filterMappingIds.add((Long) prop.get("id"));
		}
		return filterMappingIds;
	}
	public static List<Long> getReadingFilterMappingIdForFilterId(Long widgetId, Long filterId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getDashboardReadingWidgetFieldMappingModule().getTableName())
				.select(FieldFactory.getDashboardReadingWidgetFieldMappingFields())
				.andCondition(CriteriaAPI.getCondition("USER_FILTER_ID","userFilterId", String.valueOf(filterId),NumberOperators.EQUALS));
		if(widgetId != null && widgetId > 0){
			builder.andCondition(CriteriaAPI.getCondition("WIDGET_ID","widgetId", String.valueOf(widgetId),NumberOperators.EQUALS));
		}
		List<Map<String, Object>> props = builder.get();
		List<Long> filterMappingIds = new ArrayList<>();
		for(Map<String, Object> prop : props){
			filterMappingIds.add((Long) prop.get("id"));
		}
		return filterMappingIds;
	}
	public static Map<String,DashboardReadingWidgetFilterContext> getReadingFilterMappingsForFilterId(Long filterId, Long widgetId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getDashboardReadingWidgetFieldMappingModule().getTableName())
				.select(FieldFactory.getDashboardReadingWidgetFieldMappingFields());
		if(filterId != null && filterId > 0){
			builder.andCondition(CriteriaAPI.getCondition("USER_FILTER_ID","userFilterId", String.valueOf(filterId),NumberOperators.EQUALS));
		}
		if(widgetId != null && widgetId > 0){
			builder.andCondition(CriteriaAPI.getCondition("WIDGET_ID","widgetId", String.valueOf(widgetId),NumberOperators.EQUALS));
		}
		List<Map<String, Object>> props = builder.get();
		Map<String,DashboardReadingWidgetFilterContext> filterMappings = new HashMap<>();
		for(Map<String, Object> prop : props){
			filterMappings.put((String) prop.get("moduleName"),FieldUtil.getAsBeanFromMap(prop, DashboardReadingWidgetFilterContext.class));
		}
		return filterMappings;
	}
	public static List<DashboardFieldMappingContext> getFilterMappingsForFilterId(long id) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(ModuleFactory.getDashboardFieldMappingModule().getTableName()).select(FieldFactory.getDashboardFieldMappingsFields())
				.andCondition(CriteriaAPI.getCondition("DASHBOARD_USER_FILTER_ID","dashboardUserFilterId", String.valueOf(id),NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		List<DashboardFieldMappingContext> filterMappings = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				filterMappings.add(FieldUtil.getAsBeanFromMap(prop, DashboardFieldMappingContext.class));
			}
		}
		return filterMappings;
	}
	public static List<DashboardUserFilterContext> getDashboardFilterFromDashboardId (long dashboardId, long dashboardTabId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getDashboardFilterModule().getTableName())
				.select(FieldFactory.getDashboardFilterFields())
				.innerJoin(ModuleFactory.getDashboardFilterModule().getTableName()).on("Dashboard_User_Filter.DASHBOARD_FILTER_ID = Dashboard_Filter.id");
		if(dashboardId >0){
			builder.andCustomWhere(ModuleFactory.getDashboardFilterModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		}
		else if(dashboardTabId >0){
			builder.andCustomWhere(ModuleFactory.getDashboardFilterModule().getTableName()+".DASHBOARD_TAB_ID = ?", dashboardTabId);
		}
		List<Map<String, Object>> props = builder.get();
		List<DashboardUserFilterContext> filter = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				filter.add(FieldUtil.getAsBeanFromMap(prop, DashboardUserFilterContext.class));
			}
		}
		return filter;
	}
	public static DashboardUserFilterContext getDashboardUserFiltersForWidgetId(Long widgetId) throws Exception {
		DashboardUserFilterContext newFilterContext = new DashboardUserFilterContext();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(widgetId != null){
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getDashboardUserFilterModule().getTableName())
					.select(FieldFactory.getDashboardUserFilterFields())
					.andCondition(CriteriaAPI.getCondition("WIDGET_ID","widget_id",String.valueOf(widgetId),NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();
			if(props != null && !props.isEmpty()){
				newFilterContext = FieldUtil.getAsBeanListFromMapList(props,DashboardUserFilterContext.class).get(0);
				if(newFilterContext.getFieldId() > 0) {
					newFilterContext.setField(modBean.getField(newFilterContext.getFieldId()));
				}
			}
		}
		return newFilterContext;
	}
	public static Criteria setFieldInCriteria(Criteria criteria, String moduleName)throws Exception
	{
		if(criteria != null)
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			for (String key :  criteria.getConditions().keySet())
			{
				Condition condition = criteria.getConditions().get(key);
				if (module == null || condition == null || condition.getFieldName() == null) continue;
				FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
				if(field == null) continue;
				condition.setField(field);
			}
			return criteria;
		}
		return null;
	}
}
