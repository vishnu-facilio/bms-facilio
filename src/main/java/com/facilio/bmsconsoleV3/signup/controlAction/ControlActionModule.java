package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ControlActionModule extends BaseModuleConfig {
    public ControlActionModule() throws Exception{
        setModuleName(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlAction = new ArrayList<FacilioView>();
        controlAction.add(getAllControlActionViews().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        groupDetails.put("views", controlAction);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllControlActionViews() throws Exception{

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Control Action");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));


        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Control_Actions.ID", FieldType.NUMBER), true));
        allView.setSortFields(sortFields);

        return allView;
    }
}
