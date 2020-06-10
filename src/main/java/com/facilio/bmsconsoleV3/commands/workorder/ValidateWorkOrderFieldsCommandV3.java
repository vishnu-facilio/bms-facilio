package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.UpdateWorkOrderCommand;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ValidateWorkOrderFieldsCommandV3 extends FacilioCommand {

    private static org.apache.log4j.Logger log = LogManager.getLogger(ValidateWorkOrderFieldsCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext woContext = wos.get(0);

            if(woContext.getSubject() == null || woContext.getSubject().isEmpty()) {
                throw new IllegalArgumentException("Subject is invalid");
            }
            else {
                woContext.setSubject(woContext.getSubject().trim());
            }

            if(woContext.getDescription() != null && !woContext.getDescription().isEmpty()) {
                woContext.setDescription(woContext.getDescription().trim());
            }

            if(woContext.getDueDate() == null) {
                Calendar cal = Calendar.getInstance();
                woContext.setDueDate((cal.getTimeInMillis())+ TicketContext.DEFAULT_DURATION);
            }

            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS) && AccountUtil.getCurrentUser() != null) {
                long currentUserId = AccountUtil.getCurrentUser().getOuid();
                try {
                    V3TenantContext tenant = V3PeopleAPI.getTenantForUser(currentUserId);
                    if (tenant != null) {
                        woContext.setSiteId(tenant.getSiteId());
                        woContext.setTenant(tenant);
                    }
                }
                catch(Exception e) {
                    // Till people is migrated for all orgs
                    log.error("Error occurred in setting tenant", e);
                }
            }
        }
        return false;
    }
}
