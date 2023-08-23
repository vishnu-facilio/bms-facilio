package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class FillDefaultValuesAndValidateForVMTemplates extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VirtualMeterTemplateContext> virtualMeterTemplateContexts = Constants.getRecordList((FacilioContext) context);

		if (CollectionUtils.isNotEmpty(virtualMeterTemplateContexts)) {
			
			for (VirtualMeterTemplateContext virtualMeterTemplateContext : virtualMeterTemplateContexts) {
				
				virtualMeterTemplateContext.setStatus(Boolean.TRUE);
				if(virtualMeterTemplateContext.getVmTemplateStatusEnum() == null) {
					virtualMeterTemplateContext.setVmTemplateStatusEnum(VirtualMeterTemplateContext.VMTemplateStatus.UN_PUBLISHED);
				}
				if (CollectionUtils.isNotEmpty(virtualMeterTemplateContext.getReadings())) {

					for(VirtualMeterTemplateReadingContext reading : virtualMeterTemplateContext.getReadings()) {
						reading.setStatus(Boolean.TRUE);
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return false;
	}

}
