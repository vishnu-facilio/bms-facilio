package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class PrepareEnergyStarMeterDataRDMEntry extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		module.setFields(modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME));
		
		List<EnergyStarMeterContext> meters = (List<EnergyStarMeterContext>) context.get(EnergyStarUtil.ENERGY_STAR_METER_CONTEXTS);
		context.put(FacilioConstants.ContextNames.MODULE_LIST,Collections.singletonList(module));
		
		List<Long> energyStarMeterId = new ArrayList<>();
		
		for(EnergyStarMeterContext meter : meters) {
			
			energyStarMeterId.add(meter.getId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,energyStarMeterId);
		
		return false;
	}

}
