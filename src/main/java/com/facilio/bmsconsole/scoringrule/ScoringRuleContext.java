package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.ScoringRuleTrigger;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScoringRuleContext extends WorkflowRuleContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoringRuleContext.class.getSimpleName());

    private List<ScoringCommitmentContext> scoringCommitmentContexts;
    public List<ScoringCommitmentContext> getScoringCommitmentContexts() {
        return scoringCommitmentContexts;
    }
    public void setScoringCommitmentContexts(List<ScoringCommitmentContext> scoringCommitmentContexts) {
        this.scoringCommitmentContexts = scoringCommitmentContexts;
    }

    private List<ScoringRuleTrigger> triggersToBeExecuted;
    public List<ScoringRuleTrigger> getTriggersToBeExecuted() {
        return triggersToBeExecuted;
    }
    public void setTriggersToBeExecuted(List<ScoringRuleTrigger> triggersToBeExecuted) {
        this.triggersToBeExecuted = triggersToBeExecuted;
    }

    private ScoreType scoreType;
    public int getScoreType() {
        if (scoreType != null) {
            return scoreType.getIndex();
        }
        return -1;
    }
    public void setScoreType(int type) {
        scoreType = ScoreType.valueOf(type);
    }
    public ScoreType getScoreTypeEnum() {
        return scoreType;
    }
    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    private int scoreRange = -1;
    public int getScoreRange() {
        return scoreRange;
    }
    public void setScoreRange(int scoreRange) {
        this.scoreRange = scoreRange;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        return super.evaluateMisc(moduleName, record, placeHolders, context);
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (record instanceof ModuleBaseWithCustomFields) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

            float totalScore = 0f;
            boolean updateParent = false;
            for (ScoringCommitmentContext scoringCommitmentContext : scoringCommitmentContexts) {
                if (scoringCommitmentContext.evaluate(record, context, placeHolders)) {
                    for (BaseScoringContext scoringContext : scoringCommitmentContext.getBaseScoringContexts()) {
                        if (!updateParent) {
                            updateParent = scoringContext.shouldUpdateParent();
                        }
                        float score = scoringContext.getScore(record, context, placeHolders, getModuleId());
                        totalScore += score;
                    }
                    break;
                }
            }

            // todo add data to scoring sub module
            FacilioModule scoreModule = ScoringRuleAPI.getScoreModule(getModuleId());
            ScoreContext scoreContext = ScoringRuleAPI.getScoreRecord(scoreModule, getId(), moduleRecord);
            if (scoreContext == null) {
                scoreContext = new ScoreContext();
                scoreContext.setScoreRuleId(getId());
                scoreContext.setParent(moduleRecord);
            }
            float oldScore = scoreContext.getScore();
            if (oldScore != totalScore) {
                scoreContext.setScore(totalScore);
                ScoringRuleAPI.addOrUpdateScoreRecord(scoreModule, scoreContext);

                List<Map<String, Object>> triggersToBeExecuted = ScoringRuleAPI.getTriggersToBeExecuted(getId());
                if (CollectionUtils.isNotEmpty(triggersToBeExecuted)) {
                    List<Long> nodeScoreIds = triggersToBeExecuted.stream().map(map -> (long) map.get("nodeScoringId")).collect(Collectors.toList());
                    List<BaseScoringContext> baseScoringContexts = ScoringRuleAPI.getBaseScoringContexts(nodeScoreIds);
                    Map<Long, BaseScoringContext> scoringMap = baseScoringContexts.stream().collect(Collectors.toMap(BaseScoringContext::getId, Function.identity()));

                    List<Long> triggerIds = triggersToBeExecuted.stream().map(map -> (long) map.get("triggerId")).collect(Collectors.toList());
                    List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(triggerIds);
                    Map<Long, BaseTriggerContext> triggerMap = triggers.stream().collect(Collectors.toMap(BaseTriggerContext::getId, Function.identity()));

                    for (Map<String, Object> triggerToBeExecuted : triggersToBeExecuted) {
                        NodeScoringContext nodeScoring = (NodeScoringContext) scoringMap.get(triggerToBeExecuted.get("nodeScoringId"));
                        if (nodeScoring == null) {
                            continue;
                        }
                        if (nodeScoring.getNodeTypeEnum() == NodeScoringContext.NodeType.CURRENT_MODULE) {
                            // no need to trigger, and trigger won't be added to this type
                            continue;
                        }
                        long triggerId = (long) triggerToBeExecuted.get("triggerId");
                        BaseTriggerContext triggerContext = triggerMap.get(triggerId);
                        if (triggerContext == null) {
                            continue;
                        }

                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = modBean.getModule(triggerContext.getModuleId());
                        if (module == null) {
                            continue;
                        }

                        List<? extends ModuleBaseWithCustomFields> records = null;
                        switch (nodeScoring.getNodeTypeEnum()) {
//                            case PARENT_MODULE: {
//                                SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
//                                        .module(module)
//                                        .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
//                                        .select(modBean.getAllFields(module.getName()))
//                                        .andCondition(CriteriaAPI.getCondition(modBean.getField(nodeScoring.getFieldId(), nodeScoring.getFieldModuleId()), String.valueOf(moduleRecord.getId()), NumberOperators.EQUALS));
//                                records = builder.get();
//                                break;
//                            }
                            case SUB_MODULE:{
                                Object value = FieldUtil.getValue(moduleRecord, nodeScoring.getFieldId(), getModule());
                                if (value instanceof ModuleBaseWithCustomFields) {
                                    records = Collections.singletonList((ModuleBaseWithCustomFields) value);
                                }
                            }
                        }

                        if (CollectionUtils.isNotEmpty(records)) {
                            FacilioChain triggerExecuteChain = TransactionChainFactoryV3.getTriggerExecuteChain();
                            FacilioContext triggerExecutionContext = triggerExecuteChain.getContext();
                            triggerExecutionContext.put(FacilioConstants.ContextNames.ID, triggerId);
                            triggerExecutionContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.INVOKE_TRIGGER);
                            triggerExecutionContext.put(FacilioConstants.ContextNames.RECORD_LIST, records);
                            triggerExecuteChain.execute();
                        }
                    }
                }

                // update only when the values are changed..
                if (updateParent) {
                    LOGGER.debug("Should update parent score also");
//                    ScoringRuleAPI.updateParentScores(moduleRecord, scoreFieldId, !(Objects.equals(value, totalScore)));
                }
            }
        }
    }

    public enum ScoreType implements FacilioEnum {
        PERCENTAGE("Percentage"),
        RANGE("Range"),
        ;

        private String name;

        ScoreType(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public static ScoreType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
