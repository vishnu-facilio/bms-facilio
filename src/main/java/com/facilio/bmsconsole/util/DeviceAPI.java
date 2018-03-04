package com.facilio.bmsconsole.util;

import java.util.ArrayList;
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
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.util.ExpressionEvaluator;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());
	
	public static final int VM_HISTORICAL_DATA_CALCULATION_INTERVAL = -30;

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
	
	public static List<EnergyMeterContext> getVirtualMeters(String deviceList) throws Exception {
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
	
	public static void insertVirtualMeterReadings(long startTime, long endTime, int minutesInterval) throws Exception {
		insertVirtualMeterReadings(DeviceAPI.getAllVirtualMeters(), startTime,endTime, minutesInterval);
	}
	
	
    public static void insertVirtualMeterReadings(List<EnergyMeterContext> virtualMeters, long startTime, long endTime, int minutesInterval) throws Exception {
		HashMap<Long,Long> intervalMap= DateTimeUtil.getTimeIntervals(startTime, endTime, minutesInterval);
		
		for(EnergyMeterContext meter : virtualMeters) {
			GenericSelectRecordBuilder childMeterBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getVirtualMeterRelFields())
															.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
															.andCustomWhere("VIRTUAL_METER_ID = ?", meter.getId());
			List<Map<String, Object>> childProps = childMeterBuilder.get();
			if(childProps != null && !childProps.isEmpty()) {
				List<Long> childMeterIds = new ArrayList<>();
				for(Map<String, Object> childProp : childProps) {
					childMeterIds.add((Long) childProp.get("childMeterId"));
				}
				List<ReadingContext> readings = new ArrayList<>();
				for(Map.Entry<Long, Long> map:intervalMap.entrySet()) {
					ReadingContext virtualMeterReading = calculateVMReading(meter, childMeterIds, map.getKey(), map.getValue());
					if(virtualMeterReading != null) {
						readings.add(virtualMeterReading);
					}
				}
				
				if (!readings.isEmpty()) {
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.ENERGY_DATA_READING );
					context.put(FacilioConstants.ContextNames.READINGS, readings);
					context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
					Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
					addReading.execute(context);
				}
			}
		}
	}
	
	public static void insertVirtualMeterReadings(List<EnergyMeterContext> virtualMeters, long startTime, long endTime) throws Exception {
		for(EnergyMeterContext meter : virtualMeters) {
			try {
				
				GenericSelectRecordBuilder childMeterBuilder = new GenericSelectRecordBuilder()
																	.select(FieldFactory.getVirtualMeterRelFields())
																	.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
																	.andCustomWhere("VIRTUAL_METER_ID = ?", meter.getId());
				List<Map<String, Object>> childProps = childMeterBuilder.get();
				if(childProps != null && !childProps.isEmpty()) {
					List<Long> childMeterIds = new ArrayList<>();
					for(Map<String, Object> childProp : childProps) {
						childMeterIds.add((Long) childProp.get("childMeterId"));
					}
					ReadingContext virtualMeterReading = calculateVMReading(meter, childMeterIds, startTime, endTime);
					if(virtualMeterReading != null) {
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.ENERGY_DATA_READING );
						context.put(FacilioConstants.ContextNames.READING, virtualMeterReading);
						Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
						//not adding in bulk to maintain the added sequence, 
						//so that even a Virtual meter can be added in the virtual meter formula
						addReading.execute(context);
					}
				}
			}
			catch(Exception e) {
				logger.log(Level.WARNING, "Exception occurred during calculation of energy data for meter : "+meter.getId(), e);
			}
		}
	}
	
	public static ReadingContext calculateVMReading(EnergyMeterContext meter, List<Long> childIds, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ReadingContext> getReadings = new SelectRecordsBuilder<ReadingContext>()
																.beanClass(ReadingContext.class)
																.select(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING))
																.moduleName(FacilioConstants.ContextNames.ENERGY_DATA_READING)
																.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentId", StringUtils.join(childIds, ","), NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+", "+endTime, DateOperators.BETWEEN));
		
		List<ReadingContext> readings = getReadings.get();
		if(readings != null && !readings.isEmpty()) {
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
			
			EnergyDataEvaluator evluator = new EnergyDataEvaluator(readingMap);
			String expression = meter.getChildMeterExpression();
			ReadingContext virtualMeterReading = evluator.evaluateExpression(expression);
			virtualMeterReading.setTtime(((Double)StatUtils.mean(timestamps.stream().mapToDouble(Long::doubleValue).toArray())).longValue());
			virtualMeterReading.setParentId(meter.getId());
			return virtualMeterReading;
		}
		return null;
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
			aggregatedReading.addReading("totalEnergyConsumptionDelta", StatUtils.mean(totalConsumptions.stream().mapToDouble(Double::doubleValue).toArray()));
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
