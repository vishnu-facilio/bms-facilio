package com.facilio.agent.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsoleV3.context.DataLogSummaryContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class AgentLoggerSummaryAfterFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> recordMap = (Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<DataLogSummaryContextV3> recordList = (List<DataLogSummaryContextV3>) recordMap.get((String)context.get(FacilioConstants.ContextNames.MODULE_NAME));
        List<Long>fieldIds = recordList.stream().map(x->(Long)x.getReadingId()).collect(Collectors.toList());
        Map<Long, Map<String, Object>> readings = CommissioningApi.getFields(new HashSet<>(fieldIds));

        recordMap.put("readings",readings);

        Map<Integer, Map<Long, String>> resourceMap = new HashMap<>();
        Map<Integer, Set<Long>> scopeVsParentIds = new HashMap<>();

        for (DataLogSummaryContextV3 dataLog : recordList) {
            ResourceType scope = null;
            if (dataLog.getReadingScope() > 0){
                scope = ResourceType.valueOf(dataLog.getReadingScope());
            }
            else {
                scope = ResourceType.ASSET_CATEGORY;
            }
            int scopeIntVal = scope.getIndex();
            long resourceId = dataLog.getResourceId();

            scopeVsParentIds.computeIfAbsent(scopeIntVal, k -> new HashSet<>()).add(resourceId);
        }

        scopeVsParentIds.forEach((scope, parentIds) -> {
            Map<Long, String> pickList = null;
            try {
                pickList = CommissioningApi.getParent(parentIds, ResourceType.valueOf(scope).getModuleName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            resourceMap.put(scope, pickList);
        });


        recordMap.put("resourceMap",resourceMap);

        return false;
    }
}
