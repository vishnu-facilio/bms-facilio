package com.facilio.bmsconsole.workflow.rule.impact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.impact.util.AlarmImpactAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllAlarmImpactsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long assetCategoryId = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY_ID);
        if (assetCategoryId == null) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        List<BaseAlarmImpactContext> alarmImpacts = AlarmImpactAPI.getAllAlarmImpacts(assetCategoryId);
        context.put(FacilioConstants.ContextNames.ALARM_IMPACT_LIST, alarmImpacts);

        return false;
    }
}
