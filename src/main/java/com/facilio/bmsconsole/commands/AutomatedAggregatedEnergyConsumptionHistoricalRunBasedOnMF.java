package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF extends FacilioCommand{

	private static final Logger LOGGER = LogManager.getLogger(AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

			if(moduleName == null || !moduleName.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
				return false;
			}
			
			if (recordMap != null && MapUtils.isNotEmpty(recordMap))
			{
				for (Entry<String, List> entry : recordMap.entrySet()) 
				{
					if(entry.getKey() == null || !entry.getKey().equals(FacilioConstants.ContextNames.ENERGY_METER)) {
						continue;
					}
					List<AssetContext> assetList = entry.getValue();
					
					if(assetList != null && !assetList.isEmpty()) 
					{	
						AssetCategoryContext energyMeterCategoryContext = AssetsAPI.getCategory("Energy Meter");
						if(energyMeterCategoryContext != null && energyMeterCategoryContext.getId() != -1) 
						{
							for(AssetContext asset:assetList) 
							{
								boolean isEnergyMeterCategory = (asset != null && asset.getCategory() != null && asset.getCategory().getId() != -1l && asset.getCategory().getId() == energyMeterCategoryContext.getId());
								EnergyMeterContext energyMeterContext = DeviceAPI.getEnergyMeter(asset.getId());
								boolean isEnergyMeterAtUpdate = (energyMeterContext != null) ? true : false;

								if(isEnergyMeterCategory || isEnergyMeterAtUpdate) 
								{
									EnergyMeterContext energyMeter = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(asset), EnergyMeterContext.class);
									if(energyMeter != null && !energyMeter.isConnected()) 
									{
										if(energyMeter.getMultiplicationFactor() == -99l || energyMeter.getMultiplicationFactor() == -1) {									
											asset.setDatum("multiplicationFactor", 1l);;
											energyMeter.setMultiplicationFactor(1l); 
										}
										if(isEnergyMeterAtUpdate && energyMeterContext.getMultiplicationFactor() != -1 &&  energyMeterContext.getMultiplicationFactor() == energyMeter.getMultiplicationFactor()) {
											continue;
										}
										AggregatedEnergyConsumptionUtil.calculateHistoryForAggregatedEnergyConsumption(-1l, -1l, Collections.singletonList(energyMeter.getId()), Collections.singletonList(energyMeter));
									}
								}			
							}
						}	
					}		
				}
			}
		
		}
		catch(Exception e) {
			LOGGER.log(Level.ERROR,"AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF Error -- "+e, e);
//			LOGGER.log(Level.ERROR, "AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF Error -- " +e+ " Context -- " +context, e);
		}
		return false;
	}
}
