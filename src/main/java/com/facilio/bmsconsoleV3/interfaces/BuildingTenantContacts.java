package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuildingTenantContacts implements CommunitySharedPeople {

	@Override
    public List<V3PeopleContext> getPeople(Long id,Long filterSharingType,List<Long> roleIds) throws Exception{
    	List<Long> tenantIds = new ArrayList<Long>();
        if(id != null){
            tenantIds = CommunityFeaturesAPI.getBuildingTenants(id);
        }
        else {
        	List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
        	if(CollectionUtils.isNotEmpty(buildings)) {
        		tenantIds = CommunityFeaturesAPI.getBuildingTenants(buildings.stream().map(building -> building.getId()).collect(Collectors.toList()));
        	}
        }
		if(CollectionUtils.isNotEmpty(tenantIds)) {
	    	  List<V3PeopleContext> users = new ArrayList<>();
		      for(Long tenantId : tenantIds) {
				  List<V3TenantContactContext> list = new ArrayList<>();
				  if(filterSharingType != null && CollectionUtils.isNotEmpty(roleIds)){
					  list = V3PeopleAPI.getTenantContactTenantAndRoles(tenantId,roleIds);
				  }
				  else{
					  list = V3PeopleAPI.getTenantContacts(tenantId, false, true);
				  }
		          if(CollectionUtils.isNotEmpty(list)) {
		              users.addAll(list);
		          }
		      }
			return users;
	      }
        return null;
    }
}
