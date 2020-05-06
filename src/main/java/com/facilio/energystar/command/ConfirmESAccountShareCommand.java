package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class ConfirmESAccountShareCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer.getType() == EnergyStarCustomerContext.Type.SHARED.getIntVal() && customer.getShareKey() != null && customer.getShareStatus() == EnergyStarCustomerContext.Share_Status.CREATED.getIntVal()) {
			
			String id = EnergyStarSDK.confirmAccountShare(customer.getShareKey());
			
			customer.setEnergyStarCustomerId(id);
			
			customer.setShareStatus(EnergyStarCustomerContext.Share_Status.SHARED.getIntVal());
			
			Criteria criteria = new Criteria();
			
			 criteria.addAndCondition(CriteriaAPI.getIdCondition(customer.getId(), ModuleFactory.getEnergyStarCustomerModule()));
			
			EnergyStarUtil.updateEnergyStarRelModule(ModuleFactory.getEnergyStarCustomerModule(), FieldFactory.getEnergyStarCustomerFields(), customer, criteria);
		}
		
		return false;
	}

}
