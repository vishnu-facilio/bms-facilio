package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class VendorDocumentsModule extends BaseModuleConfig{
    public VendorDocumentsModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_DOCUMENTS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorDocuments = new ArrayList<FacilioView>();
        vendorDocuments.add(getAllDocumentsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_DOCUMENTS);
        groupDetails.put("views", vendorDocuments);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDocumentsView() {

        FacilioField sysCreatedtimeField = new FacilioField();
        sysCreatedtimeField.setName("sysCreatedTime");
        sysCreatedtimeField.setColumnName("SYS_CREATED_TIME");
        sysCreatedtimeField.setDataType(FieldType.DATE_TIME);
        sysCreatedtimeField.setModule(ModuleFactory.getVendorDocumentsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedtimeField, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
