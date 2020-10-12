package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.relations.BaseRelationContext;
import com.facilio.modules.fields.relations.RelationFieldUtil;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class CreateDependencyHistoryCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long fieldDependencyId = (Long) context.get(FacilioConstants.ContextNames.FIELD_DEPENDENCY_ID);

        if (assetId == null) {
            throw new IllegalArgumentException("Asset id cannot be empty");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time or end time cannot be empty");
        }
        BaseRelationContext fieldRelation = RelationFieldUtil.getFieldRelation(fieldDependencyId);
        if (fieldRelation == null) {
            throw new IllegalArgumentException("Field Relation cannot be empty");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("assetId", assetId);
        map.put("fieldRelationId", fieldDependencyId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        long l = RelationFieldUtil.addDependencyJobDetail(map);

        FacilioTimer.scheduleOneTimeJobWithDelay(l, "runDependencyFieldHistory", 10, "history");
        return false;
    }
}
