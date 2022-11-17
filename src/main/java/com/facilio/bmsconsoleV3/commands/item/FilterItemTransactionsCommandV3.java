package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import com.facilio.db.criteria.Condition;


public class FilterItemTransactionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Criteria criteria = new Criteria();
        Condition softReserveFilterCondition = CriteriaAPI.getCondition("TRANSACTION_STATE", "transactionState", String.valueOf(TransactionState.SOFT_RESERVE.getValue()), NumberOperators.NOT_EQUALS);
        Condition hardReserveFilterCondition = CriteriaAPI.getCondition("TRANSACTION_STATE", "transactionState", String.valueOf(TransactionState.HARD_RESERVE.getValue()), NumberOperators.NOT_EQUALS);
        criteria.addAndCondition(softReserveFilterCondition);
        criteria.addAndCondition(hardReserveFilterCondition);
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        return false;
    }
}
