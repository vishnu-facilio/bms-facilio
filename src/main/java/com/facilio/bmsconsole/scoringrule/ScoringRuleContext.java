package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.*;

public class ScoringRuleContext extends WorkflowRuleContext {

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

    private List<BaseScoringContext> baseScoringContexts;
    public List<BaseScoringContext> getBaseScoringContexts() {
        return baseScoringContexts;
    }
    public void setBaseScoringContexts(List<BaseScoringContext> baseScoringContexts) {
        this.baseScoringContexts = baseScoringContexts;
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

            List<Map<String, Object>> scores = new ArrayList<>();
            float totalScore = 0f;
            for (BaseScoringContext scoringContext : baseScoringContexts) {
                float score = scoringContext.getScore(record, context, placeHolders);
                totalScore += score;

                Map<String, Object> map = new HashMap<>();
                map.put("recordId", moduleRecord.getId());
                map.put("recordModuleId", moduleRecord.getModuleId());
                map.put("score", score);
                map.put("baseScoreId", scoringContext.getId());
                scores.add(map);
            }
            ScoringRuleAPI.addActualScore(scores, moduleRecord.getId(), moduleRecord.getModuleId());

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
                ScoringRuleAPI.updateParentScores(moduleRecord, scoreFieldId, !(Objects.equals(value, totalScore)));
            }
        }
    }
}
