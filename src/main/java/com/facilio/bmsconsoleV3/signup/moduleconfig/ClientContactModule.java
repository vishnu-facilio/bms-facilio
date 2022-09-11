package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class ClientContactModule extends BaseModuleConfig{

    public ClientContactModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CLIENT_CONTACT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllHiddenClientContacts().setOrder(order++));
        clientContact.add(getAllClientContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT_CONTACT);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenClientContacts() {

        FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Client Contacts");
        allView.setModuleName(clientContactModule.getName());
        allView.setSortFields(sortFields);

        allView.setHidden(true);

        return allView;
    }

    private static FacilioView getAllClientContacts() {

        FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Client Contacts");
        allView.setModuleName(clientContactModule.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
