package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.ModuleFactory;

public class DeleteEnergyStarPropertyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		
		//delete prop here.
		
		Criteria criteria = new Criteria();
		
		 criteria.addAndCondition(CriteriaAPI.getIdCondition(propContext.getId(), ModuleFactory.getEnergyStarPropertyModule()));
		
		EnergyStarUtil.deleteEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), criteria);
		return false;
	}

}
