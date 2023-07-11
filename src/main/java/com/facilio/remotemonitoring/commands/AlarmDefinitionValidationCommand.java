package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmDefinitionMappingContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AlarmDefinitionValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmDefinitionMappingContext> alarmDefinitionMappings = (List<AlarmDefinitionMappingContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmDefinitionMappings)) {
            for(AlarmDefinitionMappingContext alarmDefinitionMapping : alarmDefinitionMappings) {
                if(StringUtils.isEmpty(alarmDefinitionMapping.getRegularExpression())) {
                    FacilioUtil.throwIllegalArgumentException(true,"Regular expression cannot be empty");
                } else {
                    try {
                        Pattern.compile(alarmDefinitionMapping.getRegularExpression());
                    } catch (PatternSyntaxException e) {
                        FacilioUtil.throwIllegalArgumentException(true,"Invalid regular expression");
                    }
                }
            }
        }
        return false;
    }
}
