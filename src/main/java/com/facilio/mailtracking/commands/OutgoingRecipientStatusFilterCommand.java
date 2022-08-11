package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class OutgoingRecipientStatusFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String loggerId = (String) Constants.getQueryParam(context, "loggerId");
        V3Util.throwRestException(loggerId==null, ErrorCode.VALIDATION_ERROR, "loggerId queryparam is missing");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("LOGGER", "logger", loggerId, NumberOperators.EQUALS));

        String qStatus = (String) Constants.getQueryParam(context, "status");
        if(qStatus!=null && !qStatus.equals("all")) {
            MailStatus status = null;
            if(qStatus.equals("inprogress")) {
                status = MailStatus.IN_PROGRESS;
            } else {
                try {
                    status = MailStatus.valueOf(qStatus.toUpperCase());
                } catch (Exception e) {
                    V3Util.throwRestException(true, ErrorCode.VALIDATION_ERROR, e.getMessage());
                }
            }
            criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", status.getValue()+"", NumberOperators.EQUALS));
        }

        if(!criteria.isEmpty()){
            context.put(Constants.BEFORE_FETCH_CRITERIA, criteria);
        }
        return false;
    }
}
