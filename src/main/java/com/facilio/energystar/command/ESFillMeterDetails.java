package com.facilio.energystar.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ESFillMeterDetails extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarMeterDataContext> meterDatas = (List<EnergyStarMeterDataContext>)context.get(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS);
		
		Map<Long,EnergyStarMeterContext> meterMap = new HashMap<Long, EnergyStarMeterContext>();
		for(EnergyStarMeterDataContext meterData : meterDatas) {
			
			if(!meterMap.containsKey(meterData.getParentId())) {
				Map<String, Object> prop = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(), null, CriteriaAPI.getIdCondition(meterData.getParentId(), ModuleFactory.getEnergyStarMeterModule())).get(0);
				
				EnergyStarMeterContext meter = FieldUtil.getAsBeanFromMap(prop, EnergyStarMeterContext.class);
				
				meterMap.put(meter.getId(), meter);
			}
			
			meterData.setParent(meterMap.get(meterData.getParentId()));
		}
		
		return false;
	}

}
