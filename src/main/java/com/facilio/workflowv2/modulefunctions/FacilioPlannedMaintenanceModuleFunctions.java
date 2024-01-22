package com.facilio.workflowv2.modulefunctions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.AttachResourcePlannerViaScriptCommand;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.ExecutorBase;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.plannedmaintenance.PreCreateWorkOrderRecord;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.*;
import java.util.stream.Collectors;

@ScriptModule(moduleName = FacilioConstants.ContextNames.PLANNEDMAINTENANCE)
public class FacilioPlannedMaintenanceModuleFunctions extends FacilioModuleFunctionImpl {

    private static final Logger LOGGER = LogManager.getLogger(FacilioPlannedMaintenanceModuleFunctions.class.getName());

    /**
     * addAssetsToPPMPlanner adds the asset to the mentioned Planner in PPM.
     *
     * @param objects contains
     *                   1. resourcePlannerObj [* represents mandatory property]
     *                      {
     *                          resource*: {id: #id }, // validation available in v3 chain
     *                          pmId*: #id,
     *                          planner*:{id: #id },
     *                          jobPlan: {id: #id},
     *                          assignedTo: {id: #id } // validation available in v3 chain, but same team validation not there
     *                      }
     * @param globalParams
     * @param objects
     * @param scriptContext
     * @return
     * @throws Exception
     */
    public String addAssetsToPPMPlanner(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
        //objects[moduleName, resourcePlannerObj]
        Object resourcePlannerObj = objects.get(1);

        if (resourcePlannerObj instanceof Map) {
            Map<String, Object> resourcePlannerObjMap = (Map<String, Object>) resourcePlannerObj;
            StringBuilder stringBuilder = new StringBuilder();


            /* validations */

            // 1. validate resource
            if (resourcePlannerObjMap.get("resource") == null || !(resourcePlannerObjMap.get("resource") instanceof Map)) {
                throw new RuntimeException("Please check the resource of planner.");
            } else {
                Map<String, Object> resourceMap = (Map<String, Object>) resourcePlannerObjMap.get("resource");
                if (!(resourceMap.get("id") instanceof Long)) {
                    LOGGER.log(Priority.ERROR,"Resource ID should be number.");
                    throw new RuntimeException("Resource ID should be number.");
                } else if ((Long) resourceMap.get("id") < 0) {
                    LOGGER.log(Priority.ERROR,"Please check the resource ID.");
                    throw new RuntimeException("Please check the resource ID.");
                }

                V3ResourceContext resource = V3ResourceAPI.getResource((Long) resourceMap.get("id"));
                if (resource == null) {
                    LOGGER.log(Priority.ERROR,"Invalid Resource.");
                    throw new RuntimeException("Invalid Resource.");
                }
                stringBuilder.append("Attached the Resource #").append(resource.getId()).append(" to ");
                // TODO: check if the resource is an asset if required
//            else if (!resource.getResourceTypeEnum().equals(V3ResourceContext.ResourceType.ASSET)) {
//                    throw new RuntimeException("Resource should of ASSET type only.");
//                }
            }

            // 2. validate PPM
            if (resourcePlannerObjMap.get("pmId") == null) {
                LOGGER.log(Priority.ERROR,"PlannedMaintenance ID of planner is mandatory.");
                throw new RuntimeException("PlannedMaintenance ID of planner is mandatory.");
            }
            PlannedMaintenance plannedMaintenance = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, (Long) resourcePlannerObjMap.get("pmId"));
            if (plannedMaintenance == null) {
                LOGGER.log(Priority.ERROR,"Please check associated PlannedMaintenance.");
                throw new RuntimeException("Please check associated PlannedMaintenance.");
            }

            // 3. validate planner
            if (resourcePlannerObjMap.get("planner") == null) {
                throw new RuntimeException("Planner ID is mandatory.");
            }
            Map<String, Object> plannerMap = (Map<String, Object>) resourcePlannerObjMap.get("planner");
            PMPlanner planner = V3RecordAPI.getRecord(FacilioConstants.PM_V2.PM_V2_PLANNER, (Long) plannerMap.get("id"));
            if (planner == null) {
                LOGGER.log(Priority.ERROR,"Please check associated Planner.");
                throw new RuntimeException("Please check associated Planner.");
            }

            if (plannedMaintenance.getId() != planner.getPmId()) {
                LOGGER.log(Priority.ERROR,"Planner should be from the same PlannedMaintenance.");
                throw new RuntimeException("Planner should be from the same PlannedMaintenance.");
            }

            stringBuilder.append(" planner #").append(planner.getId()).append(" of PM #").append(plannedMaintenance.getId());

            // 4. validate JobPlan
            if (resourcePlannerObjMap.get("jobPlan") != null) {
                Map<String, Object> jobPlanMap = (Map<String, Object>) resourcePlannerObjMap.get("jobPlan");
                Long jobPlanId = (Long) jobPlanMap.get("id");
                if (jobPlanId != null && jobPlanId > 0) {
                    JobPlanContext jobPlanContext = JobPlanAPI.getJobPlan(jobPlanId);
                    if (jobPlanContext != null && (jobPlanContext.getJobPlanCategoryEnum().getVal() != plannedMaintenance.getAssignmentTypeEnum().getVal())) {
                        LOGGER.log(Priority.ERROR,"JobPlan should be of same type as of PlannedMaintenance ( " + plannedMaintenance.getAssignmentTypeEnum().getValue() + ").");
                        throw new RuntimeException("JobPlan should be of same type as of PlannedMaintenance ( " + plannedMaintenance.getAssignmentTypeEnum().getValue() + ").");
                    }
                }
            }

            // Create the resource planner
            FacilioChain chain = new FacilioChain(true);

            List<ModuleBaseWithCustomFields> resourcePlannerList = new ArrayList<>();
            resourcePlannerList.add(FieldUtil.getAsBeanFromMap(resourcePlannerObjMap, PMResourcePlanner.class));

            Map<String, List<ModuleBaseWithCustomFields>> moduleBaseWithCustomFieldsMap = new HashMap<>();
            moduleBaseWithCustomFieldsMap.put(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER, resourcePlannerList);

            Constants.setRecordMap(chain.getContext(), moduleBaseWithCustomFieldsMap);
            Constants.setModuleName(chain.getContext(), FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
            chain.getContext().put("plannerId", planner.getId());

            chain.addCommand(TransactionChainFactoryV3.getPmV2ResourcePlannerBeforeSaveCommand());
            chain.addCommand(new AttachResourcePlannerViaScriptCommand());
            chain.execute();

            return stringBuilder.toString();
        }
        throw new RuntimeException("Input not of expected type.");
    }

