package com.facilio.qa.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
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
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class SerializeAnswersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(answers)) {
            boolean isSingleResponse = (boolean) context.getOrDefault(FacilioConstants.QAndA.Command.IS_SINGLE_RESPONSE, false);
            Map<Long, QuestionContext> questions = getQuestionMap((FacilioContext) context, answers);
            List<ClientAnswerContext> clientAnswers = answers.stream().map(a -> serialze(a, questions, isSingleResponse)).collect(Collectors.toList());
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
    private ClientAnswerContext serialze(AnswerContext answer, Map<Long, QuestionContext> questions, boolean isSingleResponse) {
        QuestionContext question = questions.get(answer.getQuestion().getId());
        Map<String, Long> errorParams = new HashMap<>();
        errorParams.put("questionId", answer.getQuestion().getId());
        V3Util.throwRestException(question == null, ErrorCode.VALIDATION_ERROR,"errors.qa.serializeAnswersCommand.questionIdCheck",true,errorParams);
        //V3Util.throwRestException(question == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Question ID ({0}) is not present in given question map. This is not supposed to happen", answer.getQuestion().getId()),true,errorParams);
        answer.setQuestion(question);
        ClientAnswerContext clientAnswer = QAndAUtil.serializedAnswer(question,answer);

        if (isSingleResponse) {
            clientAnswer.setComments(answer.getComments()); // Here not doing any check with question because comments can be disabled after few responses have been added
        }
        else {
            clientAnswer.setResponseId(answer.getResponse().getId());
        }

        return clientAnswer;
    }

    private Map<Long, QuestionContext> fetchQuestions (Collection<AnswerContext> answers) throws Exception {
        List<Long> questionIds = answers.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
        Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);
        return questions;
    }
}
