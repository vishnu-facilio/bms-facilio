package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SetResponseForStatusChange extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> toBeAdded = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED);
        List<AnswerContext> toBeUpdated = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED);
        ResponseContext response = Objects.requireNonNull((ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE), "Response cannot be null here");

        if (CollectionUtils.isNotEmpty(toBeAdded) || CollectionUtils.isNotEmpty(toBeUpdated)) {
            context.put(FacilioConstants.QAndA.Command.RESPONSE_LIST, Collections.singletonList(response));
        }
        return false;
    }
}
