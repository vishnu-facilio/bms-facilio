package com.facilio.qa.command;

import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExecuteOnSubmitProcessOfResponse extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ResponseContext> responses = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(responses)) {
            Map<Long, ? extends ResponseContext> oldResponseMap = Constants.getOldRecordMap(context);
            Map<String, Long> errorParams = new HashMap<>();
            for (ResponseContext response : responses) {
                ResponseContext oldResponse = oldResponseMap.get(response.getId());
                errorParams.put("responseId", response.getId());
                V3Util.throwRestException(oldResponse == null, ErrorCode.VALIDATION_ERROR, "errors.qa.executeOnSubmitProcessOfResponse.actionValidation",true,errorParams);
                V3Util.throwRestException(response.getParent() != null && response.getParent().getId() > 0 && response.getParent().getId() != oldResponse.getParent().getId(), ErrorCode.VALIDATION_ERROR, "errors.qa.executeOnSubmitProcessOfResponse.parentUpdationValidation",true,errorParams);
//                V3Util.throwRestException(oldResponse == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid response ID ({0}) is specified for updating", response.getId()),true,errorParams);
//                V3Util.throwRestException(response.getParent() != null && response.getParent().getId() > 0 && response.getParent().getId() != oldResponse.getParent().getId(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Cannot update parent of response : {0}", response.getId()),true,errorParams);
                if (response.getResStatus() != oldResponse.getResStatus() && response.getResStatus() == ResponseContext.ResponseStatus.COMPLETED) {

					QAndAUtil.validateResponseExpiry(response);
					if(response.isRetake() && response.getRetakeExpiry() == null){
						QAndAUtil.updateResponseRetakeExpiry(response);
					}

                    FacilioChain onSubmitProcess = QAndATransactionChainFactory.onSubmitProcessOfResponse();
                    onSubmitProcess.getContext().put(FacilioConstants.QAndA.RESPONSE, response);

                    onSubmitProcess.execute();
                }
            }
        }

        return false;
    }
}
