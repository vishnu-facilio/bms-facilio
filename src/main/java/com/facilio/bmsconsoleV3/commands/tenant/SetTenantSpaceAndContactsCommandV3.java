package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3TenantsAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetTenantSpaceAndContactsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                List<V3TenantSpaceContext> tenantSpaces = V3TenantsAPI.fetchTenantSpacesAssociation(tenant.getId() , true , "desc");
                   List<Long> spaceIds;
                    if(CollectionUtils.isNotEmpty(tenantSpaces)) {
                    	spaceIds = new ArrayList<Long>();
                        for(V3TenantSpaceContext tenantSpace : tenantSpaces) {
                        	spaceIds.add(tenantSpace.getSpace().getId());
                        }
                    	tenant.setTenantSpaces(tenantSpaces);
                        tenant.setSpaces(tenantSpaces.stream().map(V3TenantSpaceContext::getSpace).collect(Collectors.toList()));
                    }
                    List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(tenant.getId(), false, false);
                    tenant.setPeopleTenantContacts(tenantContacts);

            }
        }
        return false;
    }
}
