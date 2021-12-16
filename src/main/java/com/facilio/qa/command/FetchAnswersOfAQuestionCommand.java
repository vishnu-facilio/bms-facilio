package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class FetchAnswersOfAQuestionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long questionId = (long) context.get(FacilioConstants.QAndA.Command.QUESTION_ID);
        V3Util.throwRestException(questionId <= 0, ErrorCode.VALIDATION_ERROR, "Invalid question id for fetching answers");
        DateRange range = (DateRange) context.get(FacilioConstants.QAndA.Command.ANSWER_RANGE);
        V3Util.throwRestException(range.getStartTime() <= 0 || range.getEndTime() <= 0 || range.getEndTime() < range.getStartTime(), ErrorCode.VALIDATION_ERROR, "Invalid startTime/ endTime specified for fetching answers");
        QuestionContext question = QAndAUtil.fetchQuestionWithProps(questionId);
        V3Util.throwRestException(question == null, ErrorCode.VALIDATION_ERROR, "Invalid question id for fetching answers");

        QAndAUtil.populateAnswersForQuestions(Collections.singletonMap(questionId, question), getCriteria(question.getParent().getId(), range), false);
        context.put(FacilioConstants.QAndA.Command.CLIENT_ANSWER_LIST, question.getAnswers());

        return false;
    }

    private Criteria getCriteria (long parentId, DateRange range) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.ANSWER));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), String.valueOf(parentId), PickListOperators.IS));

        Criteria lookupCriteria = new Criteria();
        Map<String, FacilioField> responseFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.RESPONSE));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(responseFieldMap.get("template"), String.valueOf(parentId), PickListOperators.IS));
        lookupCriteria.addAndCondition(CriteriaAPI.getCondition(responseFieldMap.get("sysModifiedTime"), range.toString(), DateOperators.BETWEEN));

        FacilioField responseField = fieldMap.get("response");
        criteria.addAndCondition(CriteriaAPI.getCondition(responseField, lookupCriteria, LookupOperator.LOOKUP));

        return criteria;
    }
}
