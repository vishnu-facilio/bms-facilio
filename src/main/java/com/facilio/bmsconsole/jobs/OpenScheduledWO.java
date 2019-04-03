package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class OpenScheduledWO extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(OpenScheduledWO.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO)) {
                return;
            }
            long woId = jc.getJobId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
            selectRecordsBuilder.select(fields)
                    .module(module)
                    .beanClass(WorkOrderContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(woId, module));

            List<WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();
            if (workOrderContexts == null || workOrderContexts.isEmpty()) {
                return;
            }

            WorkOrderContext wo = workOrderContexts.get(0);
            if ((wo.getAssignedTo() != null && wo.getAssignedTo().getId() > 0)
                    || (wo.getAssignmentGroup() != null && (wo.getAssignmentGroup().getId() > 0 || wo.getAssignmentGroup().getGroupId() > 0))) {
                TicketStatusContext status = TicketAPI.getStatus("Assigned");
                wo.setStatus(status);
            } else {
                TicketStatusContext status = TicketAPI.getStatus("Submitted");
                wo.setStatus(status);
            }

            wo.setJobStatus(WorkOrderContext.JobsStatus.COMPLETED);

            UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
            updateRecordBuilder.module(module)
                    .fields(Arrays.asList(fieldMap.get("status"), fieldMap.get("jobStatus")))
                    .andCondition(CriteriaAPI.getIdCondition(woId, module));
            updateRecordBuilder.update(wo);

            FacilioContext context = new FacilioContext();
            
            List<EventType> activities = new ArrayList<>();
            activities.add(EventType.CREATE);

            //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

            String status = wo.getStatus().getStatus();
            if (status != null && status.equals("Assigned")) {
                activities.add(EventType.ASSIGN_TICKET);
            }
            context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);

            List<UpdateChangeSet> changeSets = FieldUtil.constructChangeSet(wo.getId(), FieldUtil.getAsProperties(wo), fieldMap);
            if (changeSets != null && !changeSets.isEmpty()) {
                Map<Long, List<UpdateChangeSet>> changeSetMap = new HashMap<>();
                changeSetMap.put(woId, changeSets);
                context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, changeSetMap));
            }
			if (!changeSets.isEmpty()) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
				Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get("workorder");
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); //All IDs (bulk and individual) of WOs to be updated
				Iterator it = recordIds.iterator();
				List<UpdateChangeSet> changeSetList = null;
				while (it.hasNext()) {
					Object record = it.next();
					 changeSetList = currentChangeSet == null ? null : currentChangeSet.get(record);
				}
                JSONObject addWO = new JSONObject();
                List<Object> wolist = new ArrayList<Object>();
               
					JSONObject newinfo = new JSONObject();
					newinfo.put("pmid", wo.getPm().getId());
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

    				addWO.put("addWO", wolist);

    				CommonCommandUtil.addActivityToContext(wo.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, (FacilioContext) context);
                

			}
            context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(wo)));
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));
            Chain c = TransactionChainFactory.getWorkOrderWorkflowsChain();
            c.execute(context);

            PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(wo.getPm().getId(), true);
            Map<Long, WorkOrderContext> pmToWo = new HashMap<>();
            pmToWo.put(pm.getId(), wo);

            PreventiveMaintenanceAPI.schedulePostReminder(Arrays.asList(pm), wo.getResource().getId(), pmToWo, wo.getScheduledStart());
        } catch (Exception e) {
            CommonCommandUtil.emailException("OpenScheduledWO", ""+jc.getJobId(), e);
            LOGGER.error("WorkOrder Status Change failed: ", e);
            throw e;
        }
    }
}
