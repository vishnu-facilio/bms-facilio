package com.facilio.agent.commands;

import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsoleV3.context.DataLogSummaryContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentLoggerSummaryAfterFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> recordMap = (Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<DataLogSummaryContextV3> recordList = (List<DataLogSummaryContextV3>) recordMap.get((String)context.get(FacilioConstants.ContextNames.MODULE_NAME));
        List<Long>fieldIds = recordList.stream().map(x->(Long)x.getReadingId()).collect(Collectors.toList());
        Map<Long, Map<String, Object>> readings = CommissioningApi.getFields(new HashSet<>(fieldIds));

        recordMap.put("readings",readings);
        return false;
    }
}
