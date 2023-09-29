package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.context.AlarmTypeContext;

import java.util.Collections;

public class DefaultAlarmTypeAndCategory extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmTypeContext alarmType = new AlarmTypeContext();
        alarmType.setName("Undefined");
        alarmType.setDescription("Undefined Alarm Type");
        alarmType.setUncategorisedAlarm(true);
        V3RecordAPI.addRecord(false, Collections.singletonList(alarmType), modBean.getModule(AlarmTypeModule.MODULE_NAME), modBean.getAllFields(AlarmTypeModule.MODULE_NAME),false);
    }
}