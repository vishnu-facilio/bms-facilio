package com.facilio.bmsconsole.jobs.monitoring;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.jobs.monitoring.utils.MonitoringFeature;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class PMV1MonitoringToolJob extends FacilioJob {

    final class PMV1SchedulerCheckCommand extends FacilioCommand{

        @Override
        public boolean executeCommand(Context context) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            long orgId = AccountUtil.getCurrentOrg().getId();
            StringBuilder resultStringBuilder = (StringBuilder) context.get("resultStringBuilder");

            /**
             * WorkOrder count check at Trigger Level and PM level - working on this
             */
            List<PreventiveMaintenance>  activePMs = getActivePMIds(modBean, orgId);
            Map<Long, PMTriggerContext> triggerMap = getTriggerMap();

            if(triggerMap.isEmpty()){
                resultStringBuilder  = appendMessage(resultStringBuilder, "TriggerMap is empty in org #" + orgId);
                return false;
            }

            resultStringBuilder.append("ORGID: ").append(orgId).append(" \n ");

            for(PreventiveMaintenance pm: activePMs) {
                if(CollectionUtils.isEmpty(pm.getTriggers())){
                    continue;
                } else if (CollectionUtils.isNotEmpty(pm.getTriggers())) {
                    Optional<PMTriggerContext> minTrigger = pm.getTriggers().stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.SCHEDULE).min(Comparator.comparingInt(PMTriggerContext::getFrequency));
                    if(!minTrigger.isPresent()){
                        continue;
                    }
                }
                List<Long> scope;
                Long baseSpaceId = pm.getBaseSpaceId();
                if (baseSpaceId == null || baseSpaceId < 0) {
                    scope = PreventiveMaintenanceAPI.getPMSites(pm.getId());
                } else {
                    scope = Arrays.asList(baseSpaceId);
                }
                List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(), scope, pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), true);
                Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());

                long countOfNextExecutionTime_PMLevel = 0L;
                long endTime_in_s = PreventiveMaintenanceAPI.getEndTime(-1L, pm.getTriggers());
                long currentTime_in_s = System.currentTimeMillis();

                for(Long resourceId :resourceIds) {
                    List<PMTriggerContext> triggers = getResourceTriggers(triggerMap, pmResourcePlanner, resourceId);
                    if (CollectionUtils.isEmpty(triggers)){
                        LOGGER.info("No Trigger available for resource #" + resourceId + ", in PM #" + pm.getId() + ".");
                        continue;
                    }

                    for(PMTriggerContext trigger: triggers) {
                        if(trigger.getTriggerType() == PreventiveMaintenance.TriggerType.ONLY_SCHEDULE_TRIGGER.getVal() && trigger.getSchedule() != null) {
                            long startTime_in_s = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                            long minTime = -1L;
                            long countOfNextExecutionTime_TriggerLevel = PreventiveMaintenanceAPI.getNextExecutionTimesCountForPMMonitoring(pm, trigger, startTime_in_s, endTime_in_s, minTime);
                            countOfNextExecutionTime_PMLevel += countOfNextExecutionTime_TriggerLevel;
                        }else {
                            LOGGER.info("Trigger "+ trigger.getId() +" isn't of Scheduled Type.");
                        }
                    }
                }
                // PM wise WorkOrder count check
                long actualCountOfWorkOrdersPMLevel = getActualPreOpenWorkOrderCount(modBean, pm.getId(), currentTime_in_s,endTime_in_s * 1000);
                if(actualCountOfWorkOrdersPMLevel != countOfNextExecutionTime_PMLevel){
                    String message = "WorkOrder count didn't match for PM #" + pm.getId() +
                            " countOfWorkOrders_PMLevel = " + countOfNextExecutionTime_PMLevel +
                            ", actualCountOfWorkOrdersPMLevel = " + actualCountOfWorkOrdersPMLevel;
                    resultStringBuilder = appendMessage(resultStringBuilder, message);
                }
            }


            /**
             * Count of PMs with WO_GENERATION_STATUS = 1
             */
            long countOfPMsWithWoGenerationStatusAsOne = getCountOfPMsWithWoGenerationStatusAsOne(modBean);
            if(countOfPMsWithWoGenerationStatusAsOne > 0){
                resultStringBuilder  = appendMessage(resultStringBuilder, countOfPMsWithWoGenerationStatusAsOne + "PMs are locked in org #" + orgId);
            }
            return false;
        }

        private List<PreventiveMaintenance> getActivePMIds(ModuleBean modBean, long orgid) throws Exception {
            //List<Long> pmIds = new ArrayList<>();
            List<PreventiveMaintenance> preventiveMaintenanceList = PreventiveMaintenanceAPI.getAllPMs(orgid, true);
            if(CollectionUtils.isNotEmpty(preventiveMaintenanceList)){
                //pmIds = preventiveMaintenanceList.stream().map(PreventiveMaintenance::getId).collect(Collectors.toList());
                for (PreventiveMaintenance pm: preventiveMaintenanceList){
                    pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
                }
                return preventiveMaintenanceList;
            }
            return new ArrayList<>();
        }

        private List<PMTriggerContext> getResourceTriggers(Map<Long, PMTriggerContext> triggerMap, Map<Long, PMResourcePlannerContext> pmResourcePlanner, Long resourceId) {
            if(pmResourcePlanner.get(resourceId) == null) {
                return null;
            }
            PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
            List<PMTriggerContext> triggers = new ArrayList<>();
            if (currentResourcePlanner.getTriggerContexts() != null) {
                for (PMTriggerContext t: currentResourcePlanner.getTriggerContexts()) {
                    if (triggerMap.get(t.getId()) != null) {
                        triggers.add(triggerMap.get(t.getId()));
                    }
                }
            }
//            if (currentResourcePlanner.getAssignedToId() != null && currentResourcePlanner.getAssignedToId() > 0 ) {
//                workorderTemplate.setAssignedToId(currentResourcePlanner.getAssignedToId());
//            }
            currentResourcePlanner.setTriggerContexts(triggers);
            return triggers;
        }

        private Map<Long,PMTriggerContext> getTriggerMap() throws Exception {
            Map<Long,PMTriggerContext> pmTriggerContextMap = new HashMap<>();
            FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
            FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
            List<FacilioField> fields = FieldFactory.getPMTriggerFields();
            List<FacilioField> pmFields = FieldFactory.getPreventiveMaintenanceFields();
            Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(pmFields);
            fields.addAll(pmFields);

            GenericSelectRecordBuilder pmTriggerBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(pmTriggerModule.getTableName())
                    .innerJoin(pmModule.getTableName())
                    .on(pmTriggerModule.getTableName() + ".PM_ID = " + pmModule.getTableName() + ".ID")
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
                    .andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("triggerType"), String.valueOf(PreventiveMaintenance.TriggerType.ONLY_SCHEDULE_TRIGGER.getVal()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));

            List<Map<String, Object>> triggerProps = pmTriggerBuilder.get();

            if(triggerProps != null && !triggerProps.isEmpty()) {
                triggerProps.forEach(triggerProp -> {
                    PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);
                    pmTriggerContextMap.put(trigger.getId(), trigger);
                });
            }
            return pmTriggerContextMap;
        }

        private long getActualPreOpenWorkOrderCount(ModuleBean modBean,Long pmId, Long startTime, Long endTime) throws Exception {

            SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
                    .aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()), FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER)))
                    .module(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER))
                    .beanClass(WorkOrderContext.class)
                    .andCustomWhere("PM_ID = ? AND CREATED_TIME >= ? AND CREATED_TIME <= ?", pmId, startTime, endTime)
                    .skipModuleCriteria();

            List<Map<String, Object>> resPlannerProps = selectRecordsBuilder.getAsProps();

            if(CollectionUtils.isNotEmpty(resPlannerProps)) {
                return ((Long) resPlannerProps.get(0).get("id")).intValue();
            }else {
                return -1L;
            }
        }

        private long getCountOfPMsWithWoGenerationStatusAsOne(ModuleBean modBean) throws Exception {
            FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
            List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
            Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("woGenerationStatus"), String.valueOf(true), BooleanOperators.IS));

            List<Map<String, Object>> props = selectBuilder.get();

            if(CollectionUtils.isEmpty(props)){
                return 0;
            }
            return props.size();
        }
        private StringBuilder appendMessage(StringBuilder stringBuilder, String message){
            return stringBuilder.append(message).append(" \n ");
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(PMV1MonitoringToolJob.class.getName());
    @Override
    public void execute(JobContext jobContext) throws Exception {
        Long orgID = jobContext.getOrgId();
        StringBuilder res = new StringBuilder();
        try {
            long startTime = System.currentTimeMillis();
            FacilioChain facilioChain = FacilioChain.getTransactionChain();
            facilioChain.addCommand(new PMV1MonitoringToolJob.PMV1SchedulerCheckCommand());
            facilioChain.getContext().put("resultStringBuilder", res);
            facilioChain.execute();
            if(!res.toString().isEmpty()) {
                insertResultIntoMonitoringToolMetaTable(res.toString(), orgID);
            }
            LOGGER.info("Time taken to delete run PMV1MonitoringToolJob = " + (System.currentTimeMillis() - startTime) + ", in org " + orgID);
        } catch (Exception e) {
            LOGGER.error("Error occurred when ORGID = " + orgID, e);
        }
    }

    private boolean insertResultIntoMonitoringToolMetaTable(String result, long orgId){
        String query = "INSERT INTO Monitoring_Tool_Meta(ORGID, FEATURE,TTIME,META) VALUES(?,?,?,?)";

        try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement stmt=conn.prepareStatement(query);) {
            stmt.setLong(1, orgId);
            stmt.setInt(2, MonitoringFeature.PM_V1.getVal());
            stmt.setLong(3, DateTimeUtil.getCurrenTime());
            stmt.setString(4, result);

            int i=stmt.executeUpdate();
            LOGGER.info(i + " records inserted");
            return true;
        }catch (Exception e){
            LOGGER.error("Error occurred while inserting into Monitoring_Tool_Meta ORGID = " + orgId, e);
        }
        return false;
    }
}
