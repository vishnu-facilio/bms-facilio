package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.Predicate;

import java.util.Map;

@Getter
@Setter
@Log4j
public abstract class RuleCondition {
    private Long id;
    private Long ruleId;
    private Long criteriaId;
    private Criteria criteria;
    private Integer sequence;
    private Long sysCreatedTime;
    private Long sysCreatedBy;
    private Long sysModifiedTime;
    private Long sysModifiedBy;
	private Long rowId;
	private Long columnId;

    public Integer getOperator() {
        return operatorEnum == null ? null : operatorEnum.getOperatorId();
    }
    public void setOperator(Integer operator) {
        this.operatorEnum = operator == null ? null : Operator.getOperator(operator);
    }
    private String value;

    public boolean hasCriteria() {
        return criteriaId != null;
    }

    @JsonIgnore
    private Operator operatorEnum;
    public static final String ANSWER_FIELD_NAME = "answer";

    public abstract boolean hasAction();
    public boolean actionIsEmpty() {
        return !hasAction();
    }

    public boolean evaluate (QuestionContext question, Map<String, Object> answerProp) {
        return evalMisc(this,answerProp,question) && evaluateOperatorValue(answerProp) && evaluateCriteria(answerProp);
    }

	private boolean evalMisc(RuleCondition ruleCondition, Map<String, Object> answerProp, QuestionContext question){

		RuleHandler ruleHandler = question.getQuestionType().getRuleHandler();
		boolean val =  ruleHandler.evalMisc(ruleCondition, answerProp);
        LOGGER.info("#### Multi question evalMisc... result  : "+ val);
        return val;
	}


	private boolean evaluateOperatorValue(Map<String, Object> answerProp) {
        if (operatorEnum == null) {
            return true;
        }
        Condition condition = CriteriaAPI.getCondition(ANSWER_FIELD_NAME, value, operatorEnum);
        Predicate predicate = condition.computePredicate();
        boolean val =  predicate == null ? false : predicate.evaluate(answerProp);
        LOGGER.info("#### Multi question evaluateOperatorValue... result  : "+ val);
        return val;
    }

    private boolean evaluateCriteria (Map<String, Object> answerProp) {

        boolean val = criteria == null ? true : criteria.computePredicate().evaluate(answerProp);
        LOGGER.info("#### Multi question evaluateCriteria... result  : "+ val);
        return val;
    }

    public void copyDefaultProps (RuleCondition newCondition) {
        newCondition.id = this.id;
        newCondition.sysCreatedTime = this.sysCreatedTime;
        newCondition.sysCreatedBy = this.sysCreatedBy;
        newCondition.sysModifiedTime = this.sysModifiedTime;
        newCondition.sysModifiedBy = this.sysModifiedBy;
    }

    public abstract void executeTrueAction (QuestionContext question, AnswerContext answer) throws Exception;

    // Add false action if needed later. Mostly shouldn't be needed
}
