package com.facilio.bmsconsoleV3.commands;


import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.bmsconsoleV3.util.VirtualMeterTemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateNameSpaceForVMReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        VirtualMeterTemplateReadingContext virtualMeterTemplateReadingContext = (VirtualMeterTemplateReadingContext) recordMap.get(moduleName).get(0);
        NameSpaceContext nameSpaceContext = NamespaceAPI.getNameSpaceByRuleId(virtualMeterTemplateReadingContext.getId(), NSType.VIRTUAL_METER);
        WorkflowContext workflow = virtualMeterTemplateReadingContext.getNs().getWorkflowContext();
        workflow.setId(nameSpaceContext.getWorkflowId());

        Long execInterval = virtualMeterTemplateReadingContext.getFrequencyEnum() != null ? virtualMeterTemplateReadingContext.getFrequencyEnum().getMs() : null;
        virtualMeterTemplateReadingContext.getNs().setExecInterval(execInterval);

        ResourceCategory resourceCategory=new ResourceCategory();
        resourceCategory.setResType(ResourceType.METER_CATEGORY);
        virtualMeterTemplateReadingContext.setCategory(resourceCategory);//TODO:

        VirtualMeterTemplateReadingContext existingVmTemplateReading = VirtualMeterTemplateAPI.getVmTemplateReading(virtualMeterTemplateReadingContext.getId());
        context.put("oldVmTemplateReading", existingVmTemplateReading);
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
        context.put(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING, virtualMeterTemplateReadingContext);

        return false;
    }
}
