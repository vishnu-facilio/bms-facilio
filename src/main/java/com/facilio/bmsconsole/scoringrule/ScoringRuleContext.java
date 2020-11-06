package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.ScoreField;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ScoringRuleContext extends WorkflowRuleContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoringRuleContext.class.getSimpleName());

    private long scoreFieldId = -1;
    public long getScoreFieldId() {
        return scoreFieldId;
    }
    public void setScoreFieldId(long scoreFieldId) {
        this.scoreFieldId = scoreFieldId;
    }

    private FacilioField scoreField;
    public FacilioField getScoreField() {
        if (scoreField == null) {
            if (scoreFieldId > 0) {
                try {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    scoreField = modBean.getField(scoreFieldId, getModuleId());
                } catch (Exception ex) {}
            }
        }
        return scoreField;
    }
    public void setScoreField(FacilioField scoreField) {
        this.scoreField = scoreField;
        if (scoreField != null) {
            this.scoreFieldId = scoreField.getFieldId();
        }
    }

    private List<ScoringCommitmentContext> scoringCommitmentContexts;
    public List<ScoringCommitmentContext> getScoringCommitmentContexts() {
        return scoringCommitmentContexts;
    }
    public void setScoringCommitmentContexts(List<ScoringCommitmentContext> scoringCommitmentContexts) {
        this.scoringCommitmentContexts = scoringCommitmentContexts;
    }

    private Boolean draft;
    public Boolean getDraft() {
        return draft;
    }
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }
    public Boolean isDraft() {
        return draft == null || draft;
    }

    private ScoreField.Type scoreType;
    public int getScoreType() {
        if (scoreType != null) {
            return scoreType.getIndex();
        }
        return -1;
    }
    public void setScoreType(int type) {
        scoreType = ScoreField.Type.valueOf(type);
    }
    public ScoreField.Type getScoreTypeEnum() {
        return scoreType;
    }
    public void setScoreType(ScoreField.Type scoreType) {
        this.scoreType = scoreType;
    }

    private int scoreRange = -1;
    public int getScoreRange() {
        return scoreRange;
    }
    public void setScoreRange(int scoreRange) {
        this.scoreRange = scoreRange;
    }

    private String scoreFieldName;
    public String getScoreFieldName() {
        return scoreFieldName;
    }
    public void setScoreFieldName(String scoreFieldName) {
        this.scoreFieldName = scoreFieldName;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (isDraft()) {
            return false;
        }
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
                        float score = scoringContext.getScore(record, context, placeHolders);
                        totalScore += score;
                    }
                    break;
                }
            }

            System.out.println("Total score: " + totalScore);
            FacilioField scoreField = getScoreField();
            if (scoreField == null) {
                throw new IllegalArgumentException("Score Field cannot be empty");
            }
            else {
                Object value = FieldUtil.getValue(moduleRecord, scoreField);
                FieldUtil.setValue(moduleRecord, scoreField, totalScore);
                UpdateRecordBuilder<ModuleBaseWithCustomFields> builder = new UpdateRecordBuilder<>()
                        .module(getModule())
                        .fields(Collections.singletonList(scoreField))
                        .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), getModule()));
                builder.update(moduleRecord);

                // update only when the values are changed..
                if (updateParent) {
                    LOGGER.debug("Should update parent score also");
//                    ScoringRuleAPI.updateParentScores(moduleRecord, scoreFieldId, !(Objects.equals(value, totalScore)));
                }
            }
        }
    }

//    public enum ScoreType implements FacilioEnum {
//        PERCENTAGE("Percentage"),
//        RANGE("Range"),
//        ;
//
//        private String name;
//
//        ScoreType(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public int getIndex() {
//            return ordinal() + 1;
//        }
//
//        public static ScoreType valueOf(int type) {
//            if (type > 0 && type <= values().length) {
//                return values()[type - 1];
//            }
//            return null;
//        }
//
//        @Override
//        public String getValue() {
//            return name;
//        }
//    }
}
