package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.DashboardPublishContext.PublishingType;
import com.facilio.bmsconsole.context.DashboardSharingContext.SharingType;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.ReportContext.LegendMode;
import com.facilio.bmsconsole.context.ReportContext.ReportChartType;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.cards.util.CardLayout;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.accounts.dto.User;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.BaseLineContext.RangeType;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DashboardUtil.class.getName());
    private static org.apache.log4j.Logger log = LogManager.getLogger(PickListOperators.class.getName());

    public static final String WEATHER_WIDGET_WORKFLOW_STRING = "<workflow> 		<parameter name=\"parentId\" type = \"Number\"/> 		<expression name=\"a\"> 				<module name=\"weather\"/> 				<criteria pattern=\"1\"> 						<condition sequence=\"1\">parentId`=`${parentId}</condition> 				</criteria> 				<orderBy name=\"ttime\" sort=\"desc\"/> 				<limit>1</limit> 		</expression> </workflow>";
	public static final String CARBON_EMISSION_CARD = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 				<module name=\"energydata\"/> 				<criteria pattern=\"1 and 2\"> 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 						<condition sequence=\"2\">ttime`Current Month`</condition> 				</criteria> 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 		</expression> 		<expression name=\"carbonConstant\"> 				<constant>0.44</constant> 		</expression> 	<result>a*carbonConstant</result> </workflow>";
	public static final String CARBON_EMISSION_CARBON_MODULE_CARD = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 				<module name=\"co2emisson\"/> 				<criteria pattern=\"1 and 2\"> 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 						<condition sequence=\"2\">ttime`Current Month`</condition> 				</criteria> 				<field name=\"co2emisson\" aggregate = \"sum\"/> 		</expression> 	<result>a</result> </workflow>";
	
	public static final String ENERGY_COST_LAST_MONTH_CONSUMPTION_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Last Month`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 		<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	public static final String ENERGY_COST_THIS_MONTH_CONSUMPTION_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Current Month upto now`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 		<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	
	public static final String ENERGY_CONSUMPTION_LAST_MONTH_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Last Month`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 	<result>a</result> </workflow>";
	public static final String ENERGY_CONSUMPTION_THIS_MONTH_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Current Month upto now`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression>  		<result>a</result> </workflow>";
	
	
	public static final String ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_WORKFLOW = "<workflow>	<parameter name=\"startTime\" type=\"Number\"/>  		<parameter name=\"endTime\" type=\"Number\"/>	<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">TTIME`baseLine{$$BASELINE_ID$$,0}between`${startTime}, ${endTime}</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 	<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	public static final String LAST_MONTH_THIS_DATE = "<workflow> 		<parameter name=\"startTime\" type=\"Number\"/>  		<parameter name=\"endTime\" type=\"Number\"/>	<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 				<module name=\"energydata\"/> 				<criteria pattern=\"1 and 2\"> 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 						<condition sequence=\"2\">TTIME`baseLine{$$BASELINE_ID$$,0}between`${startTime}, ${endTime}</condition> 				</criteria> 				<field name=\"TTIME\" aggregate = \"max\"/> 			</expression> 		 		<result>a</result> </workflow>";
	
	public static final String ENERGY_COST_THIS_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW = "<workflow>    <parameter name=\"parentId\" type=\"Number\" />    <expression name=\"mainMeter\">       <function>default.getMainEnergyMeter(parentId)</function>    </expression>    <expression name=\"cost\">       <module name=\"cost\" />       <criteria pattern=\"1 and 2\">          <condition sequence=\"1\">parentId`=`${mainMeter}</condition>          <condition sequence=\"2\">ttime`Current Month upto now`</condition>       </criteria>       <field name=\"cost\" aggregate=\"sum\" />    </expression>    <result>cost</result> </workflow>";
	public static final String ENERGY_COST_LAST_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW = "<workflow>    <parameter name=\"parentId\" type=\"Number\" />    <expression name=\"mainMeter\">       <function>default.getMainEnergyMeter(parentId)</function>    </expression>    <expression name=\"cost\">       <module name=\"cost\" />       <criteria pattern=\"1 and 2\">          <condition sequence=\"1\">parentId`=`${mainMeter}</condition>          <condition sequence=\"2\">ttime`Last Month`</condition>       </criteria>       <field name=\"cost\" aggregate=\"sum\" />    </expression>    <result>cost</result> </workflow>";
	public static final String ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_COST_MODULE_WORKFLOW = "<workflow>    <parameter name=\"startTime\" type=\"Number\" />    <parameter name=\"endTime\" type=\"Number\" />    <parameter name=\"parentId\" type=\"Number\" />    <expression name=\"mainMeter\">       <function>default.getMainEnergyMeter(parentId)</function>    </expression>    <expression name=\"a\">       <module name=\"cost\" />       <criteria pattern=\"1 and 2\">          <condition sequence=\"1\">parentId`=`${mainMeter}</condition>          <condition sequence=\"2\">TTIME`baseLine{$$BASELINE_ID$$,0}between`${startTime}, ${endTime}</condition>       </criteria>       <field name=\"cost\" aggregate=\"sum\" />    </expression>       <result>a</result> </workflow>";
	public static final String LAST_MONTH_THIS_DATE_COST_MODULE = LAST_MONTH_THIS_DATE;
	
	public static final String STATIC_WIDGET_WEATHER_CARD = "weathercard";
	public static final String STATIC_WIDGET_ENERGY_COST_CARD = "energycost";
	public static final String STATIC_WIDGET_ENERGY_CARD = "energycard";
	public static final String STATIC_WIDGET_PROFILE_CARD = "profilecard";
	
	public static List<String> RESERVED_DASHBOARD_LINK_NAME  = new ArrayList<>();
	
	public static final String STATIC_WIDGET_WEATHER_CARD_MINI = "weathermini";
	public static final String STATIC_WIDGET_ENERGY_CARBON_CARD_MINI = "carbonmini";
	public static final String STATIC_WIDGET_ENERGY_COST_CARD_MINI = "energycostmini";
	public static final String STATIC_WIDGET_ENERGY_CARD_MINI = "energymini";
	public static final String STATIC_WIDGET_PROFILE_CARD_MINI = "profilemini";
	
	public static final String ENERGY_METER_PURPOSE_MAIN = "Main";
	
	public static String BUILDING_DASHBOARD_KEY = "buildingdashboard";
	public static String SITE_DASHBOARD_KEY = "sitedashboard";
	
	static {
		RESERVED_DASHBOARD_LINK_NAME.add("portfolio");
		RESERVED_DASHBOARD_LINK_NAME.add("buildingdashboard");
	}
	
    public static DelegationType getDelegationType() {
        return DelegationType.DASHBOARD;
    }
    
	public static FacilioField getField(String moduleName,String fieldName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(FacilioField field : modBean.getAllFields(moduleName)) {
			
			if(field.getName().equals(fieldName)) {
				
				return field;
			}
		}
		return null;
	}
	
	public static List<Long> getAllResources(Long spaceID) throws Exception {
		
		List<Long> resourceList = new ArrayList<>();
		
		List<Long> buildingList = new ArrayList<>();
		buildingList.add(spaceID);
		
		List<BaseSpaceContext> baseSpaceContexts = SpaceAPI.getBaseSpaceWithChildren(buildingList);
		if(baseSpaceContexts != null) {
			for(BaseSpaceContext baseSpaceContext :baseSpaceContexts) {
				resourceList.add(baseSpaceContext.getId());
			}
		}
		List<Long> assets = AssetsAPI.getAssetIdsFromBaseSpaceIds(resourceList);
		
		if(assets != null) {
			resourceList.addAll(assets);
		}
		
		return resourceList;
	}
	public static boolean deleteWidgetVsWorkflowContext(long widgetId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWidgetVsWorkflowFields());
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getWidgetVsWorkflowModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("widgetId"), widgetId+"", NumberOperators.EQUALS));
		
		deleteRecordBuilder.delete();
		
		return true;
	}
	public static boolean addWidgetVsWorkflowContext(WidgetVsWorkflowContext widgetVsWorkflowContext) throws Exception {
		if (widgetVsWorkflowContext != null) {
			
			if((widgetVsWorkflowContext.getWorkflowId() == null || widgetVsWorkflowContext.getWorkflowId() <= 0) && widgetVsWorkflowContext.getWorkflowString() != null) {
				WorkflowContext workflow = widgetVsWorkflowContext.getWorkflow();
				Long workflowId = WorkflowUtil.addWorkflow(workflow);
				widgetVsWorkflowContext.setWorkflowId(workflowId);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getWidgetVsWorkflowModule().getTableName())
					.fields(FieldFactory.getWidgetVsWorkflowFields());
			
			Map<String, Object> props = FieldUtil.getAsProperties(widgetVsWorkflowContext);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			widgetVsWorkflowContext.setId((Long) props.get("id"));
			return true;
		}
		return false;
	}

	public static List<EnergyMeterContext> getMainEnergyMeter(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		EnergyMeterPurposeContext energyMeterPurpose = DeviceAPI.getEnergyMetersPurposeByName(ENERGY_METER_PURPOSE_MAIN);
		
		if(energyMeterPurpose != null && spaceList != null && !spaceList.isEmpty()) {
			SelectRecordsBuilder<EnergyMeterContext> selectBuilder =
					new SelectRecordsBuilder<EnergyMeterContext>()
					.select(modBean.getAllFields(module.getName()))
					.module(module)
					.beanClass(EnergyMeterContext.class)
					.andCustomWhere("IS_ROOT= ?", true)
					.andCustomWhere("PARENT_ASSET_ID IS NULL")
					.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",energyMeterPurpose.getId()+"",NumberOperators.EQUALS))
					.maxLevel(0);
			return selectBuilder.get();
		}
		return Collections.EMPTY_LIST;
	}
	
	public static List<EnergyMeterContext> getRootServiceMeters(String buildingList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		EnergyMeterPurposeContext energyMeterPurpose = DeviceAPI.getEnergyMetersPurposeByName(DashboardUtil.ENERGY_METER_PURPOSE_MAIN);
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PURPOSE_ID !="+energyMeterPurpose.getId())
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE-SPACE_ID",buildingList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	public static void deleteDashboardTab(Long dashboardTabId) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB_ID, dashboardTabId);

		FacilioChain deleteDashboardChain = TransactionChainFactory.getDeleteDashboardTabChain();
		deleteDashboardChain.execute(context);
	}

	public static boolean deleteDashboard(Long dashboardId) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
		.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportSpaceFilter().getTableName())
		.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getDashboardSharingModule().getTableName())
		.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();

		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getDashboardPublishingModule().getTableName())
	    .andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();

		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getDashboardModule().getTableName())
		.andCustomWhere("ID = ?", dashboardId);
		
		int rows = deleteRecordBuilder.delete();
		if(rows > 0) {
			return true;
		}
		return false;
	}
	public static String getDataFromValue(Long timeValue, AggregateOperator aggregateOperator) {
		
		String timeKey = null;
		
		if(aggregateOperator.getValue() == 10) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getMonth().getValue() + "-" + dateTime.getYear();
		}
		else if (aggregateOperator.getValue() == 12) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getMonth().getValue() + "-" + dateTime.getDayOfMonth();
		}
		else if(aggregateOperator.getValue() == 18) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getDayOfMonth() + "";
		}
		else if(aggregateOperator.getValue() == 19) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getHour() + "";
		}
		else if(aggregateOperator.getValue() == 20) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getHour() + "-" + dateTime.getMonth().getValue() + "-" + dateTime.getDayOfMonth() + "-" + dateTime.getYear(); 
		}
		else if(aggregateOperator.getValue() == 8) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			timeKey = dateTime.getYear() + "";
		}
		return timeKey;
	}
	
	public static String getStringFromDateAggregator(DateAggregateOperator dateaggr) {
		if(dateaggr.equals(DateAggregateOperator.DAYSOFMONTH) || dateaggr.equals(DateAggregateOperator.FULLDATE) || dateaggr.equals(DateAggregateOperator.WEEKDAY)) {
			return "day";
		}
		else if(dateaggr.equals(DateAggregateOperator.MONTH) || dateaggr.equals(DateAggregateOperator.MONTHANDYEAR)) {
			return "month";
		}
		else if(dateaggr.equals(DateAggregateOperator.YEAR)) {
			return "year";
		}
		else if(dateaggr.equals(DateAggregateOperator.WEEKANDYEAR) || dateaggr.equals(DateAggregateOperator.WEEK)) {
			return "week";
		}
		return null;
	}
	
	public static boolean deleteWidgetFromDashboard(Long dashboardId,Long widgetId) throws Exception {
		
		if(dashboardId != null && widgetId != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId)
				.andCustomWhere("ID = ?", widgetId);
			
			
			int rowsDeleted = deleteRecordBuilder.delete();
			if(rowsDeleted > 0) {
				return true;
			}
		}
		return false;
		
	}
	
	public static JSONObject getStandardVariance(ReportContext report,List<Map<String, Object>> props,List<String> meterList) {
		
		try {
			Double min = null ,max = null,avg = null,sum = (double) 0;
			for(Map<String, Object> prop:props) {
				
				if(AccountUtil.getCurrentOrg().getOrgId() == 116l) {
					if(prop.get("dummyField") != null) {
 						
 						Long currtime = (Long) prop.get("dummyField");
 						
 						if(report.getxAxisaggregateFunction() != null && report.getxAxisaggregateFunction().equals(DateAggregateOperator.FULLDATE.getValue())) {
 							
 							DateRange range = DateOperators.TODAY.getRange(null);
	 						if(currtime < range.getEndTime() && currtime >= range.getStartTime()) {
	 							continue;
	 						}
 						}
 					}
				}
				if(prop.get("value") != null) {
					double value = Double.parseDouble(prop.get("value").toString());
					
					sum = sum + value;
					
					if(min == null && max == null) {
						min = value;
						max = value;
					}
					else {
						min = min < value ? min : value;
						max = max > value ? max : value;
					}
				}
			}
			if(sum > 0 && props.size() > 0) {
				avg = sum / props.size();
			}
			JSONObject variance = new JSONObject();
			variance.put("min", min);
			variance.put("max", max);
			variance.put("avg", avg);
			variance.put("sum", sum);
			
			boolean co2Skip = false;
			
			if(report.getY1AxisUnit() != null && report.getY1AxisUnit().equals("kg")) {
				co2Skip = true;
			}
			if(report.getId() == 5481l) {
				co2Skip = true;
			}
			if(meterList != null && !meterList.isEmpty() && !co2Skip) {
				LOGGER.log(Level.INFO, "meterList --- "+meterList);
				List<Long> bb = new ArrayList<Long>();
		        bb.add(null);
		        meterList.removeAll(bb);
				if(!meterList.isEmpty()) {
					
					List<String> uniqueList = SetUniqueList.decorate(meterList);
					LOGGER.log(Level.INFO, "uniqueList --- "+uniqueList);
			        if(uniqueList.size() == 1) {
			        	
			        	long meterID = Long.parseLong(uniqueList.get(0));
			        	
			        	EnergyMeterContext energyMeter = DeviceAPI.getEnergyMeter(meterID);
			        	if(energyMeter != null && energyMeter.isRoot()) {
			        		BaseSpaceContext purposeSpace = SpaceAPI.getBaseSpace(energyMeter.getPurposeSpace().getId());
			        		variance.put("space", purposeSpace.getId());
			        		double grossFloorArea = 0.0;
			        		if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.SITE.getIntVal()) {
			        			SiteContext sites = SpaceAPI.getSiteSpace(purposeSpace.getId());
			        			if(sites != null) {
			        				grossFloorArea = sites.getGrossFloorArea();
			        			}
			        		}
			        		else if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.BUILDING.getIntVal()) {
			        			BuildingContext building = SpaceAPI.getBuildingSpace(purposeSpace.getId());
			        			if(building != null) {
			        				grossFloorArea = building.getGrossFloorArea();
			        			}
							}
			        		else if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.FLOOR.getIntVal()) {
			        			FloorContext floor = SpaceAPI.getFloorSpace(purposeSpace.getId());
			        			if(floor != null) {
			        				grossFloorArea = floor.getArea();
			        			}
							}
			        		
			        		if(report.getId() == 1012l && grossFloorArea > 0) {
			        			
			        			Map<Integer, Double> ress = getHourlyAggregatedData(props);
			        			
			        			List <Double> hourlyeuis = new ArrayList<>();
			        			if(ress != null && !ress.isEmpty()) {
			        				
			        				for(Integer hour : ress.keySet()) {
			        					
			        					Double value = ress.get(hour);
			        					
			        					double eui = value/grossFloorArea;
					        			hourlyeuis.add(eui);
					        			LOGGER.log(Level.INFO, "hour -- "+hour +" eui --"+eui);
			        				}
			        				LOGGER.log(Level.INFO, "hourlyeuis -- "+hourlyeuis);
			        				sum = 0d;
			        				for(Double hourlyeui :hourlyeuis) {
			        					sum = sum + hourlyeui;
			        				}
			        				variance.put("eui", sum/hourlyeuis.size());
			        			}
			        		}
			        		else {
			        			
			        			if(grossFloorArea > 0 && sum > 0) {
				        			double eui = sum/grossFloorArea;
				        			variance.put("eui", eui);
				        		}
			        			
			        		}
			        	}
			        }
				}
			}
			
			return variance;
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	public static JSONObject getStandardVariance1(ReportContext report,JSONArray props,List<String> meterList) {
		
		try {
			Double min = null ,max = null,avg = null,sum = (double) 0;
			for(Object prop1:props) {
				
				JSONObject prop = (JSONObject) prop1;
				if(AccountUtil.getCurrentOrg().getOrgId() == 116l) {
					if(prop.get("dummyField") != null) {
 						
 						Long currtime = (Long) prop.get("dummyField");
 						
 						if(report.getxAxisaggregateFunction() != null && report.getxAxisaggregateFunction().equals(DateAggregateOperator.FULLDATE.getValue())) {
 							
 							DateRange range = DateOperators.TODAY.getRange(null);
	 						if(currtime < range.getEndTime() && currtime >= range.getStartTime()) {
	 							continue;
	 						}
 						}
 					}
				}
				if(prop.get("value") != null) {
					double value = Double.parseDouble(prop.get("value").toString());
					
					sum = sum + value;
					
					if(min == null && max == null) {
						min = value;
						max = value;
					}
					else {
						min = min < value ? min : value;
						max = max > value ? max : value;
					}
				}
			}
			if(sum > 0 && props.size() > 0) {
				avg = sum / props.size();
			}
			JSONObject variance = new JSONObject();
			variance.put("min", min);
			variance.put("max", max);
			variance.put("avg", avg);
			variance.put("sum", sum);
			
			boolean co2Skip = false;
			
			if(report.getY1AxisUnit() != null && report.getY1AxisUnit().equals("kg")) {
				co2Skip = true;
			}
			if(meterList != null && !meterList.isEmpty() && !co2Skip) {
				LOGGER.log(Level.INFO, "meterList --- "+meterList);
				List<Long> bb = new ArrayList<Long>();
		        bb.add(null);
		        meterList.removeAll(bb);
				if(!meterList.isEmpty()) {
					
					List<String> uniqueList = SetUniqueList.decorate(meterList);
					LOGGER.log(Level.INFO, "uniqueList --- "+uniqueList);
			        if(uniqueList.size() == 1) {
			        	
			        	long meterID = Long.parseLong(uniqueList.get(0));
			        	
			        	EnergyMeterContext energyMeter = DeviceAPI.getEnergyMeter(meterID);
			        	if(energyMeter != null && energyMeter.isRoot()) {
			        		BaseSpaceContext purposeSpace = SpaceAPI.getBaseSpace(energyMeter.getPurposeSpace().getId());
			        		variance.put("space", purposeSpace.getId());
			        		double grossFloorArea = 0.0;
			        		if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.SITE.getIntVal()) {
			        			SiteContext sites = SpaceAPI.getSiteSpace(purposeSpace.getId());
			        			if(sites != null) {
			        				grossFloorArea = sites.getGrossFloorArea();
			        			}
			        		}
			        		else if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.BUILDING.getIntVal()) {
			        			BuildingContext building = SpaceAPI.getBuildingSpace(purposeSpace.getId());
			        			if(building != null) {
			        				grossFloorArea = building.getGrossFloorArea();
			        			}
							}
			        		else if(purposeSpace.getSpaceType() == BaseSpaceContext.SpaceType.FLOOR.getIntVal()) {
			        			FloorContext floor = SpaceAPI.getFloorSpace(purposeSpace.getId());
			        			if(floor != null) {
			        				grossFloorArea = floor.getArea();
			        			}
							}
			        		
			        		if(report.getId() == 1012l && grossFloorArea > 0) {
			        			
			        			Map<Integer, Double> ress = getHourlyAggregatedData(props);
			        			
			        			List <Double> hourlyeuis = new ArrayList<>();
			        			if(ress != null && !ress.isEmpty()) {
			        				
			        				for(Integer hour : ress.keySet()) {
			        					
			        					Double value = ress.get(hour);
			        					
			        					double eui = value/grossFloorArea;
					        			hourlyeuis.add(eui);
					        			LOGGER.log(Level.INFO, "hour -- "+hour +" eui --"+eui);
			        				}
			        				LOGGER.log(Level.INFO, "hourlyeuis -- "+hourlyeuis);
			        				sum = 0d;
			        				for(Double hourlyeui :hourlyeuis) {
			        					sum = sum + hourlyeui;
			        				}
			        				variance.put("eui", sum/hourlyeuis.size());
			        			}
			        		}
			        		else {
			        			
			        			if(grossFloorArea > 0 && sum > 0) {
				        			double eui = sum/grossFloorArea;
				        			variance.put("eui", eui);
				        		}
			        			
			        		}
			        	}
			        }
				}
			}
			
			return variance;
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	
	public static Map<Integer, Double> getHourlyAggregatedData(List<Map<String, Object>> props ) {
		
		
		Map<Integer, Double> res = new HashMap<>();
		for(Map<String, Object> prop :props) {
			
			LOGGER.log(Level.INFO, "getHourlyAggregatedData prop ---- "+prop);
			long ttime = Long.parseLong(prop.get("label").toString());
			Double value = Double.parseDouble(prop.get("value").toString());
			
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(ttime);
			
			ZonedDateTime currentzdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			
			if(zdt.getHour() == currentzdt.getHour()) {
				continue;
			}
			if(res.containsKey(zdt.getHour())) {
				
				Double value1 = res.get(zdt.getHour());
				
				value1 = value1 +value;
				res.put(zdt.getHour(), value1);
			}
			else {
				res.put(zdt.getHour(), value);
			}
			
			LOGGER.log(Level.INFO, "getHourlyAggregatedData res ---- "+res);
		}
		return res;
	}
	public static List<DashboardWidgetContext> getDashboardWidgetsFormDashboardIdOrTabId(Long dashboardId,Long dashboardTabId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
		fields.addAll(FieldFactory.getWidgetStaticFields());
		fields.addAll(FieldFactory.getWidgetWebFields());
		fields.addAll(FieldFactory.getWidgetGraphicsFields());
		fields.addAll(FieldFactory.getWidgetCardFields());
		fields.addAll(FieldFactory.getWidgetSectionFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.leftJoin(ModuleFactory.getWidgetChartModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetListViewModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetListViewModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetStaticModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetStaticModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetWebModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetWebModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetGraphicsModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetGraphicsModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetCardModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetCardModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetSectionModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetSectionModule().getTableName()+".ID");
				if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
					fields.addAll(FieldFactory.getDashboardUserFilterFields());
					selectBuilder.leftJoin(ModuleFactory.getDashboardUserFilterModule().getTableName())
					.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardUserFilterModule().getTableName()+".WIDGET_ID")
					.andCustomWhere(ModuleFactory.getWidgetCardModule().getTableName()+".PARENT_ID IS NULL");
				}
				else{
					selectBuilder.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".TYPE != ?", WidgetType.FILTER.getValue());
				}
		if(dashboardId != null) {
			selectBuilder.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		}
		else if (dashboardTabId != null) {
			selectBuilder.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".DASHBOARD_TAB_ID = ?", dashboardTabId);
		}
		else {
			throw new IllegalArgumentException("No Dashboard or Tab Specified");
		}
				
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) prop.get("type"));
				DashboardWidgetContext dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop, widgetType.getWidgetContextClass());
				
				if(dashboardWidgetContext != null) {
					dashboardWidgetContext.setWidgetVsWorkflowContexts(DashboardUtil.getWidgetVsWorkflowList(dashboardWidgetContext.getId()));
				}
				if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
					if(widgetType.equals(WidgetType.CARD)){
						WidgetCardContext cardContext = (WidgetCardContext) dashboardWidgetContext;
						if(cardContext.getCardLayout().equals(CardLayout.COMBO_CARD.getName())){
							List<WidgetCardContext> childCards = CardUtil.getChildCards(cardContext.getId());
							cardContext.setChildCards(childCards);
						}
					}
				}
				dashboardWidgetContexts.add(dashboardWidgetContext);
			}
		}
		return dashboardWidgetContexts;
	}

	public static List<DashboardWidgetContext> getDashboardWidgetsWithSection(List<DashboardWidgetContext> widget_list)throws Exception
	{
		HashMap<Long, List<DashboardWidgetContext>> sectionId_vs_widget = new HashMap<>();
		List<DashboardWidgetContext> widget_list_with_section = new ArrayList<>();
		if(widget_list.size() > 0)
		{
			for(DashboardWidgetContext widget : widget_list)
			{
				if(widget.getType() != DashboardWidgetContext.WidgetType.SECTION.getValue() && widget.getSectionId() != null && widget.getSectionId() > 0 )
				{
					if(sectionId_vs_widget.containsKey(widget.getSectionId())){
						List<DashboardWidgetContext> section_widget_list = sectionId_vs_widget.get(widget.getSectionId());
						section_widget_list.add(widget);
					}else{
						List<DashboardWidgetContext> section_widget_list = new ArrayList<>();
						section_widget_list.add(widget);
						sectionId_vs_widget.put(widget.getSectionId(), section_widget_list);
					}
				}
			}
			for(DashboardWidgetContext widget : widget_list)
			{
				if(widget.getType() == DashboardWidgetContext.WidgetType.SECTION.getValue() && sectionId_vs_widget.containsKey(widget.getId())){
					((WidgetSectionContext) widget).setWidgets_in_section(sectionId_vs_widget.get(widget.getId()));
					widget_list_with_section.add(widget);
				}
				else if(widget.getSectionId() == null || widget.getSectionId() <=0)
				{
					widget_list_with_section.add(widget);
				}
			}
		}
		return widget_list_with_section;
	}

	
	public static DashboardWidgetContext getWidget(Long widgetId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
		fields.addAll(FieldFactory.getWidgetStaticFields());
		fields.addAll(FieldFactory.getWidgetWebFields());
		fields.addAll(FieldFactory.getWidgetGraphicsFields());
		fields.addAll(FieldFactory.getWidgetCardFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.leftJoin(ModuleFactory.getWidgetChartModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetListViewModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetListViewModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetStaticModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetStaticModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetWebModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetWebModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetGraphicsModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetGraphicsModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetCardModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetCardModule().getTableName()+".ID");
				if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
					fields.addAll(FieldFactory.getDashboardUserFilterFields());
					selectBuilder.leftJoin(ModuleFactory.getDashboardUserFilterModule().getTableName())
							.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardUserFilterModule().getTableName()+".WIDGET_ID");
				}
		selectBuilder.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".ID = ?", widgetId);
		List<Map<String, Object>> props = selectBuilder.get();
		DashboardWidgetContext dashboardWidgetContext = null;
		if (props != null && !props.isEmpty()) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) props.get(0).get("type"));
				dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(props.get(0), widgetType.getWidgetContextClass());
		}
		if(dashboardWidgetContext != null) {
			dashboardWidgetContext.setWidgetVsWorkflowContexts(DashboardUtil.getWidgetVsWorkflowList(dashboardWidgetContext.getId()));
		}
		return dashboardWidgetContext;
	}
	
	public static List<WidgetVsWorkflowContext> getWidgetVsWorkflowList(Long widgetId) throws Exception {
		if(widgetId != null) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWidgetVsWorkflowFields())
					.table(ModuleFactory.getWidgetVsWorkflowModule().getTableName())
					.andCustomWhere(ModuleFactory.getWidgetVsWorkflowModule().getTableName()+".WIDGET_ID= ?",widgetId);
			 List<Map<String, Object>> props = selectBuilder.get();
			 
			 List<WidgetVsWorkflowContext> widgetWorkflowContexts = new ArrayList<>();
			 
			 if (props != null && !props.isEmpty()) {
				 for(Map<String, Object> prop:props) {
					 WidgetVsWorkflowContext widgetVsWorkflowContext = FieldUtil.getAsBeanFromMap(prop, WidgetVsWorkflowContext.class);
					 if(widgetVsWorkflowContext.getWorkflowId() != null) {
						 widgetVsWorkflowContext.setWorkflow(WorkflowUtil.getWorkflowContext(widgetVsWorkflowContext.getWorkflowId()));
					 }
					 widgetWorkflowContexts.add(widgetVsWorkflowContext);
				 }
			}
			 
			return widgetWorkflowContexts;
		}
		return null;
	}

	public static WidgetChartContext getWidgetChartContext(Long reportId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetChartFields();
		fields.addAll(FieldFactory.getWidgetFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetChartModule().getTableName())
				.innerJoin(ModuleFactory.getWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getWidgetChartModule().getTableName()+".ID = ?", reportId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		LOGGER.log(Level.INFO, "111."+props);
		if (props != null && !props.isEmpty()) {
			WidgetChartContext widgetReportContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetChartContext.class);
			return widgetReportContext;
		}
		return null;
	}
	
	public static boolean updateDashboardPublishStatus(DashboardContext dashboard) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardModule().getTableName())
				.fields(FieldFactory.getDashboardFields())
				.andCustomWhere("ID = ?", dashboard.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
		props.put("mobileEnabled", dashboard.isMobileEnabled());
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	public static boolean updateDashboardLinkName(long dashboardId, String linkName) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardModule().getTableName())
				.fields(FieldFactory.getDashboardFields())
				.andCustomWhere("ID = ?", dashboardId);

		Map<String, Object> props = new HashMap<>();
		props.put("linkName", linkName);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	public static JSONArray getDashboardResponseJson(DashboardContext dashboard, boolean optimize) {
		List dashboards = new ArrayList<>();
		dashboards.add(dashboard);
		return getDashboardResponseJson(dashboards, optimize);
	}
	public static JSONArray getMobileDashboardResponseJson(DashboardContext dashboard, boolean optimize) {
		List dashboards = new ArrayList<>();
		dashboards.add(dashboard);
		return getMobileDashboardResponseJson(dashboards, optimize);
	}
	public static JSONArray getMobileDashboardResponseJson(List<DashboardContext> dashboards, boolean optimize) {

		JSONArray result = new JSONArray();

		for(DashboardContext dashboard:dashboards) {
			String dashboardName = dashboard.getDashboardName();
			Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboard.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.widgetMobileJsonObject(optimize, 0));
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("id", dashboard.getId());
			dashboardJson.put("label", dashboardName);
			dashboardJson.put("description", dashboard.getDescription());
			dashboardJson.put("dashboardFolderId", dashboard.getDashboardFolderId());
			dashboardJson.put("group", childrenArray);
			dashboardJson.put("tabs", dashboard.getDashboardTabContexts());
			dashboardJson.put("tabEnabled", dashboard.isTabEnabled());
			result.add(dashboardJson);
		}
		return result;
	}
	
	public static JSONArray getDashboardResponseJson(List<DashboardContext> dashboards, boolean optimize) {
		
		JSONArray result = new JSONArray();
		
//		Collections.sort(dashboards);
		 for(DashboardContext dashboard:dashboards) {
			 String dashboardName = dashboard.getDashboardName();
			Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboard.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.widgetJsonObject(optimize));
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("id", dashboard.getId());
			dashboardJson.put("label", dashboardName);
			if(dashboard.getReportSpaceFilterContext() != null) {
				dashboardJson.put("spaceFilter", "building");
			}
			dashboardJson.put("description", dashboard.getDescription());
			dashboardJson.put("dashboardFolderId", dashboard.getDashboardFolderId());
			dashboardJson.put("linkName", dashboard.getLinkName());
			dashboardJson.put("dateOperator",dashboard.getDateOperator());
			dashboardJson.put("dateValue",dashboard.getDateValue());
			dashboardJson.put("children", childrenArray);
			dashboardJson.put("tabs", dashboard.getDashboardTabContexts());
			dashboardJson.put("tabEnabled", dashboard.isTabEnabled());
			dashboardJson.put("locked",dashboard.getLocked());
			dashboardJson.put("createdByUserId",dashboard.getCreatedByUserId());
			dashboardJson.put("createdBy",dashboard.getCreatedBy());
			dashboardJson.put("dashboardTabPlacement", dashboard.getDashboardTabPlacement());
			dashboardJson.put("clientMetaJsonString", dashboard.getClientMetaJsonString());
			dashboardJson.put("mobileEnabled", dashboard.isMobileEnabled());
			dashboardJson.put("dashboardSharingContext", dashboard.getDashboardSharingContext());
			dashboardJson.put("dashboardFilter",dashboard.getDashboardFilter());
			result.add(dashboardJson);
		 }
		return result;
	}
	
	
	public static JSONArray getDashboardTabResponseJson(List<DashboardTabContext> dashboards) {
		
		JSONArray result = new JSONArray();
		
		 for(DashboardTabContext dashboardTab:dashboards) {

			 Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboardTab.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.widgetJsonObject(false));
			}
			dashboardTab.setClientWidgetJson(childrenArray);
		 }
		return result;
	}
	
	public static JSONArray getReportResponseJson(List<ReportFolderContext> reportFolders) {
		
		JSONArray result = new JSONArray();
		
//		Collections.sort(dashboards);
		 for(ReportFolderContext reportFolder:reportFolders) {
			 String name = reportFolder.getName();
			Collection<ReportContext> reportContexts = reportFolder.getReports();
			JSONArray childrenArray = new JSONArray();
			if(reportContexts != null) {
				for(ReportContext reportContext:reportContexts) {
					childrenArray.add(reportContext.widgetJsonObject());
				}
				JSONObject dashboardJson = new JSONObject();
				dashboardJson.put("id", reportFolder.getId());
				dashboardJson.put("label", name);
				dashboardJson.put("children", childrenArray);
				result.add(dashboardJson);
			}
			else {
				JSONObject dashboardJson = new JSONObject();
				dashboardJson.put("id", reportFolder.getId());
				dashboardJson.put("label", name);
				dashboardJson.put("children", childrenArray);
				result.add(dashboardJson);
			}
		 }
		return result;
	}
	
	public static DashboardContext getDashboardWithWidgets(Long dashboardId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("ID = ?", dashboardId);
		
		AccountUtil.getCurrentOrg().getOrgId();
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboard.getId(),null);
			dashboard.setDashboardWidgets(dashbaordWidgets);
			return dashboard;
		}
		return null;
	}
	
	public static DashboardContext getDashboardForBaseSpace(Long basespaceId, Long moduleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("BASE_SPACE_ID = ?", basespaceId)
				.andCustomWhere("MODULEID = ?", moduleId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			
			return dashboard;
		}
		return null;
	}
	
	public static DashboardContext getDashboard(Long dashboardId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			
			return dashboard;
		}
		return null;
	}
	public static Long getDashboardFromTabId(Long dashboardTabId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardTabFields())
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(dashboardTabId, ModuleFactory.getDashboardTabModule()));

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			Long dashboardId = (Long) props.get(0).get("dashboardId");
			if(dashboardId != null)
			{
				return dashboardId;
			}
		}
		return null;
	}
	public static DashboardContext getDashboardContextFromDashboardId(Long dashboardId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardTabFields())
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);

		List<Map<String, Object>> props = selectBuilder.get();

		if (props == null || props.isEmpty()) {
				return DashboardUtil.getDashboard(dashboardId);
		}
		return null;
	}

	
	public static DashboardContext getDashboard(String dashboardLinkName,long moduleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("MODULEID = ?", moduleId)
				.andCustomWhere("LINK_NAME = ?", dashboardLinkName);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			
			return dashboard;
		}
		return null;
	}
	public static DashboardContext getDashboard(String dashboardLinkName) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("LINK_NAME = ?", dashboardLinkName);

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			return dashboard;
		}
		return null;
	}

	public static DashboardContext getDashboardWithWidgets(String dashboardLinkName, String moduleName) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("LINK_NAME = ?", dashboardLinkName);
		
		if (moduleName != null && !"".equalsIgnoreCase(moduleName.trim())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			selectBuilder.andCustomWhere("MODULEID = ?", module.getModuleId());
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		LOGGER.log(Level.INFO, "selectBuilder getDashboardWithWidgets --"+selectBuilder +" props -- "+props);
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			
			if(dashboard.isTabEnabled()) {
				dashboard.setDashboardTabContexts(getDashboardTabs(dashboard.getId()));
			}
			
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboard.getId(),null);
			dashboard.setDashboardWidgets(dashbaordWidgets);
			dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
			dashboard.setDashboardSharingContext(getDashboardSharing(dashboard.getId()));
			return dashboard;
		}
		return null;
	}
	
	public static DashboardTabContext getDashboardTabWithWidgets(long dashboardTabId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardTabFields())
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(dashboardTabId, ModuleFactory.getDashboardTabModule()));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardTabContext tab = FieldUtil.getAsBeanFromMap(props.get(0), DashboardTabContext.class);
			
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(null,tab.getId());
			tab.setDashboardWidgets(dashbaordWidgets);
			return tab;
		}
		return null;
	}
	
	public static ReportSpaceFilterContext getDashboardSpaceFilter(Long dashboardId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportSpaceFilterFields())
				.table(ModuleFactory.getReportSpaceFilter().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ReportSpaceFilterContext dashboardSpaceFilterContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportSpaceFilterContext.class);
			return dashboardSpaceFilterContext;
		}
		return null;
	}

	public static List<DashboardContext> getDashboardList(String moduleName) throws Exception {			// depricated
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("BASE_SPACE_ID IS NULL")
				.andCustomWhere("MODULEID = ?", module.getModuleId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			Map<Long, DashboardContext> dashboardMap = new HashMap<Long, DashboardContext>();
			for (Map<String, Object> prop : props) {
				DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
				dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
				dashboardMap.put(dashboard.getId(), dashboard);
			}
			return getFilteredDashboards(dashboardMap);
		}
		return null;
	}
	
	public static List<DashboardTabContext> getDashboardTabs(long dashboardId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardTabFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardTabFields())
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("dashboardId"), dashboardId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			
			List<DashboardTabContext> dashboardTabContexts =  FieldUtil.getAsBeanListFromMapList(props, DashboardTabContext.class);
			dashboardTabContexts = getOrderedTabs(dashboardTabContexts);
			
			return dashboardTabContexts;
		}
		return null;
	}
	
	public static List<DashboardTabContext> getOrderedTabs(List<DashboardTabContext> dashboardTabContexts) {
		
		List<DashboardTabContext> parentTabs = new ArrayList<>();
		Map<Long,List<DashboardTabContext>> parentVsChildMap = new HashMap<>();
		for(DashboardTabContext dashboardTabContext :dashboardTabContexts) {
			if(dashboardTabContext.getDashboardTabId() > 0) {
				List<DashboardTabContext> childList = parentVsChildMap.get(dashboardTabContext.getDashboardTabId()) != null ? parentVsChildMap.get(dashboardTabContext.getDashboardTabId()) : new ArrayList<>();
				childList.add(dashboardTabContext);
				parentVsChildMap.put(dashboardTabContext.getDashboardTabId(), childList);
 			}
		}
		
		for(DashboardTabContext dashboardTabContext :dashboardTabContexts) {
			
			dashboardTabContext.setChildTabs(parentVsChildMap.get(dashboardTabContext.getId()));
			
			if(dashboardTabContext.getDashboardTabId() < 0) {
				parentTabs.add(dashboardTabContext);
				if(dashboardTabContext.getChildTabs() != null) {
					Collections.sort(dashboardTabContext.getChildTabs());
				}
			}
		}
		Collections.sort(parentTabs);
		return parentTabs;
	}
	
	public static List<DashboardContext> getDashboardFromFolderId(long folderId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("dashboardFolderId"), folderId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null) {
			return FieldUtil.getAsBeanListFromMapList(props, DashboardContext.class);
		}
		return null;
	}
	
	public static List<DashboardContext> getDashboardFromFolders(List<Long> folderIds, String moduleName, boolean getOnlyMobileDashboard) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardFields());
		StringJoiner ids = new StringJoiner(",");
		folderIds.stream().forEach(f -> ids.add(String.valueOf(f)));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("dashboardFolderId"), ids.toString(), NumberOperators.EQUALS))
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("BASE_SPACE_ID IS NULL");
		
		if(moduleName != null) {
			FacilioModule module = modBean.getModule(moduleName);
			selectBuilder.andCustomWhere("MODULEID = ?", module.getModuleId());
		}
		
		if(getOnlyMobileDashboard) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
		}
		else if(AccountUtil.getCurrentOrg().getOrgId() == 169l) {	// temp fix for emrill
			selectBuilder.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID != ?", 1058);
		}
		
		if(getOnlyMobileDashboard) {
			selectBuilder.orderBy("DISPLAY_ORDER asc");
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null) {
			return FieldUtil.getAsBeanListFromMapList(props, DashboardContext.class);
		}
		return null;
	}
	
	public static List<DashboardFolderContext> getDashboardList(String moduleName,boolean getOnlyMobileDashboard, long appId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<DashboardFolderContext> folders = getDashboardFolder(moduleName, appId);
		
		if (folders != null && !folders.isEmpty()) {
		List<Long> folderIds = folders.stream().map(a -> a.getId()).collect(Collectors.toList());
		List<DashboardContext> dashboards = getDashboardFromFolders(folderIds, moduleName, getOnlyMobileDashboard);
		
		Map<Long, DashboardContext> dashboardMap = new HashMap<Long, DashboardContext>();
		ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
		if (dashboards != null && !dashboards.isEmpty()) {
			for (DashboardContext dashboard : dashboards) {
				dashboard.setDashboardSharingContext(getDashboardSharing(dashboard.getId()));
//				if(!app.getLinkName().equals(ApplicationLinkNames.FACILIO_MAIN_APP)) {
//					dashboard.setSpaceFilteredDashboardSettings(getSpaceFilteredDashboardSettings(dashboard.getId()));
//					dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
//				}
				dashboard.setIsTabPresent(!CollectionUtils.isEmpty(getDashboardTabs(dashboard.getId())));

				if(dashboard.getModuleId() > 0) {
					dashboard.setModuleName(modBean.getModule(dashboard.getModuleId()).getName());
				}
				
				if(dashboard.isTabEnabled()) {
					dashboard.setDashboardTabContexts(getDashboardTabs(dashboard.getId()));
				}
				
				FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterChain();
				FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
				getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);		
				getDashboardFilterChain.execute();
				dashboardMap.put(dashboard.getId(), dashboard);
			}
		}
		List<DashboardContext> dashboardsFinalList = new ArrayList<>();

		if (getOnlyMobileDashboard)
		{
			dashboardsFinalList = sortDashboardByOrder(getFilteredDashboards(dashboardMap));

		}
		else {
			dashboardsFinalList = getFilteredDashboards(dashboardMap);
		}

		if(getOnlyMobileDashboard) {
			splitBuildingDashboardForMobile(dashboardsFinalList);
		}

		for(DashboardFolderContext folder :folders) {
			for(DashboardContext dashboard :dashboardsFinalList) {
				if(dashboard.getDashboardFolderId() == folder.getId()) {
					folder.addDashboard(dashboard);
				}
			}
		}

		if (getOnlyMobileDashboard) {
			folders.removeIf(folder -> CollectionUtils.isEmpty(folder.getDashboards()));
			return sortDashboardFolderByOrder(folders);
		}
		else {
			return folders;
		}
		}
		return null;
	}
	
	public static List<DashboardFolderContext> getDashboardListWithFolder(String moduleName,boolean getOnlyMobileDashboard) throws Exception {
		
//		Criteria criteria = PermissionUtil.getCurrentUserPermissionCriteria("dashboard", "read");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
//				.andCriteria(criteria)
				.andCustomWhere("BASE_SPACE_ID IS NULL");
		
		if(moduleName != null) {
			FacilioModule module = modBean.getModule(moduleName);
			selectBuilder.andCustomWhere("MODULEID = ?", module.getModuleId());
		}
		
				
		if(getOnlyMobileDashboard) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
		}
		else if(AccountUtil.getCurrentOrg().getOrgId() == 169l) {	// temp fix for emrill
			selectBuilder.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID != ?", 1058);
		}
		
		if(getOnlyMobileDashboard) {
			selectBuilder.orderBy("DISPLAY_ORDER asc");
		}
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			Map<Long, DashboardContext> dashboardMap = new HashMap<Long, DashboardContext>();
			for (Map<String, Object> prop : props) {
				DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
				dashboard.setDashboardSharingContext(getDashboardSharing(dashboard.getId()));
				if(dashboard.getModuleId() > 0) {
					dashboard.setModuleName(modBean.getModule(dashboard.getModuleId()).getName());
				}
				
				if(dashboard.isTabEnabled()) {
					dashboard.setDashboardTabContexts(getDashboardTabs(dashboard.getId()));
				}
				
				FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterChain();
				FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
				getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);		
				getDashboardFilterChain.execute();
				dashboardMap.put(dashboard.getId(), dashboard);
			}
			List<DashboardContext> dashboards = new ArrayList<>();

			if (getOnlyMobileDashboard) 
			{
				dashboards = sortDashboardByOrder(getFilteredDashboards(dashboardMap));

			}
			else {
				dashboards = getFilteredDashboards(dashboardMap);
			}

			if(getOnlyMobileDashboard) {
				splitBuildingDashboardForMobile(dashboards);
			}
			
			List<DashboardFolderContext> folders = getDashboardFolder(moduleName, -1);
			for(DashboardFolderContext folder :folders) {
				for(DashboardContext dashboard :dashboards) {
					if(dashboard.getDashboardFolderId() == folder.getId()) {
						folder.addDashboard(dashboard);
					}
				}
			}
			if (getOnlyMobileDashboard) {
				return sortDashboardFolderByOrder(folders);
			}
			else {
				return folders;
			}
		}
		return null;
	}
	
	public static void getAllBuildingsForDashboard(List<DashboardContext> dashboards) throws Exception {
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		 List<BuildingContext> buildings = new ArrayList<>();
		 List<Long> spaceIds = new ArrayList<>();
		 
         buildings = SpaceAPI.getAllBuildings();
         
         for(BuildingContext building:buildings) {
         	spaceIds.add(building.getId());
         }
			
			String spaceIdsString = new String();			
			
			List<DashboardContext> duplicatedDashboardContext =  new ArrayList<>();
			List<DashboardContext> buildDashboardtoBeRemoved = new ArrayList<>(); 
			for(DashboardContext dashboard :dashboards) {
				if(dashboard.getLinkName().equals(BUILDING_DASHBOARD_KEY) && dashboard.getBaseSpaceId() == null) {
					
					buildDashboardtoBeRemoved.add(dashboard);
					
					FacilioModule module = modBean.getModule(dashboard.getModuleId());
					if(module.getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						spaceIdsString = StringUtils.join(spaceIds, ',');
						
						List<EnergyMeterContext> energyMeters = getMainEnergyMeter(spaceIdsString);
						spaceIds.clear();
						for(EnergyMeterContext energyMeter:energyMeters) {
							spaceIds.add(energyMeter.getPurposeSpace().getId());
						}
					}
					
					for(Long spaceId:spaceIds) {
						DashboardContext buildingDashboard = (DashboardContext) dashboard.clone();
						buildingDashboard.setBaseSpaceId(spaceId);
						buildingDashboard.setId(-1l);
						
						buildingDashboard.setLinkName(buildingDashboard.getLinkName()+"/"+spaceId);
						
						buildingDashboard.setDashboardName(ResourceAPI.getResource(spaceId).getName());
						duplicatedDashboardContext.add(buildingDashboard);
					}
				}
			}
			dashboards.removeAll(buildDashboardtoBeRemoved);
			dashboards.addAll(duplicatedDashboardContext);
	}
	
	public static void getAllSitesForDashboard(List<DashboardContext> dashboards, List<Long> baseSpaceIds) throws  Exception {
		List<SiteContext> sites = new ArrayList<>();
		 List<Long> spaceIds = new ArrayList<>();
		 
        sites = SpaceAPI.getAllSites();
        
        for(SiteContext site:sites) {
        	if(!(baseSpaceIds.contains(site.getId()))) {
        		spaceIds.add(site.getId());
        	}
        }
			
			List<DashboardContext> dbContext =  new ArrayList<>();
			DashboardContext siteDashboardToBeRemoved = null;
			for(DashboardContext dashboard :dashboards) {
				if(dashboard.getLinkName().equals(SITE_DASHBOARD_KEY) && dashboard.getBaseSpaceId() == null) {
					siteDashboardToBeRemoved = dashboard;
					for(Long spaceId:spaceIds) {
						DashboardContext siteDashboard1 = (DashboardContext) dashboard.clone();
						siteDashboard1.setBaseSpaceId(spaceId);
						siteDashboard1.setId(-1l);
						
						siteDashboard1.setLinkName(siteDashboard1.getLinkName()+"/"+spaceId);
						siteDashboard1.setDashboardName(ResourceAPI.getResource(spaceId).getName());
						dbContext.add(siteDashboard1);
					}
				}
			}
			dashboards.remove(siteDashboardToBeRemoved);
			dashboards.addAll(dbContext);
		
	}
	
	public static List<DashboardFolderContext> getDashboardListWithFolder(boolean getOnlyMobileDashboard,boolean isfromPortal) throws Exception {	// wrong impl
	
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("BASE_SPACE_ID IS NULL");
		
		if(getOnlyMobileDashboard) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			Map<Long, DashboardContext> dashboardMap = new HashMap<Long, DashboardContext>();
			for (Map<String, Object> prop : props) {
				DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
				FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterChain();
				FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
				getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);
				getDashboardFilterChain.execute();
				dashboard.setSpaceFilteredDashboardSettings(getSpaceFilteredDashboardSettings(dashboard.getId()));
				dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				FacilioModule module = modBean.getModule(dashboard.getModuleId());
				dashboard.setModuleName(module.getName());
				dashboard.setModuleName(modBean.getModule(dashboard.getModuleId()).getName());
				List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboard.getId(),null);
				dashboard.setDashboardWidgets(dashbaordWidgets);
				if(dashboard.isTabEnabled()) {
					dashboard.setDashboardTabContexts(getDashboardTabs(dashboard.getId()));
				}
				dashboardMap.put(dashboard.getId(), dashboard);
			}
			List<DashboardContext> dashboards = getFilteredDashboards(dashboardMap,isfromPortal);
			
			if(getOnlyMobileDashboard) {
				splitBuildingDashboardForMobile(dashboards);
			}
			
			return sortDashboardByFolder(dashboards,getOnlyMobileDashboard);
		}
		return null;
	}
	
	private static void buildingchanges (List<DashboardContext> dashboards, List<Long> baseSpaceIds) throws Exception {
		List<DashboardContext> dbContext =  new ArrayList<>();
		for(DashboardContext dashboard :dashboards) {
			if(dashboard.getLinkName().equals(BUILDING_DASHBOARD_KEY) && dashboard.getBaseSpaceId() == null) {
		            for(int i=0;i<baseSpaceIds.size();i++) {
		            	DashboardContext buildingDashboard1 = (DashboardContext) dashboard.clone();
						buildingDashboard1.setBaseSpaceId(baseSpaceIds.get(i));
						buildingDashboard1.setId(-1l);
						dbContext.add(buildingDashboard1);
		             }
			}
		}
		dashboards.addAll(dbContext);
	}
	
	
	private static void splitBuildingDashboardForMobile(List<DashboardContext> dashboards) throws Exception {
		
		DashboardContext buildingDashboard = null;
		for(DashboardContext dashboard :dashboards) {
			if(dashboard.getLinkName().equals(BUILDING_DASHBOARD_KEY)) {
				
				buildingDashboard  = dashboard;
				if(dashboard.getSpaceFilteredDashboardSettings() != null && !dashboard.getSpaceFilteredDashboardSettings().isEmpty()) {
					
					for(SpaceFilteredDashboardSettings spaceFilteredDashboardSetting : dashboard.getSpaceFilteredDashboardSettings()) {
						if(spaceFilteredDashboardSetting.getMobileEnabled()) {
							DashboardContext buildingDashbord1 = (DashboardContext) dashboard.clone();
							ResourceContext resource = ResourceAPI.getResource(spaceFilteredDashboardSetting.getBaseSpaceId());
							buildingDashbord1.setDashboardName(resource.getName());
							buildingDashbord1.setLinkName(buildingDashbord1.getLinkName()+"/"+spaceFilteredDashboardSetting.getBaseSpaceId());
							dashboards.add(buildingDashbord1);
						}
					}
					break;
				}
			}
		}
		dashboards.remove(buildingDashboard);
	}

	public static List<DashboardFolderContext> getDashboardTree(String moduleName) throws Exception {		// depricated
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("BASE_SPACE_ID IS NULL")
				.andCustomWhere("MODULEID = ?", module.getModuleId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<Long> dashboardIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			
			Map<Long, DashboardContext> dashboardMap = new HashMap<Long, DashboardContext>();
			for (Map<String, Object> prop : props) {
				DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
				if(dashboard.getLinkName().equals("buildingdashboard") && dashboard.getOrgId() == 58l) {
					List<Long> s = new ArrayList<>();
					s.add(382l);
					dashboard.setBuildingExcludeList(s);
				}
				dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
				dashboardMap.put(dashboard.getId(), dashboard);
				dashboardIds.add(dashboard.getId());
			}
			List<DashboardContext> dashboards = getFilteredDashboards(dashboardMap);
			
			DashboardContext portfolioDashboard = null;
			DashboardContext commercialPortfolioDashboard = null;
			DashboardContext residentialPortfolioDashboard = null;
			DashboardContext buildingDashboard = null;
			DashboardContext siteDashboard = null;
			
			List<DashboardContext> otherDashboards = new ArrayList<>();
			
			for (DashboardContext dashboard : dashboards) {
				if ("portfolio".equalsIgnoreCase(dashboard.getLinkName())) {
					portfolioDashboard = dashboard;
				}
				else if ("buildingdashboard".equalsIgnoreCase(dashboard.getLinkName())) {
					buildingDashboard = dashboard;
				}
				else if ("sitedashboard".equalsIgnoreCase(dashboard.getLinkName())) {
					siteDashboard = dashboard;
				}
				else if ("commercial".equalsIgnoreCase(dashboard.getLinkName())) {
					commercialPortfolioDashboard = dashboard;
				}
				else if ("residential".equalsIgnoreCase(dashboard.getLinkName())) {
					residentialPortfolioDashboard = dashboard;
				}
				else {
					otherDashboards.add(dashboard);
				}
			}
			
			DashboardFolderContext portfolioFolder = new DashboardFolderContext();
			portfolioFolder.setName("Portfolio");
			
			DashboardFolderContext commercialPortfolioFolder = new DashboardFolderContext();
			commercialPortfolioFolder.setName("Commercial");
			
			DashboardFolderContext residentialPortfolioFolder = new DashboardFolderContext();
			residentialPortfolioFolder.setName("Residential");
			
			List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
			List<SiteContext> sites = SpaceAPI.getAllSites();
			EnumField siteTypeField = (EnumField) modBean.getField("siteType", "site");
			
			if (portfolioDashboard != null) {	
				if (portfolioDashboard != null && ((buildings != null && buildings.size() > 1) || (sites != null && sites.size() > 1))) {
					portfolioFolder.addDashboard(portfolioDashboard);
				}
			}
				
			if (buildingDashboard != null && buildings != null && buildings.size() > 0) {
				for (BuildingContext building : buildings) {
					
					DashboardContext bd = new DashboardContext();
					bd.setId(buildingDashboard.getId());
					bd.setOrgId(buildingDashboard.getOrgId());
					bd.setModuleId(buildingDashboard.getModuleId());
					bd.setPublishStatus(buildingDashboard.getPublishStatus());
					bd.setDashboardName(building.getName());
					bd.setLinkName(buildingDashboard.getLinkName() + "/" + building.getId());
					bd.setDashboardFolderId(buildingDashboard.getDashboardFolderId());
					bd.setReportSpaceFilterContext(buildingDashboard.getReportSpaceFilterContext());
					bd.setBaseSpaceId(building.getId());
					bd.setBuildingExcludeList(buildingDashboard.getBuildingExcludeList());
					bd.setCreatedByUserId(buildingDashboard.getCreatedByUserId());
					bd.setDisplayOrder(buildingDashboard.getDisplayOrder());
					
					portfolioFolder.addDashboard(bd);
				}
			}
			
			if (siteDashboard != null && sites != null && sites.size() > 0) {
				for (SiteContext site : sites) {
					
					DashboardContext bd = new DashboardContext();
					bd.setId(siteDashboard.getId());
					bd.setOrgId(siteDashboard.getOrgId());
					bd.setModuleId(siteDashboard.getModuleId());
					bd.setPublishStatus(siteDashboard.getPublishStatus());
					bd.setDashboardName(site.getName());
					bd.setLinkName(siteDashboard.getLinkName() + "/" + site.getId());
					bd.setDashboardFolderId(siteDashboard.getDashboardFolderId());
					bd.setReportSpaceFilterContext(siteDashboard.getReportSpaceFilterContext());
					bd.setBaseSpaceId(site.getId());
					bd.setBuildingExcludeList(siteDashboard.getBuildingExcludeList());
					bd.setCreatedByUserId(siteDashboard.getCreatedByUserId());
					bd.setDisplayOrder(siteDashboard.getDisplayOrder());
					
					portfolioFolder.addDashboard(bd);
				}
			}
			if (commercialPortfolioDashboard != null) {
				commercialPortfolioFolder.addDashboard(commercialPortfolioDashboard);
			}
			if (commercialPortfolioDashboard != null && buildingDashboard != null) {

				List<SiteContext> commercialSites = SpaceAPI.getAllSitesOfType(siteTypeField.getIndex("Commercial"));
				if(commercialSites != null) {
					for(SiteContext site : commercialSites) {
						for (BuildingContext building : buildings) {
							if (building.getSiteId() == site.getId()) {
								DashboardContext bd = new DashboardContext();
								bd.setId(buildingDashboard.getId());
								bd.setOrgId(buildingDashboard.getOrgId());
								bd.setModuleId(buildingDashboard.getModuleId());
								bd.setPublishStatus(buildingDashboard.getPublishStatus());
								bd.setDashboardName(building.getName());
								bd.setLinkName(buildingDashboard.getLinkName() + "/" + building.getId());
								bd.setDashboardFolderId(buildingDashboard.getDashboardFolderId());
								bd.setReportSpaceFilterContext(buildingDashboard.getReportSpaceFilterContext());
								bd.setBaseSpaceId(building.getId());
								bd.setBuildingExcludeList(buildingDashboard.getBuildingExcludeList());
								bd.setCreatedByUserId(buildingDashboard.getCreatedByUserId());
								bd.setDisplayOrder(buildingDashboard.getDisplayOrder());

								commercialPortfolioFolder.addDashboard(bd);
							}
						}
					}
				}
			}

			if (residentialPortfolioDashboard != null) {
				residentialPortfolioFolder.addDashboard(residentialPortfolioDashboard);
			}

			if (residentialPortfolioDashboard != null && buildingDashboard != null) {
				// Residential - 3 we have moved this to user defined enum
				List<SiteContext> residentialSites = SpaceAPI.getAllSitesOfType(siteTypeField.getIndex("Residential"));
				if(residentialSites != null) {
					for(SiteContext site : residentialSites) {
						for (BuildingContext building : buildings) {
							if (building.getSiteId() == site.getId()) {

								DashboardContext bd = new DashboardContext();
								bd.setId(buildingDashboard.getId());
								bd.setOrgId(buildingDashboard.getOrgId());
								bd.setModuleId(buildingDashboard.getModuleId());
								bd.setPublishStatus(buildingDashboard.getPublishStatus());
								bd.setDashboardName(building.getName());
								bd.setLinkName(buildingDashboard.getLinkName() + "/" + building.getId());
								bd.setDashboardFolderId(buildingDashboard.getDashboardFolderId());
								bd.setReportSpaceFilterContext(buildingDashboard.getReportSpaceFilterContext());
								bd.setBaseSpaceId(building.getId());
								bd.setBuildingExcludeList(buildingDashboard.getBuildingExcludeList());
								bd.setCreatedByUserId(buildingDashboard.getCreatedByUserId());
								bd.setDisplayOrder(buildingDashboard.getDisplayOrder());

								residentialPortfolioFolder.addDashboard(bd);
							}
						}
					}
				}
			}

			List<DashboardFolderContext> dList = sortDashboardByFolder(otherDashboards);
			if (residentialPortfolioFolder.getDashboards() != null && residentialPortfolioFolder.getDashboards().size() > 0) {
				dList.add(0, residentialPortfolioFolder);
			}
			if (commercialPortfolioFolder.getDashboards() != null && commercialPortfolioFolder.getDashboards().size() > 0) {
				dList.add(0, commercialPortfolioFolder);
			}
			if (portfolioFolder.getDashboards() != null && portfolioFolder.getDashboards().size() > 0) {
				dList.add(0, portfolioFolder);
			}
			return dList;
		}
		return null;
	}

	public static List<DashboardFolderContext> sortDashboardByFolder(List<DashboardContext> dashboards) throws Exception {
		return sortDashboardByFolder( dashboards, false);
	};

	public static List<DashboardFolderContext> sortDashboardByFolder(List<DashboardContext> dashboards, boolean getOnlyMobileDashboard) throws Exception {
		
		List<DashboardFolderContext> dashboardFolderContexts = new ArrayList<>();
		
		for(DashboardContext dashboard :dashboards) {
			
			if(dashboard.getDashboardFolderId() != null && dashboard.getDashboardFolderId() > 0) {
				
				boolean found = false;
				for(DashboardFolderContext dashboardFolderContext :dashboardFolderContexts) {
					
					if(dashboard.getDashboardFolderId().equals(dashboardFolderContext.getId())) {
						dashboardFolderContext.addDashboard(dashboard);
						found = true;
						break;
					}
				}
				if(!found) {
					DashboardFolderContext folder = getDashboardFolder(dashboard.getDashboardFolderId());
					folder.addDashboard(dashboard);
					dashboardFolderContexts.add(folder);
				}
			}
		}
		dashboardFolderContexts.removeIf(folder -> (getOnlyMobileDashboard && CollectionUtils.isEmpty(folder.getDashboards())));
		return dashboardFolderContexts;
	}
	public static List<DashboardContext> sortDashboardByOrder(List<DashboardContext> dashboards) throws Exception {

		dashboards.sort(new dashboardSortByOrder());
		
		return dashboards;
	}
	public static List<DashboardFolderContext> sortDashboardFolderByOrder(List<DashboardFolderContext> folders) throws Exception {

		folders.sort(new dashboardFolderSortByOrder());
		
		return folders;
	}
	public static List<DashboardContext> getFilteredDashboards(Map<Long, DashboardContext> dashboardMap) throws Exception {
		return getFilteredDashboards(dashboardMap,false);
	}
	
	public static List<DashboardContext> getFilteredPortalDashboards(Map<Long, DashboardContext> dashboardMap) throws Exception {
		
		List<Long> dashboardIds = new ArrayList<>(dashboardMap.keySet());
		
		List<DashboardContext> dashboardList = new ArrayList<DashboardContext>();
 

		
		if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null && AccountUtil.getCurrentUser().getRole().getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN)) {
			dashboardList.addAll(dashboardMap.values());
			return dashboardList;
		}
		
		if (!dashboardMap.isEmpty()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardPublishingFields())
					.table(ModuleFactory.getDashboardPublishingModule().getTableName())
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.andCondition(CriteriaAPI.getCondition("Dashboard_Publishing.DASHBOARD_ID", "dashboardId", StringUtils.join(dashboardIds, ","), NumberOperators.EQUALS));
			
			
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					DashboardPublishContext dashboardPublishing = FieldUtil.getAsBeanFromMap(prop, DashboardPublishContext.class);
					if (dashboardIds.contains(dashboardPublishing.getDashboardId())) {
						dashboardIds.remove(dashboardPublishing.getDashboardId());
					}
					
					if(!dashboardList.contains(dashboardMap.get(dashboardPublishing.getDashboardId()))) {
						if (AccountUtil.getCurrentUser().getApplicationId() == dashboardPublishing.getAppId()) {
							if (dashboardPublishing.getPublishingTypeEnum().equals(PublishingType.USER) && dashboardPublishing.getOrgUserId() == AccountUtil.getCurrentAccount().getUser().getOuid()) {
								dashboardList.add(dashboardMap.get(dashboardPublishing.getDashboardId()));
							}
							else if (dashboardPublishing.getPublishingTypeEnum().equals(PublishingType.ALL_USER)) {
								dashboardList.add(dashboardMap.get(dashboardPublishing.getDashboardId()));
							}
						}
					}
				}
				if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
					for (Long dashboardId : dashboardIds) {
						dashboardList.add(dashboardMap.get(dashboardId));
					}
				}
			}
			else {
				if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) { 
					dashboardList.addAll(dashboardMap.values());
				}
			}
		}
		return dashboardList;
	}
	public static List<DashboardContext> getOrginalFilteredDashboards(Map<Long, DashboardContext> dashboardMap,boolean isFromPortal) throws Exception {
		List<Long> dashboardIds = new ArrayList<>(dashboardMap.keySet());
		
		List<DashboardContext> dashboardList = new ArrayList<DashboardContext>();
		
		List<Integer> portalList = new LinkedList<Integer>(); 
		portalList.add(DashboardSharingContext.SharingType.PORTAL.getIntVal()); 
		portalList.add(DashboardSharingContext.SharingType.ALL_PORTAL_USER.getIntVal()); 

		
		if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null && AccountUtil.getCurrentUser().getRole().getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN)) {
			dashboardList.addAll(dashboardMap.values());
			return dashboardList;
		}
		
		if (!dashboardMap.isEmpty()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardSharingFields())
					.table(ModuleFactory.getDashboardSharingModule().getTableName())
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.andCondition(CriteriaAPI.getCondition("Dashboard_Sharing.DASHBOARD_ID", "dashboardId", StringUtils.join(dashboardIds, ","), NumberOperators.EQUALS));
			
			if(!isFromPortal) {
				selectBuilder.andCustomWhere("Dashboard_Sharing.Sharing_type != ? ", DashboardSharingContext.SharingType.PORTAL.getIntVal());
//				selectBuilder.andCustomWhere("Dashboard_Sharing.Sharing_type != ? ", DashboardSharingContext.SharingType.ALL_PORTAL_USER.getIntVal());

			}
			else {
//				selectBuilder.andCustomWhere("Dashboard_Sharing.Sharing_type = ? ", DashboardSharingContext.SharingType.PORTAL.getIntVal());
				selectBuilder.andCondition(CriteriaAPI.getCondition("Dashboard_Sharing.Sharing_type", "sharingType", StringUtils.join(portalList, ","), NumberOperators.EQUALS));
			}
			
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				User currentuser = AccountUtil.getCurrentAccount().getUser();
				List<User> alluser = new ArrayList<User>();
				alluser.add(currentuser);
				try {
					User delegateuser = DelegationUtil.getUser(AccountUtil.getCurrentAccount().getUser(), System.currentTimeMillis(), getDelegationType());
				    if(delegateuser !=null)
				    {
					 alluser.add(delegateuser);
				    }
				}
				catch(Exception e)
				{
		    	 LOGGER.info("Exception occurred :"+ e);
				}
				for (Map<String, Object> prop : props) {
					DashboardSharingContext dashboardSharing = FieldUtil.getAsBeanFromMap(prop, DashboardSharingContext.class);
					if (dashboardIds.contains(dashboardSharing.getDashboardId())) {
						dashboardIds.remove(dashboardSharing.getDashboardId());
					}
					for(User user : alluser)
					{
					if(!dashboardList.contains(dashboardMap.get(dashboardSharing.getDashboardId()))) {
						if (dashboardSharing.getSharingTypeEnum().equals(SharingType.USER) && dashboardSharing.getOrgUserId() == user.getOuid()) {
							dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.ROLE) && dashboardSharing.getRoleId() == user.getRoleId()) {
							dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.GROUP)) {
							List<Group> mygroups = AccountUtil.getGroupBean().getMyGroups(user.getOuid());
							for (Group group : mygroups) {
								if (dashboardSharing.getGroupId() == group.getId() && !dashboardList.contains(dashboardMap.get(dashboardSharing.getDashboardId()))) {
									dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
								}
							}
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.PORTAL)) {
							if(dashboardSharing.getOrgUserId() > 0) {
								if(dashboardSharing.getOrgUserId() == user.getOuid()) {
									dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
								}
							}
							else {
								dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
							}
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.ALL_PORTAL_USER)) {
							dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
						}
					}
					}
					
					
				}
				if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
					for (Long dashboardId : dashboardIds) {
						dashboardList.add(dashboardMap.get(dashboardId));
					}
				}
		    
			}
			else {
				if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) { 
					dashboardList.addAll(dashboardMap.values());
				}
			}
		}
		return dashboardList;
	}
	public static List<DashboardContext> getFilteredDashboards(Map<Long, DashboardContext> dashboardMap,boolean isFromPortal) throws Exception {
			
//		return getOrginalFilteredDashboards(dashboardMap, isFromPortal);
		return isFromPortal ? getFilteredPortalDashboards(dashboardMap): getOrginalFilteredDashboards(dashboardMap, isFromPortal);
	}
	
	public static List<DashboardSharingContext> getDashboardSharing(Long dashboardId) throws Exception {
		
		List<DashboardSharingContext> dashboardSharingList = new ArrayList<DashboardSharingContext>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardSharingFields())
				.table(ModuleFactory.getDashboardSharingModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
			
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				DashboardSharingContext dashboardSharing = FieldUtil.getAsBeanFromMap(prop, DashboardSharingContext.class);
				dashboardSharingList.add(dashboardSharing);	
			}
		}
		return dashboardSharingList;
	}

		
	public static List<DashboardSharingContext> getDashboardSharingByType(int sharingType) throws Exception { // to be removed after migration
		
		List<DashboardSharingContext> dashboardSharingList = new ArrayList<DashboardSharingContext>();
	
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getDashboardSharingModule().getTableName())
				.select(FieldFactory.getDashboardSharingFields())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("SHARING_TYPE = ?", sharingType);

		List<Map<String, Object>> props = selectBuilder.get();
			
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				DashboardSharingContext dashboardSharing = FieldUtil.getAsBeanFromMap(prop, DashboardSharingContext.class);
				dashboardSharingList.add(dashboardSharing);	
			}
		}
		return dashboardSharingList;
	}
	
	public static void applyDashboardSharing(Long dashboardId, List<DashboardSharingContext> dashboardSharingList) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getDashboardSharingModule().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteBuilder.delete();
		
		List<Map<String, Object>> dashboardSharingProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(DashboardSharingContext dashboardSharing : dashboardSharingList) {
			dashboardSharing.setOrgId(orgId);
			dashboardSharingProps.add(FieldUtil.getAsProperties(dashboardSharing));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardSharingModule().getTableName())
					.fields(FieldFactory.getDashboardSharingFields())
					.addRecords(dashboardSharingProps);
		insertBuilder.save();
	}
	
	
	public static List<DashboardPublishContext> getDashboardPublishing(Long dashboardId) throws Exception {
		
		List<DashboardPublishContext> dashboardPublishList = new ArrayList<DashboardPublishContext>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardPublishingFields())
				.table(ModuleFactory.getDashboardPublishingModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
			
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				DashboardPublishContext dashboardPublishing = FieldUtil.getAsBeanFromMap(prop, DashboardPublishContext.class);
				dashboardPublishList.add(dashboardPublishing);	
			}
		}
		return dashboardPublishList;
	}
	
	public static void applyDashboardPublishing(Long dashboardId, List<DashboardPublishContext> dashboardPublishList) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getDashboardPublishingModule().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteBuilder.delete();
		
		List<Map<String, Object>> dashboardPublishingProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(DashboardPublishContext dashboardPublishing : dashboardPublishList) {
			dashboardPublishing.setOrgId(orgId);
			dashboardPublishingProps.add(FieldUtil.getAsProperties(dashboardPublishing));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardPublishingModule().getTableName())
					.fields(FieldFactory.getDashboardPublishingFields())
					.addRecords(dashboardPublishingProps);
		insertBuilder.save();
	}
	public static void addDashboardPublishing(List<DashboardPublishContext> dashboardPublishList) throws Exception {
		
		List<Map<String, Object>> dashboardPublishingProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(DashboardPublishContext dashboardPublishing : dashboardPublishList) {
			dashboardPublishing.setOrgId(orgId);
			dashboardPublishingProps.add(FieldUtil.getAsProperties(dashboardPublishing));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardPublishingModule().getTableName())
					.fields(FieldFactory.getDashboardPublishingFields())
					.addRecords(dashboardPublishingProps);
		insertBuilder.save();
	}
	
	
	
	// old reports related APIs 
	
	public static List<ReportContext> getReportsFormReportFolderId(Long folderID) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFields())
				.table(ModuleFactory.getReport().getTableName());
		
		if (folderID != null && folderID > 0) {
			selectBuilder.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID = ?", folderID);
		}
		else {
			selectBuilder.andCustomWhere("(" + ModuleFactory.getReport().getTableName() + ".REPORT_FOLDER_ID IS NULL OR " + ModuleFactory.getReport().getTableName() + ".REPORT_FOLDER_ID = -1)");
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReportContext> reps;
		if (props != null && !props.isEmpty()) {
			reps = new ArrayList<>();
			for(Map<String, Object> prop:props) {
				ReportContext reportFolderContext = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				reps.add(reportFolderContext);
			}
			return reps;
		}
		return null;
	}
	public static ReportFolderContext getReportFolderContext(Long ReportFolderId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere(ModuleFactory.getReportFolder().getTableName()+".ID = ?", ReportFolderId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		return null;
	}
	public static ReportFolderContext getDefaultReportFolder(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere("MODULEID = ?", module.getModuleId())
				.andCustomWhere(ModuleFactory.getReportFolder().getTableName()+".NAME = ?", "Default");
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		else {
			ReportFolderContext reportFolderContext = new ReportFolderContext();
			reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			reportFolderContext.setModuleId(module.getModuleId());
			reportFolderContext.setName("Default");
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportFolder().getTableName())
					.fields(FieldFactory.getReportFolderFields());
			
			Map<String, Object> props1 = FieldUtil.getAsProperties(reportFolderContext);
			insertBuilder.addRecord(props1);
			insertBuilder.save();
			
			long folderId = (Long) props1.get("id");
			reportFolderContext.setId(folderId);
			return reportFolderContext;
		}
	}
	public static ReportFolderContext getBuildingReportFolder(String moduleName, long buildingId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
				.andCustomWhere("ORGID = ? AND MODULEID = ? AND BUILDING_ID = ?", AccountUtil.getCurrentOrg().getId(), module.getModuleId(), buildingId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ReportFolderContext reportFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFolderContext.class);
			return reportFolderContext;
		}
		return null;
	}
	
	public static ReportContext getReportContext(Long reportId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = FieldFactory.getReportFields();
		fields.addAll(FieldFactory.getReportCriteriaFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReport().getTableName())
				.leftJoin(ModuleFactory.getReportCriteria().getTableName())
				.on(ModuleFactory.getReport().getTableName()+".ID="+ModuleFactory.getReportCriteria().getTableName()+".REPORT_ID")
				.andCustomWhere(ModuleFactory.getReport().getTableName()+".ID = ?", reportId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ReportContext reportContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportContext.class);
			
			if(reportContext != null) {
				reportContext.setModuleName(modBean.getModule(reportContext.getModuleId()).getName());
				
				ReportFieldContext reportXAxisField = DashboardUtil.getReportField(reportContext.getxAxisField());
				reportContext.setxAxisField(reportXAxisField);
				if(reportContext.getxAxisaggregateFunction() == null) {
					reportContext.setxAxisaggregateFunction(CommonAggregateOperator.COUNT.getValue());
				}
				if(reportContext.getY1Axis() != null || reportContext.getY1AxisField() != null ) {
					ReportFieldContext reportY1AxisField = DashboardUtil.getReportField(reportContext.getY1AxisField());
					reportContext.setY1AxisField(reportY1AxisField);
					if(reportContext.getY1AxisaggregateFunction() == null) {
						reportContext.setY1AxisaggregateFunction(CommonAggregateOperator.COUNT.getValue());
					}
				}
				if(reportContext.getGroupBy() != null) {
					ReportFieldContext reportGroupByField = DashboardUtil.getReportField(reportContext.getGroupByField());
					reportContext.setGroupByField(reportGroupByField);
				}
			}
			
			Map<String, Object> prop = props.get(0);
			if(prop.get("criteriaId") != null) {
				Criteria criteria = CriteriaAPI.getCriteria(reportContext.getOrgId(), (Long) prop.get("criteriaId"));
				reportContext.setCriteria(criteria);
			}
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportThresholdFields())
					.table(ModuleFactory.getReportThreshold().getTableName())
					.andCustomWhere(ModuleFactory.getReportThreshold().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> thresholdProps = selectBuilder.get();
			if(thresholdProps != null && !thresholdProps.isEmpty()) {
				for(Map<String, Object> thresholdProp:thresholdProps) {
					ReportThreshold reportThreshold = FieldUtil.getAsBeanFromMap(thresholdProp, ReportThreshold.class);
					reportContext.addReportThreshold(reportThreshold);
				}
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportUserFilterFields())
					.table(ModuleFactory.getReportUserFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportUserFilter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> userFilterProps = selectBuilder.get();
			if(userFilterProps != null && !userFilterProps.isEmpty()) {
				for(Map<String, Object> userFilterProp:userFilterProps) {
					ReportUserFilterContext reportUserFilterContext = FieldUtil.getAsBeanFromMap(userFilterProp, ReportUserFilterContext.class);
					
					ReportFieldContext reportField = new ReportFieldContext();
					reportField.setId(reportUserFilterContext.getReportFieldId());
					
					reportUserFilterContext.setReportFieldContext(getReportField(reportField));
					
					reportContext.addReportUserFilter(reportUserFilterContext);
				}
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportDateFilterFields())
					.table(ModuleFactory.getReportDateFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportDateFilter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> dateFilterProps = selectBuilder.get();
			if (dateFilterProps != null && !dateFilterProps.isEmpty()) {
				ReportDateFilterContext dateFilterContext = FieldUtil.getAsBeanFromMap(dateFilterProps.get(0), ReportDateFilterContext.class);
				FacilioField ff = modBean.getFieldFromDB(dateFilterContext.getFieldId());
				dateFilterContext.setField(ff);
				
				reportContext.setDateFilter(dateFilterContext);
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportEnergyMeterFields())
					.table(ModuleFactory.getReportEnergyMeter().getTableName())
					.andCustomWhere(ModuleFactory.getReportEnergyMeter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> energyMeterProps = selectBuilder.get();
			if (energyMeterProps != null && !energyMeterProps.isEmpty()) {
				ReportEnergyMeterContext energyMeterContext = FieldUtil.getAsBeanFromMap(energyMeterProps.get(0), ReportEnergyMeterContext.class);
				reportContext.setEnergyMeter(energyMeterContext);
			}
			
			if(reportContext.getParentFolderId() != null) {
				selectBuilder = new GenericSelectRecordBuilder()
						.select(fields)
						.table(ModuleFactory.getReport().getTableName())
						.leftJoin(ModuleFactory.getReportCriteria().getTableName())
						.on(ModuleFactory.getReport().getTableName()+".ID="+ModuleFactory.getReportCriteria().getTableName()+".REPORT_ID")
						.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_ENTITY_ID = ?", reportContext.getReportEntityId())
						.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID IS NULL")
						.orderBy("REPORT_ORDER");
				
				LegendMode legendMode = LegendMode.READING_NAME;
				
				List<Map<String, Object>> compReportProps = selectBuilder.get();
				if (compReportProps != null && !compReportProps.isEmpty()) {
					
					List<FacilioField> yAxisFields = new ArrayList<>();
					List<Criteria> criterias = new ArrayList<>();
					
					if (reportContext.getY1AxisField() != null) {
						yAxisFields.add(reportContext.getY1AxisField().getField());
					}
					if(reportContext.getCriteria() != null) {
						criterias.add(reportContext.getCriteria());
					}
					
					for(Map<String, Object> compReportProp:compReportProps) {
						ReportContext compReportContext = FieldUtil.getAsBeanFromMap(compReportProp, ReportContext.class);
						if(compReportProp.get("criteriaId") != null) {
							Criteria criteria = CriteriaAPI.getCriteria(reportContext.getOrgId(), (Long) compReportProp.get("criteriaId"));
							if(criteria != null) {
								compReportContext.setCriteria(criteria);
								criterias.add(compReportContext.getCriteria());
							}
						}
						if (compReportContext.getY1AxisField() != null) {
							ReportFieldContext y1Axis = DashboardUtil.getReportField(compReportContext.getY1AxisField());
							if(y1Axis.getField() != null) {
								yAxisFields.add(y1Axis.getField());
							}
						}
					}
					
					if (compReportProps.size() > 0) {
						
						boolean isSameFieldComparision = true;
						boolean isSameResourceComparision = true;
						
						String parentId = null;
						if(!criterias.isEmpty()) {
							for(Criteria criteria :criterias) {
								if(parentId == null) {
									parentId = WorkflowUtil.getParentIdFromCriteria(criteria);
								}
								else if(!parentId.equals(WorkflowUtil.getParentIdFromCriteria(criteria))) {
									isSameResourceComparision = false;
								}
							}
						}
						
						if(!isSameResourceComparision) {
							String fieldName = null;
							for(FacilioField yAxisField :yAxisFields) {
								if(fieldName == null) {
									fieldName = yAxisField.getName();
								}
								else if(!fieldName.equals(yAxisField.getName())){
									isSameFieldComparision = false;
									break;
								}
							}
						}
						
						if(isSameResourceComparision) {
							legendMode = LegendMode.READING_NAME;
						}
						else if(isSameFieldComparision) {
							legendMode = LegendMode.RESOURCE_NAME;
						}
						else {
							legendMode = LegendMode.RESOURCE_WITH_READING_NAME;
						}
					}
					else {
						legendMode = LegendMode.RESOURCE_NAME;
					}
					for(Map<String, Object> compReportProp:compReportProps) {
						ReportContext compReportContext = FieldUtil.getAsBeanFromMap(compReportProp, ReportContext.class);
						compReportContext.setLegendMode(legendMode);
						reportContext.addComparingReportContext(compReportContext);
					}
				}
				reportContext.setLegendMode(legendMode);
				
				if (reportContext.getReportChartType() == ReportChartType.TABULAR) {
					reportContext.setReportColumns(getReportColumns(reportContext.getReportEntityId()));
				}
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportSpaceFilterFields())
					.table(ModuleFactory.getReportSpaceFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportSpaceFilter().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> spaceFilterProps = selectBuilder.get();
			if (spaceFilterProps != null && !spaceFilterProps.isEmpty()) {
				ReportSpaceFilterContext spaceFilterContext = FieldUtil.getAsBeanFromMap(spaceFilterProps.get(0), ReportSpaceFilterContext.class);
				fillFirstSpaceForEmptySpace(spaceFilterContext);
				reportContext.setReportSpaceFilterContext(spaceFilterContext);
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportBenchmarkRelFields())
					.table(ModuleFactory.getReportBenchmarkRelModule().getTableName())
					.andCustomWhere(ModuleFactory.getReportBenchmarkRelModule().getTableName()+".REPORT_ID = ?", reportId);
			
			List<Map<String, Object>> reportBenchmarkRelProps = selectBuilder.get();
			if (reportBenchmarkRelProps != null && !reportBenchmarkRelProps.isEmpty()) {
				
				for(Map<String, Object> reportBenchmarkRelProp :reportBenchmarkRelProps) {
					ReportBenchmarkRelContext reportBenchmarkRelContext = FieldUtil.getAsBeanFromMap(reportBenchmarkRelProp, ReportBenchmarkRelContext.class);
					reportContext.addReportBenchmarkRelContexts(reportBenchmarkRelContext);
				}
			}
			
			reportContext.setBaseLineContexts(BaseLineAPI.getBaseLinesOfReport(reportId));
			return reportContext;
		}
		return null;
	}
	private static void fillFirstSpaceForEmptySpace(ReportSpaceFilterContext spaceFilterContext) throws Exception {
		
		boolean isSiteFilter =  spaceFilterContext.getSiteId() != null ? true : false; 
		boolean isBuildingFilter =  spaceFilterContext.getBuildingId() != null ? true : false; 
		
		if(isSiteFilter && spaceFilterContext.getSiteId().equals(-1l)) {
			
			List<SiteContext> sites = SpaceAPI.getAllSites();
			if(sites != null && !sites.isEmpty()) {
				spaceFilterContext.setSiteId(sites.get(0).getId());
			}
		}
		else if (isBuildingFilter  && spaceFilterContext.getBuildingId().equals(-1l)) {
			
			List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
			if(buildings != null && !buildings.isEmpty()) {
				spaceFilterContext.setBuildingId(buildings.get(0).getId());
			}
		}
		
	}

	public static ReportFieldContext getReportField(ReportFieldContext reportField) throws Exception {
		
		if (reportField.getId() == null) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField ff = modBean.getFieldFromDB(reportField.getModuleFieldId());
			
			reportField.setModuleField(ff);
			return reportField;
		}
		else {
			List<FacilioField> fields = FieldFactory.getReportFieldFields();
			fields.addAll(FieldFactory.getReportFormulaFieldFields());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getReportField().getTableName())
					.leftJoin(ModuleFactory.getReportFormulaField().getTableName())
					.on(ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID="+ModuleFactory.getReportFormulaField().getTableName()+".ID")
					.andCustomWhere(ModuleFactory.getReportField().getTableName()+".ID = ?", reportField.getId());
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				ReportFieldContext reportFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFieldContext.class);
				
				if(reportFieldContext != null && reportFieldContext.getIsFormulaField()) {
					ReportFormulaFieldContext reportFormulaFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFormulaFieldContext.class);
					reportFieldContext.setReportFormulaContext(reportFormulaFieldContext);
				}
				return reportFieldContext;
			}
			return null;
		}
	}
	public static ReportFieldContext addOrGetReportfield(ReportFieldContext reportFieldContext, String moduleName) throws Exception {
		
		String where;
		if(reportFieldContext.getIsFormulaField()) {
			where = ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID = "+reportFieldContext.getFormulaFieldId();
		}
		else {
			where = ModuleFactory.getReportField().getTableName()+".MODULE_FIELD_ID = "+reportFieldContext.getModuleFieldId();
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFieldFields())
				.table(ModuleFactory.getReportField().getTableName())
				.leftJoin(ModuleFactory.getReportFormulaField().getTableName())
				.on(ModuleFactory.getReportField().getTableName()+".FORMULA_FIELD_ID="+ModuleFactory.getReportFormulaField().getTableName()+".ID")
				.andCustomWhere(where);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			reportFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFieldContext.class);
			
			if(reportFieldContext != null && reportFieldContext.getIsFormulaField()) {
				ReportFormulaFieldContext reportFormulaFieldContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportFormulaFieldContext.class);
				reportFieldContext.setReportFormulaContext(reportFormulaFieldContext);
			}
			return reportFieldContext;
		}
		else {
			
			if (reportFieldContext.getModuleFieldId() == null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField(reportFieldContext.getModuleField().getName(), moduleName);
				reportFieldContext.setModuleFieldId(field.getFieldId());
				reportFieldContext.setModuleField(field);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportField().getTableName())
					.fields(FieldFactory.getReportFieldFields());

			Map<String, Object> prop = FieldUtil.getAsProperties(reportFieldContext);
			insertBuilder.addRecord(prop);
			insertBuilder.save();

			reportFieldContext.setId((Long) prop.get("id"));
			
			return reportFieldContext;
		}
	}
	
	public static void deleteReport(long reportId) throws Exception {
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
													.fields(FieldFactory.getReportFields())
													.table(ModuleFactory.getReport().getTableName())
													.andCustomWhere("ID = ?",reportId)
													;
		
		Map<String,Object> value = new HashMap<>();
		value.put("parentFolderId", null);
		
		update.update(value);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getReportThreshold().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportUserFilter().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportCriteria().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportEnergyMeter().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportDateFilter().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportSpaceFilter().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getBaseLineReportRelModule().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		List<WidgetChartContext> widgets = getWidgetFromDashboard(reportId,false);
		
		List<Long> removedWidgets = new ArrayList<>();
		for(WidgetChartContext widget :widgets) {
			removedWidgets.add(widget.getId());
		}
		
//		deleteRecordBuilder = new GenericDeleteRecordBuilder();
//		deleteRecordBuilder.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
//		.andCondition(CriteriaAPI.getCondition(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID", "widgetId", StringUtils.join(removedWidgets, ","),StringOperators.IS));
//		
//		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "id", StringUtils.join(removedWidgets, ","), StringOperators.IS));
		
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReport().getTableName())
		.andCustomWhere("ID = ?", reportId);
		deleteRecordBuilder.delete();
	}
	
	public static ReportContext getParentReportForEntitiyId(Long entityId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFields())
				.table(ModuleFactory.getReport().getTableName())
				.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_ENTITY_ID = "+entityId)
				.andCustomWhere(ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID is not null");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			return getReportContext((long)props.get(0).get("id"));
		}
		return null;
	}
	
	public static JSONArray consolidateResult(List<JSONArray> reportDatas,int xAxisAggr,int yAxisAggr) {
		
		List<JSONObject> reportDatasNew = new ArrayList<>();
		
		Map<String,Long> labelMap = new HashMap<>();
		
		for(JSONArray reportData :reportDatas) {
			
			JSONObject reportDataNew = new JSONObject();
			
			for(Object resultObject : reportData) {
				
				JSONObject result = (JSONObject) resultObject;
				
				long labelLong = (long) result.get("label");
				String labelString = covertLabelLongToString(labelLong,AggregateOperator.getAggregateOperator(xAxisAggr));
				
				labelMap.put(labelString, labelLong);
				reportDataNew.put(labelString, result.get("value"));
			}
			reportDatasNew.add(reportDataNew);
		}
		
		JSONObject reportData = reportDatasNew.get(0);
		Multimap<String, Double> resultMap = ArrayListMultimap.create();
		
		for(Object resultLabel : reportData.keySet()) {
			
			resultMap.put((String) resultLabel, (Double) reportData.get(resultLabel));
			
			for(int i=1;i<reportDatas.size();i++) {
				JSONObject reportData1 = reportDatasNew.get(i);
				resultMap.put((String) resultLabel, (Double) reportData1.get(resultLabel));
			}
		}
		
		JSONArray finalRes = new JSONArray();
		Multiset<String> keys = resultMap.keys();
		
		ArrayList<String> keys1 = new ArrayList();

		for(String key:keys) {
			keys1.add(key);
		}
		Collections.sort(keys1);
		
		for(String StringLabel :keys1) {
			double aggr = performAggregation((List<Double>) resultMap.get(StringLabel),AggregateOperator.getAggregateOperator(yAxisAggr));
			JSONObject result = new JSONObject();
			result.put("label", labelMap.get(StringLabel));
			result.put("value", aggr);
			
			finalRes.add(result);
		}
		
		return finalRes;
	}
	
	public static double performAggregation(List<Double> values, AggregateOperator yAggrOpr) {
		
		if (yAggrOpr == CommonAggregateOperator.COUNT) {
			return values.size();
		}
		
		NumberAggregateOperator xAxisAggr1 = (NumberAggregateOperator) yAggrOpr;
		values.removeIf(value -> value == null);
		switch(xAxisAggr1) {
		case SUM: {
			return values.stream().mapToDouble(Double::doubleValue).sum();
		}
		case AVERAGE: {
			OptionalDouble optionalDouble = values.stream().mapToDouble(Double::doubleValue).average();
			double value = optionalDouble.orElse(0);
			return value;
		}
		case MIN: {
			OptionalDouble optionalDouble = values.stream().mapToDouble(Double::doubleValue).min();
			double value = optionalDouble.orElse(0);
			return value;
		}
		case MAX: {
			OptionalDouble optionalDouble = values.stream().mapToDouble(Double::doubleValue).max();
			double value = optionalDouble.orElse(0);
			return value;
		}
		}
		return 0;
	}
	
	public static String covertLabelLongToString(long labelLong,AggregateOperator xAxisAggr) {
		DateAggregateOperator xAxisAggr1 = (DateAggregateOperator) xAxisAggr;
		ZonedDateTime zonedDateTime = DateTimeUtil.getZonedDateTime(labelLong);
		switch(xAxisAggr1) {
		
		case HOURSOFDAY: {
			return zonedDateTime.getHour()+"";
		}
		case HOURSOFDAYONLY: {
			return zonedDateTime.getYear()+" "+zonedDateTime.getMonth().getValue()+" "+zonedDateTime.getDayOfMonth()+" "+zonedDateTime.getHour();
		}
		case DAYSOFMONTH: {
			return zonedDateTime.getDayOfMonth()+"";
		}
		case WEEKDAY: {
			return zonedDateTime.getDayOfWeek().getValue()+"";
		}
		case WEEK: {
			break;
		}
		case MONTH: {
			return zonedDateTime.getMonth().getValue()+"";
		}
		case DATEANDTIME: {
			return zonedDateTime.getYear()+" "+zonedDateTime.getMonth().getValue()+" "+zonedDateTime.getDayOfMonth()+" "+zonedDateTime.getHour() +":"+zonedDateTime.getMinute();
		}
		case FULLDATE: {
			return zonedDateTime.getYear()+" "+zonedDateTime.getMonth().getValue()+" "+zonedDateTime.getDayOfMonth();
		}
		case WEEKANDYEAR: {
			break;
		}
		case MONTHANDYEAR: {
			return zonedDateTime.getYear()+" "+zonedDateTime.getMonth().getValue();
		}
		case YEAR: {
			return zonedDateTime.getYear()+"";
		}
		}
		return null;
	}
	
	
	public static ReportDateFilterContext getReportDateFilter(Long dateFilterId) throws Exception {
		if(dateFilterId != null && dateFilterId > 0) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getReportDateFilterFields())
					.table(ModuleFactory.getReportDateFilter().getTableName())
					.andCustomWhere(ModuleFactory.getReportDateFilter().getTableName()+".ID = ?",dateFilterId);
			
			List<Map<String, Object>>  props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				ReportDateFilterContext reportDateFilterContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportDateFilterContext.class);
				return reportDateFilterContext;
			}
		}
		return null;
	}
	
	public static final long ONE_MIN_MILLIS = 60000l;
	public static final long ONE_DAY_MILLIS = 86400000l;
	public static int predictDateOpperator(JSONArray dateFilter) {
	
		long diff = (Long) dateFilter.get(1) - (Long) dateFilter.get(0);
		
		if(diff <= ONE_MIN_MILLIS * 60 * 24) {
			return DateOperators.TODAY.getOperatorId();
		}
		else if (diff <= ONE_MIN_MILLIS * 60 * 24 * 7) {
			return DateOperators.CURRENT_WEEK.getOperatorId();
		}
		else if (diff <= ONE_MIN_MILLIS * 60 * 24 * 31) {
			return DateOperators.CURRENT_MONTH.getOperatorId();
		}
		else {
			return DateOperators.CURRENT_YEAR.getOperatorId();
		}
	
	}
	public static int getNoOfDaysBetweenDateRange(long startTime,long endTime) {
		
		if((endTime-startTime) > ONE_DAY_MILLIS) {
			return (int) ((endTime-startTime)/ONE_DAY_MILLIS);
		}
		return -1;
	}
	public static Condition getDateCondition(ReportContext reportContext, JSONArray dateFilter, FacilioModule module) throws Exception {
		Condition dateCondition = new Condition();
		dateCondition.setField(reportContext.getDateFilter().getField());
		
		if (dateFilter != null) {
			if (dateFilter.size() > 1) {

				dateCondition.setOperator(DateOperators.BETWEEN);
				long fromValue = (long)dateFilter.get(0);
				long toValue = (long)dateFilter.get(1);
				if(module.getName().equals("energydata") && toValue > DateTimeUtil.getCurrenTime()) {
					toValue = DateTimeUtil.getCurrenTime();
				}
				if(module != null && module.getName().equals("dewabill")) {
					toValue = toValue + 1001;
				}
				dateCondition.setValue(fromValue+","+toValue);
			}
		}
		else {
			if(reportContext.getDateFilter().getOperatorId() != null) {
				dateCondition.setOperatorId(reportContext.getDateFilter().getOperatorId());
				if(reportContext.getDateFilter().getValue() != null) {
					dateCondition.setValue(reportContext.getDateFilter().getValue());
				}
			}
			else if(reportContext.getDateFilter().getStartTime() != null && reportContext.getDateFilter().getEndTime() != null) {
				dateCondition.setOperator(DateOperators.BETWEEN);
				long fromValue = reportContext.getDateFilter().getStartTime();
				long toValue = reportContext.getDateFilter().getEndTime();
				dateCondition.setValue(fromValue+","+toValue);
			}
		}
		return dateCondition;
	}
	
	public static List<ReportColumnContext> getReportColumns (long entityId) throws Exception {
		FacilioModule module = ModuleFactory.getReportColumnsModule();
		List<FacilioField> fields = FieldFactory.getReportColumnFields();
		FacilioField entityField = FieldFactory.getAsMap(fields).get("entityId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(entityField, String.valueOf(entityId), PickListOperators.IS));
		
		selectBuilder.orderBy("SEQUENCE_NUMBER");
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ReportColumnContext> reportColumns = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				reportColumns.add(FieldUtil.getAsBeanFromMap(prop, ReportColumnContext.class));
			}
			return reportColumns;
		}
		return null;
	}
	public static Integer getLastDashboardDisplayOrder(Long orgid,Long moduleId) throws Exception { 	// move up
		
		if(orgid != null && moduleId != null) {
			BeanFactory.lookup("ModuleBean");
		
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
			select.select(FieldFactory.getDashboardFields())
			.table(ModuleFactory.getDashboardModule().getTableName())
//			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getDashboardModule()))
			.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".MODULEID = ?", moduleId)
			.orderBy("DISPLAY_ORDER desc")
			.limit(1);
			
			
			List<Map<String, Object>> props = select.get();
			
			if(props != null && !props.isEmpty()) {
				Object order = props.get(0).get("displayOrder");
				if(order != null) {
					return (Integer)order;
				}
				else {
					return 1;
				}
			}
			else {
				return 1;
			}
		}
		return null;
	}
	
	public static ReportContext UpdateReport(ReportContext reportContext) throws Exception {
		
		getReportContext(reportContext.getId());
		if (reportContext != null) {
			
			reportContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			List<FacilioField> fields = FieldFactory.getReportFields();
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
			.table(ModuleFactory.getReport().getTableName())
			.fields(fields)
			.andCustomWhere(ModuleFactory.getReport().getTableName()+".ID = ?", reportContext.getId());
			
			
			Map<String, Object> reportProp = FieldUtil.getAsProperties(reportContext);
			LOGGER.log(Level.INFO, "Update Report Prop --"+reportProp);
			update.update(reportProp);
			

//			if(reportContext.getCriteria() != null) {
//				
//				if(oldReport.getCriteria() != null) {
//					
//					GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder();
//					delete.table(ModuleFactory.getReportCriteria().getTableName())
//					.andCustomWhere(ModuleFactory.getReportCriteria().getTableName()+".REPORT_ID = ?", oldReport.getId());
//					
//					delete.delete();
//					CriteriaAPI.deleteCriteria(oldReport.getCriteria().getCriteriaId());
//				}
//				
//				Long criteriaId = CriteriaAPI.addCriteria(reportContext.getCriteria(), AccountUtil.getCurrentOrg().getId());
//				
//				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
//						.table(ModuleFactory.getReportCriteria().getTableName())
//						.fields(FieldFactory.getReportCriteriaFields());
//				
//				Map<String, Object> prop = new HashMap<String, Object>();
//				prop.put("reportId", reportContext.getId());
//				prop.put("criteriaId", criteriaId);
//				insertBuilder.addRecord(prop).save();
//			}
			if(reportContext.getDateFilter() != null) {
				
				fields = FieldFactory.getReportDateFilterFields();
				
				update = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getReportDateFilter().getTableName())
						.fields(fields)
						.andCustomWhere(ModuleFactory.getReportDateFilter().getTableName()+".REPORT_ID = ?", reportContext.getId());
						
				Map<String, Object> reportDateFilterProp = FieldUtil.getAsProperties(reportContext.getDateFilter());
				update.update(reportDateFilterProp);
				
			}
		}
		reportContext = getReportContext(reportContext.getId());
		return reportContext;
	}
	
	public static FacilioFrequency getAggrFrequency(int aggr) {
		DateAggregateOperator operator = (DateAggregateOperator)AggregateOperator.getAggregateOperator(aggr);
		FacilioFrequency frequency;
		switch(operator) {
			case HOURSOFDAYONLY: 
				frequency = FacilioFrequency.HOURLY;
				break;
			case FULLDATE:
				frequency = FacilioFrequency.DAILY;
				break;
			case WEEKANDYEAR:
				frequency = FacilioFrequency.WEEKLY;
				break;
			case MONTHANDYEAR:
				frequency = FacilioFrequency.MONTHLY;
				break;
			default:
				frequency = FacilioFrequency.DAILY;
		}
		return frequency;
	}
	
	public static Map<Long, Double> calculateWorkHours (List<Map<String, Object>> props, long startTime, long endTime) {
		return calculateWorkHours(props,startTime,endTime,false,false);
	}
	
	public static Map<Long, Double> calculateWorkHours (List<Map<String, Object>> props, long startTime, long endTime,boolean isAvgResoultionTime,boolean ispercent) { //Expects it to be in order by time
		if (props != null && !props.isEmpty()) {
			Map<Long, List<Map<String, Object>>> userWiseProps = new HashMap<>();
			for(Map<String, Object> prop : props) {
				Long userId = (Long) prop.get("parentId");
				List<Map<String, Object>> userWiseList = userWiseProps.get(userId);
				if (userWiseList == null) {
					userWiseList = new ArrayList<>();
					userWiseProps.put(userId, userWiseList);
				}
				userWiseList.add(prop);
			}
			
			Map<Long, Double> workDuration = new HashMap<>();
			for (Map.Entry<Long, List<Map<String, Object>>> entry : userWiseProps.entrySet()) {
				Long userId = entry.getKey();
				long workTime = 0;
				Stack<Long> startTimeStack = new Stack<>();
				List<Long> workordersId = new ArrayList<>();
				Map<Long, UserWorkHourReading> lastAactivity = new HashMap<>();
				for (Map<String, Object> prop : entry.getValue()) {
					Long woId = (Long) prop.get("woId");
					if(!workordersId.contains(woId)) {
						workordersId.add(woId);
					}
					Boolean hasManualEntry = (Boolean) prop.get("hasManualEntry");
					if (hasManualEntry == null || !hasManualEntry) {
						long currentTime = (long) prop.get("actualTtime");
						UserWorkHourReading workHour = UserWorkHourReading.valueOf((int) prop.get("workHoursEntry"));
						switch (workHour) {
							case START:
							case RESUME:
								startTimeStack.push(currentTime);
								break;
							case PAUSE:
							case CLOSE:
								UserWorkHourReading prevActivity = lastAactivity.get(prop.get("woId"));
								if (prevActivity == null || (prevActivity != UserWorkHourReading.CLOSE && prevActivity != UserWorkHourReading.PAUSE)) {
									if (startTimeStack.isEmpty()) {
										workTime = currentTime - startTime;
									}
									else {
										Long prevTime = startTimeStack.pop();
										if (startTimeStack.isEmpty()) { //Calculate time only for the longest WO. e.g : 1 1 6 6
											workTime += (currentTime - prevTime);
										}
									}
								}
								break;
						}
						lastAactivity.put((Long) prop.get("woId"), workHour);
					}
				}
				if (!startTimeStack.isEmpty()) {
					Long time = startTimeStack.firstElement();
					workTime += (endTime - time);
				}
				Double convertedValue = UnitsUtil.convert(workTime, Unit.MILLIS, Unit.HOUR);
				if(isAvgResoultionTime) {
					convertedValue = convertedValue/workordersId.size();
				}
				if(ispercent) {
					//get his shift time;
					//workTime = convertedValue / shift * 100;
				}
				workDuration.put(userId, convertedValue);
			}
			return workDuration;
		}
		return null;
	}
	
	public static List<Map<String, Object>> convertMapToProps(Map<Long, Double> map) {
		
		if(map != null && !map.isEmpty()) {
			List<Map<String, Object>> rs = new ArrayList<>();
			for(Long lable: map.keySet()) {
				
				Double value = map.get(lable);
				
				Map<String, Object> map1 = new HashMap<>();
				map1.put("label", lable);
				map1.put("value", value);
				
				rs.add(map1);
			}
			return rs;
		}
		return null;
	}
	
	public static void addDashboardFolder(DashboardFolderContext dashboardFolder) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardFolderModule().getTableName())
				.fields(FieldFactory.getDashboardFolderFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(dashboardFolder);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		dashboardFolder.setId((Long) props.get("id"));
	}
	
	public static void updateDashboard(DashboardContext dashboard) throws Exception {

		dashboard.setModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
		dashboard.setModifiedTime(System.currentTimeMillis());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardModule().getTableName())
				.fields(FieldFactory.getDashboardFields())
				.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID = ?", dashboard.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
		updateBuilder.update(props);
	}
	
	public static void updateDashboardTab(DashboardTabContext dashboardTabContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.fields(FieldFactory.getDashboardTabFields())
				.andCondition(CriteriaAPI.getIdCondition(dashboardTabContext.getId(), ModuleFactory.getDashboardTabModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(dashboardTabContext);
		updateBuilder.update(props);
	}
	
	public static void updateDashboardTabList(List<DashboardTabContext> dashboardTabContextList) throws Exception {
		
		for (DashboardTabContext dashboardTabContext :dashboardTabContextList) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getDashboardTabModule().getTableName())
					.fields(FieldFactory.getDashboardTabFields())
					.andCondition(CriteriaAPI.getIdCondition(dashboardTabContext.getId(), ModuleFactory.getDashboardTabModule()));
	
			Map<String, Object> props = FieldUtil.getAsProperties(dashboardTabContext);
			updateBuilder.update(props);
		}
	}
	
	public static void deleteDashboardFolder(DashboardFolderContext dashboardFolder) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getDashboardFolderModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(dashboardFolder.getId(), ModuleFactory.getDashboardFolderModule()));
		deleteRecordBuilder.delete();
	}
	
	public static void updateDashboardFolder(List<DashboardFolderContext> dashboardFolders) throws Exception {
		
		for(DashboardFolderContext dashboardFolder :dashboardFolders) {
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getDashboardFolderModule().getTableName());
			update.fields(FieldFactory.getDashboardFolderFields())
			.andCustomWhere("ID = ?", dashboardFolder.getId());
			
			Map<String, Object> prop = FieldUtil.getAsProperties(dashboardFolder);
			update.update(prop);
		}
		
	}
	
	public static List<DashboardFolderContext> getDashboardFolder(String moduleName, long appId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		 List<DashboardFolderContext> dashboardFolderContexts = new ArrayList<>();
		 
		 GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardFolderFields())
					.table(ModuleFactory.getDashboardFolderModule().getTableName());
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getDashboardFolderModule()));
		 
		 if(moduleName != null) {
			 FacilioModule module = modBean.getModule(moduleName);
			 selectBuilder.andCustomWhere(ModuleFactory.getDashboardFolderModule().getTableName()+".MODULEID = ?", module.getModuleId());
		 }
		 
		 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardFolderFields());
		 ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);

	
			Criteria appCriteria = new Criteria();
			appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
			if(app.getLinkName().equals(ApplicationLinkNames.FACILIO_MAIN_APP)) {
				appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), CommonOperators.IS_EMPTY));
			}
		selectBuilder.andCriteria(appCriteria);
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				
				DashboardFolderContext dashboardFolderContext = FieldUtil.getAsBeanFromMap(prop, DashboardFolderContext.class);
				dashboardFolderContext.setModuleName(modBean.getModule(dashboardFolderContext.getModuleId()).getName());
				dashboardFolderContexts.add(dashboardFolderContext);
			}
			
		}
		 
		return dashboardFolderContexts;
	}	
	
	public static DashboardFolderContext getorAddDashboardFolder(String moduleName,String dashboardFolderName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		 
		 GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardFolderFields())
					.table(ModuleFactory.getDashboardFolderModule().getTableName())
					.andCustomWhere(ModuleFactory.getDashboardFolderModule().getTableName()+".MODULEID = ?", module.getModuleId())
		 			.andCustomWhere(ModuleFactory.getDashboardFolderModule().getTableName()+".NAME = ?", dashboardFolderName);
			
		List<Map<String, Object>> props = selectBuilder.get();
		DashboardFolderContext dashboardFolderContext = null;
		if (props != null && !props.isEmpty()) {
			
			dashboardFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), DashboardFolderContext.class);
			
		}
		else {
			dashboardFolderContext = new DashboardFolderContext();
			dashboardFolderContext.setModuleId(module.getModuleId());
			dashboardFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			dashboardFolderContext.setName(dashboardFolderName);
			addDashboardFolder(dashboardFolderContext);
		}
		return dashboardFolderContext;
	}
	
	public static DashboardFolderContext getDashboardFolder(long id) throws Exception {
		
		 GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardFolderFields())
					.table(ModuleFactory.getDashboardFolderModule().getTableName())
					.andCustomWhere(ModuleFactory.getDashboardFolderModule().getTableName()+".ID = ?", id);
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			DashboardFolderContext dashboardFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), DashboardFolderContext.class);
			return dashboardFolderContext;
		}
		 
		return null;
	}
	
	public static DateRange getDateFilterFromDashboard(long dashboardId) throws Exception {
		
		DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
		if(dashboard != null && dashboard.getDateOperator() >0) {
			DateOperators dateOperators = (DateOperators) Operator.getOperator(dashboard.getDateOperator());
			if(dateOperators != null) {
				DateRange range = dateOperators.getRange(dashboard.getDateValue());
				return range;
			}
		}
		return null;
	}
	
	public static List<WidgetChartContext> getWidgetFromDashboard(long reportId,boolean isNewReport) throws Exception {
		
		String reportCollumn = null;
		if(isNewReport) {
			reportCollumn = "NEW_REPORT_ID";
		}
		else {
			reportCollumn = "REPORT_ID";
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWidgetChartFields())
					.table(ModuleFactory.getWidgetChartModule().getTableName())
					.andCustomWhere(ModuleFactory.getWidgetChartModule().getTableName()+"."+reportCollumn+" = ?", reportId);
			
		List<Map<String, Object>> props =  selectBuilder.get();
		
		List<WidgetChartContext> widgetCharts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				WidgetChartContext widgetChart = FieldUtil.getAsBeanFromMap(prop, WidgetChartContext.class);
				widgetCharts.add(widgetChart);
			}
		}
		
		return widgetCharts;
	}
	
	
	
	
	public static JSONArray getGroupedBooleanFields( List<Map<String, Object>> rs,JSONArray booleanResultOptions) {
		
		
		String previousValue = null;
		JSONObject booleanRes = new JSONObject();	
		JSONArray booleanResArray = new JSONArray();
		
		for(int i=0;i<rs.size();i++) {
 			Map<String, Object> thisMap = rs.get(i);
 			
 			String booleanValue = thisMap.get("value").toString();
 			
 			if(!booleanResultOptions.contains(booleanValue)) {
 				booleanResultOptions.add(booleanValue);
 			}
 			
 			if(previousValue == null) {
 				
 				booleanRes.put("startTime", thisMap.get("label"));
 				booleanRes.put("endTime", thisMap.get("label"));
 				booleanRes.put("value", booleanValue);
 				booleanRes.put("label", thisMap.get("label"));
 				
 				previousValue = booleanValue;
 				continue;
 			}
 			
 			if(previousValue.equals(booleanValue)) {
 				booleanRes.put("endTime", thisMap.get("label"));
 			}
 			else {
 				
 				booleanResArray.add(booleanRes);
 				
 				Object lastEndTime = booleanRes.get("endTime");
 				
 				booleanRes = new JSONObject();
 				booleanRes.put("startTime", lastEndTime);
 				booleanRes.put("endTime", thisMap.get("label"));
 				booleanRes.put("value", booleanValue);
 				booleanRes.put("label", thisMap.get("label"));
 			}
 			
 			if(i == rs.size()-1) {
 				booleanResArray.add(booleanRes);
 			}
 			previousValue = booleanValue;
		}
		
		return booleanResArray;
	}
	
	public static List<WidgetVsWorkflowContext> getCardWorkflowBasedOnStaticKey(String staticKey) throws Exception {
		
		List<WidgetVsWorkflowContext> workflowList = new ArrayList<>();
		
		switch(staticKey) {
			
		case STATIC_WIDGET_PROFILE_CARD_MINI :
			
			WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			workflowList.add(widgetVsWorkflowContext);
			
			return workflowList;
			
		case STATIC_WIDGET_WEATHER_CARD_MINI:
			
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			widgetVsWorkflowContext.setWorkflowString(WEATHER_WIDGET_WORKFLOW_STRING);
			widgetVsWorkflowContext.setWorkflowName("weather");
			workflowList.add(widgetVsWorkflowContext);
			return workflowList;
			
		case STATIC_WIDGET_ENERGY_CARBON_CARD_MINI:
			
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			
			if(AccountUtil.getCurrentOrg().getId() == 116l) {
				widgetVsWorkflowContext.setWorkflowString(CARBON_EMISSION_CARBON_MODULE_CARD);
			}
			else {
				widgetVsWorkflowContext.setWorkflowString(CARBON_EMISSION_CARD);
			}
			
			widgetVsWorkflowContext.setWorkflowName("carbonEmission");
			workflowList.add(widgetVsWorkflowContext);
			
			return workflowList;
			
		case STATIC_WIDGET_ENERGY_COST_CARD_MINI:
		
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			
			widgetVsWorkflowContext.setWorkflowName("currentMonth");
			
			if(AccountUtil.getCurrentOrg().getId() == 116l) {
				widgetVsWorkflowContext.setWorkflowString(ENERGY_COST_THIS_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
			}
			else {
				widgetVsWorkflowContext.setWorkflowString(ENERGY_COST_THIS_MONTH_CONSUMPTION_WORKFLOW);
			}
			workflowList.add(widgetVsWorkflowContext);
			
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			
			widgetVsWorkflowContext.setWorkflowName("lastMonth");
			
			if(AccountUtil.getCurrentOrg().getId() == 116l) {
				widgetVsWorkflowContext.setWorkflowString(ENERGY_COST_LAST_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
			}
			else {
				widgetVsWorkflowContext.setWorkflowString(ENERGY_COST_LAST_MONTH_CONSUMPTION_WORKFLOW);
			}
			workflowList.add(widgetVsWorkflowContext);
			
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			
			BaseLineContext baseline = BaseLineAPI.getBaseLine(RangeType.PREVIOUS_MONTH);
			
			String energyCostLastMonth = null;
			
			if(AccountUtil.getCurrentOrg().getId() == 116l) {
				energyCostLastMonth = ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_COST_MODULE_WORKFLOW;
			}
			else {
				energyCostLastMonth = ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_WORKFLOW;
			}
			
			energyCostLastMonth = energyCostLastMonth.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
			widgetVsWorkflowContext.setWorkflowName("lastMonthThisDate");
			widgetVsWorkflowContext.setWorkflowString(energyCostLastMonth);
			workflowList.add(widgetVsWorkflowContext);
			
			widgetVsWorkflowContext = new WidgetVsWorkflowContext();
			
			energyCostLastMonth = DashboardUtil.LAST_MONTH_THIS_DATE.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
			widgetVsWorkflowContext.setWorkflowName("lastMonthDate");
			widgetVsWorkflowContext.setWorkflowString(energyCostLastMonth);
			workflowList.add(widgetVsWorkflowContext);
			return workflowList;
			
		}
	
		return null;
	
	}
	
	public static JSONObject getCardParams(JSONObject jsonObject) {
		try {
			if(jsonObject == null) {
				jsonObject = new JSONObject();
			}
			jsonObject.put("orgId", AccountUtil.getCurrentOrg().getId());
			jsonObject.put("currentUserId", AccountUtil.getCurrentUser().getId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(jsonObject != null && jsonObject.containsKey("fieldId")) {
				FacilioField field = modBean.getField((Long) jsonObject.get("fieldId"));
				jsonObject.put("moduleName", field.getModule().getName());
				jsonObject.put("fieldName", field.getName());
			}
			if(jsonObject != null && !jsonObject.containsKey("fieldId") && jsonObject.containsKey("moduleName") && jsonObject.containsKey("fieldName")) {
				FacilioField field = modBean.getField((String)jsonObject.get("fieldName"), (String)jsonObject.get("moduleName"));
				jsonObject.put("fieldId", field.getFieldId());
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(),e);
		}
		return jsonObject;
	}
	
	public static SpaceFilteredDashboardSettings getSpaceFilteredDashboardSettings(Long dashboardId,Long basespaceId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSpaceFilteredDashboardSettingsFields())
				.table(ModuleFactory.getSpaceFilteredDashboardSettingsModule().getTableName())
				.orCustomWhere("DASHBOARD_ID = ? and BASESPACE_ID = ?", dashboardId,basespaceId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			SpaceFilteredDashboardSettings spaceFilteredDashboardSettings = FieldUtil.getAsBeanFromMap(props.get(0), SpaceFilteredDashboardSettings.class);
			return spaceFilteredDashboardSettings;
		}
		return null;
	}
	public static List<SpaceFilteredDashboardSettings> getSpaceFilteredDashboardSettings(Long dashboardId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSpaceFilteredDashboardSettingsFields())
				.table(ModuleFactory.getSpaceFilteredDashboardSettingsModule().getTableName())
				.orCustomWhere("DASHBOARD_ID = ?", dashboardId)
				.andCondition(CriteriaAPI.getCondition("MOBILE_ENABLED", "mobileEnabled", "true", BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<SpaceFilteredDashboardSettings> spaceFilteredDashboardSettings = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				SpaceFilteredDashboardSettings spaceFilteredDashboardSetting = FieldUtil.getAsBeanFromMap(prop, SpaceFilteredDashboardSettings.class);
				spaceFilteredDashboardSettings.add(spaceFilteredDashboardSetting);
			}
		}
		return spaceFilteredDashboardSettings;
	}
	
	public static SpaceFilteredDashboardSettings addSpaceFilteredDashboardSettings(SpaceFilteredDashboardSettings spaceFilteredDashboardSettings) throws Exception {
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSpaceFilteredDashboardSettingsModule().getTableName())
				.fields(FieldFactory.getSpaceFilteredDashboardSettingsFields())
				.addRecord(FieldUtil.getAsProperties(spaceFilteredDashboardSettings));
		
		insertRecordBuilder.save();
		
		spaceFilteredDashboardSettings.setId((long)insertRecordBuilder.getRecords().get(0).get("id"));
		
		return spaceFilteredDashboardSettings;
	}
	
	public static SpaceFilteredDashboardSettings updateSpaceFilteredDashboardSettings(SpaceFilteredDashboardSettings spaceFilteredDashboardSettings) throws Exception {
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getSpaceFilteredDashboardSettingsModule().getTableName())
				.fields(FieldFactory.getSpaceFilteredDashboardSettingsFields())
				.andCustomWhere("DASHBOARD_ID = ? and BASESPACE_ID = ?", spaceFilteredDashboardSettings.getDashboardId(),spaceFilteredDashboardSettings.getBaseSpaceId());
		
		Map<String, Object> fields = FieldUtil.getAsProperties(spaceFilteredDashboardSettings);
		fields.put("mobileEnabled", spaceFilteredDashboardSettings.getMobileEnabled());
		updateRecordBuilder.update(fields);
		return spaceFilteredDashboardSettings;
	}
	
	public static SpaceFilteredDashboardSettings addOrUpdateSpaceFilteredDashboardSettings(SpaceFilteredDashboardSettings spaceFilteredDashboardSettings) throws Exception {
		
		SpaceFilteredDashboardSettings spaceFilteredDashboardSettings1 = getSpaceFilteredDashboardSettings(spaceFilteredDashboardSettings.getDashboardId(),spaceFilteredDashboardSettings.getBaseSpaceId());
		
		if(spaceFilteredDashboardSettings1!= null) {
			spaceFilteredDashboardSettings1.setMobileEnabled(spaceFilteredDashboardSettings.getMobileEnabled());
			return updateSpaceFilteredDashboardSettings(spaceFilteredDashboardSettings1);
		}
		else {
			return addSpaceFilteredDashboardSettings(spaceFilteredDashboardSettings);
		}
	}
	
	public static void getEmrillFCUWidgetResult(Map<String,Object> result, List<Map<String, Object>> props) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		result.put("totalFcu", props.size());
		
		FacilioField fanStatusField = modBean.getField(453787l);
		
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		
		List<String> runningFcu = new ArrayList<>();
		List<String> allFcu = new ArrayList<>();
		
		int runningCount = 0;
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				rdmPairs.add(Pair.of((Long) prop.get("id"),fanStatusField));
				allFcu.add(prop.get("id").toString());
			}
			List<ReadingDataMeta> rdms = ReadingsAPI.getReadingDataMetaList(rdmPairs);
			
			LOGGER.log(Level.INFO,"rdms ---- "+rdms.size());
			
			for(ReadingDataMeta rdm :rdms) {
				try {
					Double runStatus = Double.valueOf(rdm.getValue().toString());
					if(runStatus > 0) {
						runningCount++;
						runningFcu.add(rdm.getResourceId()+"");
					}
				}
				catch(Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
				
			}
			
			result.put("runningFcu", runningCount);
			
			result.put("runningFcuList", runningFcu);
			result.put("allFcuList", allFcu);
			
			result.put("runningFcuPercentage", (runningCount/props.size()) * 100);
		}
	}
	
	public static JSONArray getEmrillFCUListWidgetResult(List<Map<String, Object>> props, Map<String, Object> result) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField fanStatusField = modBean.getField(453787l);
		FacilioField valveFeedbackField = modBean.getField(446206l);
		FacilioField returnTempField = modBean.getField(446196l);
		FacilioField setPointTempField = modBean.getField(446195l);
		
		
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		
		if(props != null && !props.isEmpty()) {
			
			Map<Long,Long> resourceSpaceMaps = new HashMap<>();
			Map<Long,String> resourceNameMaps = new HashMap<>();
			Map<Long,List<ReadingDataMeta>> rdmMap = new HashMap<>();
			for(Map<String, Object> prop :props) {
				
				rdmPairs.add(Pair.of((Long) prop.get("id"),fanStatusField));
				rdmPairs.add(Pair.of((Long) prop.get("id"),valveFeedbackField));
				rdmPairs.add(Pair.of((Long) prop.get("id"),returnTempField));
				rdmPairs.add(Pair.of((Long) prop.get("id"),setPointTempField));
				
				resourceSpaceMaps.put((Long) prop.get("id"), (Long) prop.get("space"));
				resourceNameMaps.put((Long) prop.get("id"), (String) prop.get("name"));
			}
			
			List<ReadingDataMeta> rdms = ReadingsAPI.getReadingDataMetaList(rdmPairs);
			
			for(ReadingDataMeta rdm :rdms) {
				
				if(rdmMap.containsKey(rdm.getResourceId())) {
					rdmMap.get(rdm.getResourceId()).add(rdm);
				}
				else {
					List<ReadingDataMeta> rdmList = new ArrayList<>();
					rdmList.add(rdm);
					rdmMap.put(rdm.getResourceId(), rdmList);
				}
			}
			Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(resourceSpaceMaps.values());
			
			JSONArray runStatusOnArray = new JSONArray();
			JSONArray runStatusOffArray = new JSONArray();
			
			for(Long resourceId :resourceSpaceMaps.keySet()) {
				
				JSONObject resJson = new JSONObject();
				resJson.put("id", resourceId);
				resJson.put("name", resourceNameMaps.get(resourceId));
				resJson.put("spaceName", spaceMap.get(resourceSpaceMaps.get(resourceId)).getName());
				resJson.put("spaceId", resourceSpaceMaps.get(resourceId));
				
				boolean isRunning = false;
				
				for(ReadingDataMeta rdm :rdmMap.get(resourceId)) {
					
					if(rdm.getFieldId() == fanStatusField.getFieldId()) {
						resJson.put("fanStatus", rdm.getValue());
						if (rdm.getValue() != null && ((Integer)rdm.getValue()).intValue() > 0) {
							isRunning = true;
						}
//						Double runStatus = Double.valueOf(rdm.getValue().toString());
//						if(runStatus > 0) {
//							isRunning = true;
//						}
					}
					else if (rdm.getFieldId() == valveFeedbackField.getFieldId()) {
						resJson.put("valveFeedback", rdm.getValue());
					}
					else if (rdm.getFieldId() == returnTempField.getFieldId()) {
						resJson.put("returnTemp", rdm.getValue());
					}
					else if (rdm.getFieldId() == setPointTempField.getFieldId()) {
						resJson.put("setPointTemp", rdm.getValue());
					}
					
				}
				if(isRunning) {
					runStatusOnArray.add(resJson);
				}
				else {
					runStatusOffArray.add(resJson);
				}
			}
			runStatusOnArray.addAll(runStatusOffArray);
			
			result.put("resultList", runStatusOnArray);
			result.put("runStatusField", fanStatusField);
			result.put("valveFeedbackField", valveFeedbackField);
			result.put("returnTempField", returnTempField);
			result.put("setPointTempField", setPointTempField);
			return runStatusOnArray;
		}
		return null;
	}
	public static void addWidgetFieldMapping(DashboardWidgetContext widget) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getDashboardUserFilterWidgetFieldMappingModule().getTableName())
				.andCustomWhere("WIDGET_ID = ?", widget.getId());
		deleteBuilder.delete();

		List<Map<String, Object>> widgetFieldMappingProps = new ArrayList<>();
		for(DashboardUserFilterWidgetFieldMappingContext widgetFieldMapping : widget.getWidgetFieldMapping()) {
			widgetFieldMappingProps.add(FieldUtil.getAsProperties(widgetFieldMapping));
		}
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardUserFilterWidgetFieldMappingModule().getTableName())
				.fields(FieldFactory.getDashboardUserFilterWidgetFieldMappingFields())
				.addRecords(widgetFieldMappingProps);
		insertRecordBuilder.save();
	}
}
class dashboardFolderSortByOrder implements Comparator<DashboardFolderContext> 
{ 
    // Used for sorting in ascending order of dashboard folders
	public int compare(DashboardFolderContext a, DashboardFolderContext b) 
    { 
    	if (b.getDisplayOrder() == -1) {
            return (a.getDisplayOrder()== -1) ? 0 : -1;
        }
        if (a.getDisplayOrder() == -1) {
            return 1;
        }
        return (int) (a.getDisplayOrder() - b.getDisplayOrder());
    }
}
class dashboardSortByOrder implements Comparator<DashboardContext> 
{ 
    // Used for sorting in ascending order of dashboards
    public int compare(DashboardContext a, DashboardContext b) 
    { 
    	if (b.getDisplayOrder() == null) {
            return (a.getDisplayOrder()== null) ? 0 : -1;
        }
        if (a.getDisplayOrder() == null) {
            return 1;
        }
        return (int) (a.getDisplayOrder() - b.getDisplayOrder());
    } 
}

