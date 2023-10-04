package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class ActionModule extends BaseModuleConfig {
    public ActionModule(){
        setModuleName(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllActionsView().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllActionsView() throws Exception{

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Actions");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));


        List<ViewField> fields = new ArrayList<>();
        fields.add(new ViewField("actionVariableType","Variable Type"));
        fields.add(new ViewField("scheduledActionOperatorType","Schedule Action Operator"));
        fields.add(new ViewField("scheduleActionValue","Schedule Action Value"));
        fields.add(new ViewField("revertActionOperatorType","Revert Action Operator"));
        fields.add(new ViewField("revertActionValue","Revert Action Value"));
        fields.add(new ViewField("sysCreatedByPeople","Created By"));
        fields.add(new ViewField("sysModifiedByPeople","Modified By"));
        fields.add(new ViewField("sysCreatedTime","Created Time"));
        fields.add(new ViewField("sysModifiedTime","Modified Time"));
        allView.setFields(fields);


        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Actions.ID", FieldType.NUMBER), true));
        allView.setSortFields(sortFields);

        return allView;
    }
}
