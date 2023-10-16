package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSType;
import com.facilio.v3.context.Constants;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class PopulateNameSpaceAndFieldForVMReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	List<VirtualMeterTemplateReadingContext> vmTemplateReadings = Constants.getRecordList((FacilioContext) context);
		
		if (CollectionUtils.isNotEmpty(vmTemplateReadings)) {
			
        	for(VirtualMeterTemplateReadingContext vmTemplateReading : vmTemplateReadings) {
        		
        		vmTemplateReading.getNs().setParentRuleId(vmTemplateReading.getId());
        		vmTemplateReading.getNs().getWorkflowContext().setIsV2Script(true);
        		vmTemplateReading.getNs().setExecInterval(vmTemplateReading.getFrequencyEnum().getMs());
        		vmTemplateReading.getNs().setType(NSType.VIRTUAL_METER.getIndex());
				vmTemplateReading.getNs().setStatus(Boolean.TRUE);
        		
        		FacilioChain chain = TransactionChainFactoryV3.addNamespaceAndFieldsChain();
        		FacilioContext newContext = chain.getContext();
        		
        		newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT,vmTemplateReading.getNs().getWorkflowContext());
        		newContext.put(NamespaceConstants.NAMESPACE,vmTemplateReading.getNs());
        		newContext.put(NamespaceConstants.PARENT_RULE_ID,vmTemplateReading.getId());
        		newContext.put(NamespaceConstants.NAMESPACE_FIELDS,vmTemplateReading.getNs().getFields());
        		
        		chain.execute();
        	}
			
		}
    	
		return false;
	}

}
