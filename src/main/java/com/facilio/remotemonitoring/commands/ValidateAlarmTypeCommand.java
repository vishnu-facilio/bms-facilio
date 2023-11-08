package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmTypeContext;
import com.facilio.remotemonitoring.signup.AlarmTypeModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateAlarmTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmTypeContext> alarmTypes = recordMap.get(moduleName);
        List<String> systemLinkNames = Arrays.asList("undefined", "heartbeat", "controlleroffline");
        if(CollectionUtils.isNotEmpty(alarmTypes)) {
            for(AlarmTypeContext alarmType : alarmTypes) {
                if(systemLinkNames.contains(alarmType.getLinkName())) {
                    throw new IllegalArgumentException("Modifying undefined alarm type is not allowed");
                }
                if(StringUtils.isNotEmpty(alarmType.getLinkName())) {
                    throw new IllegalArgumentException("Cannot modify link name");
                }
                if(alarmType.getId() < 0) {
                    String linkName = constructLinkName(null, alarmType.getName());
                    alarmType.setLinkName(linkName);
                }
                LRUCache.getAlarmTypeCache().removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
            }
        }

        return false;
    }


    private String constructLinkName(String linkName, String displayName) throws Exception {
        List<AlarmTypeContext> alarmTypes = V3RecordAPI.getRecordsList(AlarmTypeModule.MODULE_NAME,null,AlarmTypeContext.class);
        if (CollectionUtils.isEmpty(alarmTypes)) {
            if (StringUtils.isNotEmpty(linkName)) {
                return linkName;
            }
            if (StringUtils.isNotEmpty(displayName)) {
                return displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
        }
        if (CollectionUtils.isNotEmpty(alarmTypes)) {
            List<String> existingNames = alarmTypes.stream().map(AlarmTypeContext::getLinkName).collect(Collectors.toList());
            String foundName = null;
            if (StringUtils.isNotEmpty(linkName)) {
                foundName = linkName;
            } else if (StringUtils.isNotEmpty(displayName)) {
                foundName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
            if (StringUtils.isEmpty(foundName)) {
                throw new IllegalArgumentException("Unable to construct link name for global scope variable");
            }
            int i = 0;
            String constructedName = foundName;
            while (true) {
                if (existingNames.contains(constructedName)) {
                    constructedName = foundName + "_" + (++i);
                } else {
                    return constructedName;
                }
            }
        }
        throw new IllegalArgumentException("Unable to construct link name for global scope variable");
    }
}
