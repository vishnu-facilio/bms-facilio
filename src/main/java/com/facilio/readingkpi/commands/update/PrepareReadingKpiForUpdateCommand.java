package com.facilio.readingkpi.commands.update;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class PrepareReadingKpiForUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        ReadingKPIContext readingKPIContext = (ReadingKPIContext) recordMap.get(moduleName).get(0);
        NameSpaceContext nameSpaceContext = NamespaceAPI.getNameSpaceByRuleId(readingKPIContext.getId(), NSType.KPI_RULE);
        WorkflowContext workflow = readingKPIContext.getNs().getWorkflowContext();
        workflow.setId(nameSpaceContext.getWorkflowId());
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
        readingKPIContext.getNs().setStatus(readingKPIContext.getStatus());

        Long execInterval = readingKPIContext.getFrequencyEnum() != null ? readingKPIContext.getFrequencyEnum().getMs() : null;
        readingKPIContext.getNs().setExecInterval(execInterval);
        ReadingKPIContext existingReadingKpi = ReadingKpiAPI.getReadingKpi(readingKPIContext.getId());
        context.put("oldReadingKpi", existingReadingKpi);
        readingKPIContext.getNs().setId(existingReadingKpi.getNs().getId());
        context.put(FacilioConstants.ReadingKpi.READING_KPI, readingKPIContext);
        return false;
    }
}
