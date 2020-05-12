package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarPushDataForNonConnectedCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarMeterDataContext> meterDatas = (List<EnergyStarMeterDataContext>)context.get(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS);
		
//		Map<Long,List<EnergyStarMeterDataContext>> meterDataMap = new HashMap<>(); 
//		
//		for(EnergyStarMeterDataContext meterData :meterDatas) {
//			List<EnergyStarMeterDataContext> data = meterDataMap.get(meterData.getParentId()) == null ? new ArrayList<>() : meterDataMap.get(meterData.getParentId());
//			
//			data.add(meterData);
//			
//			meterDataMap.put(meterData.getParentId(), data);
//		}
		
		for(EnergyStarMeterDataContext meterData :meterDatas) {
			
			EnergyStarMeterContext esMeter = (EnergyStarMeterContext)meterData.getParent();
			
			
		}
		
		return false;
	}

}
