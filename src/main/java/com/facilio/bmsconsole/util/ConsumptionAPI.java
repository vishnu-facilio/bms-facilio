package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ConsumptionAPI {

	
public static Map<String,Object> getTotalConsumptionByBuildings(Long startTime,Long endTime, String moduleName, String fieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    
		FacilioModule dataModule = modBean.getModule(moduleName);
		FacilioModule resourcesModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		
		
		List<FacilioField> baseSpaceFields = modBean.getAllFields(baseSpaceModule.getName());
		Map<String, FacilioField> baseSpaceFieldMap = FieldFactory.getAsMap(baseSpaceFields);

		List<FacilioField> resourceFields = modBean.getAllFields(resourcesModule.getName());
		List<FacilioField> moduleFields = modBean.getAllFields(dataModule.getName());
		Map<String, FacilioField> moduleFieldMap = FieldFactory.getAsMap(moduleFields);
		
		
		FacilioField consumptionField = moduleFieldMap.get(fieldName);
		String unit = null;
		
		if (consumptionField instanceof NumberField) {
			
			unit = ((NumberField) consumptionField).getUnit();
		}
		
		
		FacilioField parentField = moduleFieldMap.get("parentId");
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		FacilioField totalConsumptionField = FormulaContext.NumberAggregateOperator.SUM.getSelectField(consumptionField);
		totalConsumptionField.setName("total_consumption");
		fields.add(totalConsumptionField);
		
		FacilioField buildingField = baseSpaceFieldMap.get("buildingId");
		fields.add(buildingField);
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(dataModule.getTableName())
													.innerJoin(resourcesModule.getTableName()).on(parentField.getCompleteColumnName()+" = "+resourcesModule.getTableName()+".ID")
													.innerJoin(baseSpaceModule.getTableName()).on(resourcesModule.getTableName()+".SPACE_ID = "+baseSpaceModule.getTableName()+".ID")
													.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
													.andCondition(CriteriaAPI.getCondition(dataModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
													.groupBy(buildingField.getCompleteColumnName())
													;
				
		List<Map<String,Object>> totalConsumptionByBuilding = selectBuilder.get();
		
		List<Map<String,Object>> outputTotalConsumptionByBuilding = new ArrayList<Map<String,Object>>();
		
		 Map<Long, Object> buildingArray = getLookupFieldPrimary("building");

		for(int i=0;i<totalConsumptionByBuilding.size();i++)
		{
			Map<String,Object> consumptionObj = totalConsumptionByBuilding.get(i) ;
			Long buildingId = (Long)consumptionObj.get("buildingId");
			consumptionObj.put("buildingName",buildingArray.get(buildingId) );
			outputTotalConsumptionByBuilding.add(consumptionObj);
		}
		Map<String,Object> resp = new HashMap<String, Object>();
		
		resp.put("moduleName",dataModule.getDisplayName());
		resp.put("fieldName",consumptionField.getName());
		resp.put("unit",unit);
		resp.put("totalConsumptionData",outputTotalConsumptionByBuilding);
		

     
       return resp;
	}


public static Map<Long, Object> getLookupFieldPrimary(String moduleName) throws Exception {
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule module = modBean.getModule(moduleName);
	FacilioField mainField = modBean.getPrimaryField(moduleName);
	
	List<FacilioField> selectFields = new ArrayList<>();
	selectFields.add(mainField);
	selectFields.add(FieldFactory.getIdField(module));
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
			.select(selectFields)
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
	FacilioModule prevModule = module;
	while (prevModule.getExtendModule() != null) {
		builder.innerJoin(prevModule.getExtendModule().getTableName())
			.on(prevModule.getTableName()+".ID = " + prevModule.getExtendModule().getTableName()+ ".ID");
		prevModule = prevModule.getExtendModule();
	}

	List<Map<String,Object>> asProps = builder.get();
	Map lookupMap = new HashMap<>();
	for (Map<String, Object> map : asProps) {
		lookupMap.put((Long) map.get("id"), map.get(mainField.getName()));
	}
	return lookupMap;
}

}
