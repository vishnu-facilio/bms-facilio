package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.*;

public class FetchCriteriaObjectCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3SpaceBookingPolicyContext policyContext = (V3SpaceBookingPolicyContext) CommandUtil.getModuleData(context,FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY,id);
        if (policyContext != null) {
            Long criteriaId = policyContext.getCriteriaId();
            if (criteriaId != null) {
                Criteria criteria = CriteriaAPI.getCriteria(criteriaId);
                policyContext.setCriteria(criteria);
                context.put(FacilioConstants.ContextNames.SpaceBooking.CRITERIA,criteria);
            }
        }
        return false;
    }
}
