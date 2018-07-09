package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.RateCardServiceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantUtility;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.TenantsAPI;

public class CalculateUtilityService implements Command {

	private static final Logger LOGGER = Logger.getLogger(CalculateUtilityService.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {

		TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		RateCardContext rateCard = (RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT);
		
		Long startTime = (Long) context.get(TenantsAPI.START_TIME);
		Long endTime = (Long) context.get(TenantsAPI.END_TIME);
		
		List<RateCardServiceContext> utilityServices = rateCard.getServiceOfType(RateCardServiceContext.ServiceType.UTILITY.getValue());
		
		if(utilityServices != null && !utilityServices.isEmpty()) {
			
			Map<Long,Double> utilityVsValue = new HashMap<>();
			double utilitySumValue = 0.0;
			for(RateCardServiceContext utilityService : utilityServices) {
				
				List<UtilityAsset> utilityAssets = tenant.getUtilityAssetsOfUtility(utilityService.getUtility());
				
				TenantUtility.ENERGY.getValue();
				for(UtilityAsset utilityAsset :utilityAssets) {
					
					switch(TenantUtility.valueOf(utilityAsset.getUtility())) {
					
					case ENERGY:
						Double value = 0.0;
						
						if(utilityVsValue.containsKey(utilityService.getId())) {
							value = utilityVsValue.get(utilityService.getId());
						}
						
						long assetId = utilityAsset.getAssetId();
						List<Map<String, Object>> props = ReportsUtil.fetchMeterData(assetId+"", startTime, endTime);
						LOGGER.log(Level.SEVERE, "props --- "+props);
						if(props != null && !props.isEmpty()) {
							Double consumption = (Double) props.get(0).get("CONSUMPTION");
							consumption = consumption * utilityService.getPrice();
							value = value + consumption;
						}
						utilityVsValue.put(utilityService.getId(), value);
						break;
						
					case WATER:
						break;
						
					case NATURAL_GAS:
						break;
						
					case BTU:
						break;
						
					}
				}
			}
			LOGGER.log(Level.SEVERE, "utilityVsValue --- "+utilityVsValue);
			for(Long key : utilityVsValue.keySet()) {
				utilitySumValue = utilitySumValue + utilityVsValue.get(key);
			}
			
			LOGGER.log(Level.SEVERE, "utilitySumValue --- "+utilitySumValue);
			
			context.put(TenantsAPI.UTILITY_VALUES, utilityVsValue);
			context.put(TenantsAPI.UTILITY_SUM_VALUE, utilitySumValue);
			
		}
		
		return false;
	}

}
