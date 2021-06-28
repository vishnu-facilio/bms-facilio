package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchQuestionsAndAnswersOfResponse extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
        Long templateId = Objects.requireNonNull(response, "Response cannot be null for on submit process").getParent().getId();
        resetScore(response);

        List<QuestionContext> questions = QAndAUtil.getQuestionsFromTemplate(templateId);
        context.put(FacilioConstants.QAndA.Command.TEMPLATE_ID, templateId);
        if (CollectionUtils.isNotEmpty(questions)) {
            Map<Long, QuestionContext> questionMap = questions.stream().collect(Collectors.toMap(QuestionContext::_getId, Function.identity()));
            List<AnswerContext> answers = QAndAUtil.getAnswersFromTemplateAndResponse(templateId, response.getId());
            resetScores(answers);

            context.put(FacilioConstants.QAndA.Command.QUESTION_LIST, questions);
            context.put(FacilioConstants.QAndA.Command.QUESTION_MAP, questionMap);
            context.put(FacilioConstants.QAndA.Command.ANSWER_LIST, answers);
        }
        return false;
    }

    private void resetScores (List<AnswerContext> answers) {
        if (CollectionUtils.isNotEmpty(answers)) {
            answers.forEach(this::resetScores);
        }
    }

    private void resetScores (AnswerContext answer) {
        answer.setScore(null);
        answer.setFullScore(null);
        answer.setScorePercent(null);
    }

    private void resetScore (ResponseContext response) {
        response.setFullScore(null);
        response.setTotalScore(null);
        response.setScorePercent(null);
    }
}
