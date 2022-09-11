package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class AudienceModule extends BaseModuleConfig{
    public AudienceModule(){
        setModuleName(FacilioConstants.ContextNames.AUDIENCE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> audience = new ArrayList<FacilioView>();
        audience.add(getAllAudienceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.AUDIENCE);
        groupDetails.put("views", audience);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAudienceView() {

        FacilioModule module = ModuleFactory.getAudienceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Audiences");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
