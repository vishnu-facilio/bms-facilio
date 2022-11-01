package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class E2ControllerModule extends BaseModuleConfig {
    public E2ControllerModule() throws Exception{
        setModuleName("e2controller");
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> e2controller = new ArrayList<FacilioView>();
        e2controller.add(getE2Controller().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", e2controller);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getE2Controller() {

        FacilioModule e2tControllerModule = ModuleFactory.getE2ControllerModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All E2 Controllers");
        allView.setModuleName(e2tControllerModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));
        allView.setFields(getE2ControllerColumns());

        return allView;
    }

    private static List<ViewField>getE2ControllerColumns(){
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("ipAddress","IP Address"));
        columns.add(new ViewField("port","Port"));
        columns.add(new ViewField("deviceId","Device Id"));
        columns.add(new ViewField("sysCreatedTime", "Created Time"));
        columns.add(new ViewField("sysModifiedTime","Modified Time"));
        return columns;
    }
}
