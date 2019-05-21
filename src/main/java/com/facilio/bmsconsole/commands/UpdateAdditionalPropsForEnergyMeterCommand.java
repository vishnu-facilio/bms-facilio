package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class UpdateAdditionalPropsForEnergyMeterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyMeterContext energyMeter = (EnergyMeterContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(energyMeter != null && energyMeter.getId() != -1) {
			energyMeter.setCategory(null); //Since we set it to Energy Meter during addition, we aren't gonna update category any day
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(energyMeter.getId()));
			
			if (energyMeter.isVirtual() && (energyMeter.getChildMeterExpression() == null || energyMeter.getChildMeterExpression().isEmpty())) {
				throw new IllegalArgumentException("Child meter expression cannot be empty for virtual meters");
			}
			
			if (energyMeter.getChildMeterExpression() != null) {
				//Deleting old rows in rel table
				
				FacilioModule module = ModuleFactory.getVirtualMeterRelModule();
				FacilioField vmField = FieldFactory.getAsMap(FieldFactory.getVirtualMeterRelFields()).get("virtualMeterId");
				GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
																		.table(module.getTableName())
																		.andCondition(CriteriaAPI.getCondition(vmField, String.valueOf(energyMeter.getId()), NumberOperators.EQUALS));
				
				deleteRecordBuilder.delete();
			}
		}
		else {
			throw new IllegalArgumentException("Record cannot be null during updation");
		}
		return false;
	}

}
