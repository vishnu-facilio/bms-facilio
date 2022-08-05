package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateShiftsUsageCommand extends FacilioCommand {


    private boolean shiftUsageBreach(long id) throws Exception {

        Condition shiftCriteria = CriteriaAPI.getCondition("SHIFTID", "shiftId",
                String.valueOf(id), NumberOperators.EQUALS);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getShiftUserRelModule().getTableName())
                .select(FieldFactory.getShiftUserRelModuleFields())
                .andCondition(shiftCriteria)
                .orderBy("START_TIME");
        List<Map<String, Object>> list = selectBuilder.get();

        return CollectionUtils.isNotEmpty(list);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> shiftIDs = (List<Long>) context.get("recordIds");
        for (long id : shiftIDs) {
            if (shiftUsageBreach(id)) {
                throw new IllegalArgumentException("Shift is associated with employees. Remove employees before delete");
            }
        }

        return false;
    }

}
