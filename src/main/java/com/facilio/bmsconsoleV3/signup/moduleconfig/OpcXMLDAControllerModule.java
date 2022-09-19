package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class OpcXMLDAControllerModule extends BaseModuleConfig {
    public OpcXMLDAControllerModule() throws Exception{
        setModuleName("opcxmldacontroller");
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> opcxmldacontroller = new ArrayList<FacilioView>();
        opcxmldacontroller.add(getOpcXmldaController().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", opcxmldacontroller);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getOpcXmldaController() {

        FacilioModule opcXmlDaControllerModule = ModuleFactory.getOpcXmlDaControllerModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All OPC XML DA Controller");
        allView.setModuleName(opcXmlDaControllerModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP));

        return allView;
    }
}
