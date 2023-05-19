package com.facilio.bmsconsole.jobs;

import java.util.*;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;

/*
    TODO(4): Test
    If the resources has Assigned to,
        - ensure if the WO goes to ASSIGNED STATE and
        - if it doesn't have any assigned - WO goes to OPEN/SUBMITTED STATE
 */
@Log4j
public class OpenScheduleWOV2 extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            long woId = jobContext.getJobId();
            if(validateWorkOrder(woId)) {
                V3Util.postCreateRecord(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(woId), null, null, null);
            }
            else{
                LOGGER.error("Open Scheduled WorkOrder Skipped for - "+woId);
            }
            
        } catch (Exception e) {
            CommonCommandUtil.emailException("OpenScheduledWOV2", ""+jobContext.getJobId(), e);
            LOGGER.error("WorkOrder Status Change failed: ", e);
            throw e;
        }
    }

    public static boolean validateWorkOrder(long woId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> workOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        SelectRecordsBuilder<V3WorkOrderContext> v3WorkOrderContextSelectRecordsBuilder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .module(workOrderModule)
                .select(workOrderFields)
                .beanClass(V3WorkOrderContext.class)
                .andCondition(CriteriaAPI.getIdCondition(woId,workOrderModule))
                .skipModuleCriteria();
        V3WorkOrderContext workOrderContext = v3WorkOrderContextSelectRecordsBuilder.fetchFirst();

        if(workOrderContext == null ){
            return false;
        }

        if(!FacilioUtil.isEmptyOrNull(workOrderContext.getResource())){
            ResourceContext resource = ResourceAPI.getResource(workOrderContext.getResource().getId());
            if(resource != null && resource.isDecommission()){
//                FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);

                DeleteRecordBuilder<WorkOrderContext> delete = new DeleteRecordBuilder<WorkOrderContext>()
                        .module(workOrderModule)
                        .skipModuleCriteria()
                        .andCondition(CriteriaAPI.getIdCondition(workOrderContext.getId(), workOrderModule));
                delete.markAsDelete();

//                DeleteRecordBuilder<TicketContext> delete = new DeleteRecordBuilder<TicketContext>()
//                        .module(ticketModule)
//                        .andCondition(CriteriaAPI.getIdCondition(workOrderContext.getId(), ticketModule));
//                delete.markAsDelete();

                LOGGER.log(Level.ERROR, "PMV2 WorkOrder has been deleted as the corresponding resource is decommissioned: WO_ID = " + workOrderContext.getId());
                return false;
            }
        }

        if(workOrderContext.getPmV2() != null && workOrderContext.getPmPlanner() != null && workOrderContext.getPmTriggerV2() != null){
            Long pmV2Id = workOrderContext.getPmV2();
            Long plannedId = workOrderContext.getPmPlanner();
            Long triggerId = workOrderContext.getPmTriggerV2();

            if(pmV2Id == null || plannedId == null || triggerId == null){
                return false;
            }
            PlannedMaintenance plannedMaintenance = PlannedMaintenanceAPI.getPmV2fromId(pmV2Id);
            if(plannedMaintenance == null){
                return false;
            }
            if(plannedMaintenance.isDeleted() || plannedMaintenance.getPmStatus() != PlannedMaintenance.PMStatus.ACTIVE.getVal()){
                return false;
            }

            PMPlanner pmPlanner = PlannedMaintenanceAPI.getPmPlannerFromId(plannedId);
            if(pmPlanner == null){
                return false;
            }
            if(pmPlanner.isDeleted() || pmPlanner.getPmId() != pmV2Id){
                return false;
            }

            PMTriggerV2 pmTriggerV2 = PlannedMaintenanceAPI.getPmV2TriggerFromId(triggerId);
            if(pmTriggerV2 == null){
                return false;
            }
            if(!pmTriggerV2.isDeleted() && pmPlanner.getTrigger().getId() == triggerId){
                return true;
            }
        }
        return false;
    }
}
