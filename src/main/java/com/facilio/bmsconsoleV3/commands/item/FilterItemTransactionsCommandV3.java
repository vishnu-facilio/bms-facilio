package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.db.criteria.Condition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class FilterItemTransactionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        Criteria criteria = new Criteria();
        Condition softReserveFilterCondition = CriteriaAPI.getCondition("TRANSACTION_STATE", "transactionState", String.valueOf(TransactionState.SOFT_RESERVE.getValue()), NumberOperators.NOT_EQUALS);
        Condition hardReserveFilterCondition = CriteriaAPI.getCondition("TRANSACTION_STATE", "transactionState", String.valueOf(TransactionState.HARD_RESERVE.getValue()), NumberOperators.NOT_EQUALS);
        criteria.addAndCondition(softReserveFilterCondition);
        criteria.addAndCondition(hardReserveFilterCondition);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("showItemsForIssue") && queryParams.containsKey("siteId")){
            boolean showItemsForIssue = FacilioUtil.parseBoolean((Constants.getQueryParam(context, "showItemsForIssue")));
            Long siteId = FacilioUtil.parseLong(Objects.requireNonNull(Constants.getQueryParam(context, "siteId")));
            if(showItemsForIssue && siteId!=null){
                criteria.addAndCondition(CriteriaAPI.getCondition("REMAINING_QUANTITY","remainingQuantity",
                        String.valueOf(0), NumberOperators.GREATER_THAN));
                criteria.addAndCondition(CriteriaAPI.getCondition("TRANSACTION_STATE","transactionState",
                        String.valueOf(2), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("ISSUED_TO","issuedTo",
                        String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));

                Set<Long> storeIds = V3StoreroomApi.getStoreRoomList(siteId, true);
                if (CollectionUtils.isNotEmpty(storeIds)) {
                    criteria.addAndCondition(CriteriaAPI.getConditionFromList("STORE_ROOM_ID", "storeRoomId", storeIds, NumberOperators.EQUALS));
                }
            }
        }
        else if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("showItemsForReturn") && FacilioUtil.parseBoolean((Constants.getQueryParam(context, "showItemsForReturn")))){
            criteria.addAndCondition(CriteriaAPI.getCondition("REMAINING_QUANTITY","remainingQuantity",
                    String.valueOf(0), NumberOperators.GREATER_THAN));
            criteria.addAndCondition(CriteriaAPI.getCondition("IS_RETURNABLE","isReturnable",
                    String.valueOf(true), BooleanOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition("TRANSACTION_STATE","transactionState",
                    String.valueOf(2), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("TRANSACTION_TYPE","transactionType",
                    String.valueOf(4), NumberOperators.NOT_EQUALS));
        }
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        return false;
    }
}
