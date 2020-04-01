package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public class DataUtil {
	
	
	public static Double getSumOfEnergyData(List<Long> energyMeterIds,long from,long to) throws Exception {
		
		if(energyMeterIds != null && !energyMeterIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			List<FacilioField> energyDataFields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(energyDataFields);
			
			FacilioField clonedEnergyField = fieldMap.get("totalEnergyConsumptionDelta").clone();
			
			clonedEnergyField.setName("result");
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(energyDataModule.getTableName())
					.module(energyDataModule)
					.aggregate(NumberAggregateOperator.SUM, clonedEnergyField)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(energyDataModule), String.valueOf(energyDataModule.getModuleId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), from+","+to, DateOperators.BETWEEN))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), energyMeterIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("marked"), Boolean.FALSE.toString(), BooleanOperators.IS))
					;
			
			List<Map<String, Object>> props = selectBuilder.getAsProps();
			
			if(props != null && !props.isEmpty()) {
				Double result = (Double) props.get(0).get("result");
				
//				result = FacilioUtil.decimalClientFormat(result);
				return result;
			}
		}
		return -1d;
	}

}
