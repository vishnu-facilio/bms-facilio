package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IncludeServingSiteFilterCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("includeServingSite") && queryParams.containsKey("siteId")) {
            boolean includeServingSite = FacilioUtil.parseBoolean((Constants.getQueryParam(context, "includeServingSite")));
            Long siteId = FacilioUtil.parseLong(Objects.requireNonNull(Constants.getQueryParam(context, "siteId")));
            if (Objects.nonNull(Constants.getQueryParam(context, "includeServingSite")) && includeServingSite && siteId != null) {
                Set<Long> storeIds = V3StoreroomApi.getStoreRoomList(siteId, includeServingSite);
                if (CollectionUtils.isNotEmpty(storeIds)) {
                    Condition condition = CriteriaAPI.getConditionFromList("STORE_ROOM_ID", "storeRoomId", storeIds, NumberOperators.EQUALS);
                    context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
                }
            }
        }
        return false;
    }
}
