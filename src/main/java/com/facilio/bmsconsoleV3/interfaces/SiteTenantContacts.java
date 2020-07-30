package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SiteTenantContacts implements AnnouncementSharedPeople{
    @Override
    public List<V3PeopleContext> getPeople(Long id) throws Exception {
        if(id != null){
            List<BuildingContext> buildings = SpaceAPI.getSiteBuildings(id);
            if(CollectionUtils.isEmpty(buildings)) {
                return null;
            }
            List<V3PeopleContext> users = new ArrayList<>();
            for(BuildingContext building : buildings) {
                List<V3TenantContext> tenants = AnnouncementAPI.getBuildingTenants(building.getId());
                if(CollectionUtils.isEmpty(tenants)) {
                    continue;
                }
                for (V3TenantContext tenant : tenants) {
                    List<V3TenantContactContext> list = V3PeopleAPI.getTenantContacts(tenant.getId(), false, false);
                    if (CollectionUtils.isNotEmpty(list)) {
                        users.addAll(list);
                    }
                }
            }
            return users;
        }
        return null;
    }
}
