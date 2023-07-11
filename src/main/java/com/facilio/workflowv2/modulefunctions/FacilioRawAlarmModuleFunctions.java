package com.facilio.workflowv2.modulefunctions;

import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import com.facilio.scriptengine.annotation.ScriptModule;

import com.facilio.scriptengine.context.ScriptContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ScriptModule(moduleName = RawAlarmModule.MODULE_NAME)
public class FacilioRawAlarmModuleFunctions extends FacilioModuleFunctionImpl {
    public Object addRawAlarm(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
        if(CollectionUtils.isNotEmpty(objects) && objects.size() > 1) {
            List<Map<String,Object>> rawAlarmsList = (List<Map<String, Object>>) objects.get(1);
            if(CollectionUtils.isNotEmpty(rawAlarmsList)) {
                List<RawAlarmContext> rawAlarms = FieldUtil.getAsBeanListFromMapList(rawAlarmsList, RawAlarmContext.class);
                if(CollectionUtils.isNotEmpty(rawAlarms)) {
                    for(RawAlarmContext rawAlarm : rawAlarms) {
                        if(rawAlarm.getSourceType() == null) {
                            rawAlarm.setSourceType(RawAlarmContext.RawAlarmSourceType.SCRIPT);
                        }
                        RawAlarmUtil.pushToStormRawAlarmQueue(rawAlarm);
                    }
                }
            }
        }
        return null;
    }
}