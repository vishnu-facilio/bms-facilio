package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchAnswersForQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        Long responseId = getResponseId((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(pages) && responseId != null) {
            Map<Long, QuestionContext> questions = pages.stream().flatMap(this::getQuestions).collect(Collectors.toMap(QuestionContext::_getId, Function.identity()));
            QAndAUtil.fetchChildrenFromParent(questions.values(),
                                            FacilioConstants.QAndA.ANSWER,
                                            "question",
                                            null,
                                            ClientAnswerContext::getQuestion,
                                            ClientAnswerContext::addQuestionId,
                                            QuestionContext::setAnswer,
                                            null,
                                            getResponseCriteria(responseId),
                                            c -> addQuestionToFetchChain(c, questions));

        }
        return false;
    }

    private void addQuestionToFetchChain(FacilioContext context, Map<Long, QuestionContext> questions) {
        context.put(FacilioConstants.QAndA.Command.QUESTION_MAP, questions);
    }

    private Long getResponseId (FacilioContext context) {
        Map<String, List<Object>> bodyParams = Constants.getQueryParams(context);
        List<Object> responseId = MapUtils.isEmpty(bodyParams) ? null : bodyParams.get("response");
        return CollectionUtils.isEmpty(responseId) ? null : FacilioUtil.parseLong(responseId.get(0));
    }

    private Stream<QuestionContext> getQuestions (PageContext page) {
        return (page.getQuestions() == null ? Collections.EMPTY_LIST : page.getQuestions()).stream();
    }

    private Criteria getResponseCriteria (Long responseId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("response", FacilioConstants.QAndA.ANSWER), responseId.toString(), PickListOperators.IS));
        return criteria;
    }
}
