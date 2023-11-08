package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmTypeContext;

import java.util.Collections;

public class DefaultAlarmTypeAndCategory extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmTypeContext undefined = new AlarmTypeContext();
        undefined.setName("Undefined");
        undefined.setDescription("Undefined Alarm Type");
        undefined.setLinkName(RemoteMonitorConstants.SystemAlarmTypes.UNDEFINED);
        undefined.setUncategorisedAlarm(true);
        V3RecordAPI.addRecord(false, Collections.singletonList(undefined), modBean.getModule(AlarmTypeModule.MODULE_NAME), modBean.getAllFields(AlarmTypeModule.MODULE_NAME),false);

        AlarmTypeContext hearbeatAlarmType = new AlarmTypeContext();
        hearbeatAlarmType.setName("Ping");
        hearbeatAlarmType.setDescription("Heartbeat Alarm Type");
        hearbeatAlarmType.setLinkName(RemoteMonitorConstants.SystemAlarmTypes.HEARBEAT);
        hearbeatAlarmType.setUncategorisedAlarm(true);
        V3RecordAPI.addRecord(false, Collections.singletonList(hearbeatAlarmType), modBean.getModule(AlarmTypeModule.MODULE_NAME), modBean.getAllFields(AlarmTypeModule.MODULE_NAME),false);

        AlarmTypeContext controllerOfflineAlarmType = new AlarmTypeContext();
        controllerOfflineAlarmType.setName("Controller Offline");
        controllerOfflineAlarmType.setDescription("Controller Offline Alarm Type");
        controllerOfflineAlarmType.setLinkName(RemoteMonitorConstants.SystemAlarmTypes.CONTROLLER_OFFLINE);
        controllerOfflineAlarmType.setUncategorisedAlarm(true);
        V3RecordAPI.addRecord(false, Collections.singletonList(controllerOfflineAlarmType), modBean.getModule(AlarmTypeModule.MODULE_NAME), modBean.getAllFields(AlarmTypeModule.MODULE_NAME),false);

        AlarmTypeContext siteOfflineAlarmType = new AlarmTypeContext();
        siteOfflineAlarmType.setName("Site Offline");
        siteOfflineAlarmType.setDescription("Site Offline Alarm Type");
        siteOfflineAlarmType.setLinkName(RemoteMonitorConstants.SystemAlarmTypes.SITE_OFFLINE);
        siteOfflineAlarmType.setUncategorisedAlarm(true);
        V3RecordAPI.addRecord(false, Collections.singletonList(siteOfflineAlarmType), modBean.getModule(AlarmTypeModule.MODULE_NAME), modBean.getAllFields(AlarmTypeModule.MODULE_NAME),false);
    }
}