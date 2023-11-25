package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.storm.InstructionType;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;

public class RunHistoryForVMTemplateReadings extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long vmTemplateId = (Long)context.get("vmTemplateId");
		
		Long startTime = (Long) context.get("startTime");
		Long endTime = (Long) context.get("endTime");
		
		List<Long> resourceIds = (List<Long>) context.get("resourceIds");
		
		VirtualMeterTemplateContext vmTemplate = (VirtualMeterTemplateContext) V3Util.getRecord(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE, vmTemplateId, null);
		
		V3Context category = CommonConnectedUtil.getCategory(ResourceType.METER_CATEGORY, vmTemplate.getUtilityType().getId());
		
		for(VirtualMeterTemplateReadingContext reading : vmTemplate.getReadings()) {
			if (category != null) {
	        	reading.setCategory(new ResourceCategory<>(ResourceType.METER_CATEGORY, category));
	        }
			CommonConnectedUtil.postConRuleHistoryInstructionToStorm(Collections.singletonList(reading), startTime, endTime, resourceIds, false, InstructionType.VM_HISTORICAL, false);
		}
		return false;
	}

}
