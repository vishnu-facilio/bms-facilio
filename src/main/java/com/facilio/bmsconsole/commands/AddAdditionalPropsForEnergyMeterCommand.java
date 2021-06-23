package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;

public class AddAdditionalPropsForEnergyMeterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyMeterContext energyMeter = (EnergyMeterContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(energyMeter != null) {
			AssetCategoryContext category = AssetsAPI.getCategory("Energy Meter");
			energyMeter.setCategory(category);
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
			if (energyMeter.isVirtual() && (energyMeter.getChildMeterExpression() == null || energyMeter.getChildMeterExpression().isEmpty())) {
				throw new IllegalArgumentException("Child meter expression cannot be empty for virtual meters");
			}
		}
		else {
			throw new IllegalArgumentException("Record cannot be null during addition");
		}
		return false;
	}

}
