package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ServiceModule extends BaseModuleConfig{
    public ServiceModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> service = new ArrayList<FacilioView>();
        service.add(getAllServiceView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SERVICE);
        groupDetails.put("views", service);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllServiceView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getServiceModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service");
        allView.setSortFields(Arrays.asList(new SortField(name, true)));

        return allView;
    }
}
