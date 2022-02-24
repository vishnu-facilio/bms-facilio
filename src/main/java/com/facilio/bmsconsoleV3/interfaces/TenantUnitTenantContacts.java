package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TenantUnitTenantContacts implements CommunitySharedPeople {
    @Override
    public List<V3PeopleContext> getPeople(Long id) throws Exception{
        if(id != null){
	      Map<Long, TenantContext> tenants= TenantsAPI.getTenantForResources(Collections.singletonList(id));
	      if(!tenants.isEmpty()) {
	    	  List<Long> tenantIds = tenants.entrySet().stream().map(tn -> tn.getValue().getId()).collect(Collectors.toList());
	          if(CollectionUtils.isEmpty(tenantIds)) {
	              return null;
	          }
	          List<V3PeopleContext> users = new ArrayList<>();
	          for(Long tenantId : tenantIds) {
	              List<V3TenantContactContext> list = V3PeopleAPI.getTenantContacts(tenantId, false, true);
	              if(CollectionUtils.isNotEmpty(list)) {
	                  users.addAll(list);
	              }
	          }
	          return users;
	       }
        }
        return null;
    }
}
