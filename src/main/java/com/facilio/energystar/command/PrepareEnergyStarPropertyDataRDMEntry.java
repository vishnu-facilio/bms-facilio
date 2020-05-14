package com.facilio.energystar.command;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class PrepareEnergyStarPropertyDataRDMEntry extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
		
		module.setFields(modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME));
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		context.put(FacilioConstants.ContextNames.MODULE_LIST,Collections.singletonList(module));
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(propContext.getId()));
		
		return false;
	}

}
