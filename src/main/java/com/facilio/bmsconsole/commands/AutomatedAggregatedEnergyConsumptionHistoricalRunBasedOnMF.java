package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMF extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
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
				List<EnergyMeterContext> energyMeterList = entry.getValue();
				
				if(energyMeterList != null && !energyMeterList.isEmpty()) 
				{	
					for(EnergyMeterContext energyMeter:energyMeterList) 
					{
						if(energyMeter != null && energyMeter.getMultiplicationFactor() != -1 && energyMeter.getMultiplicationFactor() != 1l) {
							AggregatedEnergyConsumptionUtil.calculateHistoryForAggregatedEnergyConsumption(-1l, -1l, Collections.singletonList(energyMeter.getId()));
						}
					}
				}		
			}
		}
		
		return false;
	}

}
