package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.PositionUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;

public class ValidateQuestionUpdateAndUpdatePos extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuestionContext> list = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(list)) {
            List<QuestionContext> positionToBeUpdated = new ArrayList<>();
            Map<Long, QuestionContext> oldQuestionsMap = Constants.getOldRecordMap(context, FacilioConstants.QAndA.QUESTION);
            for (QuestionContext question : list) {
                QuestionContext oldQuestion = oldQuestionsMap.get(question.getId());
                V3Util.throwRestException(question.getParent() != null && question.getParent().getId() > 0 && question.getParent().getId() != question.getParent().getId(), ErrorCode.VALIDATION_ERROR, "Cannot update parent template of question");
                if ((question.getPosition() != null && question.getPosition() != oldQuestion.getPosition()) ||
                        (question.getPage() != null && question.getPage().getId() > 0 && question.getPage().getId() != oldQuestion.getPage().getId())
                ) {
                    V3Util.throwRestException(question.getPosition() <= 0, ErrorCode.VALIDATION_ERROR, "Invalid position for question");
                    positionToBeUpdated.add(question);
                }
            }

            if (CollectionUtils.isNotEmpty(positionToBeUpdated)) {
                Map<Long, List<QuestionContext>> pageVsQuestions = new HashMap<>();
                for (QuestionContext question : positionToBeUpdated) {
                    pageVsQuestions.computeIfAbsent(question.getPage().getId(), k -> new ArrayList<>()).add(question);
                }

                if (!pageVsQuestions.isEmpty()) {
                    validatePage(positionToBeUpdated, oldQuestionsMap, pageVsQuestions.keySet());

                    PositionUtil.computeAndUpdatePosition(FacilioConstants.QAndA.QUESTION,
                            "page",
                            "position",
                            QuestionContext.class,
                            pageVsQuestions,
                            QuestionContext::getPosition,
                            QuestionContext::setPosition,
                            q -> q.getPage().getId(),
                            true,
                            (q, parentId) -> q.setPage(new PageContext(parentId)),
                            oldQuestionsMap
                    );
                }
            }
            QAndAUtil.splitAndAddQuestionModules((FacilioContext) context, list);
        }
        return false;
    }

    private void validatePage(List<QuestionContext> questions, Map<Long, QuestionContext> oldRecordMap, Collection<Long> pageIds) throws Exception {
        Map<Long, PageContext> pages = V3RecordAPI.getRecordsMap(FacilioConstants.QAndA.PAGE, pageIds);
        for (QuestionContext question : questions) {
            if (question.getPage() != null && question.getPage().getId() > 0) {
                PageContext page = pages.get(question.getPage().getId());
                V3Util.throwRestException(page == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid page ({0}) specified for question ({1})", question.getPage().getId(), question.getId()));
                V3Util.throwRestException(question.getParent().getId() != page.getParent().getId(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid page ({0}) from another template ({1}) specified for question ({2})", page.getId(), page.getParent().getId(), question.getId()));
            }
        }
    }
}
