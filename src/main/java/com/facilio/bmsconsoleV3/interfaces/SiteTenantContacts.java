package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SiteTenantContacts implements CommunitySharedPeople {
    @Override
    public List<V3PeopleContext> getPeople(Long id) throws Exception {
        List<BuildingContext> buildings = new ArrayList<>();
        if(id == null) {
             buildings = SpaceAPI.getAllBuildings();
        }
        else {
            buildings = SpaceAPI.getSiteBuildings(id);
        }
        if(CollectionUtils.isEmpty(buildings)) {
            return null;
        }
        List<V3PeopleContext> users = new ArrayList<>();
        for(BuildingContext building : buildings) {
            List<Long> tenantIds = CommunityFeaturesAPI.getBuildingTenants(building.getId());
            if(CollectionUtils.isEmpty(tenantIds)) {
                continue;
            }
            for (Long tenantId : tenantIds) {
                List<V3TenantContactContext> list = V3PeopleAPI.getTenantContacts(tenantId, false, true);
                if (CollectionUtils.isNotEmpty(list)) {
                    users.addAll(list);
                }
            }
        }
        return users;
      }
}
