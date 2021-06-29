package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.ExtendedModuleUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class FetchResponsesForStatusUpdateOnAnswerDelete extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);
        if (CollectionUtils.isNotEmpty(recordIds)) {
            List<AnswerContext> answers = V3RecordAPI.getRecordsList(FacilioConstants.QAndA.ANSWER, recordIds);
            V3Util.throwRestException(CollectionUtils.isEmpty(answers) || recordIds.size() != answers.size(), ErrorCode.VALIDATION_ERROR, "Invalid answer ID given for deletion");

            List<Long> responseIds = answers.stream().map(AnswerContext::getResponseId).collect(Collectors.toList());
            List<ResponseContext> responses = V3RecordAPI.getRecordsList(FacilioConstants.QAndA.RESPONSE, responseIds);
            FacilioUtil.throwRunTimeException(CollectionUtils.isEmpty(responses), "Responses cannot be empty while deleting answer");
            ExtendedModuleUtil.replaceWithExtendedRecords(responses, r -> r.getQAndAType().getResponseModule());
            context.put(FacilioConstants.QAndA.Command.RESPONSE_LIST, responses);
        }
        return false;
    }
}
