package com.facilio.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.beans.ModuleCRUDBeanImpl;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Log4j
public class FetchPlannerDetails extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long plannerId = null;
        PlannedMaintenanceAPI.ScheduleOperation operation = null;
        if(context.containsKey("plannerId")){
            plannerId = (Long) context.get("plannerId");
        }
        if(context.containsKey("operation")){
            operation = (PlannedMaintenanceAPI.ScheduleOperation) context.get("operation");
        }
        if(plannerId == null || operation == null){
            throw new IllegalArgumentException("PlannerId or Operation cannot be null");
        }
        PMPlanner pmPlanner = getPmPlanners(plannerId);
        PlannedMaintenance plannedmaintenance = V3RecordAPI.getRecord("plannedmaintenance", pmPlanner.getPmId());
        List<PMResourcePlanner> pmResourcePlanners = getPMResourcePlanner(plannerId);
        if(pmResourcePlanners != null){
            for(PMResourcePlanner resourcePlanner : pmResourcePlanners){
                LOGGER.debug(resourcePlanner.toString());
            }
        }

        context.put("trigger", pmPlanner.getTrigger());

        Map<FacilioStatus.StatusType, FacilioStatus> statusMap = new HashMap<>();
        getPreOpenStatus(statusMap);
        context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);
        ExecutorBase scheduleExecutor = operation.getExecutorClass();

        scheduleExecutor.deletePreOpenworkOrder(plannerId);

        pmPlanner.setResourcePlanners(pmResourcePlanners);

        context.put(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, plannedmaintenance);
        context.put(FacilioConstants.PM_V2.PM_V2_PLANNER, pmPlanner);
        ExecutorBase executorBase = operation.getExecutorClass();
        executorBase.execute(context);

        return false;
    }
    private PMPlanner getPmPlanners(long plannerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        SelectRecordsBuilder<PMPlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(pmPlannerFields);
        selectRecordsBuilder.beanClass(PMPlanner.class);
        selectRecordsBuilder.module(pmPlannerModule);

        // add supplement to be fetched
        List<SupplementRecord> supplementList = new ArrayList<>();
        supplementList.add((LookupField) fieldMap.get("trigger"));

        selectRecordsBuilder.fetchSupplements(supplementList);
        selectRecordsBuilder.andCondition(CriteriaAPI.getIdCondition(plannerId, pmPlannerModule));
        List<PMPlanner> pmPlanners = selectRecordsBuilder.get();

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(pmPlanners)) {
            return pmPlanners.get(0);
        }
        return null;
    }
    private List<PMResourcePlanner> getPMResourcePlanner(Long pmPlannerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(pmPlannerFields)
                .beanClass(PMResourcePlanner.class)
                .module(pmPlannerModule)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("planner"), pmPlannerId+"", NumberOperators.EQUALS));

        // add supplements to be fetched
        List<SupplementRecord> supplementList = new ArrayList<>();
        supplementList.add((LookupField) fieldMap.get("resource"));
        selectRecordsBuilder.fetchSupplements(supplementList);

        return selectRecordsBuilder.get();
    }
    private void getPreOpenStatus(Map<FacilioStatus.StatusType, FacilioStatus> statusMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioStatus> statusOfStatusType = TicketAPI.getStatusOfStatusType(workorderModule, FacilioStatus.StatusType.PRE_OPEN);
        statusMap.put(FacilioStatus.StatusType.PRE_OPEN, statusOfStatusType.get(0));
    }
}
