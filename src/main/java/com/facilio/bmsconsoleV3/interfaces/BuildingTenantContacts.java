package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3TenantsAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.facilio.bmsconsoleV3.util.V3PeopleAPI.getTenantContacts;

public class BuildingTenantContacts implements AnnouncementSharedPeople{
    @Override
    public List<V3PeopleContext> getPeople(Long id) throws Exception{
        if(id != null){
          List<V3TenantContext> tenants = AnnouncementAPI.getBuildingTenants(id);
          if(CollectionUtils.isEmpty(tenants)) {
              return null;
          }
          List<V3PeopleContext> users = new ArrayList<>();
          for(V3TenantContext tenant : tenants) {
              List<V3TenantContactContext> list = V3PeopleAPI.getTenantContacts(tenant.getId(), false, false);
              if(CollectionUtils.isNotEmpty(list)) {
                  users.addAll(list);
              }
          }
          return users;
        }
        return null;
    }
}
