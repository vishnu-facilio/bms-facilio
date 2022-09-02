package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class FilterOutFailedMailLogsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("RECIPIENT_COUNT", "recipientCount", "0", NumberOperators.NOT_EQUALS));
        context.put(Constants.BEFORE_FETCH_CRITERIA, criteria);
        return false;
    }
}
