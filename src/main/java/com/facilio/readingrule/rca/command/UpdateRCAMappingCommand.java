package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class UpdateRCAMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newReadingRule = NewReadingRuleAPI.destructureRuleFromRecordMap(context).get(0);
        NewReadingRuleContext oldReadingRule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);

        ReadingRuleRCAContext rca = newReadingRule.getRca();
        List<Long> newRcaRuleIds=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(rca.getRcaRuleIds())) {
            newRcaRuleIds.addAll(rca.getRcaRuleIds());
        }
        List<Long> oldRcaRuleIds = oldReadingRule.getRca() != null ? oldReadingRule.getRca().getRcaRuleIds() : new ArrayList<>();

        ReadingRuleRcaAPI.removeIntersection(newRcaRuleIds, oldRcaRuleIds);

        if (CollectionUtils.isNotEmpty(newRcaRuleIds)) {
            ReadingRuleRcaAPI.insertRcaMapping(ReadingRuleRcaAPI.getRcaMappingProps(rca.getId(), newRcaRuleIds));
        }
        if (CollectionUtils.isNotEmpty(oldRcaRuleIds)) {
            ReadingRuleRcaAPI.deleteRcaMapping(rca.getId(), oldRcaRuleIds);
        }

        return false;
}

}
