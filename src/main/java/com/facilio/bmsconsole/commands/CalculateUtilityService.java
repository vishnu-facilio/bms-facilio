package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.tenant.*;
import com.facilio.bmsconsole.util.TenantsAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			List<Map<String, Object>> itemDetails = new ArrayList<>();
			
			double utilitySumValue = 0.0;
			for(RateCardServiceContext utilityService : utilityServices) {
				
				List<UtilityAsset> utilityAssets = tenant.getUtilityAssetsOfUtility(utilityService.getUtility());
				
				if (utilityAssets == null) {
					continue;
				}
				
				for(UtilityAsset utilityAsset :utilityAssets) {
					
					Map<String, Object> item = new HashMap<>();
					Double consumption = 0.0;
					Double itemCost = 0.0;
					
					switch(FacilioUtility.valueOf(utilityAsset.getUtility())) {
					
					case ENERGY:
						
						Double value = 0.0;
						if(utilityVsValue.containsKey(utilityService.getId())) {
							value = utilityVsValue.get(utilityService.getId());
						}
						
						long assetId = utilityAsset.getAssetId();
						List<Map<String, Object>> props = ReportsUtil.fetchMeterData(assetId+"", startTime, endTime);
						LOGGER.log(Level.SEVERE, "props --- "+props);
						if(props != null && !props.isEmpty()) {
							consumption = (Double) props.get(0).get("CONSUMPTION");
							itemCost = consumption * utilityService.getPrice();
							value = value + itemCost;
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
					item.put("name", utilityService.getName());
					item.put("consumption", consumption);
					item.put("costPerUnit", utilityService.getPrice());
					item.put("cost", itemCost);
					item.put("utilityId", utilityAsset.getUtility());
					itemDetails.add(item);
				}
			}
			LOGGER.log(Level.SEVERE, "utilityVsValue --- "+utilityVsValue);
			for(Long key : utilityVsValue.keySet()) {
				utilitySumValue = utilitySumValue + utilityVsValue.get(key);
			}
			
			LOGGER.log(Level.SEVERE, "utilitySumValue --- "+utilitySumValue);
			
			context.put(TenantsAPI.UTILITY_VALUES, itemDetails);
			context.put(TenantsAPI.UTILITY_SUM_VALUE, utilitySumValue);
			
		}
		
		return false;
	}

}
