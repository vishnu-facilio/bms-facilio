package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.ResponseContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.ExtendedModuleUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.Collections;

public class ValidateResponseStatus extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
        FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(answerData), "Data cannot be null for add or updating answers");
        Long responseId = (Long) answerData.get("response");
        FacilioUtil.throwIllegalArgumentException(responseId == null, "Response cannot be null while adding answers");
        ResponseContext response = V3RecordAPI.getRecord(FacilioConstants.QAndA.RESPONSE, responseId, ResponseContext.class);
        FacilioUtil.throwIllegalArgumentException(response == null, "Invalid response specified while adding/ updating answers");
        FacilioUtil.throwIllegalArgumentException(response.getResStatus() == ResponseContext.ResponseStatus.DISABLED, MessageFormat.format("Cannot add/ update answers to the given response ({0}) as it's in disabled state", responseId));

        // TODO Check if record is locked
        response = ExtendedModuleUtil.fetchExtendedRecord(response, r -> r.getQAndAType().getResponseModule());
        context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(response));
        context.put(FacilioConstants.QAndA.RESPONSE, response);
        return false;
    }
}
