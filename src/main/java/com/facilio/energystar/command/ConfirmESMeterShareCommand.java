package com.facilio.energystar.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class ConfirmESMeterShareCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer.getType() == EnergyStarCustomerContext.Type.SHARED.getIntVal() && customer.getEnergyStarCustomerId() != null &&customer.getShareStatus() == EnergyStarCustomerContext.Share_Status.SHARED.getIntVal()) {
			
			List<EnergyStarMeterContext> meters = EnergyStarSDK.confirmMeterShare(customer.getEnergyStarCustomerId());
			
			for(EnergyStarMeterContext meter : meters) {
				
				if(meter.getEnergyStarPropertyId() != null) {
					
					List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(), null, CriteriaAPI.getCondition("ES_PROPERTY_ID", "energyStarPropertyId", meter.getEnergyStarPropertyId(), StringOperators.IS));
					
					if(props != null && !props.isEmpty()) {
						long propertyId = (long)props.get(0).get("id");
						meter.setPropertyId(propertyId);
					}
				}
				
			}
			
			FacilioChain chain = TransactionChainFactory.addEnergyStarMeterOnlyChain();
			
			FacilioContext newContext = chain.getContext();
			
			newContext.put(EnergyStarUtil.ENERGY_STAR_METER_CONTEXTS, meters);
			
			newContext.putAll(context);
			
			chain.execute();
		}
		return false;
	}

}
