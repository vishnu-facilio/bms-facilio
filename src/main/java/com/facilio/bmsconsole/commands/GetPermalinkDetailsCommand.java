package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class GetPermalinkDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String token = (String) context.get(FacilioConstants.ContextNames.TOKEN);

        if (StringUtils.isNotEmpty(token)) {
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(IAMAccountConstants.getUserSessionFields())
                    .table("UserSessions");

            selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
            selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "email", "1", NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if (props != null && !props.isEmpty()) {
                Map<String, Object> session = props.get(0);
                Object sessionInfo = session.get("sessionInfo");

                context.put(FacilioConstants.ContextNames.SESSION, sessionInfo);
            }
        }
        return false;
    }
}
