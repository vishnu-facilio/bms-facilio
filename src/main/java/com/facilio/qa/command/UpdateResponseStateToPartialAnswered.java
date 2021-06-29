package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.ResponseContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateResponseStateToPartialAnswered extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ResponseContext> responses = (List<ResponseContext>) context.get(FacilioConstants.QAndA.Command.RESPONSE_LIST);
        if (CollectionUtils.isNotEmpty(responses)) {
            for (ResponseContext response : responses) {
                ResponseContext updatedResponse = FieldUtil.cloneBean(response, ResponseContext.class);
                updatedResponse.setResStatus(ResponseContext.ResponseStatus.PARTIALLY_ANSWERED);
                int rowsUpdated = QAndAUtil.updateRecordViaV3Chain(response.getQAndAType().getResponseModule(), updatedResponse, response);
            }
        }
        return false;
    }
}
