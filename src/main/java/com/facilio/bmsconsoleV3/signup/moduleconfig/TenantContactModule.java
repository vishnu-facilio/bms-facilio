package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TenantContactModule extends BaseModuleConfig{
    public TenantContactModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_CONTACT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantContact = new ArrayList<FacilioView>();
        tenantContact.add(getAllHiddenTenantContacts().setOrder(order++));
        tenantContact.add(getAllTenantContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_CONTACT);
        groupDetails.put("views", tenantContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        allView.setHidden(true);

        return allView;
    }

    private static FacilioView getAllTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);


        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return allView;
    }
}
