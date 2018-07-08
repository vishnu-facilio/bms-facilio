
package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardSharingContext;
import com.facilio.bmsconsole.context.DashboardSharingContext.SharingType;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.DerivationContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.NumberAggregateOperator;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReportBenchmarkRelContext;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportContext.LegendMode;
import com.facilio.bmsconsole.context.ReportContext.ReportChartType;
import com.facilio.bmsconsole.context.ReportDateFilterContext;
import com.facilio.bmsconsole.context.ReportEnergyMeterContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.context.ReportFormulaFieldContext;
import com.facilio.bmsconsole.context.ReportSpaceFilterContext;
import com.facilio.bmsconsole.context.ReportThreshold;
import com.facilio.bmsconsole.context.ReportUserFilterContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.UserWorkHourReading;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class DashboardUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DashboardUtil.class.getName());
    private static org.apache.log4j.Logger log = LogManager.getLogger(PickListOperators.class.getName());

    public static final String WEATHER_WIDGET_WORKFLOW_STRING = "<workflow> 		<parameter name=\"parentId\" type = \"Number\"/> 		<expression name=\"a\"> 				<module name=\"weather\"/> 				<criteria pattern=\"1\"> 						<condition sequence=\"1\">parentId`=`${parentId}</condition> 				</criteria> 				<orderBy name=\"ttime\" sort=\"desc\"/> 				<limit>1</limit> 		</expression> </workflow>";
	public static final String CARBON_EMISSION_CARD = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 				<module name=\"energydata\"/> 				<criteria pattern=\"1 and 2\"> 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 						<condition sequence=\"2\">ttime`Current Month`</condition> 				</criteria> 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 		</expression> 		<expression name=\"carbonConstant\"> 				<constant>0.44</constant> 		</expression> 	<result>a*carbonConstant</result> </workflow>";
	
	public static final String ENERGY_COST_LAST_MONTH_CONSUMPTION_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Last Month`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 		<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	public static final String ENERGY_COST_THIS_MONTH_CONSUMPTION_WORKFLOW = "<workflow> 		<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">ttime`Current Month upto now`</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 		<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	
	public static final String ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_WORKFLOW = "<workflow>	<parameter name=\"startTime\" type=\"Number\"/>  		<parameter name=\"endTime\" type=\"Number\"/>	<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 		 				<module name=\"energydata\"/> 		 				<criteria pattern=\"1 and 2\"> 			 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 			 						<condition sequence=\"2\">TTIME`baseLine{$$BASELINE_ID$$,0}between`${startTime}, ${endTime}</condition> 		 				</criteria> 		 				<field name=\"totalEnergyConsumptionDelta\" aggregate = \"sum\"/> 	 		</expression> 	<expression name=\"unitcost\"> 		 			<constant>0.41</constant> 	 		</expression> 	 		<result>a*unitcost</result> </workflow>";
	public static final String LAST_MONTH_THIS_DATE = "<workflow> 		<parameter name=\"startTime\" type=\"Number\"/>  		<parameter name=\"endTime\" type=\"Number\"/>	<parameter name=\"parentId\" type=\"Number\"/> 		<expression name=\"mainMeter\"> 		 		 				<function>default.getMainEnergyMeter(parentId)</function>	 		</expression>	<expression name=\"a\"> 				<module name=\"energydata\"/> 				<criteria pattern=\"1 and 2\"> 						<condition sequence=\"1\">parentId`=`${mainMeter}</condition> 						<condition sequence=\"2\">TTIME`baseLine{$$BASELINE_ID$$,0}between`${startTime}, ${endTime}</condition> 				</criteria> 				<field name=\"TTIME\" aggregate = \"max\"/> 			</expression> 		 		<result>a</result> </workflow>";
	
	
	public static final String STATIC_WIDGET_WEATHER_CARD = "weathercard";
	public static final String STATIC_WIDGET_ENERGY_COST_CARD = "energycost";
	public static final String STATIC_WIDGET_ENERGY_CARD = "energycard";
	public static final String STATIC_WIDGET_PROFILE_CARD = "profilecard";
	
	public static final String ENERGY_METER_PURPOSE_MAIN = "Main";
	public static List<String> workOrderXaxisOmitFields = new ArrayList<String>();
	public static List<String> workOrderYaxisOmitFields = new ArrayList<String>();
	public static List<String> workOrderGroupByOmitFields = new ArrayList<String>();
	
	public static List<String> workRequestXaxisOmitFields = new ArrayList<String>();
	public static List<String> workRequestYaxisOmitFields = new ArrayList<String>();
	public static List<String> workRequestGroupByOmitFields = new ArrayList<String>();
	
	public static List<String> alarmsXaxisOmitFields = new ArrayList<String>();
	public static List<String> alarmsYaxisOmitFields = new ArrayList<String>();
	public static List<String> alarmsGroupByOmitFields = new ArrayList<String>();
	
	public static List<String> eventsXaxisOmitFields = new ArrayList<String>();
	public static List<String> eventsYaxisOmitFields = new ArrayList<String>();
	public static List<String> eventsGroupByOmitFields = new ArrayList<String>();
	
	public static List<String> energyDataXaxisOmitFields = new ArrayList<String>();
	public static List<String> energyDataYaxisOmitFields = new ArrayList<String>();
	public static List<String> energyDataGroupByOmitFields = new ArrayList<String>();
	
	public static List<String> assetOmitFields = new ArrayList<String>();
	
	static {
		
		assetOmitFields.add("description");
		assetOmitFields.add("parentAssetId");
		assetOmitFields.add("serialNumber");
		assetOmitFields.add("localId");
		assetOmitFields.add("photoId");
		assetOmitFields.add("resourceType");
		assetOmitFields.add("id");
		
		energyDataXaxisOmitFields.add("activePowerB");
		energyDataXaxisOmitFields.add("activePowerR");
		energyDataXaxisOmitFields.add("activePowerY");
		energyDataXaxisOmitFields.add("apparentPowerB");
		energyDataXaxisOmitFields.add("apparentPowerR");
		energyDataXaxisOmitFields.add("apparentPowerY");
		energyDataXaxisOmitFields.add("frequencyB");
		energyDataXaxisOmitFields.add("frequencyR");
		energyDataXaxisOmitFields.add("frequencyY");
		energyDataXaxisOmitFields.add("lineCurrentB");
		energyDataXaxisOmitFields.add("lineCurrentR");
		energyDataXaxisOmitFields.add("lineCurrentY");
		energyDataXaxisOmitFields.add("lineVoltageB");
		energyDataXaxisOmitFields.add("lineVoltageR");
		energyDataXaxisOmitFields.add("lineVoltageY");
		energyDataXaxisOmitFields.add("phaseEnergyB");
		energyDataXaxisOmitFields.add("phaseEnergyBDelta");
		energyDataXaxisOmitFields.add("phaseEnergyR");
		energyDataXaxisOmitFields.add("phaseEnergyRDelta");
		energyDataXaxisOmitFields.add("phaseEnergyY");
		energyDataXaxisOmitFields.add("phaseEnergyYDelta");
		energyDataXaxisOmitFields.add("phaseVoltageB");
		energyDataXaxisOmitFields.add("phaseVoltageR");
		energyDataXaxisOmitFields.add("phaseVoltageY");
		energyDataXaxisOmitFields.add("powerFactorB");
		energyDataXaxisOmitFields.add("powerFactorR");
		energyDataXaxisOmitFields.add("powerFactorY");
		energyDataXaxisOmitFields.add("reactivePowerB");
		energyDataXaxisOmitFields.add("reactivePowerR");
		energyDataXaxisOmitFields.add("reactivePowerY");
		energyDataXaxisOmitFields.add("totalEnergyConsumption");
		energyDataXaxisOmitFields.add("totalEnergyConsumptionDelta");
		
		
		energyDataYaxisOmitFields.add("ttime");
		energyDataYaxisOmitFields.add("week");
		energyDataYaxisOmitFields.add("date");
		energyDataYaxisOmitFields.add("day");
		energyDataYaxisOmitFields.add("hour");
		energyDataYaxisOmitFields.add("month");
		energyDataYaxisOmitFields.add("parentId");
		
		workOrderXaxisOmitFields.add("subject");
		workOrderXaxisOmitFields.add("description");
		workOrderXaxisOmitFields.add("serialNumber");
		workOrderXaxisOmitFields.add("noOfNotes");
		workOrderXaxisOmitFields.add("noOfAttachments");
		workOrderXaxisOmitFields.add("noOfTasks");
		workOrderXaxisOmitFields.add("noOfClosedTasks");
		workOrderXaxisOmitFields.add("actualWorkDuration");
		workOrderXaxisOmitFields.add("estimatedWorkDuration");
		workOrderXaxisOmitFields.add("resumedWorkStart");
		workOrderXaxisOmitFields.add("isWorkDurationChangeAllowed");
		workOrderXaxisOmitFields.add("modifiedTime");
		workOrderXaxisOmitFields.add("resumedWorkStart");
		workOrderXaxisOmitFields.add("resource");
		
		alarmsXaxisOmitFields.add("subject");
		alarmsXaxisOmitFields.add("additionalInfoJsonStr");
		alarmsXaxisOmitFields.add("description");
		alarmsXaxisOmitFields.add("entityId");
		alarmsXaxisOmitFields.add("node");
		alarmsXaxisOmitFields.add("noOfAttachments");
		alarmsXaxisOmitFields.add("noOfEvents");
		alarmsXaxisOmitFields.add("noOfNotes");
		alarmsXaxisOmitFields.add("noOfTasks");
		alarmsXaxisOmitFields.add("previousSeverity");
		alarmsXaxisOmitFields.add("serialNumber");
//		alarmsXaxisOmitFields.add("subject");
		
		alarmsYaxisOmitFields.add("subject");
		alarmsYaxisOmitFields.add("additionalInfoJsonStr");
		alarmsYaxisOmitFields.add("description");
		alarmsYaxisOmitFields.add("entityId");
		alarmsYaxisOmitFields.add("node");
		alarmsYaxisOmitFields.add("noOfAttachments");
		alarmsYaxisOmitFields.add("noOfEvents");
		alarmsYaxisOmitFields.add("noOfNotes");
		alarmsYaxisOmitFields.add("noOfTasks");
		alarmsYaxisOmitFields.add("previousSeverity");
		alarmsYaxisOmitFields.add("serialNumber");
		alarmsYaxisOmitFields.add("acknowledgedTime");
		alarmsYaxisOmitFields.add("actualWorkEnd");
		alarmsYaxisOmitFields.add("actualWorkStart");
		alarmsYaxisOmitFields.add("scheduledStart");
		
		
		workOrderYaxisOmitFields.add("subject");
		workOrderYaxisOmitFields.add("description");
		workOrderYaxisOmitFields.add("dueDate");
		workOrderYaxisOmitFields.add("serialNumber");
		workOrderYaxisOmitFields.add("noOfNotes");
		workOrderYaxisOmitFields.add("noOfAttachments");
		workOrderYaxisOmitFields.add("noOfTasks");
		workOrderYaxisOmitFields.add("noOfClosedTasks");
		workOrderYaxisOmitFields.add("scheduledStart");
		workOrderYaxisOmitFields.add("estimatedEnd");
		workOrderYaxisOmitFields.add("requester");
		workOrderYaxisOmitFields.add("createdTime");
		workOrderYaxisOmitFields.add("resource");
		workOrderYaxisOmitFields.add("resumedWorkStart");
		workOrderYaxisOmitFields.add("isWorkDurationChangeAllowed");
		workOrderYaxisOmitFields.add("assignedBy");
		workOrderYaxisOmitFields.add("modifiedTime");
		
		
		workOrderGroupByOmitFields.add("subject");
		workOrderGroupByOmitFields.add("description");
		workOrderGroupByOmitFields.add("dueDate");
		workOrderGroupByOmitFields.add("serialNumber");
		workOrderGroupByOmitFields.add("scheduledStart");
		workOrderGroupByOmitFields.add("estimatedEnd");
		workOrderGroupByOmitFields.add("createdTime");
		workOrderGroupByOmitFields.add("resumedWorkStart");
		workOrderGroupByOmitFields.add("isWorkDurationChangeAllowed");
		workOrderGroupByOmitFields.add("modifiedTime");
		
		workRequestXaxisOmitFields.add("subject");
		workRequestXaxisOmitFields.add("description");
		workRequestXaxisOmitFields.add("serialNumber");
		workRequestXaxisOmitFields.add("noOfNotes");
		workRequestXaxisOmitFields.add("noOfAttachments");
		workRequestXaxisOmitFields.add("noOfTasks");
		workRequestXaxisOmitFields.add("noOfClosedTasks");
		
		
		workRequestYaxisOmitFields.add("subject");
		workRequestYaxisOmitFields.add("description");
		workRequestYaxisOmitFields.add("dueDate");
		workRequestYaxisOmitFields.add("serialNumber");
		workRequestYaxisOmitFields.add("scheduledStart");
		workRequestYaxisOmitFields.add("estimatedEnd");
		workRequestYaxisOmitFields.add("actualWorkStart");
		workRequestYaxisOmitFields.add("actualWorkEnd");
		workRequestYaxisOmitFields.add("requester");
		workRequestYaxisOmitFields.add("createdTime");
		workRequestYaxisOmitFields.add("assignedBy");
		
		
		workRequestGroupByOmitFields.add("subject");
		workRequestGroupByOmitFields.add("description");
		workRequestGroupByOmitFields.add("dueDate");
		workRequestGroupByOmitFields.add("serialNumber");
		workRequestGroupByOmitFields.add("scheduledStart");
		workRequestGroupByOmitFields.add("estimatedEnd");
		workRequestGroupByOmitFields.add("actualWorkStart");
		workRequestGroupByOmitFields.add("actualWorkEnd");
		workRequestGroupByOmitFields.add("requester");
		workRequestGroupByOmitFields.add("createdTime");
		workRequestGroupByOmitFields.add("assignedBy");
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
		for(BaseSpaceContext baseSpaceContext :baseSpaceContexts) {
			resourceList.add(baseSpaceContext.getId());
		}
		
		List<Long> assets = AssetsAPI.getAssetIdsFromBaseSpaceIds(resourceList);
		
		if(assets != null) {
			resourceList.addAll(assets);
		}
		
		return resourceList;
	}
	
	public static boolean addWidgetVsWorkflowContext(WidgetVsWorkflowContext widgetVsWorkflowContext) throws Exception {
		if (widgetVsWorkflowContext != null) {
			
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
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",energyMeterPurpose.getId()+"",NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static boolean deleteDashboard(Long dashboardId) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
		.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
		.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getDashboardSharingModule().getTableName())
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
	public static Integer getDataFromValue(Long timeValue,AggregateOperator aggregateOperator) {
		
		if(aggregateOperator.getValue().equals(10) || aggregateOperator.getValue().equals(12)) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			return dateTime.getMonth().getValue();
		}
		else if(aggregateOperator.getValue().equals(18)) {
			ZonedDateTime dateTime = DateTimeUtil.getDateTime(timeValue);
			return dateTime.getDayOfMonth();
		}
		return null;
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
	
	public static boolean deleteWidgetFromDashboard(Long dashboardId,Long widgetId) throws SQLException {
		
		if(dashboardId != null && widgetId != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId)
				.andCustomWhere("WIDGET_ID = ?", widgetId);
			
			
			int rowsDeleted = deleteRecordBuilder.delete();
			if(rowsDeleted > 0) {
				return true;
			}
		}
		return false;
		
	}
	
	public static boolean deleteWidgetFromDashboard(Long dashboardVsWidgetId) throws SQLException {
		
		if(dashboardVsWidgetId != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.andCustomWhere("ID = ?", dashboardVsWidgetId);
			
			int rowsDeleted = deleteRecordBuilder.delete();
			if(rowsDeleted > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static JSONObject getStandardVariance(List<Map<String, Object>> props,List<String> meterList) {
		
		try {
			Double min = null ,max = null,avg = null,sum = (double) 0;
			for(Map<String, Object> prop:props) {
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
			
			if(meterList != null && !meterList.isEmpty()) {
				LOGGER.log(Level.SEVERE, "meterList --- "+meterList);
				List<Long> bb = new ArrayList<Long>();
		        bb.add(null);
		        meterList.removeAll(bb);
				if(!meterList.isEmpty()) {
					
					List<String> uniqueList = (List<String>) SetUniqueList.decorate(meterList);
					LOGGER.log(Level.SEVERE, "uniqueList --- "+uniqueList);
			        if(uniqueList.size() == 1) {
			        	
			        	long meterID = Long.parseLong(uniqueList.get(0));
			        	
			        	EnergyMeterContext energyMeter = DeviceAPI.getEnergyMeter(meterID);
			        	if(energyMeter.isRoot()) {
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
			        		if(grossFloorArea > 0 && sum > 0) {
			        			double eui = sum/grossFloorArea;
			        			eui = eui * ReportsUtil.conversionMultiplier;
			        			variance.put("eui", eui);
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
	
	public static List<DashboardWidgetContext> getDashboardWidgetsFormDashboardId(Long dashboardId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
		fields.addAll(FieldFactory.getWidgetStaticFields());
		fields.addAll(FieldFactory.getWidgetWebFields());
		fields.addAll(FieldFactory.getDashbaordVsWidgetFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getWidgetModule().getTableName())
				.innerJoin(ModuleFactory.getDashboardVsWidgetModule().getTableName())
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getDashboardVsWidgetModule().getTableName()+".WIDGET_ID")
				.leftJoin(ModuleFactory.getWidgetChartModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetChartModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetListViewModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetListViewModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetStaticModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetStaticModule().getTableName()+".ID")
				.leftJoin(ModuleFactory.getWidgetWebModule().getTableName())		
				.on(ModuleFactory.getWidgetModule().getTableName()+".ID="+ModuleFactory.getWidgetWebModule().getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".DASHBOARD_ID = ?", dashboardId);
		
		selectBuilder.orderBy(ModuleFactory.getDashboardVsWidgetModule().getTableName() + ".LAYOUT_POSITION");
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<DashboardWidgetContext> dashboardWidgetContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) prop.get("type"));
				DashboardWidgetContext dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop, widgetType.getWidgetContextClass());
				dashboardWidgetContexts.add(dashboardWidgetContext);
			}
		}
		return dashboardWidgetContexts;
	}
	
	public static DashboardWidgetContext getWidget(Long widgetId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getWidgetFields();
		fields.addAll(FieldFactory.getWidgetChartFields());
		fields.addAll(FieldFactory.getWidgetListViewFields());
		fields.addAll(FieldFactory.getWidgetStaticFields());
		fields.addAll(FieldFactory.getWidgetWebFields());
		
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
				.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".ID = ?", widgetId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		DashboardWidgetContext dashboardWidgetContext = null;
		if (props != null && !props.isEmpty()) {
				WidgetType widgetType = WidgetType.getWidgetType((Integer) props.get(0).get("type"));
				dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(props.get(0), widgetType.getWidgetContextClass());
		}
		if(dashboardWidgetContext != null) {
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWidgetVsWorkflowFields())
					.table(ModuleFactory.getWidgetVsWorkflowModule().getTableName())
					.andCustomWhere(ModuleFactory.getWidgetVsWorkflowModule().getTableName()+".WIDGET_ID= ?",dashboardWidgetContext.getId());
			 props = selectBuilder.get();
			 if (props != null && !props.isEmpty()) {
				 for(Map<String, Object> prop:props) {
					 WidgetVsWorkflowContext widgetVsWorkflowContext = FieldUtil.getAsBeanFromMap(prop, WidgetVsWorkflowContext.class);
					 dashboardWidgetContext.addWidgetVsWorkflowContexts(widgetVsWorkflowContext);
				 }
			}
		}
		return dashboardWidgetContext;
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
		LOGGER.severe("111."+props);
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
	
	public static JSONArray getDashboardResponseJson(DashboardContext dashboard) {
		List dashboards = new ArrayList<>();
		dashboards.add(dashboard);
		return getDashboardResponseJson(dashboards);
	}
	
	public static JSONArray getDashboardResponseJson(List<DashboardContext> dashboards) {
		
		JSONArray result = new JSONArray();
		
//		Collections.sort(dashboards);
		 for(DashboardContext dashboard:dashboards) {
			 String dashboardName = dashboard.getDashboardName();
			Collection<DashboardWidgetContext> dashboardWidgetContexts = dashboard.getDashboardWidgets();
			JSONArray childrenArray = new JSONArray();
			for(DashboardWidgetContext dashboardWidgetContext:dashboardWidgetContexts) {
				childrenArray.add(dashboardWidgetContext.widgetJsonObject());
			}
			JSONObject dashboardJson = new JSONObject();
			dashboardJson.put("id", dashboard.getId());
			dashboardJson.put("label", dashboardName);
			if(dashboard.getReportSpaceFilterContext() != null) {
				dashboardJson.put("spaceFilter", "building");
			}
			dashboardJson.put("linkName", dashboard.getLinkName());
			dashboardJson.put("children", childrenArray);
			result.add(dashboardJson);
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
		
		Long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			dashboard.setDashboardWidgets(dashbaordWidgets);
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
		
		if (props != null && !props.isEmpty()) {
			DashboardContext dashboard = FieldUtil.getAsBeanFromMap(props.get(0), DashboardContext.class);
			List<DashboardWidgetContext> dashbaordWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			dashboard.setDashboardWidgets(dashbaordWidgets);
			dashboard.setReportSpaceFilterContext(getDashboardSpaceFilter(dashboard.getId()));
			return dashboard;
		}
		return null;
	}
	
	public static ReportSpaceFilterContext getDashboardSpaceFilter(Long dashboardId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportSpaceFilterFields())
				.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
				.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ReportSpaceFilterContext dashboardSpaceFilterContext = FieldUtil.getAsBeanFromMap(props.get(0), ReportSpaceFilterContext.class);
			return dashboardSpaceFilterContext;
		}
		return null;
	}

	public static List<DashboardContext> getDashboardList(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
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
				dashboardMap.put(dashboard.getId(), dashboard);
				dashboardIds.add(dashboard.getId());
			}
			return getFilteredDashboards(dashboardMap, dashboardIds);
		}
		return null;
	}
	
	public static List<DashboardContext> getFilteredDashboards(Map<Long, DashboardContext> dashboardMap, List<Long> dashboardIds) throws Exception {
		
		List<DashboardContext> dashboardList = new ArrayList<DashboardContext>();
		if (!dashboardMap.isEmpty()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDashboardSharingFields())
					.table(ModuleFactory.getDashboardSharingModule().getTableName())
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.andCondition(CriteriaAPI.getCondition("Dashboard_Sharing.DASHBOARD_ID", "dashboardId", StringUtils.join(dashboardIds, ","), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					DashboardSharingContext dashboardSharing = FieldUtil.getAsBeanFromMap(prop, DashboardSharingContext.class);
					if (dashboardIds.contains(dashboardSharing.getDashboardId())) {
						dashboardIds.remove(dashboardSharing.getDashboardId());
					}
					if(!dashboardList.contains(dashboardMap.get(dashboardSharing.getDashboardId()))) {
						if (dashboardSharing.getSharingTypeEnum().equals(SharingType.USER) && dashboardSharing.getOrgUserId() == AccountUtil.getCurrentAccount().getUser().getOuid()) {
							dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.ROLE) && dashboardSharing.getRoleId() == AccountUtil.getCurrentAccount().getUser().getRoleId()) {
							dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
						}
						else if (dashboardSharing.getSharingTypeEnum().equals(SharingType.GROUP)) {
							List<Group> mygroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentAccount().getUser().getOuid());
							for (Group group : mygroups) {
								if (dashboardSharing.getGroupId() == group.getGroupId() && !dashboardList.contains(dashboardMap.get(dashboardSharing.getDashboardId()))) {
									dashboardList.add(dashboardMap.get(dashboardSharing.getDashboardId()));
								}
							}
						}
					}
				}
				for (Long dashboardId : dashboardIds) {
					dashboardList.add(dashboardMap.get(dashboardId));
				}
			}
			else {
				dashboardList.addAll(dashboardMap.values());
			}
		}
		return dashboardList;
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
	
	public static String getWhereClauseForUserFilter(FacilioField field) {
		if(field.getDataTypeEnum().equals(FieldType.STRING)) {
			return field.getColumnName() +" = ?";
		}
		else {
			return field.getColumnName() +" between ? and ?";
		}
	}
	
	public static Object getFormulaValue(Long formulaId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFields())
				.table(ModuleFactory.getFormulaModule().getTableName())
				.andCustomWhere(ModuleFactory.getFormulaModule().getTableName()+".ID = ?", formulaId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		FormulaContext formulaContext = null;
		if (props != null && !props.isEmpty()) {
			formulaContext = FieldUtil.getAsBeanFromMap(props.get(0), FormulaContext.class);
		}
		
		if(formulaContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField selectField = formulaContext.getAggregateOperator().getSelectField(modBean.getFieldFromDB(formulaContext.getSelectFieldId()));
			
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(selectField);
			GenericSelectRecordBuilder selectValueBuilder = new GenericSelectRecordBuilder()
					.select(selectFields)
					.table(modBean.getModule(formulaContext.getModuleId()).getTableName());
			
			if(formulaContext.getCriteriaId() != null) {
				Criteria criteria = CriteriaAPI.getCriteria(formulaContext.getOrgId(), formulaContext.getCriteriaId());
				if(criteria != null) {
					selectValueBuilder.andCriteria(criteria);
				}
			}
			List<Map<String, Object>> rs = selectValueBuilder.get();
			if (rs != null && !rs.isEmpty()) {
				Object result = rs.get(0).get("value");
				return result;
			}
		}
		return null;
	}
	
	public static String getTimeFrameFloorValue(Operator dateOperator,String value) {
		
		switch(dateOperator.getOperatorId()) {
		
		case 30: 
		case 31:
		case 32:
			return "FLOOR("+value+"/(1000*60*60*24))";
		default:
			return null;
		}
 	}
	
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
					reportContext.setxAxisaggregateFunction(FormulaContext.NumberAggregateOperator.COUNT.getValue());
				}
				if(reportContext.getY1Axis() != null || reportContext.getY1AxisField() != null ) {
					ReportFieldContext reportY1AxisField = DashboardUtil.getReportField(reportContext.getY1AxisField());
					reportContext.setY1AxisField(reportY1AxisField);
					if(reportContext.getY1AxisaggregateFunction() == null) {
						reportContext.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.COUNT.getValue());
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
					.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
					.andCustomWhere(ModuleFactory.getReportSpaceFilterModule().getTableName()+".REPORT_ID = ?", reportId);
			
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
	
	public static void deleteReport(long reportId) throws SQLException {
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
		deleteRecordBuilder.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getBaseLineReportRelModule().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReport().getTableName())
		.andCustomWhere("ID = ?", reportId);
		deleteRecordBuilder.delete();
	}
	
	public static boolean addReportFolder(ReportFolderContext reportFolder) throws Exception {
		
		if (reportFolder != null) {
			reportFolder.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportFolder().getTableName())
					.fields(FieldFactory.getReportFolderFields());
			
			Map<String, Object> props = FieldUtil.getAsProperties(reportFolder);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			reportFolder.setId((Long) props.get("id"));
			return true;
		}
		return false;
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
	
	public static boolean addReport(ReportContext reportContext) throws Exception {
		
		boolean isSubReport = true;
		ReportContext parentReportContext = null;
		if (reportContext != null) {
			
			reportContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			if (reportContext.getDerivation() != null) {
				setDerivationFormulaProps(reportContext);
			}
			if(reportContext.getModuleId() == -1) {
				FacilioModule module = modBean.getModule(reportContext.getModuleName());
				reportContext.setModuleId(module.getModuleId());
			}
			else {
				FacilioModule module = modBean.getModule(reportContext.getModuleId());
				reportContext.setModuleName(module.getName());
			}
			
			List<FacilioField> fields = FieldFactory.getReportFields();
			
			if(reportContext.getParentFolderId() != null && reportContext.getReportEntityId() == null) {
				isSubReport = false;
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportEntityModule().getTableName())
						.fields(FieldFactory.getReportEntityFields());
				
				Map<String, Object> props = new HashMap<>();
				props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
				insertBuilder.addRecord(props);
				insertBuilder.save();
				
				reportContext.setReportEntityId((Long)props.get("id"));
				if(reportContext.getComparingReportContexts() != null) {
					for(ReportContext report :reportContext.getComparingReportContexts()) {
						report.setReportEntityId(reportContext.getReportEntityId());
					}
				}
			}
			else {
				parentReportContext = getParentReportForEntitiyId(reportContext.getReportEntityId());
				if(parentReportContext != null && parentReportContext.getIsComparisionReport()) {
					reportContext.setIsComparisionReport(parentReportContext.getIsComparisionReport());
				}
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReport().getTableName())
					.fields(fields);

			reportContext.setxAxis(DashboardUtil.addOrGetReportfield(reportContext.getxAxisField(), reportContext.getModuleName()).getId());
			if(reportContext.getxAxisaggregateFunction() == null) {
				reportContext.setxAxisaggregateFunction(FormulaContext.NumberAggregateOperator.COUNT.getValue());
			}
			if(reportContext.getY1AxisField() != null && reportContext.getY1AxisField().getModuleField() != null) {
				reportContext.setY1Axis(DashboardUtil.addOrGetReportfield(reportContext.getY1AxisField(), reportContext.getModuleName()).getId());
				if(reportContext.getY1AxisaggregateFunction() == null) {
					reportContext.setxAxisaggregateFunction(FormulaContext.NumberAggregateOperator.COUNT.getValue());
				}
			}
			if(reportContext.getGroupByField() != null && reportContext.getGroupByField().getModuleField() != null) {
				reportContext.setGroupBy(DashboardUtil.addOrGetReportfield(reportContext.getGroupByField(), reportContext.getModuleName()).getId());
			}
			

			Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			reportContext.setId((Long) props.get("id"));
			if(reportContext.getCriteria() != null) {
				
				Long criteriaId = CriteriaAPI.addCriteria(reportContext.getCriteria(), AccountUtil.getCurrentOrg().getId());
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportCriteria().getTableName())
						.fields(FieldFactory.getReportCriteriaFields());
				
				Map<String, Object> prop = new HashMap<String, Object>();
				prop.put("reportId", reportContext.getId());
				prop.put("criteriaId", criteriaId);
				insertBuilder.addRecord(prop).save();
			}
			if(reportContext.getReportUserFilters() != null) {
				for(ReportUserFilterContext userFilter : reportContext.getReportUserFilters()) {
					ReportFieldContext userFilterField = DashboardUtil.addOrGetReportfield(userFilter.getReportFieldContext(), reportContext.getModuleName());
					Map<String, Object> prop = new HashMap<String, Object>();
					prop.put("reportId", reportContext.getId());
					prop.put("reportFieldId", userFilterField.getId());
					prop.put("whereClause", DashboardUtil.getWhereClauseForUserFilter(userFilterField.getField()));
					
					insertBuilder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getReportUserFilter().getTableName())
							.fields(FieldFactory.getReportUserFilterFields());
					
					insertBuilder.addRecord(prop).save();
				}
			}
			if(reportContext.getReportThresholds() != null) {
				for(ReportThreshold threshhold : reportContext.getReportThresholds()) {
					
					Map<String, Object> prop = FieldUtil.getAsProperties(threshhold);
					prop.put("reportId", reportContext.getId());

					insertBuilder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getReportThreshold().getTableName())
							.fields(FieldFactory.getReportThresholdFields());
					
					insertBuilder.addRecord(prop).save();
				}
			}
			if(reportContext.getDateFilter() != null) {
				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getDateFilter());
				prop.put("reportId", reportContext.getId());

				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportDateFilter().getTableName())
						.fields(FieldFactory.getReportDateFilterFields());
				
				insertBuilder.addRecord(prop).save();
			}
			else if(isSubReport) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("reportId", reportContext.getId());
				prop.put("fieldId", reportContext.getxAxisField().getField().getId());
				prop.put("operatorId", parentReportContext.getDateFilter().getOperatorId());

				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportDateFilter().getTableName())
						.fields(FieldFactory.getReportDateFilterFields());
				
				insertBuilder.addRecord(prop).save();
				
				LOGGER.severe("sssssaaaa --- "+prop);
			}
//			if(reportContext.getEnergyMeter() != null) {
//				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getEnergyMeter());
//				prop.put("reportId", reportContext.getId());
//
//				insertBuilder = new GenericInsertRecordBuilder()
//						.table(ModuleFactory.getReportEnergyMeter().getTableName())
//						.fields(FieldFactory.getReportEnergyMeterFields());
//				
//				insertBuilder.addRecord(prop).save();
//				
//				prop = FieldUtil.getAsProperties(reportContext.getEnergyMeter());
//				prop.put("reportId", reportContext.getId());
//				LOGGER.severe("getReportSpaceFilterModule -- "+prop);
//				insertBuilder = new GenericInsertRecordBuilder()
//						.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
//						.fields(FieldFactory.getReportSpaceFilterFields());
//				
//				insertBuilder.addRecord(prop).save();
//			}
			if(reportContext.getReportSpaceFilterContext() != null) {
				
//				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getEnergyMeter());
//				if(prop == null) {
//					prop = new HashMap<>();
//				}
//				prop.put("reportId", reportContext.getId());
//
//				insertBuilder = new GenericInsertRecordBuilder()
//						.table(ModuleFactory.getReportEnergyMeter().getTableName())
//						.fields(FieldFactory.getReportEnergyMeterFields());
//				
//				insertBuilder.addRecord(prop).save();
				
				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getReportSpaceFilterContext());
				prop.put("reportId", reportContext.getId());
				
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportSpaceFilterModule().getTableName())
						.fields(FieldFactory.getReportSpaceFilterFields());
				
				insertBuilder.addRecord(prop).save();
			}
			if(reportContext.getBaseLineId() != -1) {
				
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getBaseLineReportRelModule().getTableName())
						.fields(FieldFactory.getBaseLineReportsRelFields());
				
				Map<String, Object> prop = new HashMap<>();
				prop.put("reportId", reportContext.getId());
				prop.put("id", reportContext.getBaseLineId());
				prop.put("adjustType", BaseLineContext.AdjustType.NONE.getValue());
				insertBuilder.addRecord(prop).save();
			}
			if(reportContext.getComparingReportContexts() != null) {
				for(ReportContext report: reportContext.getComparingReportContexts()) {
					DashboardUtil.addReport(report);
				}
			}
			return true;
		}
		return false;
	}
	
	public static List<ReportFormulaFieldContext> getFormulaFields(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
		select.select(FieldFactory.getReportFormulaFieldFields());
		select.table(ModuleFactory.getReportFormulaField().getTableName());
		select.andCustomWhere(ModuleFactory.getReportFormulaField().getTableName()+".ORGID = ?", AccountUtil.getCurrentOrg().getId());
		select.andCustomWhere(ModuleFactory.getReportFormulaField().getTableName()+".MODULEID = ?", modBean.getModule(moduleName).getModuleId());
		
		List<Map<String, Object>> props = select.get();
		List<ReportFormulaFieldContext> result = null;
		for(Map<String, Object> prop:props) {
			if(result == null) {
				result = new ArrayList<>();
			}
			ReportFormulaFieldContext reportFormulaFieldContext = FieldUtil.getAsBeanFromMap(prop, ReportFormulaFieldContext.class);
			result.add(reportFormulaFieldContext);
		}
		return result;
	}
	
	public static JSONObject getReportFields(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject result = new JSONObject();
		
		List<ReportFieldContext> dateFilterField = new ArrayList<ReportFieldContext>();
		List<ReportFieldContext> xAxisField = new ArrayList<ReportFieldContext>();
		List<ReportFieldContext> yAxisField = new ArrayList<ReportFieldContext>();
		List<ReportFieldContext> groupField = new ArrayList<ReportFieldContext>();
		
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		
		for(FacilioField field:allFields) {
			ReportFieldContext reportFieldContext = new ReportFieldContext();
			reportFieldContext.setFieldLabel(field.getDisplayName());
			reportFieldContext.setModuleFieldId(field.getId());
			reportFieldContext.setModuleField(field);
			
			if(field.getDataType() == FieldType.DATE_TIME.getTypeAsInt()) {
				dateFilterField.add(reportFieldContext);
			}
			
			if(moduleName.equals(ContextNames.WORK_ORDER)) {
				if(!workOrderXaxisOmitFields.contains(field.getName())) {
					xAxisField.add(reportFieldContext);
				}
				if(!workOrderYaxisOmitFields.contains(field.getName())) {
					yAxisField.add(reportFieldContext);
				}
				if(!workOrderGroupByOmitFields.contains(field.getName())) {
					groupField.add(reportFieldContext);
				}
			}
			else if (moduleName.endsWith(ContextNames.WORK_ORDER_REQUEST)) {
				
				if(!workRequestXaxisOmitFields.contains(field.getName())) {
					xAxisField.add(reportFieldContext);
				}
				if(!workRequestYaxisOmitFields.contains(field.getName())) {
					yAxisField.add(reportFieldContext);
				}
				if(!workRequestGroupByOmitFields.contains(field.getName())) {
					groupField.add(reportFieldContext);
				}
			}
			else if (moduleName.endsWith(ContextNames.ALARM)) {
				if(!alarmsXaxisOmitFields.contains(field.getName())) {
					xAxisField.add(reportFieldContext);
				}
				if(!alarmsYaxisOmitFields.contains(field.getName())) {
					yAxisField.add(reportFieldContext);
				}
				if(!alarmsGroupByOmitFields.contains(field.getName())) {
					groupField.add(reportFieldContext);
				}
			}
			else if (moduleName.endsWith(ContextNames.ENERGY_DATA_READING)) {
				if(!energyDataXaxisOmitFields.contains(field.getName())) {
					xAxisField.add(reportFieldContext);
				}
				if(!energyDataYaxisOmitFields.contains(field.getName())) {
					yAxisField.add(reportFieldContext);
				}
				if(!energyDataGroupByOmitFields.contains(field.getName())) {
					groupField.add(reportFieldContext);
				}
			}
		}
		List<ReportFormulaFieldContext> formulaFieldContexts = getFormulaFields(moduleName);
		if(formulaFieldContexts != null) {
			for(ReportFormulaFieldContext formulaFieldContext:formulaFieldContexts) {
				
				ReportFieldContext reportFieldContext = new ReportFieldContext();
				reportFieldContext.setFieldLabel(formulaFieldContext.getName());
				reportFieldContext.setReportFormulaContext(formulaFieldContext);
				reportFieldContext.setIsFormulaField(true);
				yAxisField.add(reportFieldContext);
			}
		}
		
		result.put("dateFilterFields", dateFilterField);
		result.put("moduleXAxisFields", xAxisField);
		result.put("yAxisFields", yAxisField);
		result.put("groupByFields", groupField);
		result.put("allFields", allFields);
		
		if(moduleName.endsWith(ContextNames.WORK_ORDER) || moduleName.endsWith(ContextNames.WORK_ORDER_REQUEST) || moduleName.endsWith(ContextNames.ALARM)) {
			allFields = modBean.getAllFields(ContextNames.ASSET);
			xAxisField = new ArrayList<>();
			for(FacilioField field:allFields) {
				if(!assetOmitFields.contains(field.getName())) {
					ReportFieldContext reportFieldContext = new ReportFieldContext();
					reportFieldContext.setFieldLabel(field.getDisplayName());
					reportFieldContext.setModuleFieldId(field.getId());
					reportFieldContext.setModuleField(field);
					xAxisField.add(reportFieldContext);
				}
			}
			result.put("assetXAxisFields", xAxisField);
			
			ReportFieldContext reportFieldContext = new ReportFieldContext();
			FacilioField resourceField = modBean.getField("resource", moduleName);
			reportFieldContext.setFieldLabel("Space");
			reportFieldContext.setModuleField(resourceField);
			reportFieldContext.setModuleFieldId(resourceField.getId());
			
			xAxisField = new ArrayList<>();
			
			xAxisField.add(reportFieldContext);
			
			result.put("spaceXAxisFields", xAxisField);
		}
		else if (moduleName.endsWith(ContextNames.ENERGY_DATA_READING)) {
			ReportFieldContext reportFieldContext = new ReportFieldContext();
			FacilioField resourceField = modBean.getField("purposeSpace", "energymeter");
			reportFieldContext.setFieldLabel("Space");
			reportFieldContext.setModuleField(resourceField);
			reportFieldContext.setModuleFieldId(resourceField.getId());
			
			xAxisField = new ArrayList<>();
			
			xAxisField.add(reportFieldContext);
			
			result.put("spaceXAxisFields", xAxisField);
			
			reportFieldContext = new ReportFieldContext();
			resourceField = modBean.getField("purpose", "energymeter");
			reportFieldContext.setFieldLabel("Purpose");
			reportFieldContext.setModuleField(resourceField);
			reportFieldContext.setModuleFieldId(resourceField.getId());
			
			xAxisField = new ArrayList<>();
			
			xAxisField.add(reportFieldContext);
			
			result.put("purposeXAxisFields", xAxisField);
		}
		
		return result;
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
		case COUNT: {
			return values.size();
			
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
	
	public static boolean populateBuildingEnergyReports(long buildingId, String buildingName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		
		if (getBuildingReportFolder(module.getName(), buildingId) != null) {
			// already building report folder exists...
			return true;
		}
		
		FacilioField ttimeFld = modBean.getField("ttime", module.getName());
		
		// create building folder
		ReportFolderContext buildingFolder = new ReportFolderContext();
		buildingFolder.setName(buildingName + " Reports");
		buildingFolder.setModuleId(module.getModuleId());
		buildingFolder.setBuildingId(buildingId);
		
		DashboardUtil.addReportFolder(buildingFolder);
		
		// High-res data report
		ReportContext highResReport = new ReportContext();
		highResReport.setName("Electricity Demand Today");
		highResReport.setDescription("Daily Electricity Load in High-Resolution");
		highResReport.setParentFolderId(buildingFolder.getId());
		highResReport.setChartType(ReportContext.ReportChartType.AREA.getValue());
		
		ReportFieldContext xAxisFld = new ReportFieldContext();
		xAxisFld.setModuleFieldId(ttimeFld.getId());
		highResReport.setxAxisField(xAxisFld);
		highResReport.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.ACTUAL.getValue());
		highResReport.setxAxisLabel("Time");
		
		ReportFieldContext y1AxisFld = new ReportFieldContext();
		y1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		highResReport.setY1AxisField(y1AxisFld);
		highResReport.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		highResReport.setY1AxisLabel("Energy Consumption");
		highResReport.setY1AxisUnit("kw");
		
		ReportEnergyMeterContext energyMeterFilter = new ReportEnergyMeterContext();
		highResReport.setEnergyMeter(energyMeterFilter);
		
		ReportSpaceFilterContext reportSpaceFilterContext = new ReportSpaceFilterContext();
		reportSpaceFilterContext.setBuildingId(buildingId);
		highResReport.setReportSpaceFilterContext(reportSpaceFilterContext);
		
		ReportDateFilterContext dateFilter = new ReportDateFilterContext();
		dateFilter.setFieldId(ttimeFld.getId());
		dateFilter.setOperatorId(DateOperators.TODAY.getOperatorId());
		highResReport.setDateFilter(dateFilter);
		
		DashboardUtil.addReport(highResReport);
		
		// End Use breakdown
		ReportContext endUseBreakdown = new ReportContext();
		endUseBreakdown.setName("End Use Breakdown");
		endUseBreakdown.setDescription("End Use Breakdown");
		endUseBreakdown.setParentFolderId(buildingFolder.getId());
		endUseBreakdown.setChartType(ReportContext.ReportChartType.STACKED_BAR.getValue());
		
		ReportFieldContext endUseXAxisFld = new ReportFieldContext();
		endUseXAxisFld.setModuleFieldId(ttimeFld.getId());
		endUseBreakdown.setxAxisField(endUseXAxisFld);
		endUseBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		endUseBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext endUseY1AxisFld = new ReportFieldContext();
		endUseY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		endUseBreakdown.setY1AxisField(endUseY1AxisFld);
		endUseBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		endUseBreakdown.setY1AxisLabel("Energy Consumption");
		endUseBreakdown.setY1AxisUnit("kwh");
		
		ReportEnergyMeterContext endUseEnergyMeterFilter = new ReportEnergyMeterContext();
		endUseEnergyMeterFilter.setGroupBy("service");
		endUseBreakdown.setEnergyMeter(endUseEnergyMeterFilter);
		
		reportSpaceFilterContext = new ReportSpaceFilterContext();
		reportSpaceFilterContext.setBuildingId(buildingId);
		endUseBreakdown.setReportSpaceFilterContext(reportSpaceFilterContext);
		
		ReportDateFilterContext endUseDateFilter = new ReportDateFilterContext();
		endUseDateFilter.setFieldId(ttimeFld.getId());
		endUseDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		endUseBreakdown.setDateFilter(endUseDateFilter);
		
		DashboardUtil.addReport(endUseBreakdown);
		
		// Cost usage by End use
		ReportContext costUseBreakdown = new ReportContext();
		costUseBreakdown.setName("Cost usage by End use");
		costUseBreakdown.setDescription("End Use Breakdown");
		costUseBreakdown.setParentFolderId(buildingFolder.getId());
		costUseBreakdown.setChartType(ReportContext.ReportChartType.STACKED_BAR.getValue());
		
		ReportFieldContext costUseXAxisFld = new ReportFieldContext();
		costUseXAxisFld.setModuleFieldId(ttimeFld.getId());
		costUseBreakdown.setxAxisField(costUseXAxisFld);
		costUseBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		costUseBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext costUseY1AxisFld = new ReportFieldContext();
		costUseY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		costUseBreakdown.setY1AxisField(costUseY1AxisFld);
		costUseBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		costUseBreakdown.setY1AxisLabel("Cost Usage");
		costUseBreakdown.setY1AxisUnit("cost");
		
		ReportEnergyMeterContext costUseEnergyMeterFilter = new ReportEnergyMeterContext();
		costUseEnergyMeterFilter.setGroupBy("service");
		costUseBreakdown.setEnergyMeter(costUseEnergyMeterFilter);
		
		reportSpaceFilterContext = new ReportSpaceFilterContext();
		reportSpaceFilterContext.setBuildingId(buildingId);
		costUseBreakdown.setReportSpaceFilterContext(reportSpaceFilterContext);
		
		ReportDateFilterContext costUseDateFilter = new ReportDateFilterContext();
		costUseDateFilter.setFieldId(ttimeFld.getId());
		costUseDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		costUseBreakdown.setDateFilter(costUseDateFilter);
		
		DashboardUtil.addReport(costUseBreakdown);
		
		// Daily Energy breakdown
		ReportContext dailyBreakdown = new ReportContext();
		dailyBreakdown.setName("Daily energy breakdown");
		dailyBreakdown.setDescription("Daily energy breakdown");
		dailyBreakdown.setParentFolderId(buildingFolder.getId());
		dailyBreakdown.setChartType(ReportContext.ReportChartType.BAR.getValue());
		
		ReportFieldContext dailyXAxisFld = new ReportFieldContext();
		dailyXAxisFld.setModuleFieldId(ttimeFld.getId());
		dailyBreakdown.setxAxisField(dailyXAxisFld);
		dailyBreakdown.setxAxisaggregateFunction(FormulaContext.DateAggregateOperator.FULLDATE.getValue());
		dailyBreakdown.setxAxisLabel("Date");
		
		ReportFieldContext dailyY1AxisFld = new ReportFieldContext();
		dailyY1AxisFld.setModuleFieldId(modBean.getField("totalEnergyConsumptionDelta", module.getName()).getId());
		dailyBreakdown.setY1AxisField(dailyY1AxisFld);
		dailyBreakdown.setY1AxisaggregateFunction(FormulaContext.NumberAggregateOperator.SUM.getValue());
		dailyBreakdown.setY1AxisLabel("Energy Consumption");
		dailyBreakdown.setY1AxisUnit("kwh");
		
		ReportEnergyMeterContext dailyEnergyMeterFilter = new ReportEnergyMeterContext();
		dailyBreakdown.setEnergyMeter(dailyEnergyMeterFilter);
		
		reportSpaceFilterContext = new ReportSpaceFilterContext();
		reportSpaceFilterContext.setBuildingId(buildingId);
		costUseBreakdown.setReportSpaceFilterContext(reportSpaceFilterContext);
		
		ReportDateFilterContext dailyDateFilter = new ReportDateFilterContext();
		dailyDateFilter.setFieldId(ttimeFld.getId());
		dailyDateFilter.setOperatorId(DateOperators.CURRENT_MONTH.getOperatorId());
		dailyBreakdown.setDateFilter(dailyDateFilter);
		
		DashboardUtil.addReport(dailyBreakdown);
		
		return true;
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
	
	public static List<Long> getDataSendingMeters(Long orgid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		
		FacilioField field = new FacilioField();
		field.setColumnName("DISTINCT PARENT_METER_ID");
		field.setName("parentId");
		
		ArrayList<FacilioField> selectFields = new ArrayList<FacilioField>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.andCustomWhere("ORGID = ?",orgid);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> meterIds = null;
		if (props != null && !props.isEmpty()) {
			meterIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				meterIds.add((Long) prop.get("parentId"));
			}
		}
		return meterIds;
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
	
	public static void addReportColumns (List<ReportColumnContext> reportColumns) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getReportColumnsModule().getTableName())
													.fields(FieldFactory.getReportColumnFields());
		
		for(int i = 0; i < reportColumns.size(); i++) {
			ReportColumnContext reportColumn = reportColumns.get(i);
			reportColumn.setOrgId(AccountUtil.getCurrentOrg().getId());
			reportColumn.setSequence(i+1);
			insertBuilder.addRecord(FieldUtil.getAsProperties(reportColumn));
		}
		insertBuilder.save();
	}
	
	public static List<ReportColumnContext> getReportColumns (long entityId) throws Exception {
		FacilioModule module = ModuleFactory.getReportColumnsModule();
		List<FacilioField> fields = FieldFactory.getReportColumnFields();
		FacilioField entityField = FieldFactory.getAsMap(fields).get("entityId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	public static void UpdateDashboardDisplayOrder(JSONObject dashboardDisplayOrder) throws Exception {
		
		for(Object key:dashboardDisplayOrder.keySet()) {
			Long dashboardId = Long.parseLong( key.toString()); 
			Integer order = Integer.parseInt(dashboardDisplayOrder.get(key).toString());
			
			Map<String,Object> value = new HashMap<>(); 
			
			value.put("displayOrder", order);
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getDashboardModule().getTableName())
			.fields(FieldFactory.getDashboardFields())
			.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID = ?", dashboardId);
			
			update.update(value);
		}
	}
	public static Integer getLastDashboardDisplayOrder(Long orgid,Long moduleId) throws Exception {
		
		if(orgid != null && moduleId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
			select.select(FieldFactory.getDashboardFields())
			.table(ModuleFactory.getDashboardModule().getTableName())
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getDashboardModule()))
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
		
		ReportContext oldReport = getReportContext(reportContext.getId());
		if (reportContext != null) {
			
			reportContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			if(reportContext.getModuleId() == -1) {
				FacilioModule module = modBean.getModule(reportContext.getModuleName());
				reportContext.setModuleId(module.getModuleId());
			}
			else {
				FacilioModule module = modBean.getModule(reportContext.getModuleId());
				reportContext.setModuleName(module.getName());
			}
			
			List<FacilioField> fields = FieldFactory.getReportFields();
			
			reportContext.setxAxis(DashboardUtil.addOrGetReportfield(reportContext.getxAxisField(), reportContext.getModuleName()).getId());
			if(reportContext.getY1AxisField() != null && reportContext.getY1AxisField().getModuleField() != null) {
				reportContext.setY1Axis(DashboardUtil.addOrGetReportfield(reportContext.getY1AxisField(), reportContext.getModuleName()).getId());
			}
			if(reportContext.getGroupByField() != null && reportContext.getGroupByField().getModuleField() != null) {
				reportContext.setGroupBy(DashboardUtil.addOrGetReportfield(reportContext.getGroupByField(), reportContext.getModuleName()).getId());
			}
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
			.table(ModuleFactory.getReport().getTableName())
			.fields(fields)
			.andCustomWhere(ModuleFactory.getReport().getTableName()+".ID = ?", reportContext.getId());
			
			
			Map<String, Object> reportProp = FieldUtil.getAsProperties(reportContext);
			update.update(reportProp);
			

			GenericInsertRecordBuilder insertBuilder;
			
			if(oldReport.getCriteria() != null) {
				
				GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder();
				delete.table(ModuleFactory.getReportCriteria().getTableName())
				.andCustomWhere(ModuleFactory.getReportCriteria().getTableName()+".REPORT_ID = ?", oldReport.getId());
				
				delete.delete();
				CriteriaAPI.deleteCriteria(oldReport.getCriteria().getCriteriaId());
			}
			
			if(reportContext.getCriteria() != null) {
				
				Long criteriaId = CriteriaAPI.addCriteria(reportContext.getCriteria(), AccountUtil.getCurrentOrg().getId());
				
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportCriteria().getTableName())
						.fields(FieldFactory.getReportCriteriaFields());
				
				Map<String, Object> prop = new HashMap<String, Object>();
				prop.put("reportId", reportContext.getId());
				prop.put("criteriaId", criteriaId);
				insertBuilder.addRecord(prop).save();
			}
			if(reportContext.getReportUserFilters() != null) {
			}
			if(reportContext.getReportThresholds() != null) {
			}
			if(reportContext.getDateFilter() != null) {
				
				GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder();
				
				delete.table(ModuleFactory.getReportDateFilter().getTableName())
				.andCustomWhere(ModuleFactory.getReportDateFilter().getTableName()+".REPORT_ID = ?", reportContext.getId());
				delete.delete();
				
				Map<String, Object> prop = FieldUtil.getAsProperties(reportContext.getDateFilter());
				prop.put("reportId", reportContext.getId());

				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportDateFilter().getTableName())
						.fields(FieldFactory.getReportDateFilterFields());
				
				insertBuilder.addRecord(prop).save();
			}
		}
		reportContext = getReportContext(reportContext.getId());
		return reportContext;
	}
	
	private static void setDerivationFormulaProps (ReportContext reportContext) throws Exception {
		
		DerivationContext derivation = reportContext.getDerivation();
		FormulaFieldContext formulaField = derivation.getFormulaField();
		if (formulaField.getId() == -1) { 
			addDefaultFormulaProps(derivation);
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formulaField);
			context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingInputType.HIDDEN_FORMULA_FIELD);
			context.put(FacilioConstants.ContextNames.DERIVATION, derivation);
			context.put(FacilioConstants.ContextNames.DATE_RANGE, derivation.getDateRange());
			
			Chain addFormulaFieldChain = FacilioChainFactory.addDerivationFormulaChain();
			addFormulaFieldChain.execute(context);
			formulaField = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		}
		
		FacilioField readingField = formulaField.getReadingField();
		reportContext.setModuleId(readingField.getModuleId());
		
		ReportFieldContext yAxisField = new ReportFieldContext();
		yAxisField.setModuleFieldId(readingField.getFieldId());
		reportContext.setY1AxisField(yAxisField);
		
		if (reportContext.getDateFilter() != null) {
			ReportDateFilterContext dateFilter = reportContext.getDateFilter();
			dateFilter.setField(readingField);
		}
	}
	
	private static void addDefaultFormulaProps(DerivationContext derivation) throws Exception {
		FormulaFieldContext formula = derivation.getFormulaField();
		List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowFields(derivation.getWorkflowId()).stream()
				.filter(f -> f.getResourceId() != -1).collect(Collectors.toList());
		formula.setResourceType(ResourceType.ONE_RESOURCE);
		formula.setResourceId(workflowFields.get(0).getResourceId()); // TODO Handle intelligently
		int interval = ReadingsAPI.getDataInterval(workflowFields);
		formula.setInterval(interval);
		// Temp...needs to remove once new analytics done
		if (formula.getWorkflow() == null) {
			WorkflowContext workflow = WorkflowUtil.getWorkflowContext(derivation.getWorkflowId());
			formula.setWorkflow(workflow);
		}
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
	
	public static Map<Long, Long> calculateWorkHours (List<Map<String, Object>> props, long startTime, long endTime) { //Expects it to be in order by time
		if (props != null && !props.isEmpty()) {
			Map<Long, List<Map<String, Object>>> userWiseProps = new HashMap<>();
			for (Map<String, Object> prop : props) {
				Long userId = (Long) prop.get("parentId");
				List<Map<String, Object>> userWiseList = userWiseProps.get(userId);
				if (userWiseList == null) {
					userWiseList = new ArrayList<>();
				}
				userWiseList.add(prop);
			}
			
			Map<Long, Long> workDuration = new HashMap<>();
			for (Map.Entry<Long, List<Map<String, Object>>> entry : userWiseProps.entrySet()) {
				Long userId = entry.getKey();
				long workTime = 0;
				long prevTime = startTime;
				for (Map<String, Object> prop : entry.getValue()) {
					Boolean hasManualEntry = (Boolean) prop.get("hasManualEntry");
					if (hasManualEntry == null || !hasManualEntry) {
						long currentTime = (long) prop.get("actualTtime");
						UserWorkHourReading workHour = UserWorkHourReading.valueOf((int) prop.get("workHoursEntry"));
						switch (workHour) {
							case START:
							case RESUME:
							case SHIFT_RESUME:
								prevTime = currentTime;
								break;
							case PAUSE:
							case SHIFT_PAUSE:
							case CLOSE:
								workTime += (currentTime - prevTime);
								break;
						}
					}
				}
				workDuration.put(userId, workTime);
			}
			return workDuration;
		}
		return null;
	}
	
	public static List<Map<String, Object>> convertMapToProps(Map<Long, Long> map) {
		
		if(map != null && !map.isEmpty()) {
			List<Map<String, Object>> rs = new ArrayList<>();
			for(Long lable: map.keySet()) {
				
				Long value = map.get(lable);
				
				Map<String, Object> map1 = new HashMap<>();
				map1.put("lable", lable);
				map1.put("value", value);
				
				rs.add(map1);
			}
			return rs;
		}
		return null;
	}
}
