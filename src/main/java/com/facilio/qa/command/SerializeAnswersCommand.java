package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SerializeAnswersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(answers)) {
            Map<Long, QuestionContext> questions = getQuestionMap((FacilioContext) context, answers);
            List<ClientAnswerContext> clientAnswers = answers.stream().map(a -> serialze(a, questions)).collect(Collectors.toList());
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            recordMap.put(FacilioConstants.QAndA.ANSWER, clientAnswers);
            Constants.setJsonRecordMap(context, FieldUtil.getAsJSON(recordMap));
        }

        return true; // To prevent subsequent commands from executing as they are not needed in this case
    }

    private Map<Long, QuestionContext> getQuestionMap (FacilioContext context, Collection<AnswerContext> answers) throws Exception {
        Map<Long, QuestionContext> questions = (Map<Long, QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_MAP);
        if (questions == null) {
            questions = fetchQuestions(answers);
        }
        return questions;
    }

    @SneakyThrows
    private ClientAnswerContext serialze (AnswerContext answer, Map<Long, QuestionContext> questions) {
        QuestionContext question = questions.get(answer.getQuestion()._getId());
        V3Util.throwRestException(question == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Question ID ({0}) is not present in given question map. This is not supposed to happen", answer.getQuestion()._getId()));
        answer.setQuestion(question);
        ClientAnswerContext clientAnswer = question.getQuestionType().getAnswerHandler().serialize(answer);
        clientAnswer.addQuestionId(question);
        clientAnswer.setId(answer._getId());

        return clientAnswer;
    }

    private Map<Long, QuestionContext> fetchQuestions (Collection<AnswerContext> answers) throws Exception {
        List<Long> questionIds = answers.stream().map(a -> a.getQuestion()._getId()).collect(Collectors.toList());
        Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);
        return questions;
    }
}
