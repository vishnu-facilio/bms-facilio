package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateAnswersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> answers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED);
        if (CollectionUtils.isNotEmpty(answers)) {
            ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            List<Long> ids = answers.stream().map(AnswerContext::getId).collect(Collectors.toList());
            Map<String, FacilioField> fields = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.QAndA.ANSWER));

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("parent"), String.valueOf(response.getParent().getId()), PickListOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("response"), String.valueOf(response.getId()), PickListOperators.IS));

            Map<Long, AnswerContext> oldRecords = V3RecordAPI.getRecordsMap(FacilioConstants.QAndA.ANSWER, ids, criteria);
            answers.stream().forEach(a -> setDefaultProps(a, oldRecords));

            FacilioModule module = ChainUtil.getModule(FacilioConstants.QAndA.ANSWER);
            V3Config v3Config = ChainUtil.getV3Config(module);
            Class beanClass = ChainUtil.getBeanClass(v3Config, module);

            FacilioChain patchChain = ChainUtil.getBulkPatchChain(module.getName());
            FacilioContext updateContext = patchChain.getContext();

            Constants.setV3config(updateContext, v3Config);
            Constants.setModuleName(updateContext, module.getName());
            updateContext.put(Constants.BEAN_CLASS, beanClass);
            updateContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
            updateContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(module.getName(), answers);
            updateContext.put(Constants.RECORD_MAP, recordMap);

            patchChain.execute();
        }
        return false;
    }

    @SneakyThrows
    private void setDefaultProps(AnswerContext answer, Map<Long, AnswerContext> oldRecords) {
        AnswerContext oldAnswer = oldRecords.get(answer.getId());
        V3Util.throwRestException(oldAnswer == null || oldAnswer.getQuestion().getId() != answer.getQuestion().getId()
                                    , ErrorCode.VALIDATION_ERROR, "errors.qa.updateAnswersCommand.answerIdCheck",true,null);
        //V3Util.throwRestException(oldAnswer == null || oldAnswer.getQuestion().getId() != answer.getQuestion().getId()
        //        , ErrorCode.VALIDATION_ERROR, "Invalid answer id sent while saving answer");

        answer.setSysCreatedBy(oldAnswer.getSysCreatedBy());
        answer.setSysCreatedTime(oldAnswer.getSysCreatedTime());
        // All other props are updated
    }
}
