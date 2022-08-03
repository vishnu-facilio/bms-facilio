package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.Break;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class RemoveBreakShiftRelationshipCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        if (isDeleteEvent(context)) {
            List<Long> shiftIDs = (List<Long>) context.get("recordIds");
            for (long id : shiftIDs) {
                removeBreakShiftRelationship(id);
            }
            return false;
        }

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<Break> breaks = recordMap.get(moduleName);

        if (FacilioUtil.isEmptyOrNull(breaks)) {
            return false;
        }

        for (Break br : breaks) {
            if (FacilioUtil.isEmptyOrNull(br.getShifts())) {
                continue;
            }
            removeBreakShiftRelationship(br.getId());
        }
        return false;
    }

    private boolean isDeleteEvent(Context context) {
        if (context.get("eventType") == null) {
            return false;
        }
        return context.get("eventType").equals(EventType.DELETE);
    }

    private void removeBreakShiftRelationship(long brID) throws Exception {
        Condition idCriteria = CriteriaAPI.getCondition("BREAK_ID",
                "breakId", String.valueOf(brID), NumberOperators.EQUALS);

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getShiftBreakRelModule().getTableName())
                .andCondition(idCriteria);
        builder.delete();
    }
}
