package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateResponseStateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> toBeAdded = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED);
        List<AnswerContext> toBeUpdated = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED);
        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);

        if (CollectionUtils.isNotEmpty(toBeAdded) || CollectionUtils.isNotEmpty(toBeUpdated)) {
            ResponseContext oldResponse = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            ResponseContext updatedResponse = FieldUtil.cloneBean(oldResponse, ResponseContext.class);
            updatedResponse.setResStatus(ResponseContext.ResponseStatus.PARTIALLY_ANSWERED);
            int rowsUpdated = QAndAUtil.updateRecordViaV3Chain(response.getQAndAType().getResponseModule(), updatedResponse, oldResponse);
        }
        return false;
    }
}
