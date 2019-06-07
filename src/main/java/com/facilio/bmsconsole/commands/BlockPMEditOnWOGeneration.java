package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockPMEditOnWOGeneration implements Command {

    private boolean isStatusChange;

    private boolean isBulkUpdate;
    BlockPMEditOnWOGeneration(boolean b, boolean isStatusChange) {
        this.isBulkUpdate = b;
        this.isStatusChange = isStatusChange;
    }

    BlockPMEditOnWOGeneration() {}

    @Override
    public boolean execute(Context context) throws Exception {
        List<PreventiveMaintenance> pms =  (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
        PreventiveMaintenance pm = (PreventiveMaintenance)  context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        List<Long> pmIds = new ArrayList<>();
        if (isStatusChange) {
            if (context.get(FacilioConstants.ContextNames.RECORD_ID_LIST) != null) {
                pmIds.addAll((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
            }
        } else {
            if (isBulkUpdate) {
                if (CollectionUtils.isNotEmpty(pms)) {
                    pms.forEach(i -> pmIds.add(i.getId()));
                }
            } else {
                if (pm != null && pm.getId() > 0) {
                    pmIds.add(pm.getId());
                }
            }
        }

        if (pmIds.isEmpty()) {
            throw new IllegalStateException("No PMs in list");
        }

        FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
        List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(Arrays.asList(fieldMap.get("woGenerationStatus")))
                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), pmIds, NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop: props) {
                if ((Boolean) prop.get("woGenerationStatus")) {
                    throw new IllegalStateException("The PPMs cannot be edited right now.");
                }
            }
        }
        return false;
    }
}
