package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class TenantSpacesModule extends BaseModuleConfig{
    public TenantSpacesModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_SPACES);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantSpaces = new ArrayList<FacilioView>();
        tenantSpaces.add(getAllTenantSpaces().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_SPACES);
        groupDetails.put("views", tenantSpaces);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantSpaces() {

        FacilioModule tenantSpaceModule = ModuleFactory.getTenantSpacesModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Spaces");
        allView.setModuleName(tenantSpaceModule.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
