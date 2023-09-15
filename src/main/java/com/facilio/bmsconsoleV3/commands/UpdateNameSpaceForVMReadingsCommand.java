package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.bmsconsoleV3.util.VirtualMeterTemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
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
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
        virtualMeterTemplateReadingContext.getNs().setStatus(virtualMeterTemplateReadingContext.getStatus());

        Long execInterval = virtualMeterTemplateReadingContext.getFrequencyEnum() != null ? virtualMeterTemplateReadingContext.getFrequencyEnum().getMs() : null;
        virtualMeterTemplateReadingContext.getNs().setExecInterval(execInterval);
        VirtualMeterTemplateReadingContext existingVmTemplateReading = VirtualMeterTemplateAPI.getVmTemplateReading(virtualMeterTemplateReadingContext.getId());
        context.put("oldVmTemplateReading", existingVmTemplateReading);
        virtualMeterTemplateReadingContext.getNs().setId(existingVmTemplateReading.getNs().getId());
        virtualMeterTemplateReadingContext.getNs().setParentRuleId(existingVmTemplateReading.getNs().getParentRuleId());
        virtualMeterTemplateReadingContext.getNs().setType(existingVmTemplateReading.getNs().getType());
        context.put(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING, virtualMeterTemplateReadingContext);

        return false;
    }
}
