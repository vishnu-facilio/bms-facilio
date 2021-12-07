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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateQuestionAddAndUpdatePos extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuestionContext> list = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long, List<QuestionContext>> pageVsQuestions = new HashMap<>();
            for (QuestionContext question : list) {
                Long pageId = question.getPage() == null ? null : question.getPage().getId();
                V3Util.throwRestException(pageId == null, ErrorCode.VALIDATION_ERROR, "Parent page of question cannot be null");
                V3Util.throwRestException(question.getQuestionType() == null, ErrorCode.VALIDATION_ERROR, "Question type cannot be null");
                V3Util.throwRestException(question.getPosition() == null || question.getPosition() <= 0, ErrorCode.VALIDATION_ERROR, "Invalid position for question");
                pageVsQuestions.computeIfAbsent(pageId, k -> new ArrayList<>()).add(question);
            }

            Map<Long, PageContext> pageMap = V3RecordAPI.getRecordsMap(FacilioConstants.QAndA.PAGE, pageVsQuestions.keySet(), PageContext.class);
            for (Map.Entry<Long, List<QuestionContext>> entry : pageVsQuestions.entrySet()) {
                PageContext parentPage = pageMap.get(entry.getKey());
                V3Util.throwRestException(parentPage == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid parent ({0}) specified for question", entry.getKey()));
                entry.getValue().stream().forEach(q -> q.setParent(parentPage.getParent()));
            }

            PositionUtil.computeAndUpdatePosition(FacilioConstants.QAndA.QUESTION,
                    "page",
                    "position",
                    QuestionContext.class,
                    pageVsQuestions,
                    QuestionContext::getPosition,
                    QuestionContext::setPosition,
                    q -> q.getPage().getId()
                    );

            QAndAUtil.splitAndAddQuestionModules((FacilioContext) context, list);
        }

        return false;
    }
}
