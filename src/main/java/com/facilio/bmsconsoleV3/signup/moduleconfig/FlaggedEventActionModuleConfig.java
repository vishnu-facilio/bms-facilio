
package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.signup.*;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FlaggedEventActionModuleConfig extends BaseModuleConfig {
    public FlaggedEventActionModuleConfig() {
        setModuleName(FlaggedEventBureauActionModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> list = new ArrayList<FacilioView>();
        list.add(getAllView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FlaggedEventBureauActionModule.MODULE_NAME);
        groupDetails.put("views", list);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField order = new FacilioField();
        order.setName("order");
        order.setColumnName("EXECUTION_ORDER");
        order.setDataType(FieldType.NUMBER);
        order.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Evaluation Teams");
        allView.setSortFields(Arrays.asList(new SortField(order, true)));
        allView.setModuleName(FlaggedEventBureauActionModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("order", "Execution Order"));
        viewFields.add(new ViewField("team", "Assigned Team"));
        viewFields.add(new ViewField("assignedPeople", "Assigned People"));
        viewFields.add(new ViewField("eventStatus", "Status"));
        viewFields.add(new ViewField("takeCustodyTimestamp", "Take Custody Time"));
        viewFields.add(new ViewField("takeActionTimestamp", "Take Action Time"));
        viewFields.add(new ViewField("inhibitTimeStamp", "Inhibit Time"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
