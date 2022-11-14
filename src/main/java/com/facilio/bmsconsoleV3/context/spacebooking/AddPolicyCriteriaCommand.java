package com.facilio.bmsconsoleV3.context.spacebooking;

 import com.facilio.bmsconsoleV3.util.V3SpaceBookingApi;
 import com.facilio.command.FacilioCommand;
 import com.facilio.db.criteria.Criteria;
 import com.facilio.v3.context.Constants;
 import org.apache.commons.chain.Context;
 import org.apache.commons.collections.CollectionUtils;
 import java.util.List;
 import java.util.Map;

public class AddPolicyCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String module = Constants.getModuleName(context);

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3SpaceBookingPolicyContext> policy = recordMap.get(module);


        if (CollectionUtils.isNotEmpty(policy)) {

            V3SpaceBookingPolicyContext policyData = policy.get(0);

            String moduleName = policyData.getModuleName();
            Criteria criteria = policyData.getCriteria();

            long criteriaId = V3SpaceBookingApi.generatePolicyCriteriaId(criteria,moduleName);
            policyData.setCriteriaId(criteriaId);

        }
        return false;
    }
}

