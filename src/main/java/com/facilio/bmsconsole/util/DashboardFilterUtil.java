package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GetDbTimeLineFilterToWidgetMapping;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterWidgetFieldMappingContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetCardContext.ScriptMode;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.cards.util.CardLayout;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;

public class DashboardFilterUtil {
    private static final Logger LOGGER = Logger.getLogger(DashboardFilterUtil.class.getName());

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
						CardLayout.PMREADINGS_LAYOUT_1.getName(), CardLayout.TABLE_LAYOUT_1.getName()

				)));
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
				}
				
				//filter.setWidgetFieldMap(getUserFilterToWidgetColumnMapping(filter.getId()));

			}
			
			//set cross cascading rels
			try {
				for (DashboardUserFilterContext filter : dashboardUserFilters) {
						if(filter.getShowOnlyRelevantValues()==true)
						{
						filter.setCascadingFilters(DashboardFilterUtil.findCascadingFilterRel(filter,dashboardUserFilters));
						}
				}
				
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE,"Exception finding cascading filter relation");
				
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

	public static void deleteDashboardUserFilterRel(List<Long> toRemove) throws Exception {
		FacilioModule module = ModuleFactory.getDashboardUserFilterModule();

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(toRemove, module));
		builder.delete();

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
			 
			 				 
			 if(report.getTypeEnum()==ReportType.WORKORDER_REPORT)
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
			 if (newCardWidget.getCardLayout().equals(CardLayout.KPICARD_LAYOUT_1.getName())) {
					JSONObject cardParams = newCardWidget.getCardParams();
//				
					String kpiType = (String) cardParams.get("kpiType");
					if (kpiType.equalsIgnoreCase("module")) {

						
							JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
							long kpiId = (long) kpiObj.get("kpiId");
							KPIContext kpi = KPIUtil.getKPI(kpiId);
							moduleId=kpi.getModuleId();
						
					}

				}
		 }
		 
		 
		 return moduleId;
		 
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

public static FacilioField getFilterApplicableField(FacilioModule filterModule, FacilioModule widgetModule){
		
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
				FacilioField applicableField=DashboardFilterUtil.getFilterApplicableField(otherFilter.getModule(), currentFilter.getModule());
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

}
