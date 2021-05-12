package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.util.FacilioStreamUtil;
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
            Map<Long, QuestionContext> questions = pages.stream().flatMap(QAndAUtil::getQuestionStream).collect(Collectors.toMap(QuestionContext::_getId, Function.identity()));
            QAndAUtil.populateAnswersForQuestions(questions, getResponseCriteria(pages.get(0).getParent()._getId(), responseId), true);
        }
        return false;
    }

    private Long getResponseId (FacilioContext context) {
        Object responseId = Constants.getQueryParam(context, "response");
        return responseId == null ? null : FacilioUtil.parseLong(responseId);
    }

    private Criteria getResponseCriteria (Long parentId, Long responseId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.ANSWER));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), parentId.toString(), PickListOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("response"), responseId.toString(), PickListOperators.IS));
        return criteria;
    }
}
