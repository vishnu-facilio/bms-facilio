package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.StatUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerSettingsContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.ExpressionEvaluator;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());
	
	public static final int VM_HISTORICAL_DATA_CALCULATION_INTERVAL = -3;

	public static List<ControllerSettingsContext> getAllControllers() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getControllerModule().getTableName())
				.select(FieldFactory.getControllerFields())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ControllerSettingsContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				controllers.add(FieldUtil.getAsBeanFromMap(prop, ControllerSettingsContext.class));
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
//			FacilioField spaceIdFld = new FacilioField();
//			spaceIdFld.setName("space_id");
//			spaceIdFld.setColumnName("SPACE_ID");
//			spaceIdFld.setModule(ModuleFactory.getAssetsModule());
//			spaceIdFld.setDataType(FieldType.NUMBER);
			
			FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			FacilioField spaceIdFld = modBean.getField("spaceId", resourceModule.getName());
			
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
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fields.get("parentId"), String.valueOf(meterId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fields.get("ttime"), startTime+", "+endTime, DateOperators.BETWEEN))
														;
		
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
																.andCustomWhere("IS_VIRTUAL = ?", true)
																.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<EnergyMeterContext> meters = selectBuilder.get();
		if (meters != null && !meters.isEmpty()) {
			return meters.get(0);
		}
		return null;
	}
	
	public static void addHistoricalVMCalculationJob(long meterId, long startTime, long endTime, int interval,boolean updateReading) throws Exception {
		
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
		long jobId= historyVMBuilder.insert(jobProp);
		FacilioTimer.scheduleOneTimeJob(jobId, "HistoricalVMCalculation", 30, "priority");
	}
	
	
	public static void addVMReadingsJob(long startTime, long endTime, int minutesInterval) throws Exception {
		addVMReadingsJob(DeviceAPI.getAllVirtualMeters(), startTime,endTime, minutesInterval);
	}
	
	public static void addVirtualMeterReadingsJob(long startTime, long endTime, int minutesInterval, List<Long> vmList) throws Exception {
		if(vmList == null || vmList.isEmpty()) {
			addVMReadingsJob(startTime,endTime, minutesInterval);
			return;
		}
		addVMReadingsJob(getVirtualMeters(vmList), startTime,endTime, minutesInterval);
	}
	
	
	private static void addVMReadingsJob(List<EnergyMeterContext> virtualMeters, long startTime, long endTime,
			int minutesInterval) throws Exception {

		for(EnergyMeterContext meter : virtualMeters) {

			addHistoricalVMCalculationJob(meter.getId(), startTime, endTime, minutesInterval,false);
		}
	}

	

	public static void insertVirtualMeterReadings(EnergyMeterContext meter, long startTime, long endTime, int minutesInterval,boolean updateReading) throws Exception {

		HashMap<Long,Long> intervalMap= DateTimeUtil.getTimeIntervals(startTime, endTime, minutesInterval);
		GenericSelectRecordBuilder childMeterBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVirtualMeterRelFields())
				.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
				.andCustomWhere("VIRTUAL_METER_ID = ?", meter.getId());
		List<Map<String, Object>> childProps = childMeterBuilder.get();
		if(childProps == null || childProps.isEmpty()) {
			return;
		}
		List<Long> childMeterIds = new ArrayList<>();
		for(Map<String, Object> childProp : childProps) {
			childMeterIds.add((Long) childProp.get("childMeterId"));
		}
		List<ReadingContext> completeReadings = getChildMeterReadings(childMeterIds, startTime, endTime, minutesInterval);
		if(completeReadings.isEmpty()) {
			return;
		}
		List<ReadingContext> vmReadings = new ArrayList<ReadingContext>();
		List<ReadingContext> intervalReadings=new ArrayList<ReadingContext>();
		for(Map.Entry<Long, Long> map:intervalMap.entrySet()) {
			long iStartTime=map.getKey();
			long iEndTime=map.getValue();
			for(ReadingContext reading:completeReadings) {

				double ttime = Math.floor(reading.getTtime()/1000); //Checking only in second level
				if(ttime >= Math.floor(iStartTime/1000) && ttime <= Math.floor(iEndTime/1000)) {
					intervalReadings.add(reading);
				}
				else {
					break;
				}
			}
			ReadingContext virtualMeterReading = calculateVMReading(meter,intervalReadings, childMeterIds);
			completeReadings.removeAll(intervalReadings);
			if(virtualMeterReading != null) {
				vmReadings.add(virtualMeterReading);
				intervalReadings=new ArrayList<ReadingContext>();
			}
		}

		if (!vmReadings.isEmpty()) {

			deleteEnergyData(meter.getId(), startTime, endTime); //Deleting anyway to avoid duplicate entries
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.ENERGY_DATA_READING );
			context.put(FacilioConstants.ContextNames.READINGS, vmReadings);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
			Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReading.execute(context);

			if(updateReading) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
				ReadingsAPI.updateLastReading(Collections.singletonList(deltaField), Collections.singletonList(vmReadings.get(vmReadings.size() - 1)), null);
			}
		}
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

		List<FacilioField> fields= new ArrayList<FacilioField>();

		fields.add(timeField);
		fields.add(meterField);
		fields.add(deltaField);

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
	
	
	private static ReadingContext calculateVMReading(EnergyMeterContext meter,List<ReadingContext> readings, List<Long> childIds) throws Exception {

		if(readings.isEmpty()) {
			return null;
		}

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
		for(Long childId : childIds) {
			if(!readingMap.containsKey(childId)) {
				return null;
			}
		}

		EnergyDataEvaluator evaluator = new EnergyDataEvaluator(readingMap);
		String expression = meter.getChildMeterExpression();
		ReadingContext virtualMeterReading = evaluator.evaluateExpression(expression);
		virtualMeterReading.setTtime(((Double)StatUtils.mean(timestamps.stream().mapToDouble(Long::doubleValue).toArray())).longValue());
		virtualMeterReading.setParentId(meter.getId());
		return virtualMeterReading;
	}
	
    private static class EnergyDataEvaluator extends ExpressionEvaluator<ReadingContext> {

		private Map<Long, List<ReadingContext>> readingMap;
		public EnergyDataEvaluator(Map<Long, List<ReadingContext>> readingMap) {
			// TODO Auto-generated constructor stub
			super.setRegEx(EnergyMeterContext.EXP_FORMAT);
			this.readingMap = readingMap;
		}
		
		@Override
		public ReadingContext getOperand(String operand) {
			// TODO Auto-generated method stub
			List<ReadingContext> readings = readingMap.get(Long.parseLong(operand));
			if(readings.size() == 1) {
				return readings.get(0);
			}
			ReadingContext aggregatedReading = new ReadingContext();
			
			List<Double> totalConsumptions = new ArrayList<Double>();
			for(ReadingContext reading : readings) {
				totalConsumptions.add((Double) reading.getReading("totalEnergyConsumptionDelta"));
			}
			aggregatedReading.addReading("totalEnergyConsumptionDelta", StatUtils.sum(totalConsumptions.stream().mapToDouble(Double::doubleValue).toArray()));
			return aggregatedReading;
		}

		@Override
		public ReadingContext applyOp(String operator, ReadingContext rightOperand, ReadingContext leftOperand) {
			// TODO Auto-generated method stub
			if(operator.equals("+")) {
				ReadingContext reading = new ReadingContext();
				reading.addReading("totalEnergyConsumptionDelta", ((Double)leftOperand.getReading("totalEnergyConsumptionDelta") + (Double)rightOperand.getReading("totalEnergyConsumptionDelta")));
				return reading;
			}
			else if(operator.equals("-")) {
				ReadingContext reading = new ReadingContext();
				reading.addReading("totalEnergyConsumptionDelta", ((Double)leftOperand.getReading("totalEnergyConsumptionDelta") - (Double)rightOperand.getReading("totalEnergyConsumptionDelta")));
				return reading;
			}
			return null;
		}
		
	}

}
