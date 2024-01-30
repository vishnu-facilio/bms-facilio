package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;

public class AddVendorScopeForPortalCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
    	String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Boolean isVendorPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_VENDOR_PORTAL);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (isVendorPortal != null && isVendorPortal) {
        		long currentUserId = AccountUtil.getCurrentUser().getOuid();
            if (filterCriteria == null) {
                filterCriteria = new Criteria();
            }
                VendorContext vendorContext = PeopleAPI.getVendorForUser(currentUserId);
                if (vendorContext != null) {
                    Condition condition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorContext.getId()), NumberOperators.EQUALS);
                    filterCriteria.addAndCondition(condition);
                } else {
                    throw new IllegalArgumentException("No Valid Vendor Contact Found for this user");
                }


            Condition notEmtpyCondition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", "-1", CommonOperators.IS_NOT_EMPTY);
            filterCriteria.addAndCondition(notEmtpyCondition);
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);

        }
        
        // handling tenant scoping temp here...later will be handled in framework
        boolean isTenantPortal = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_TENANT_PORTAL, false);
        if (isTenantPortal ) {
        		long currentUserId = AccountUtil.getCurrentUser().getOuid();
            TenantContext tenantContext = PeopleAPI.getTenantForUser(currentUserId);
            if (tenantContext != null) {
                Condition condition = CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantContext.getId()), NumberOperators.EQUALS);
                
                if (filterCriteria == null) {
                    filterCriteria = new Criteria();
                    context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
                }
                if(StringUtils.isNotEmpty(moduleName) && !moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOGGING)) {
                	filterCriteria.addAndCondition(condition);
                }
            }
        }
        
        return false;
    }

}
