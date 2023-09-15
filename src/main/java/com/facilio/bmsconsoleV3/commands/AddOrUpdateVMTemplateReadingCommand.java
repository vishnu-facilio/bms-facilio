package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class AddOrUpdateVMTemplateReadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VirtualMeterTemplateContext> virtualMeterTemplateContexts = Constants.getRecordList((FacilioContext) context);
		
		List<VirtualMeterTemplateReadingContext> addReadings = new ArrayList<>();
		
		if (CollectionUtils.isNotEmpty(virtualMeterTemplateContexts)) {
			
			for (VirtualMeterTemplateContext virtualMeterTemplateContext : virtualMeterTemplateContexts) {
				
				if (CollectionUtils.isNotEmpty(virtualMeterTemplateContext.getReadings())) {
					
					for(VirtualMeterTemplateReadingContext reading : virtualMeterTemplateContext.getReadings()) {

						if(reading.getId() != -1) {
							Long readingId = reading.getId();
							V3Util.updateBulkRecords(Constants.getModBean().getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING).getName(), FacilioUtil.getAsMap(FieldUtil.getAsJSON(reading)), Collections.singletonList(readingId),false);
						}
						else {
							reading.setStatus(Boolean.TRUE);
							VirtualMeterTemplateContext vmTemplate = new VirtualMeterTemplateContext();
							vmTemplate.setId(virtualMeterTemplateContext.getId());
							vmTemplate.setUtilityType(virtualMeterTemplateContext.getUtilityType());
							reading.setVirtualMeterTemplate(vmTemplate);
							addReadings.add(reading);
						}
					}
				}
			}
			
			V3Util.createRecordList(Constants.getModBean().getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING), FieldUtil.getAsMapList(addReadings, VirtualMeterTemplateReadingContext.class, false),null,null);
			
		}
		
		return false;
	}

}
