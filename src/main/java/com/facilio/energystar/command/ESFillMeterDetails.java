package com.facilio.energystar.command;

import java.util.ArrayList;
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
		
		Map<Long,List<EnergyStarMeterDataContext>> meterDataMap = new HashMap<>(); 
		
		for(EnergyStarMeterDataContext meterData :meterDatas) {
			
			Map<String, Object> prop = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(), null, CriteriaAPI.getIdCondition(meterData.getParentId(), ModuleFactory.getEnergyStarMeterModule())).get(0);
			
			EnergyStarMeterContext meter = FieldUtil.getAsBeanFromMap(prop, EnergyStarMeterContext.class);
			
			meterMap.put(meter.getId(), meter);
			
			meterData.setParent(meter);
			
			List<EnergyStarMeterDataContext> data = meterDataMap.get(meterData.getParentId()) == null ? new ArrayList<>() : meterDataMap.get(meterData.getParentId());
			
			data.add(meterData);
			
			meterDataMap.put(meterData.getParentId(), data);
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_METER_VS_ID_MAP, meterMap);
		context.put(EnergyStarUtil.ENERGY_STAR_METER_VS_METER_DATA_MAP, meterDataMap);
		
		return false;
	}

}
