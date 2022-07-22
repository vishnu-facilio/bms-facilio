package com.facilio.bmsconsole.jobs;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class OpenScheduleWOV2 extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            long woId = jobContext.getJobId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            List<V3WorkOrderContext> workOrderContexts = getWorkorderContext(woId, module, fields);
            if (workOrderContexts == null || workOrderContexts.isEmpty()) {
                return;
            }

            V3WorkOrderContext wo = workOrderContexts.get(0);

            PlannedMaintenance plannedmaintenance = (PlannedMaintenance) V3Util.getRecord("plannedmaintenance", wo.getPmV2().getId(), null);

            if (!plannedmaintenance.isActive()) {
                return;
            }

            updatedJobStatus(module, fieldMap, wo);

            FacilioContext context = new FacilioContext();

            addActivities(context, wo);

            setChangeset(wo, context);

            context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(wo)));
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));

            FacilioChain c = TransactionChainFactory.getWorkOrderWorkflowsChain(true);
            c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
            c.execute(context);
        } catch (Exception e) {
            CommonCommandUtil.emailException("OpenScheduledWOV2", ""+jobContext.getJobId(), e);
            LOGGER.error("WorkOrder Status Change failed: ", e);
            throw e;
        }
    }

    private void setChangeset(V3WorkOrderContext wo, FacilioContext context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        List<UpdateChangeSet> changeSets = FieldUtil.constructChangeSet(wo.getId(), FieldUtil.getAsProperties(wo), fieldMap);
        if (changeSets != null && !changeSets.isEmpty()) {
            Map<Long, List<UpdateChangeSet>> changeSetMap = new HashMap<>();
            changeSetMap.put(wo.getId(), changeSets);
            context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, changeSetMap));
        }

        if (!changeSets.isEmpty()) {
            context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
            Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap(context);
            Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap.get(FacilioConstants.ContextNames.WORK_ORDER);

            List<UpdateChangeSet> changeSetList = currentChangeSet.get(wo.getId());
            JSONObject addWO = new JSONObject();
            List<Object> wolist = new ArrayList<Object>();

            JSONObject newinfo = new JSONObject();
            newinfo.put("pmidV2", wo.getPmV2().getId());
            wolist.add(newinfo);

            for (UpdateChangeSet changeset : changeSetList) {
                long fieldid = changeset.getFieldId();
                Object oldValue = changeset.getOldValue();
                Object newValue = changeset.getNewValue();
                FacilioField field = modBean.getField(fieldid, "workorder");

                JSONObject info = new JSONObject();
                info.put("field", field.getName());
                info.put("displayName", field.getDisplayName());
                info.put("oldValue", oldValue);
                info.put("newValue", newValue);
                wolist.add(info);
            }

            addWO.put("addPMWO", wolist);

            CommonCommandUtil.addActivityToContext(wo.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, context);
        }
    }

    private void addActivities(FacilioContext context, V3WorkOrderContext wo) {
        List<EventType> activities = new ArrayList<>();
        activities.add(EventType.CREATE);

        //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
        String status = wo.getStatus().getStatus();

        if (status != null && status.equals("Assigned")) {
            activities.add(EventType.ASSIGN_TICKET);
        }

        context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);
    }

    private void updatedJobStatus(FacilioModule module, Map<String, FacilioField> fieldMap, V3WorkOrderContext wo) throws Exception {
        wo.setJobStatus(WorkOrderContext.JobsStatus.COMPLETED.getValue());

        UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.module(module)
                .fields(Arrays.asList(fieldMap.get("status"), fieldMap.get("jobStatus")))
                .andCondition(CriteriaAPI.getIdCondition(wo.getId(), module))
                .skipModuleCriteria();
        updateRecordBuilder.update(wo);
    }

    private List<V3WorkOrderContext> getWorkorderContext(long woId, FacilioModule module, List<FacilioField> fields) throws Exception {
        SelectRecordsBuilder<V3WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(fields)
                .module(module)
                .beanClass(V3WorkOrderContext.class)
                .innerJoin("Resources")
                .on("Resources.ID = Tickets.RESOURCE_ID AND (Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = 0)")
                .andCondition(CriteriaAPI.getIdCondition(woId, module))
                .skipModuleCriteria();
        List<V3WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();
        return workOrderContexts;
    }
}
