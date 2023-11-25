package com.facilio.apiv3;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;

public class FetchNameSpaceAndFieldForVMReadingsCommand extends FacilioCommand {
   
	@Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VirtualMeterTemplateReadingContext> vmTemplateReadings = Constants.getRecordList((FacilioContext) context);
		
		NamespaceBean nsBean = (NamespaceBean) BeanFactory.lookup("NamespaceBean");
		
		if (CollectionUtils.isNotEmpty(vmTemplateReadings)) {
			
        	for(VirtualMeterTemplateReadingContext vmTemplateReading : vmTemplateReadings) {
        		
        		NameSpaceContext nameSpace = new NameSpaceContext(nsBean.getNamespaceForParent(vmTemplateReading.getId(), NSType.VIRTUAL_METER));
        		vmTemplateReading.setNs(nameSpace);
        		
        		VirtualMeterTemplateContext vmTemplate = (VirtualMeterTemplateContext)V3Util.getRecord(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE, vmTemplateReading.getVirtualMeterTemplate().getId(), null);
        		
        		V3Context category = CommonConnectedUtil.getCategory(ResourceType.METER_CATEGORY, vmTemplate.getUtilityType().getId());
		        if (category != null) {
		        	vmTemplateReading.setCategory(new ResourceCategory<>(ResourceType.METER_CATEGORY, category));
		        }
        		
        		vmTemplateReading.setVirtualMeterTemplate(vmTemplate);
        	}
			
		}
		
		return false;
	}

}
