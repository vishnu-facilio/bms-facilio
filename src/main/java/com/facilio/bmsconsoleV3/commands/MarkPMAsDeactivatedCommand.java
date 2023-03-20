package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class MarkPMAsDeactivatedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("plannedmaintenance");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("plannedmaintenance");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        Long pmId = (Long) context.get("pmId");
        List<Long> pmIds = new ArrayList<>();
        if (pmId != null) {
            pmIds.add(pmId);
        } else if(context.containsKey("pmIds")){
            pmIds = (List<Long>) context.get("pmIds");
        }
        else if(context.containsKey(FacilioConstants.ContextNames.RECORD_ID_LIST)){
            pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        }

        if (CollectionUtils.isEmpty(pmIds)) {
            throw new IllegalArgumentException("Pm ids cannot be empty");
        }

        Map<String, Object> valMap = new HashMap<>();
        valMap.put("pmStatus", PlannedMaintenance.PMStatus.IN_ACTIVE.getVal());

        UpdateRecordBuilder<PlannedMaintenance> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.module(pmPlannerModule)
                .fields(Collections.singletonList(fieldMap.get("pmStatus")))
                .andCondition(CriteriaAPI.getIdCondition(pmIds, pmPlannerModule))
                .updateViaMap(valMap);

        return false;
    }
}
