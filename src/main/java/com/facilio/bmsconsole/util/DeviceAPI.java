package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.util.ExpressionEvaluator;

public class DeviceAPI 
{
	private static final Logger LOGGER = LogManager.getLogger(DeviceAPI.class.getName());
	
	public static final int VM_HISTORICAL_DATA_CALCULATION_INTERVAL = -3;
	private static final String TOTAL_ENERGY_CONSUMPTION_DELTA = "totalEnergyConsumptionDelta";
	private static final String TOTAL_DEMAND = "totalDemand";

	public static List<ControllerContext> getAllControllers() throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getControllerFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ControllerContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				controllers.add(FieldUtil.getAsBeanFromMap(prop, ControllerContext.class));
			}
			return controllers;
		}
		return null;
	}
	
	public static List<EnergyMeterContext> getPhysicalMeter(Long baseSpaceId,Long purposeId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		
		if (baseSpaceId != null) {
			FacilioField spaceIdFld = modBean.getField("purposeSpace", module.getName());
			
			Condition spaceCond = new Condition();
			spaceCond.setField(spaceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(baseSpaceId+"");
			
			selectBuilder.andCondition(spaceCond);
		}
		
		if(purposeId != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(modBean.getField("purpose", module.getName()), ""+purposeId, NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("IS_VIRTUAL","isVirtual","false",BooleanOperators.IS));
		return selectBuilder.get();
		
	}
	public static List<EnergyMeterContext> getVirtualMeters(Long baseSpaceId,Long purposeId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		
		if (baseSpaceId != null) {
			FacilioField spaceIdFld = modBean.getField("purposeSpace", module.getName());
			
			Condition spaceCond = new Condition();
			spaceCond.setField(spaceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(baseSpaceId+"");
			
			selectBuilder.andCondition(spaceCond);
		}
		if(purposeId != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(modBean.getField("purpose", module.getName()), ""+purposeId, NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("IS_VIRTUAL","isVirtual","true",BooleanOperators.IS));
		return selectBuilder.get();
		
	}
	
	public static Map<Long, ControllerContext> getAllControllersAsMap() throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getControllerFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<Long, ControllerContext> controllers = new HashMap<>();
			for(Map<String, Object> prop : props) {
				ControllerContext controller = FieldUtil.getAsBeanFromMap(prop, ControllerContext.class); 
				controllers.put(controller.getId(), controller);
			}
			return controllers;
		}
		return null;
	}

	//for the org..
	public static List<EnergyMeterContext> getAllEnergyMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<AssetContext> getAllChillerMeters() throws Exception {
		
		AssetCategoryContext chillerCategory = AssetsAPI.getCategory("Chiller");
		List<AssetContext> chillers = AssetsAPI.getAssetListOfCategory(chillerCategory.getId());
		return chillers;
	}
	
	public static List<AssetContext> getAllChillerPlants() throws Exception {
		AssetCategoryContext chillerPlantCategory = AssetsAPI.getCategory("Chiller Plant");
		List<AssetContext> chillerPlants = AssetsAPI.getAssetListOfCategory(chillerPlantCategory.getId());
		return chillerPlants;
	}

	//for the org..
	public static List<EnergyMeterContext> getSpecificEnergyMeters(String meters) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("Energy_Meter.ID IN (" + meters + ")")
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	//for building..
	public static List<EnergyMeterContext> getMainEnergyMeter(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	//for org..
	public static List<EnergyMeterContext> getAllMainEnergyMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.maxLevel(0);
		return selectBuilder.get();
	}
	public static Map<Long, Long> getMainEnergyMeterForAllBuildings() throws Exception {
		
		return getMainEnergyMeterForAllBuildings(null);
	}
	public static Map<Long, Long> getMainEnergyMeterForAllBuildings(List<Long> siteids) throws Exception {
		
		String idList = "";
		List<BuildingContext> buildings = null;
		if(siteids != null && !siteids.isEmpty()) {
			for(Long siteId :siteids) {
				
				List<BuildingContext> buildings1 = SpaceAPI.getSiteBuildings(siteId);
				if(buildings == null) {
					buildings = new ArrayList<>();
				}
				buildings.addAll(buildings1);
				
			}
		}
		else {
			buildings = SpaceAPI.getAllBuildings();
		}
		for(BuildingContext building :buildings) {
			idList = idList +building.getId()+",";
		}

		if(idList.length() > 0) {
			idList = idList.substring(0, idList.length() - 1);
		}
		
		List<EnergyMeterContext> energyMeters = DashboardUtil.getMainEnergyMeter(idList);
		Map <Long, Long> buildingVsMeter = new HashMap<>();
		for(EnergyMeterContext energyMeter :energyMeters) {
			buildingVsMeter.put(energyMeter.getPurposeSpace().getId(), energyMeter.getId());
		}
		return buildingVsMeter;
	}
	
	//for org..
	public static List<EnergyMeterContext> getAllServiceMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NOT NULL")
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getServiceMeter(long purposeId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("PURPOSE_ID= ?",purposeId )
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	// for building..
	public static List<EnergyMeterContext> getRootServiceMeters(String buildingList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NOT NULL")
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE-SPACE_ID",buildingList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}


	//for org..
	public static List<EnergyMeterContext> getEnergyMetersOfPurpose(String purposeList,boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT=?",root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static EnergyMeterPurposeContext getEnergyMetersPurposeByName(String purposeName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);

		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.andCondition(CriteriaAPI.getCondition("NAME","NAME",purposeName,StringOperators.IS))
				.maxLevel(0);
		 List<EnergyMeterPurposeContext> props = selectBuilder.get();
		 if(props != null && !props.isEmpty()) {
			return props.get(0);
		 }
		 return null;
	}

	//floor/space/zone service/purpose meters when root is false..
	// building's raiser Main of a purpose when root is true with building ID..
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList,String purposeList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?",root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	//building root meters including service/purpose meters when root is true..
	// floor meters when root is false & spaceList as floor ID...
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE-SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(Long buildingId, Long purposeId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		
		if (buildingId != null) {
			FacilioField spaceIdFld = modBean.getField("purposeSpace", module.getName());
			
			Condition spaceCond = new Condition();
			spaceCond.setField(spaceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
			
			selectBuilder.andCondition(spaceCond);
		}
		if (purposeId != null) {
			selectBuilder.andCustomWhere("PURPOSE_ID= ?", purposeId);
		}
		
		return selectBuilder.get();
	}
	
	//for org..
	public static List<EnergyMeterPurposeContext> getAllPurposes() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static void deleteEnergyData(long meterId, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String, FacilioField> fields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING));
		
		DeleteRecordBuilder<ReadingContext> deleteBuilder = new DeleteRecordBuilder<ReadingContext>()
														.module(module)
														.andCondition(CriteriaAPI.getCondition(fields.get("parentId"), String.valueOf(meterId), NumberOperators.EQUALS))
														.andCustomWhere("TTIME BETWEEN ? AND ?", startTime, endTime);
		deleteBuilder.delete();
	}
	
	public static List<EnergyMeterContext> getAllVirtualMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_VIRTUAL = ?", true)
				.orderBy("Energy_Meter.ID")
				;

		List<EnergyMeterContext> virtualMeters = selectBuilder.get();
		return virtualMeters;
	}
	
	public static List<EnergyMeterContext> getVirtualMeters(List<Long> deviceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_VIRTUAL = ?", true)
				.andCondition(CriteriaAPI.getIdCondition(deviceList, module));
		List<EnergyMeterContext> virtualMeters = selectBuilder.get();
		return virtualMeters;
	}
	
	public static EnergyMeterContext getEnergyMeter (long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																.select(fields)
																.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
																.beanClass(EnergyMeterContext.class)
																.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<EnergyMeterContext> meters = selectBuilder.get();
		if (meters != null && !meters.isEmpty()) {
			return meters.get(0);
		}
		return null;
	}
	
	public static void addHistoricalVMCalculationJob(long meterId, long startTime, long endTime, int interval,boolean updateReading, boolean runParentMeter) throws Exception {
		
		GenericInsertRecordBuilder historyVMBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getHistoricalVMCalculationFields())
				.table(ModuleFactory.getHistoricalVMModule().getTableName());
		Map<String, Object> jobProp = new HashMap<>();
		jobProp.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		jobProp.put("meterId",meterId);
		jobProp.put("startTime", startTime);
		jobProp.put("endTime",endTime);
		jobProp.put("intervalValue", interval);
		jobProp.put("updateReading", updateReading);
		jobProp.put("runParentMeter", runParentMeter);
		long jobId= historyVMBuilder.insert(jobProp);
		FacilioTimer.scheduleOneTimeJob(jobId, "HistoricalVMCalculation", 30, "facilio");
	}
	
	
	public static void addVMReadingsJob(long startTime, long endTime, int minutesInterval, boolean runParentMeter) throws Exception {
		addVMReadingsJob(DeviceAPI.getAllVirtualMeters(), startTime,endTime, minutesInterval, runParentMeter);
	}
	
	public static void addVirtualMeterReadingsJob(long startTime, long endTime, int minutesInterval, List<Long> vmList, boolean runParentMeter) throws Exception {
		if(vmList == null || vmList.isEmpty()) {
			addVMReadingsJob(startTime,endTime, minutesInterval, runParentMeter);
			return;
		}
		addVMReadingsJob(getVirtualMeters(vmList), startTime,endTime, minutesInterval, runParentMeter);
	}
	
	
	private static void addVMReadingsJob(List<EnergyMeterContext> virtualMeters, long startTime, long endTime,
			int minutesInterval, boolean runParentMeter) throws Exception {

		for(EnergyMeterContext meter : virtualMeters) {

			addHistoricalVMCalculationJob(meter.getId(), startTime, endTime, minutesInterval,false, runParentMeter);
		}
	}

	

	public static void insertVirtualMeterReadings(EnergyMeterContext meter, List<Long> childMeterIds,  long startTime, long endTime, int minutesInterval, boolean updateReading, boolean isHistorical) throws Exception {

		
		if(childMeterIds == null) {
			return;
		}
		List<DateRange> intervals= DateTimeUtil.getTimeIntervals(startTime, endTime, minutesInterval);
		List<ReadingContext> completeReadings = new LinkedList<>(getChildMeterReadings(childMeterIds, startTime, endTime, minutesInterval));
		if(completeReadings.isEmpty()) {
			return;
		}
		List<ReadingContext> vmReadings = new ArrayList<ReadingContext>();
		List<ReadingContext> intervalReadings=new ArrayList<ReadingContext>();
		for(int i = 0; i < intervals.size(); i++) {
			DateRange interval = intervals.get(i);
			double iStartTime = Math.floor(interval.getStartTime()/1000);
			double iEndTime = Math.floor(interval.getEndTime()/1000);
			
			Iterator<ReadingContext> itr = completeReadings.iterator();
			while (itr.hasNext()) {
				ReadingContext reading= itr.next();
				double ttime = Math.floor(reading.getTtime()/1000); //Checking only in second level
				if(ttime >= iStartTime && ttime <= iEndTime) {
					intervalReadings.add(reading);
					itr.remove();
				}
				else {
					break;
				}
			}
			
			LOGGER.debug("Calculating Consumption for VM : "+meter.getName() + " between " + interval);
//			if (AccountUtil.getCurrentOrg().getId() == 116) {
				LOGGER.debug("Intervals : "+interval);
//			}
			ReadingContext virtualMeterReading = calculateVMReading(meter,intervalReadings, childMeterIds, interval, isHistorical || i != (intervals.size() - 1));
//			if (AccountUtil.getCurrentOrg().getId() == 116) {
				LOGGER.debug("Calculated VM Reading : "+virtualMeterReading);
//			}
			if(virtualMeterReading != null) {
//				if (AccountUtil.getCurrentOrg().getId() == 116) {
					LOGGER.debug("Adding VM reading for time : "+virtualMeterReading.getTtime());
//				}
				vmReadings.add(virtualMeterReading);
				intervalReadings=new ArrayList<ReadingContext>();
			}
		}

		LOGGER.debug("VM Readings size : "+vmReadings.size());
		if (!vmReadings.isEmpty()) {

			ReadingContext firstReading=vmReadings.get(0);
			long firstReadingTime =firstReading.getTtime();
			ReadingContext lastReading=vmReadings.get(vmReadings.size() - 1);
			deleteEnergyData(meter.getId(), firstReadingTime, lastReading.getTtime()); //Deleting anyway to avoid duplicate entries
			
			
			boolean runThroughUpdate= Math.floor((System.currentTimeMillis()-endTime)/(60*1000)) < minutesInterval;
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.ENERGY_DATA_READING );
			context.put(FacilioConstants.ContextNames.READINGS, vmReadings);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, updateReading || runThroughUpdate);
			context.put(FacilioConstants.ContextNames.HISTORY_READINGS, isHistorical);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
			
			//data Gap implementation starts here..
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			FacilioField energyField=modBean.getField(TOTAL_ENERGY_CONSUMPTION_DELTA, module.getName());
			long resourceId=meter.getId();
			long previousTime=getPreviousDataTime(resourceId,energyField);
			if(!isHistorical && getDataGapCount(resourceId,energyField, module,firstReadingTime,previousTime)>1) {
				
				firstReading.setMarked(true);
				List<MarkedReadingContext> markedList=new ArrayList<MarkedReadingContext> ();
				markedList.add(MarkingUtil.getMarkedReading(firstReading,energyField.getFieldId(),module.getModuleId(),MarkType.HIGH_VALUE_HOURLY_VIOLATION,firstReading,firstReading));
				context.put(FacilioConstants.ContextNames.MARKED_READINGS, markedList);
			}
			//data Gap implementation ends..
			Chain addReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			addReading.execute(context);
		}
	}

	
	private static long getPreviousDataTime(long resourceId,FacilioField energyField) {
		try {
			List<Pair<Long, FacilioField>> deltaRdmPairs = new ArrayList<>();
			deltaRdmPairs.add(Pair.of(resourceId, energyField));
			List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(deltaRdmPairs) ;
			for(ReadingDataMeta meta : metaList) {
				return meta.getTtime();
			}
		}
		catch(Exception e) {
			LOGGER.error("Exception while fetching previousTime: ", e);
		}
		return 0;
	}
	
	public static List<Long> getChildrenMeters(EnergyMeterContext meter) throws Exception {
		GenericSelectRecordBuilder childMeterBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVirtualMeterRelFields())
				.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
				.andCustomWhere("VIRTUAL_METER_ID = ?", meter.getId());
		List<Map<String, Object>> childProps = childMeterBuilder.get();
		if(childProps == null || childProps.isEmpty()) {
			return null;
		}
		List<Long> childMeterIds = new ArrayList<>();
		for(Map<String, Object> childProp : childProps) {
			childMeterIds.add((Long) childProp.get("childMeterId"));
		}
		return childMeterIds;
	}

	
	public static List<Long> getParentMeters(EnergyMeterContext meter) throws Exception {
		GenericSelectRecordBuilder parentMeterBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVirtualMeterRelFields())
				.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
				.andCustomWhere("CHILD_METER_ID = ?", meter.getId());
		List<Map<String, Object>> parentProps = parentMeterBuilder.get();
		if(parentProps == null || parentProps.isEmpty()) {
			return null;
		}
		List<Long> parentMeterIds = new ArrayList<>();
		for(Map<String, Object> childProp : parentProps) {
			parentMeterIds.add((Long) childProp.get("virtualMeterId"));
		}
		return parentMeterIds;
	}
	
	
	public static float getDataGapCount (long resourceId,FacilioField field, FacilioModule module, long currentTime, long previousTime)  {
		
		try {
			long dataIntervalSeconds=ReadingsAPI.getDataInterval(resourceId, field, module)*60;
			//here doing the floor roundoff..
			SecondsChronoUnit defaultAdjustUnit = new SecondsChronoUnit(dataIntervalSeconds);
			ZonedDateTime zdt=	DateTimeUtil.getDateTime(currentTime).truncatedTo(defaultAdjustUnit);
			long timeDiff=DateTimeUtil.getMillis(zdt, true)-previousTime;
			float gapCount=timeDiff/(dataIntervalSeconds*1000);
			return gapCount;
		}
		catch(Exception e) {
			LOGGER.error("Exception while cheking data Gap", e);
		}
		return 1;
	}
	private static List<ReadingContext> getChildMeterReadings(List<Long> childIds, long startTime, long endTime, int minutesInterval) throws Exception{

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField meterField=modBean.getField("parentId", FacilioConstants.ContextNames.ENERGY_DATA_READING);

		FacilioField timeField = new FacilioField();
		timeField.setName("ttime");
		timeField.setColumnName("MAX(TTIME)");
		timeField.setDataType(FieldType.NUMBER);
		
		FacilioField deltaField = new FacilioField();
		deltaField.setName("totalEnergyConsumptionDelta");
		deltaField.setColumnName("SUM(TOTAL_ENERGY_CONSUMPTION_DELTA)");
		deltaField.setDataType(FieldType.DECIMAL);
		
		FacilioField powerField = new FacilioField();
		powerField.setName("totalDemand");
		powerField.setColumnName("AVG(TOTAL_DEMAND)");
		powerField.setDataType(FieldType.DECIMAL);

		List<FacilioField> fields= new ArrayList<FacilioField>();

		fields.add(timeField);
		fields.add(meterField);
		fields.add(deltaField);
		fields.add(powerField);

		long timeInterval=minutesInterval*60*1000;

		SelectRecordsBuilder<ReadingContext> getReadings = new SelectRecordsBuilder<ReadingContext>()
				.beanClass(ReadingContext.class)
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.ENERGY_DATA_READING)
				.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentId", StringUtils.join(childIds, ","), NumberOperators.EQUALS))
				.andCustomWhere("TTIME BETWEEN ? AND ?", startTime, endTime)
