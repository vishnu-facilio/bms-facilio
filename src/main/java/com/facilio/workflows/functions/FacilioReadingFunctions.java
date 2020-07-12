package com.facilio.workflows.functions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DataUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.WorkflowReadingContext;
import com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public enum FacilioReadingFunctions implements FacilioWorkflowFunctionInterface  {

	ENERGY_READINGS(1,"getEnergyReading") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Long baseSpaceId = Long.parseLong(objects[0].toString());
			int dateOperator = Integer.parseInt(objects[1].toString());
			
			String dateOperatorValue = null;
			if(objects.length > 2 && objects[2] != null) {
				dateOperatorValue = objects[2].toString();
			}
			
			
			DateOperators oper = (DateOperators) Operator.getOperator(dateOperator);
			BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(baseSpaceId);
			
			if(baseSpace != null && oper != null) {
				
				DateRange range = oper.getRange(dateOperatorValue);
				
				switch(baseSpace.getSpaceTypeEnum()) {
				
				case SITE:
					
					List<EnergyMeterContext> energyMeters = DashboardUtil.getMainEnergyMeter(baseSpaceId+"");
					if(energyMeters != null && !energyMeters.isEmpty()) {
						EnergyMeterContext meter = energyMeters.get(0);
						
						return DataUtil.getSumOfEnergyData(Collections.singletonList(meter.getId()),range.getStartTime(),range.getEndTime());
					}
					else {
						List<BuildingContext> buildings = SpaceAPI.getSiteBuildings(baseSpaceId);
						List<Long> buildingMeters = new ArrayList<>();
						for(BuildingContext building :buildings) {
							
							energyMeters = DashboardUtil.getMainEnergyMeter(building.getId()+"");
							if(energyMeters != null && !energyMeters.isEmpty()) {
								buildingMeters.add(energyMeters.get(0).getId());
							}
						}
						if (CollectionUtils.isNotEmpty(buildingMeters)) {
							return DataUtil.getSumOfEnergyData(buildingMeters, range.getStartTime(), range.getEndTime());
						}
					}
				case BUILDING:
				case FLOOR:
				case SPACE:
					
					energyMeters = DashboardUtil.getMainEnergyMeter(baseSpaceId+"");
					if(energyMeters != null && !energyMeters.isEmpty()) {
						EnergyMeterContext meter = energyMeters.get(0);
						
						return DataUtil.getSumOfEnergyData(Collections.singletonList(meter.getId()),range.getStartTime(),range.getEndTime());
					}
					break;
				}
			}
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET_LAST_VALUE(2,"getLastValue") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Criteria criteria = new Criteria();
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			FacilioModule module = field.getModule();
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
			
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), workflowReadingContext.getParentId()+"", NumberOperators.EQUALS));
			
			DBParamContext dbParamContext = new DBParamContext();
			dbParamContext.setFieldName(field.getName());
			dbParamContext.setAggregateString("lastValue");
			dbParamContext.setCriteria(criteria);
			
			List<Object> params = new ArrayList<>();
			params.add(field.getModule());
			params.add(dbParamContext);
			
			FacilioModuleFunctionImpl functions = new FacilioModuleFunctionImpl();
			
			return functions.fetch(globalParam,params);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET(3,"get") {															//change name
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			
			DBParamContext dbParamContext = null;
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			FacilioModule module = field.getModule();
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
			
			Condition parentIdCondition = CriteriaAPI.getCondition(fieldMap.get("parentId"), workflowReadingContext.getParentId()+"", NumberOperators.EQUALS);
			
			if(objects[1] instanceof Criteria) {
				Criteria criteria = (Criteria)objects[1];
				
				dbParamContext = new DBParamContext();

				dbParamContext.setCriteria(criteria);
			}
			else if(objects[1] instanceof DBParamContext) {
				
				dbParamContext =(DBParamContext) objects[1];
			}
			
			dbParamContext.getCriteria().addAndCondition(parentIdCondition);
			
			dbParamContext.setFieldName(field.getName());
			
			if(objects.length >2) {
				boolean skipUnitConversion = (boolean) objects[2];
				dbParamContext.setSkipUnitConversion(skipUnitConversion);
			}
			
			List<Object> params = new ArrayList<>();
			params.add(field.getModule());
			params.add(dbParamContext);
			
			FacilioModuleFunctionImpl functions = new FacilioModuleFunctionImpl();
			
			return functions.fetch(globalParam,params);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET_ENUM_MAP(4,"getEnumMap") {															//change name
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			Map<String, Object> enumMap = new HashMap<>();
			if (field instanceof BooleanField) {
				BooleanField boolField = (BooleanField) field;
				if (boolField.getTrueVal() != null && !boolField.getTrueVal().isEmpty()) {
					enumMap.put("1", boolField.getTrueVal());
					enumMap.put("0", boolField.getFalseVal());
				}
				else {
					enumMap.put("1", "True");
					enumMap.put("0", "False");
				}
			}
			else if (field instanceof EnumField) {
				Map<Integer, Object> enumMap1 = ((EnumField) field).getEnumMap();
				
				for(Integer key : enumMap1.keySet()) {
					enumMap.put(key+"", enumMap1.get(key));
				}
			}
			return enumMap;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET_RDM(5,"getRDM") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			return FieldUtil.getAsProperties(ReadingsAPI.getReadingDataMeta(workflowReadingContext.getParentId(), field));
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	ADD(5,"add") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			FacilioChain addCurrentReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			
			ReadingContext reading = new ReadingContext();
			
			reading.setParentId(workflowReadingContext.getParentId());
			
			Object readingValue = objects[1];
			
			reading.addReading(field.getName(), readingValue);
			
			long ttime = -1;
			if(objects.length >2) {
				ttime = (long) objects[2];
			}
			else {
				ttime = DateTimeUtil.getCurrenTime();
			}
			
			reading.setTtime(ttime);
			
			FacilioContext context = addCurrentReading.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
			context.put(FacilioConstants.ContextNames.READINGS, Collections.singletonList(reading));
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.SCRIPT);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			
			addCurrentReading.execute();
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	UPDATE(6,"update") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			
			Map<String,Object> readingProps = (Map<String,Object>)objects[1];
			
			if(readingProps.get("id") == null) {
				throw new Exception("cannot update without reading id");
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			ReadingContext reading = FieldUtil.getAsBeanFromMap(readingProps, ReadingContext.class);
			
			reading.setParentId(workflowReadingContext.getParentId());
			
			FacilioChain updateCurrentReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			
			FacilioContext context = updateCurrentReading.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
			context.put(FacilioConstants.ContextNames.READINGS, Collections.singletonList(reading));
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.SCRIPT);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			
			updateCurrentReading.execute();
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	CONVERT_TO_INPUT_UNIT_FROM_DISPLAY_UNIT(7,"convertToInputUnitFromDisplayUnit") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			WorkflowReadingContext workflowReadingContext = (WorkflowReadingContext)objects[0];
			
			Object readingVal = objects[1];
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(workflowReadingContext.getFieldId());
			
			if(field instanceof NumberField) {
				
				NumberField numberField = (NumberField) field;
				
				if(numberField.getMetric() > 0) {
					
					ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(workflowReadingContext.getParentId(), field);
					int inputUnit = rdm.getUnit();
					if(inputUnit < 0) {
						inputUnit = numberField.getMetricEnum().getSiUnitId();
					}
					int displayUnit = numberField.getUnitId();
					if(displayUnit < 0) {
						displayUnit = UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), numberField.getMetric()).getUnitId();
					}
					
					readingVal = UnitsUtil.convert(readingVal, displayUnit, inputUnit);
					
				}
			}
			
			return readingVal;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "readings";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.READINGS;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioReadingFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioReadingFunctions> getAllFunctions() {
		return READING_FUNCTIONS;
	}
	public static FacilioReadingFunctions getFacilioReadingFunctions(String functionName) {
		return READING_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioReadingFunctions> READING_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioReadingFunctions> initTypeMap() {
		Map<String, FacilioReadingFunctions> typeMap = new HashMap<>();
		for(FacilioReadingFunctions type : FacilioReadingFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
