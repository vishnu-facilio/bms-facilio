package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class TenantUnitSpaceModule extends BaseModuleConfig{
    public TenantUnitSpaceModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantUnitSpace = new ArrayList<FacilioView>();
        tenantUnitSpace.add(getAllTenantUnitSpace().setOrder(order++));
        tenantUnitSpace.add(getAllTenantUnitSpaceDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        groupDetails.put("views", tenantUnitSpace);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantUnitSpace() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Unit");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getAllTenantUnitSpaceDetailsView() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("All Tenant Units");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);
        allView.setHidden(true);

        return allView;
    }
}