//				.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+", "+endTime, DateOperators.BETWEEN))
				.groupBy("PARENT_METER_ID,TTIME/"+timeInterval)
				.orderBy("ttime");

		return getReadings.get();

	}
	
	
	private static ReadingContext calculateVMReading(EnergyMeterContext meter,List<ReadingContext> readings, List<Long> childIds, DateRange interval, boolean ignoreNullValues) throws Exception {
		ReadingContext virtualMeterReading = null;
		if (!readings.isEmpty()) {
			Map<Long, List<ReadingContext>> readingMap = new HashMap<>();
			List<Long> timestamps = new ArrayList<>();
			for(ReadingContext reading : readings) {
				List<ReadingContext> readingList = readingMap.get(reading.getParentId());
				if(readingList == null) {
					readingList = new ArrayList<>();
					readingMap.put(reading.getParentId(), readingList);
				}
				readingList.add(reading);
				timestamps.add(reading.getTtime());
			}
			
//			if (AccountUtil.getCurrentOrg().getId() == 116) {
				LOGGER.debug("Child Meter IDs : "+childIds);
				LOGGER.debug("Meter wise readings : "+readingMap);
//			}
			
			if (!ignoreNullValues) {
				for(Long childId : childIds) {
					if(!readingMap.containsKey(childId)) {
						return null;
					}
				}
			}
			
			EnergyDataEvaluator evaluator = new EnergyDataEvaluator(readingMap, ignoreNullValues);
			String expression = meter.getChildMeterExpression();
			virtualMeterReading = evaluator.evaluateExpression(expression);
			if(virtualMeterReading==null) {
				return null;
			}
			virtualMeterReading.setTtime(((Double)StatUtils.max(timestamps.stream().mapToDouble(Long::doubleValue).toArray())).longValue());
			virtualMeterReading.setParentId(meter.getId());
		}
		return virtualMeterReading;
	}
	
	private static class EnergyDataEvaluator extends ExpressionEvaluator<ReadingContext> {

		private Map<Long, List<ReadingContext>> readingMap;
		private boolean ignoreNullValues;
		public EnergyDataEvaluator(Map<Long, List<ReadingContext>> readingMap, boolean ignoreNullValues) {
			super.setRegEx(EnergyMeterContext.EXP_FORMAT);
			this.readingMap = readingMap;
			this.ignoreNullValues = ignoreNullValues;
		}
		
		@Override
		public ReadingContext getOperand(String operand) {
			// TODO Auto-generated method stub
			List<ReadingContext> readings = readingMap.get(Long.parseLong(operand));
			
			if (readings == null || readings.size() == 0) {
				if (ignoreNullValues) {
					ReadingContext reading = new ReadingContext();
					reading.addReading(TOTAL_ENERGY_CONSUMPTION_DELTA, 0d);
					reading.addReading(TOTAL_DEMAND, 0d);
					return reading;
				}
				else {
					return null;
				}
			}
			if(readings.size() == 1) {
				return readings.get(0);
			}
			ReadingContext aggregatedReading = new ReadingContext();
			double deltaSum = -1d;
			double demandSum = -1d;
			for(ReadingContext reading : readings) {
				Double delta = (Double) reading.getReading(TOTAL_ENERGY_CONSUMPTION_DELTA);
				Double demand = (Double) reading.getReading(TOTAL_DEMAND);
				
			    if(delta != null) {
                    deltaSum += delta;
                }
			    if(demand != null) {
                    demandSum += demand;
			    }
			}
			if (deltaSum != -1) {
				aggregatedReading.addReading(TOTAL_ENERGY_CONSUMPTION_DELTA, ++deltaSum);
			}
			if (demandSum != -1) {
				aggregatedReading.addReading(TOTAL_DEMAND, (++demandSum)/readings.size());//average for the same meter..
			}
			return aggregatedReading;
		}

		@Override
		public ReadingContext applyOp(String operator, ReadingContext rightOperand, ReadingContext leftOperand)  {
			// TODO Auto-generated method stub
			if( rightOperand == null || leftOperand == null) {
			    LOGGER.debug("opertor " + operator + " : left operand " + leftOperand +" right operand " + rightOperand + " : ignoreNUll" + ignoreNullValues);
				return null;
			}
			ReadingContext reading = new ReadingContext();
			Double delta=getValue(TOTAL_ENERGY_CONSUMPTION_DELTA,operator,rightOperand,leftOperand);
			Double demand=getValue(TOTAL_DEMAND,operator,rightOperand,leftOperand);
//			reading.addReading(TOTAL_ENERGY_CONSUMPTION_DELTA,delta);
//			reading.addReading(TOTAL_DEMAND, demand);
			if(delta == null && demand == null) {
				return null;
			}
			
			if (delta != null) {
				reading.addReading(TOTAL_ENERGY_CONSUMPTION_DELTA,delta);
			}
			if (demand != null) {
				reading.addReading(TOTAL_DEMAND, demand);
			}
			return reading;
		}
		
		private Double getValue(String key,String operator, ReadingContext rightOperand, ReadingContext leftOperand) {
			
			Double left = (Double) leftOperand.getReading(key);
			Double right = (Double) rightOperand.getReading(key);
			
			if (ignoreNullValues) {
				if (left == null) {
					left = 0d;
				}
				if (right == null) {
					right = 0d;
				}
			}
			if(left == null || right == null) {
			    return null;
            }
			double total = 0d;
			if("+".equals(operator)) {
				total = left + right;
			} else if("-".equals(operator)) {
				total = left - right;
			}
			return total;
		}
		
	}

}
