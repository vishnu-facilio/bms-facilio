package com.facilio.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchedulePmV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long pmPlannerId = (Long) context.get("pmPlannerId");
        PMPlanner planner = getPMPlanner(pmPlannerId);
        PlannedMaintenance plannedMaintenance = getPlannedMaintenance(planner.getPmId());
        // PMTriggerV2 pmTrigger = getPMTrigger(plannedMaintenance.getTriggerId());
       // context.put("trigger", pmTrigger);
        context.put("cutOffTime", System.currentTimeMillis());
        context.put("maxCount", 15);
        context.put(FacilioConstants.PM_V2.PM_V2_MODULE_NAME, plannedMaintenance);
        context.put(FacilioConstants.PM_V2.PM_V2_PLANNER, planner);
        
        ScheduleExecutor scheduleExecutor = new ScheduleExecutor();
        List<V3WorkOrderContext> workorders = scheduleExecutor.execute(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = workorders.stream().map(i -> (ModuleBaseWithCustomFields) i).collect(Collectors.toList());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        V3Util.createRecord(modBean.getModule("workorder"), moduleBaseWithCustomFields);
        return false;
    }

    private PMTriggerV2 getPMTrigger(long triggerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields("pmTrigger");
        SelectRecordsBuilder<PMTriggerV2> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.module(modBean.getModule("pmTrigger"))
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(triggerId, modBean.getModule("pmTrigger")));
        List<PMTriggerV2> pmTriggerV2s = selectRecordsBuilder.get();
        return pmTriggerV2s.get(0);
    }

    private PlannedMaintenance getPlannedMaintenance(long pmId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields("plannedmaintenance");
        SelectRecordsBuilder<PlannedMaintenance> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(fields)
                .module(modBean.getModule("plannedmaintenance"))
                .beanClass(PlannedMaintenance.class)
                .andCondition(CriteriaAPI.getIdCondition(pmId, modBean.getModule("plannedmaintenance")));
        List<PlannedMaintenance> plannedMaintenances = selectRecordsBuilder.get();
        return plannedMaintenances.get(0);
    }

    private PMPlanner getPMPlanner(long pmPlannerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        PMPlanner pmPlanner = (PMPlanner) V3Util.getRecord("pmPlanner", pmPlannerId, null);
        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(fields)
                .module(modBean.getModule("pmResourcePlanner"))
                .beanClass(PMResourcePlanner.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("planner"), Collections.singletonList(pmPlannerId),NumberOperators.EQUALS));
        List<PMResourcePlanner> pmResourcePlanners = selectRecordsBuilder.get();
        pmPlanner.setResourcePlanners(pmResourcePlanners);

        return null;
    }
}
