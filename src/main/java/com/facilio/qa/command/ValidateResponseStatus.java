package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.ExtendedModuleUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.Collections;

public class ValidateResponseStatus extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Boolean isDisplyLogicExecutionOnPageLoad = (Boolean) context.getOrDefault(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, false);
        JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
        V3Util.throwRestException(MapUtils.isEmpty(answerData), ErrorCode.VALIDATION_ERROR, "Data cannot be null for add or updating answers");
        Long responseId = (Long) answerData.get("response");
        V3Util.throwRestException(responseId == null, ErrorCode.VALIDATION_ERROR, "Response cannot be null while adding answers");
        ResponseContext response = V3RecordAPI.getRecord(FacilioConstants.QAndA.RESPONSE, responseId, ResponseContext.class);
        V3Util.throwRestException(response == null, ErrorCode.VALIDATION_ERROR, "Invalid response specified while adding/ updating answers");
        V3Util.throwRestException(response.getResStatus() == ResponseContext.ResponseStatus.DISABLED, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Cannot add/ update answers to the given response ({0}) as it's in disabled state", responseId));

        // TODO Check if record is locked
        response = ExtendedModuleUtil.fetchExtendedRecord(response, r -> r.getQAndAType().getResponseModule());

//		QAndAUtil.validateResponseExpiry(response);

        QAndATemplateContext parent = V3RecordAPI.getRecord(response.getQAndAType().getTemplateModule(), response.getParent().getId(),null,true);
        V3Util.throwRestException(parent == null && !isDisplyLogicExecutionOnPageLoad, ErrorCode.VALIDATION_ERROR, "Cannot add/ update answer when the template is deleted");
        context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(response));
        context.put(FacilioConstants.QAndA.RESPONSE, response);
        return false;
    }
}
