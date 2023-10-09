package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class CommandModule extends BaseModuleConfig {
    public CommandModule() throws Exception{
        setModuleName(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllCommandsView().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllCommandsView() throws Exception{

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Commands");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));


        List<ViewField> fields = new ArrayList<>();
        fields.add(new ViewField("name","Name"));
        fields.add(new ViewField("site","Site"));
        fields.add(new ViewField("asset","Asset"));
        fields.add(new ViewField("fieldId","Reading Field Id"));
        fields.add(new ViewField("controller","Controller"));
        fields.add(new ViewField("controlActionCommandStatus","Status"));
        fields.add(new ViewField("previousValue","Previous Value"));
        fields.add(new ViewField("afterValue","After Value"));
        fields.add(new ViewField("setValue","Set Value"));
        fields.add(new ViewField("commandActionType","Action Type"));
        fields.add(new ViewField("errorMsg","Error Message"));
        fields.add(new ViewField("previousValueCapturedTime","Previous Value Captured Time"));
        allView.setFields(fields);


        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Commands.ID", FieldType.NUMBER), true));
        allView.setSortFields(sortFields);

        return allView;
    }
}
