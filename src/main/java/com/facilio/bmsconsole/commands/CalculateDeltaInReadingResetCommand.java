package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class CalculateDeltaInReadingResetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(ResetCounterMetaContext resetCounter:resetCounterMetaList){
			
			if(resetCounter.getEndvalue() != null && !resetCounter.getEndvalue().equals("")){
				FacilioField field = modBean.getField(resetCounter.getFieldId());
				String moduleName = field.getModule().getName();
				FacilioModule module = modBean.getModule(moduleName);
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				ReadingContext reading = ReadingsAPI.getReading(module, fields, resetCounter.getReadingDataId());
				
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				Condition condition = CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(reading.getTtime()), NumberOperators.LESS_THAN);
				String orderBy = fieldMap.get("ttime").getColumnName() + " desc";
				ReadingContext prevReading = ReadingsAPI.getSingleReading(module, fields, reading, field.getName(), condition, orderBy);
			    
				Object prevReadingValue = getReadingValueByFieldName(prevReading,field.getName());
			    Object curReadingValue = getReadingValueByFieldName(reading,field.getName());
				
				if(prevReadingValue != null && curReadingValue != null){
					Object deltaValue;
					if(field.getDataTypeEnum().equals(FieldType.DECIMAL)){
						deltaValue = (Double.parseDouble(resetCounter.getEndvalue()) - (Double) prevReadingValue) + (Double) curReadingValue;
						if (resetCounter.getStartvalue() != null && !resetCounter.getStartvalue().equals("")) {
							deltaValue = (Double) deltaValue - Double.parseDouble(resetCounter.getStartvalue());
						}
					}else{
						deltaValue = (Long.parseLong(resetCounter.getEndvalue()) - (Long) prevReadingValue) + (Long) curReadingValue;
						if (resetCounter.getStartvalue() != null && !resetCounter.getStartvalue().equals("")) {
							deltaValue = (Long) deltaValue - Long.parseLong(resetCounter.getStartvalue());
						}
					}
					ReadingsAPI.addDeltaValue(reading, field.getName(),deltaValue);
					ReadingsAPI.updateReading(module, fields, reading);
				}
			}
		}
		return false;
	}
	
	public Object getReadingValueByFieldName(ReadingContext reading,String fieldName){
		for(Entry<String, Object> en : reading.getReadings().entrySet()){
	    	if(en.getKey().equalsIgnoreCase(fieldName)){
	    		return en.getValue();
	    	}
	    }
		return null;
	}

}
