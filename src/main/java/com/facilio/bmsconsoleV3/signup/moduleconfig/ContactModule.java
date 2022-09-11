package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ContactModule extends BaseModuleConfig{
    public ContactModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CONTACT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> contact = new ArrayList<FacilioView>();
        contact.add(getAllContactView().setOrder(order++));
        contact.add(getTenantContactView().setOrder(order++));
        contact.add(getVendorContactView().setOrder(order++));
        contact.add(getEmployeeContactView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTACT);
        groupDetails.put("views", contact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Contact");

        return allView;
    }

    private static FacilioView getTenantContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("tenant");
        allView.setDisplayName("Tenant Contact");

        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(), FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "1", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

    private static FacilioView getVendorContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("vendor");
        allView.setDisplayName("Vendor Contact");
        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "2", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

    private static FacilioView getEmployeeContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("employee");
        allView.setDisplayName("Employee Contact");

        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "3", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

}
