package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (record instanceof WorkOrderContext) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

            float totalScore = 0f;
            for (BaseScoringContext scoringContext : baseScoringContexts) {
                totalScore += scoringContext.getScore(record, context, placeHolders);
            }
            System.out.println("Total score: " + totalScore);
            FacilioField scoreField = getScoreField();
            if (scoreField == null) {
                throw new IllegalArgumentException("Score Field cannot be empty");
            }
            else {
                FieldUtil.setValue(moduleRecord, scoreField, totalScore);
                UpdateRecordBuilder<ModuleBaseWithCustomFields> builder = new UpdateRecordBuilder<>()
                        .module(getModule())
                        .fields(Collections.singletonList(scoreField))
                        .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), getModule()));
                builder.update(moduleRecord);
            }
        }
    }
}
