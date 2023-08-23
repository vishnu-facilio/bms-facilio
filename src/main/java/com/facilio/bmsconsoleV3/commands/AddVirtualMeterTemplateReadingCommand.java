package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class AddVirtualMeterTemplateReadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VirtualMeterTemplateContext> virtualMeterTemplateContexts = Constants.getRecordList((FacilioContext) context);
		
		List<VirtualMeterTemplateReadingContext> allReadings = new ArrayList<>();
		
		if (CollectionUtils.isNotEmpty(virtualMeterTemplateContexts)) {
			
			for (VirtualMeterTemplateContext virtualMeterTemplateContext : virtualMeterTemplateContexts) {
				
				if (CollectionUtils.isNotEmpty(virtualMeterTemplateContext.getReadings())) {
					
					for(VirtualMeterTemplateReadingContext reading : virtualMeterTemplateContext.getReadings()) {
						
						VirtualMeterTemplateContext vmTemplate = new VirtualMeterTemplateContext();
						vmTemplate.setId(virtualMeterTemplateContext.getId());
						vmTemplate.setUtilityType(virtualMeterTemplateContext.getUtilityType());
						reading.setVirtualMeterTemplate(vmTemplate);
						
						allReadings.add(reading);
					}
				}
			}
			
			V3Util.createRecordList(Constants.getModBean().getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING), FieldUtil.getAsMapList(allReadings, VirtualMeterTemplateReadingContext.class, false),null,null);
			
		}
		
		return false;
	}

}
