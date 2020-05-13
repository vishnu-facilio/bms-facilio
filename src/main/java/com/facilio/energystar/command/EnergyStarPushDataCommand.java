package com.facilio.energystar.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarPushDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarMeterDataContext> meterDatas = (List<EnergyStarMeterDataContext>)context.get(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS);
		
		Map<Long,EnergyStarMeterContext> meterMap = (Map<Long,EnergyStarMeterContext>)context.get(EnergyStarUtil.ENERGY_STAR_METER_VS_ID_MAP);
		Map<Long,List<EnergyStarMeterDataContext>> meterDataMap = (Map<Long,List<EnergyStarMeterDataContext>>) context.get(EnergyStarUtil.ENERGY_STAR_METER_VS_METER_DATA_MAP);
		
		for(Long meterId :meterDataMap.keySet()) {
			
			EnergyStarMeterContext esMeter = meterMap.get(meterId);
			EnergyStarSDK.addConsumptionData(esMeter, meterDataMap.get(meterId), false);
			
		}
		
		return false;
	}

}
