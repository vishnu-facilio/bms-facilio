package com.facilio.bmsconsoleV3.commands.receipts;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Objects;

public class GetReceiptsListOnReceivableIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (Objects.nonNull(Constants.getQueryParam(context, "receivableId"))) {
            long receivableId = FacilioUtil.parseLong((Objects.requireNonNull(Constants.getQueryParam(context, "receivableId"))));
            Condition condition = CriteriaAPI.getCondition("RECEIVABLE_ID", "receivableId", String.valueOf(receivableId), NumberOperators.EQUALS);
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
        }
        return false;
    }
}