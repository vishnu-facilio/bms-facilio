package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ClientAnswerContext;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConstructAnswerResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // For now not fetching/ deserializing and re using the already created client answer POJOs. If there's some issue we can fetch/ deserializs from answer context if needed
        Map<Long, AnswerContext> questionVsAnswer = (Map<Long, AnswerContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_VS_ANSWER);
        Objects.requireNonNull(questionVsAnswer, "Question Vs Answer cannot be null");
        List<ClientAnswerContext> answers = (List<ClientAnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
        Objects.requireNonNull(answers, "Client Answer list cannot be null");

        for (ClientAnswerContext answer : answers) {
            AnswerContext answerContext = questionVsAnswer.get(answer.getQuestion());
            answer.setId(answerContext._getId());
        }

        return false;
    }
}
