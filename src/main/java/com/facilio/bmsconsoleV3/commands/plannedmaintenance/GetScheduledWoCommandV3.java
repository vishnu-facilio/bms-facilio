package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GetScheduledWoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String currentCalendarView = (String) context.get(FacilioConstants.ContextNames.CURRENT_CALENDAR_VIEW);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if(filterCriteria == null) {
            filterCriteria = new Criteria();
        }
        boolean isCriteriaAdded = false;
        Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("workorder");
        if(scopeCriteria != null) {
            isCriteriaAdded = true;
            filterCriteria.andCriteria(scopeCriteria);
        }

        Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("workorder","read");
        if(permissionCriteria != null) {
            isCriteriaAdded = true;
            filterCriteria.andCriteria(permissionCriteria);
        }

        if (!isCriteriaAdded) {
            filterCriteria = null;
        }
        long startTime = (Long) context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_START_TIME);
        long endTime = (Long) context.get(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_END_TIME);
        Map<Long, List<Map<String, Object>>> pmJobsMap = PlannedMaintenanceAPI.getScheduledWofromPpmId(startTime, endTime, filterCriteria);
        Set<Long> pmIds = new HashSet<>();
        if (pmJobsMap == null || pmJobsMap.isEmpty()) {
            return false;
        }

        List<Map<String, Object>> pmJobList = new ArrayList<>();

        Set<Long> keys = pmJobsMap.keySet();
        for (Long key: keys) {
            List<Map<String, Object>> jobList = pmJobsMap.get(key);
            for (Map<String, Object> job: jobList) {
                pmIds.add((Long) job.get("pmId"));
            }
        }

        Map<Long, List<PMTriggerV2>> pmTriggersMap = PlannedMaintenanceAPI.getTriggerFromPmV2Ids(pmIds);
        Map<Long, PlannedMaintenance> pmMap = PlannedMaintenanceAPI.getActivePpm(pmIds);
        List<PlannedMaintenance> pmList = pmMap.values().stream().collect(Collectors.toList());
        Set<Long> resourceIds = new HashSet<>();
        fillResourceIds(pmJobsMap,pmTriggersMap,pmIds,resourceIds);
        Map<Long, ResourceContext> resourceAsMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
        if(CollectionUtils.isNotEmpty(pmIds)){
            for(long pmId : pmIds){
                List<PMTriggerV2> pmTrigggers = pmTriggersMap.get(pmId);
                if(CollectionUtils.isEmpty(pmTrigggers)){
                    break;
                }
                for (PMTriggerV2 trigger : pmTrigggers) {
                    if (trigger.getSchedule() == null) {
                         break;
                    }
                    List<Map<String, Object>> pmJobs = pmJobsMap.get(trigger.getId());
                    if(CollectionUtils.isEmpty(pmJobs)){
                        break;
                    }
                    for(Map<String, Object> pmJob : pmJobs) {
                        if(pmJob.get("resourceId") != null) {
                            if (resourceIds.contains(pmJob.get("resourceId"))) {
                                 ResourceContext resourceContext = resourceAsMap.get(pmJob.get("resourceId"));
                                 pmJob.put("resource",resourceContext);
                            }
                            pmJobList.add(pmJob);
                        }
                    }
                }
                context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_LIST, pmList);
                context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_JOB_LIST, pmJobList);
                context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_TRIGGER_LIST, pmTriggersMap);
                context.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE_RESOURCE_LIST,resourceAsMap);
            }
        }

        return false;
    }
    private void fillResourceIds(Map<Long, List<Map<String, Object>>> pmJobsMap, Map<Long, List<PMTriggerV2>> pmTriggersMap,Set<Long> pmIds, Set<Long> resourceIds) {
        for (long pmId : pmIds) {
            List<PMTriggerV2> pmTrigggers = pmTriggersMap.get(pmId);
            for (PMTriggerV2 trigger : pmTrigggers) {
                if(trigger.getSchedule() == null) {
                    return;
                }
                List<Map<String, Object>> pmJobs = pmJobsMap.get(trigger.getId());
                if(pmJobs == null && pmJobs.isEmpty()) {
                    return;
                }
                for(Map<String, Object> pmJob : pmJobs) {
                    if(pmJob.get("resourceId") != null) {
                        if (!resourceIds.contains(pmJob.get("resourceId"))) {
                            resourceIds.add((long) pmJob.get("resourceId"));
                        }
                    }
                }
            }
        }
    }
}
