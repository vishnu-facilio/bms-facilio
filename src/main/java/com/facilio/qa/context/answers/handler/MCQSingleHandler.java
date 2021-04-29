package com.facilio.qa.context.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.answers.MCQSingleAnswerContext;
import com.facilio.qa.context.questions.MCQSingleContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;

public class MCQSingleHandler extends AnswerHandler<MCQSingleAnswerContext> {
    public MCQSingleHandler(Class<MCQSingleAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQSingleAnswerContext serialize(AnswerContext answer) {
        MCQSingleAnswerContext mcqSingleAnswer = new MCQSingleAnswerContext();
        MCQSingleAnswerContext.MCQAnswer mcqAnswer = new MCQSingleAnswerContext.MCQAnswer();
        mcqAnswer.setSelected(answer.getEnumAnswer());
        if ( StringUtils.isNotEmpty(((MCQSingleContext) answer.getQuestion()).getOtherOptionLabel()) ) {
            mcqAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqSingleAnswer.setAnswer(mcqAnswer);
        return mcqSingleAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQSingleAnswerContext answer, QuestionContext question) {
        boolean isOther = StringUtils.isNotEmpty(((MCQSingleContext) question).getOtherOptionLabel());
        FacilioUtil.throwIllegalArgumentException(answer.getAnswer().getSelected() == null
                                                    && (!isOther || StringUtils.isEmpty(answer.getAnswer().getOther()))
                                            , "At least one option need to be selected for MCQ");
        AnswerContext answerContext = new AnswerContext();
        answerContext.setEnumAnswer(answer.getAnswer().getSelected());
        if ( isOther ) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        else {
            answer.getAnswer().setOther(null);
        }
        return answerContext;
    }
}
