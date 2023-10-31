package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
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
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import org.json.simple.JSONObject;

import java.util.*;

public class RawAlarmModuleConfig extends BaseModuleConfig {
    public RawAlarmModuleConfig(){
        setModuleName(RawAlarmModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> rawAlarmViews = new ArrayList<FacilioView>();
        rawAlarmViews.add(getAllAlarms().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", RawAlarmModule.MODULE_NAME);
        groupDetails.put("views", rawAlarmViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(RawAlarmModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Alarms");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        allView.setModuleName(RawAlarmModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("message","Message"));
        viewFields.add(new ViewField("site","Site"));
        viewFields.add(new ViewField("client","Client"));
        viewFields.add(new ViewField("controller","Controller"));
        viewFields.add(new ViewField("alarmDefinition","Alarm Definition"));
        viewFields.add(new ViewField("alarmCategory","Alarm Category"));
        viewFields.add(new ViewField("alarmType","Alarm Type"));
        viewFields.add(new ViewField("alarmApproach","Alarm Approach"));
        viewFields.add(new ViewField("occurredTime","Occurred Time"));
        viewFields.add(new ViewField("clearedTime","Cleared Time"));
        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields remoteMonitorApp = new ScopeVariableModulesFields();
        remoteMonitorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_remotemonitor_client"));
        remoteMonitorApp.setModuleId(module.getModuleId());
        remoteMonitorApp.setFieldName("client");

        scopeConfigList = Arrays.asList(remoteMonitorApp);
        return scopeConfigList;
    }
}
