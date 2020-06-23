package com.facilio.energystar.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;

public class UpdateEnergyStarMeterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		if(propContext.getMeterContexts() != null && !propContext.getMeterContexts().isEmpty()) {
			
			
			for(EnergyStarMeterContext meter : propContext.getMeterContexts()) {
				
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getIdCondition(meter.getId(), ModuleFactory.getEnergyStarMeterModule()));
				
				EnergyStarUtil.updateEnergyStarRelModule(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(), meter,criteria);
			}
			
		}
		
		return false;
	}

}
