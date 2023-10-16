package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.RCAConditionScoreContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateRCAScoreConditionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newReadingRule = NewReadingRuleAPI.destructureRuleFromRecordMap(context).get(0);
        NewReadingRuleContext oldReadingRule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);

        for (RCAGroupContext group : newReadingRule.getRca().getGroups()) {
            List<RCAGroupContext> oldGroups = oldReadingRule.getRca() != null ? oldReadingRule.getRca().getGroups().stream().filter(x -> x.getId() == group.getId()).collect(Collectors.toList()) : null;

            if(CollectionUtils.isNotEmpty(oldGroups)) { // if this is not a new group
                RCAGroupContext oldGroup = oldGroups.get(0);

                List<Long> oldScoreConditionIds = new ArrayList<>(oldGroup.getConditions()).stream().map(x -> x.getId()).collect(Collectors.toList());
                List<Long> newScoreConditionIds = new ArrayList<>(group.getConditions()).stream().map(x -> x.getId()).collect(Collectors.toList());
                List<Long> oldConditionCriteriaIds = new ArrayList<>(oldGroup.getConditions()).stream().map(x -> x.getCriteriaId()).collect(Collectors.toList());

                List<Long> intersection = ReadingRuleRcaAPI.removeIntersection(newScoreConditionIds, oldScoreConditionIds);
                group.getConditions().forEach(x -> {
                    if (intersection.contains(x.getId())) {
                        try {
                            ReadingRuleRcaAPI.updateRcaScoreCondition(x);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                List<RCAConditionScoreContext> newRcaScoreConditions = new ArrayList<>(group.getConditions()).stream().filter(x -> newScoreConditionIds.contains(x.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(newRcaScoreConditions)) {
                    CriteriaAPI.batchDeleteCriteria(oldConditionCriteriaIds);
                    for (RCAConditionScoreContext rcaCond : newRcaScoreConditions) {
                        ReadingRuleRcaAPI.setModuleNameForCriteria(rcaCond.getCriteria());
                        Long criteriaId = ReadingRuleRcaAPI.createCriteria(rcaCond.getCriteria());
                        rcaCond.setCriteriaId(criteriaId);
                        rcaCond.setGroupId(group.getId());
                    }
                    ReadingRuleRcaAPI.insertRcaScoreConditions(newRcaScoreConditions);
                }

                List<RCAConditionScoreContext> oldRcaScoreConditions = new ArrayList<>(oldGroup.getConditions()).stream().filter(x -> oldScoreConditionIds.contains(x.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(oldRcaScoreConditions)) {
                    ReadingRuleRcaAPI.deleteRcaScoreCondition(oldRcaScoreConditions);

                }
            } else {
                ReadingRuleRcaAPI.insertRcaScoreConditionsForGroup(group);
            }
        }
        return false;
    }
}
