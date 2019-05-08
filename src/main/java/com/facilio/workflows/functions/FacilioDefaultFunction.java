package com.facilio.workflows.functions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.criteria.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.cards.util.CardUtil;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.*;
import java.util.logging.Level;

public enum FacilioDefaultFunction implements FacilioWorkflowFunctionInterface {

	ALL_MATCH(1,"allMatch",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.LIST.getValue(),"list") ) {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			
			List<Object> list = (List<Object>) objects[0];
			boolean allEqual = list.isEmpty() || list.stream().allMatch(list.get(0)::equals);

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
	GET_MAIN_ENERGY_METER(2,"getMainEnergyMeter",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"spaceId")) {
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
	STRING_EQUALS(3,"stringEquals",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"string1"), WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"string2")) {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().equals(objects[1].toString()));
		}
		
	},
	STRING_CONTAINS(4,"stringContains",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"string1"), WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"string2")) {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().contains(objects[1].toString()));
		}
	},
	CONVERT_UNIT(5,"convertUnit",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.DECIMAL.getValue(),"value"), WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"fromUnit"), WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"toUnit")) {

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
	GET_FILE_PRIVATE_URL(6,"getFilePrivateUrl",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.DECIMAL.getValue(),"fileId")) {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			
			Long fileId = null;
			if(objects[0] != null) {
				fileId = Long.parseLong(objects[0].toString());
			}
			
			return fs.getPrivateUrl(fileId);
		}
	},
	
	FETCH_DATA(6,"fetchData") {
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
	
	GET_UNIT(6,"getUnit") {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 1) {
				return false;
			}
			if(objects[0] == null) {
				return null;
			}
			
			String fieldRefObject = objects[0].toString();
			String moduleRefObject = null;
			
			if(objects.length > 1 && objects[1] != null) {
				moduleRefObject = objects[1].toString();
			}
			
			int metric = -1;
			if(FacilioUtil.isNumeric(fieldRefObject)) {
				Long fieldId = Long.parseLong(fieldRefObject);
				metric = CardUtil.getMetic(fieldId);
			}
			else if(moduleRefObject != null) {
				metric = CardUtil.getMetic(moduleRefObject, fieldRefObject);
			}
			if(metric > 0) {
				return UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), metric);
			}
			return null;
		}
	},
	
	SUM_DATA(7,"sumData") {
		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 2) {
				return false;
			}
			int counter = 0;
			FacilioModule module = null;
			FacilioField field = null;
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			String arg1 = objects[counter++].toString();
			
			if(FacilioUtil.isNumeric(arg1)) {
				field = modBean.getField(Long.parseLong(arg1));
				module = field.getModule();
			}
			else {
				String moduleName =  arg1;
				String fieldName = (String) objects[counter++];
				
				module = modBean.getModule(moduleName);
				field = modBean.getField(fieldName, moduleName);
			}
			
			String startTime = null,endtime = null;
			
			if(counter < objects.length && objects[counter] != null) {
				startTime = objects[counter++].toString();
			}
			if(counter < objects.length && objects[counter] != null) {
				endtime = objects[counter++].toString();
			}
			String parentIds = null;			//comma seperated values
			if(counter < objects.length && objects[counter] != null) {
				parentIds = objects[counter++].toString();
			}
			FacilioField dateField = modBean.getField("ttime", module.getName());
			FacilioField parentField = modBean.getField("parentId", module.getName());
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
			builder.table(module.getTableName());
			
			if(field != null) {
				
				List<FacilioField> selectFields = new ArrayList<>();
				String selectFieldString = "sum("+field.getColumnName()+")";
				FacilioField selectField = null;
				if(field instanceof NumberField) {
					NumberField numberField =  (NumberField)field;
					NumberField selectFieldNumber = new NumberField();
					selectFieldNumber.setMetric(numberField.getMetric());
					selectField = selectFieldNumber;
				}
				else {
					selectField = new FacilioField();
				}
				selectField.setName(field.getName());
				selectField.setDisplayName(field.getDisplayName());
				selectField.setColumnName(selectFieldString);
				selectField.setFieldId(field.getFieldId());
				
				selectFields.add(selectField);
				builder.select(selectFields);
			}
			LOGGER.log(Level.SEVERE, "startTime -- "+startTime);
			LOGGER.log(Level.SEVERE, "endtime -- "+endtime);
			if(startTime != null && endtime != null) {
				Condition condition = CriteriaAPI.getCondition(dateField, startTime+","+endtime, DateOperators.BETWEEN);
				builder.andCondition(condition);
			}
			if(parentIds != null) {
				Condition condition = CriteriaAPI.getCondition(parentField, parentIds, StringOperators.IS);
				builder.andCondition(condition);
			}
			builder.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module));
			builder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleid", module.getModuleId()+"", NumberOperators.EQUALS));
			 List<Map<String, Object>> props = builder.get();
			 LOGGER.log(Level.SEVERE, "builder -- "+builder);
			 if(props != null && !props.isEmpty()) {
				 Object sum = props.get(0).get(field.getName());
				 LOGGER.log(Level.SEVERE, "res -- "+sum);
				 return Double.parseDouble(sum.toString());
			 }
			return null;
		}
	},
	PICKLIST(8, "picklist") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			String moduleName = objects[0].toString();
			if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				return LookupSpecialTypeUtil.getPickList(moduleName);
			}
			
			FacilioModule module = modBean.getModule(moduleName);
			FacilioField field = objects.length > 1 && objects[1] != null ? modBean.getField(objects[1].toString(), moduleName) : modBean.getPrimaryField(moduleName);
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.select(Collections.singletonList(field))
					.orderBy("ID");

			List<Map<String, Object>> records = builder.getAsProps();
			if(records != null && records.size() > 0) {
				Map<Long, String> pickList = new HashMap<>();
				for(Map<String, Object> record : records) {
					pickList.put((Long) record.get("id"), record.get(field.getName()).toString());
				}
				return pickList; 
			}
			
			return null;
		}
		
	},
	GET_PERMALINK_URL(9, "getPermaLinkUrl") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}
			
			User user = AccountUtil.getUserBean().getUser(AccountUtil.getCurrentOrg().getId(), (String) objects[4]);
			String token = AccountUtil.getUserBean().generatePermalinkForURL(objects[1].toString(), user);
			String permalLinkURL = objects[0].toString()+objects[1].toString()+"?token="+token+"&startDate="+Long.valueOf(objects[2].toString())+"&endDate="+Long.valueOf(objects[3].toString());
			
			return permalLinkURL;
		}
		
	},

	;
	private Integer value;
	private String functionName;
	private String namespace = "default";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.DEFAULT;
	
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
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioDefaultFunction(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioDefaultFunction> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
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