    /**
     * publishAssetsOfPlanner function schedules the workorders for resourcePlanners of a single planner. Since this schedules
     * only for a few resource planners, the trigger end time calculation is as follows.
     *  - Trigger endTime is first calculated is as per trigger configuration (which can be either >generatedUpTo or <generatedUpTo).
     *    Case 1: >generatedUpTo
     *             Edge case:
     *                  - If generatedUpTo is updated with lastExecutionTime (>generatedUpTo) of the generated records then
     *                      records to be created for existing resources for the time period generatedUpTo to lastExecutionTime would get skipped.
     *                  - We shall update the endTime to generatedUpTo so that workorders for the remaining resources won't be
     *                      skipped as part of nightly scheduler
     *    Case 2: <generatedUpTo
     *          Edge case:
     *              - If generatedUpTo isn't updated - no impact
     *              - If generatedUpTo is updated with lastExecutionTime (<generatedUpTo) of the generated records - duplicate records gets created for existing resources.
     *
     * Caution: This function creates workorders without deleting the already created pre-open records. Hence use it cautiously.
     *
     * @param objects'      1. Planner ID
     *                      2. ResourcePlanner IDs
     * @param globalParams
     * @param objects
     * @param scriptContext
     * @return
     * @throws Exception
     */
    public String publishAssetsOfPlanner(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
        //objects[moduleName, plannerIdObj, resourcePlannerIds]
        Object plannerIdObj = objects.get(1);
        Object resourcePlannerIds = objects.get(2);

        if (plannerIdObj instanceof Long && resourcePlannerIds instanceof List) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            long plannerId = (Long) plannerIdObj;
            List<Long> resourcePlannerIdList = (List<Long>) resourcePlannerIds;

            // 1. validate planner
            if (plannerId < 0) {
                throw new RuntimeException("Invalid Planner ID.");
            }
            PMPlanner pmPlanner = (PMPlanner) V3Util.getRecord(FacilioConstants.PM_V2.PM_V2_PLANNER, plannerId, null);
            if (pmPlanner == null) {
                throw new RuntimeException("Invalid Planner.");
            }

            // 2. validate resource planner
            if (CollectionUtils.isEmpty(resourcePlannerIdList)) {
                throw new RuntimeException("Empty Resource Planner IDs.");
            }
            Map<String, FacilioField> plannerResourceFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER));
            Criteria criteria = new Criteria();
            // Add criteria to only include the resourcePlanners that are from mentioned plannerId
            criteria.addAndCondition(CriteriaAPI.getCondition(plannerResourceFieldsMap.get("planner"), plannerId + "", NumberOperators.EQUALS));
            Map<Long, PMResourcePlanner> resourcePlannerMap = V3RecordAPI.getRecordsMap(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER, resourcePlannerIdList, PMResourcePlanner.class, criteria);
            if (MapUtils.isEmpty(resourcePlannerMap)) {
                throw new RuntimeException("No Resource Planner(s) with ID(s): " + resourcePlannerIdList);
            }

            // Prepare objects required for Publishing the resource
            Map<FacilioStatus.StatusType, FacilioStatus> statusMap = new HashMap<>();
            statusMap.put(FacilioStatus.StatusType.PRE_OPEN, getPreOpenStatus());

            //PMTriggerV2 pmTriggerV2 = V3RecordAPI.getRecord(FacilioConstants.PM_V2.PM_V2_TRIGGER, pmPlanner.getTriggerId());
            PMTriggerV2 pmTriggerV2 = pmPlanner.getTrigger();
            PlannedMaintenance plannedMaintenance = V3RecordAPI.getRecord(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, pmPlanner.getPmId());
            pmPlanner.setResourcePlanners(new ArrayList<>(resourcePlannerMap.values()));

            PlannedMaintenanceAPI.ScheduleOperation operation = PlannedMaintenanceAPI.ScheduleOperation.EXTEND_RESOURCE_PLANNER;
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, plannedMaintenance);
            context.put(FacilioConstants.PM_V2.PM_V2_PLANNER, pmPlanner);
            context.put("trigger", pmTriggerV2);
            context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);

            ExecutorBase scheduleExecutor = operation.getExecutorClass();
            scheduleExecutor.execute(context);

            FacilioChain facilioChain = FacilioChain.getTransactionChain(); // was given for 1800000ms (30 mins) in scheduler
            context.put("plannerId", plannerId);
            facilioChain.setContext(context);
            facilioChain.addCommand(new PreCreateWorkOrderRecord());
            facilioChain.execute();

            return "Scheduled workorders for the resourcePlanners: " + resourcePlannerIdList;
        }

        throw new RuntimeException("Input not of expected type.");
    }

    private FacilioStatus getPreOpenStatus() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioStatus> statusOfStatusType = TicketAPI.getStatusOfStatusType(workorderModule, FacilioStatus.StatusType.PRE_OPEN);
        return statusOfStatusType.get(0);
    }
}
