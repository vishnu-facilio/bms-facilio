package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflow.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum FacilioDefaultFunction implements FacilioWorkflowFunctionInterface {

	ALL_MATCH(1,"allMatch") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			
			List<Object> list = (List<Object>) objects[0];
			boolean allEqual = list.isEmpty() || list.stream().allMatch(list.get(0)::equals);

			System.out.println("list --  "+list+"  allEqual --- "+allEqual);
			return allEqual;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
//			else if(!(objects[0] instanceof List)) {
//				throw new FunctionParamException("Required Object is not of type List");
//			}
		}
	},
	GET_MAIN_ENERGY_METER(2,"getMainEnergyMeter") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long spaceId = (Long) objects[0];
			List<EnergyMeterContext> energyMeterContexts = DashboardUtil.getMainEnergyMeter(spaceId+"");
			
			if(energyMeterContexts.size() < 0 || energyMeterContexts.get(0) == null) {
				return null;
			}

			System.out.println("spaceId --  "+spaceId+"  allEqual --- "+energyMeterContexts.get(0).getId());
			
			return energyMeterContexts.get(0).getId();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
			if(objects[0] == null) {
				throw new FunctionParamException("Required Object is null");
			}
			else if(!(objects[0] instanceof Long)) {
				throw new FunctionParamException("Required Object is not of type List");
			}
		}
	},
	STRING_EQUALS(3,"stringEquals") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().equals(objects[1].toString()));
		}
		
	},
	STRING_CONTAINS(4,"stringContains") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().contains(objects[1].toString()));
		}
	},
	CONVERT_UNIT(5,"convertUnit") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 3) {
				return null;
			}
			Double value = null;
			Integer fromUnit = null;
			Integer toUnit = null;
			if(objects[0] != null) {
				value = Double.parseDouble(objects[0].toString());
			}
			if(objects[1] != null) {
				fromUnit = Integer.parseInt(objects[1].toString());
			}
			if(objects[2] != null) {
				toUnit = Integer.parseInt(objects[2].toString());
			}
			LOGGER.log(Level.SEVERE, "value -- "+value);
			LOGGER.log(Level.SEVERE, "fromUnit -- "+fromUnit);
			LOGGER.log(Level.SEVERE, "toUnit -- "+toUnit);
			if(value != null && fromUnit != null && toUnit != null) {
				return UnitsUtil.convert(value, Unit.valueOf(fromUnit), Unit.valueOf(toUnit));
			}
			return null;
		}
	},
	
	FATCH_DATA(6,"fetchData") {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = (String) objects[0];
			String criteriaString = (String) objects[1];
			Criteria criteria = WorkflowUtil.parseCriteriaString(moduleName, criteriaString);
			String fieldName = null, aggregateCondition = null;
			
			if(objects[2] != null) {
				fieldName = (String) objects[2];
			}
			if(objects[3] != null) {
				aggregateCondition = (String) objects[3];
			}
			
			FacilioModule module = modBean.getModule(moduleName);
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>();
			builder.module(module);
			builder.andCriteria(criteria);
			
			if(fieldName != null) {
				
				if(aggregateCondition != null) {
					
				}
			}
			return null;
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "default";
	private String params;
	
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
	FacilioDefaultFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static FacilioDefaultFunction getFacilioDefaultFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioDefaultFunction> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioDefaultFunction> initTypeMap() {
		Map<String, FacilioDefaultFunction> typeMap = new HashMap<>();
		for(FacilioDefaultFunction type : FacilioDefaultFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
