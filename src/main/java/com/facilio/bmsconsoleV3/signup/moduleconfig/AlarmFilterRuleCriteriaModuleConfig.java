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
import com.facilio.remotemonitoring.signup.AddSubModuleRelations;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AlarmFilterRuleCriteriaModuleConfig extends BaseModuleConfig {
    public AlarmFilterRuleCriteriaModuleConfig() {
        setModuleName(AlarmFilterRuleCriteriaModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> views = new ArrayList<FacilioView>();
        views.add(getAllView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", AlarmFilterRuleModule.MODULE_NAME);
        groupDetails.put("views", views);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField id = new FacilioField();
        id.setName("id");
        id.setColumnName("ID");
        id.setDataType(FieldType.ID);
        id.setModule(modBean.getModule(AlarmFilterRuleModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Alarm Filter Rules Criteria");
        allView.setSortFields(Arrays.asList(new SortField(id, false)));
        allView.setModuleName(AlarmFilterRuleModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("alarmDefinition", "Alarm Definition"));
        viewFields.add(new ViewField("controllerType", "Controller Type"));
        viewFields.add(new ViewField("filterCriteria", "Criteria Type"));
        viewFields.add(new ViewField("alarmDuration", "Duration"));
        viewFields.add(new ViewField("alarmCount", "Count"));
        viewFields.add(new ViewField("alarmCountPeriod", "Count Period"));
        viewFields.add(new ViewField("alarmClearPeriod", "Clear Period"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}