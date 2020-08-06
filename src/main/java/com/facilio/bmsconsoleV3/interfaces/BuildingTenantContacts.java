package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class BuildingTenantContacts implements CommunitySharedPeople {
    @Override
    public List<V3PeopleContext> getPeople(Long id) throws Exception{
        if(id != null){
          List<Long> tenantIds = AnnouncementAPI.getBuildingTenants(id);
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
        return null;
    }
}
