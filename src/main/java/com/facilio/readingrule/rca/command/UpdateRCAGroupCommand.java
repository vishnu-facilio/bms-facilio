package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateRCAGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newReadingRule = NewReadingRuleAPI.destructureRuleFromRecordMap(context).get(0);
        NewReadingRuleContext oldReadingRule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);

        ReadingRuleRCAContext rcaContext = newReadingRule.getRca();
        List<Long> newGroupIds = rcaContext.getGroups().stream().map(x -> x.getId()).collect(Collectors.toList());
        List<Long> oldGroupIds = oldReadingRule.getRca() != null ? oldReadingRule.getRca().getGroups().stream().map(x -> x.getId()).collect(Collectors.toList()) : new ArrayList<>();

        List<Long> intersection = ReadingRuleRcaAPI.removeIntersection(newGroupIds, oldGroupIds);

        List<RCAGroupContext> newGroups = rcaContext.getGroups();
        List<RCAGroupContext> oldGroups = oldReadingRule.getRca() != null ? oldReadingRule.getRca().getGroups() : new ArrayList<>();
        for (Long groupId : intersection) {
            RCAGroupContext rcaGroupContext = new ArrayList<>(newGroups).stream().filter(x -> x.getId() == groupId).collect(Collectors.toList()).get(0);
            RCAGroupContext oldRcaGroupContext = new ArrayList<>(oldGroups).stream().filter(x -> x.getId() == groupId).collect(Collectors.toList()).get(0);
            rcaGroupContext.setCriteriaId(oldRcaGroupContext.getCriteriaId());
            ReadingRuleRcaAPI.updateCriteriaOfGroup(rcaGroupContext);
            ReadingRuleRcaAPI.updateRcaGroup(rcaGroupContext);
        }

        List<RCAGroupContext> newRcaGroups = new ArrayList<>(rcaContext.getGroups()).stream().filter(x -> newGroupIds.contains(x.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newRcaGroups)) {
            for (RCAGroupContext group : newRcaGroups) {
                ReadingRuleRcaAPI.prepareGroupForInsert(group, rcaContext.getId());
            }
            ReadingRuleRcaAPI.insertRcaGroups(newRcaGroups);
        }

        if (CollectionUtils.isNotEmpty(oldGroupIds)) {
            ReadingRuleRcaAPI.deleteRcaGroups(oldGroupIds);
        }
        return false;
    }
}
