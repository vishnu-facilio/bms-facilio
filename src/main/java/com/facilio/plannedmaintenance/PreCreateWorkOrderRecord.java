package com.facilio.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class PreCreateWorkOrderRecord extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule("workorder");

        List<V3WorkOrderContext> generatedWorkOrder = new ArrayList<>();
        if (context.containsKey("generatedWorkOrderList")) {
            generatedWorkOrder = (List<V3WorkOrderContext>) context.get("generatedWorkOrderList");
        }
        Long plannerId = (Long) context.get("plannerId");
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = generatedWorkOrder.stream().map(i -> (ModuleBaseWithCustomFields) i).collect(Collectors.toList());

        for (ModuleBaseWithCustomFields object : moduleBaseWithCustomFields) {
            Map<String, Object> objectMap = FieldUtil.getAsProperties(object);
            V3Util.preCreateRecord(workOrderModule.getName(), Collections.singletonList(objectMap), null, null);
        }
        updateLastGeneratedTimeInPlanner(plannerId, (long) context.getOrDefault(FacilioConstants.ContextNames.LAST_EXECUTION_TIME, -1));
        return false;
    }

    private void updateLastGeneratedTimeInPlanner(long plannerId, long generatedUpto) throws Exception {
        if (generatedUpto > 0) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
            List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("generatedUpto", generatedUpto);

            UpdateRecordBuilder<PMPlanner> updateRecordBuilder = new UpdateRecordBuilder<>();
            updateRecordBuilder.fields(Collections.singletonList(fieldMap.get("generatedUpto")));
            updateRecordBuilder.module(pmPlannerModule);
            updateRecordBuilder.andCondition(CriteriaAPI.getIdCondition(plannerId, pmPlannerModule));
            updateRecordBuilder.updateViaMap(updateMap);
        }
    }
}
