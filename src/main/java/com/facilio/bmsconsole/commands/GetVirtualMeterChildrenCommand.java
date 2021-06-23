package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;

public class GetVirtualMeterChildrenCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(id != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<EnergyMeterContext> selectRecordsBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																				.select(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER))
																				.beanClass(EnergyMeterContext.class)
																				.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
																				.innerJoin("Virtual_Energy_Meter_Rel")
																				.on("Energy_Meter.ID = Virtual_Energy_Meter_Rel.CHILD_METER_ID")
																				.andCustomWhere("VIRTUAL_METER_ID = ?", id);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, selectRecordsBuilder.get());
		}
																			
		return false;
	}

}
