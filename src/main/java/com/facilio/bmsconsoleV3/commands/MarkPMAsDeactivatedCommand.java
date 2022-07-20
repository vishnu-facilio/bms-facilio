package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkPMAsDeactivatedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("plannedmaintenance");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("plannedmaintenance");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        long pmId = (long) context.get("pmId");

        Map<String, Object> valMap = new HashMap<>();
        valMap.put("isActive", false);

        UpdateRecordBuilder<PlannedMaintenance> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.module(pmPlannerModule)
                .fields(Collections.singletonList(fieldMap.get("isActive")))
                .andCondition(CriteriaAPI.getIdCondition(pmId, pmPlannerModule))
                .updateViaMap(valMap);

        return false;
    }
}
