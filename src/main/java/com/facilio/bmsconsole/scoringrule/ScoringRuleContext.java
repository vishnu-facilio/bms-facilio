package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.trigger.context.ScoringRuleTrigger;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            scoreContext.setScore(totalScore);
            ScoringRuleAPI.addOrUpdateScoreRecord(scoreModule, scoreContext);

                // update only when the values are changed..
            if (updateParent) {
                LOGGER.debug("Should update parent score also");
//                    ScoringRuleAPI.updateParentScores(moduleRecord, scoreFieldId, !(Objects.equals(value, totalScore)));
            }
//            }
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
