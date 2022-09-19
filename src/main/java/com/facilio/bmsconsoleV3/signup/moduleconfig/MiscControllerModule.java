package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class MiscControllerModule extends BaseModuleConfig {
    public MiscControllerModule() throws Exception{
        setModuleName("misccontroller");
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> misccontroller = new ArrayList<FacilioView>();
        misccontroller.add(getMiscController().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", misccontroller);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getMiscController() {

        FacilioModule miscControlerModule = ModuleFactory.getMisccontrolerModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Misc Controller");
        allView.setModuleName(miscControlerModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));

        return allView;
    }
}
