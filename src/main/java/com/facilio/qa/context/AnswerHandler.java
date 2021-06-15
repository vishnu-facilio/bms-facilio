package com.facilio.qa.context;

import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.qa.rules.pojo.ScoringRuleCondition;
import com.facilio.time.DateRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class AnswerHandler<A extends ClientAnswerContext> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Class<A> answerClass;

    public abstract A serialize (AnswerContext answer) throws Exception;
    public abstract AnswerContext deSerialize(A answer, QuestionContext question) throws Exception; // Validation can be done in this method itself
    public abstract boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception;

    public boolean checkIfAnswerIsNull (A answer, QuestionContext question) throws Exception {
        return answer.getAnswer() == null;
    }

    public void setSummaryOfResponses(Long parentId, List<QuestionContext> questions, DateRange range) throws Exception {

    }

    public int computeFullScore (List<ScoringRuleCondition> conditions) {
        return ScoringRule.computeMaxScore(conditions);
    }

}
