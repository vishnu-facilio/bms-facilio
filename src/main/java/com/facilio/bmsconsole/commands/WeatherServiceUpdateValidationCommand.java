package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j
public class WeatherServiceUpdateValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherServiceContext> weatherServiceContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherServiceContexts)) {
            return true;
        }

        Map<String, Map> oldRecordMap = (Map<String, Map>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long, V3WeatherServiceContext>  oldWeatherServices = oldRecordMap.get(moduleName);

        V3WeatherServiceContext currRecord = weatherServiceContexts.get(0);
        V3WeatherServiceContext oldRecord = oldWeatherServices.get(currRecord.getId());
        Set<String> changeSet = findChangeSet(currRecord, oldRecord);
        FacilioUtil.throwIllegalArgumentException(changeSet.contains("name"), "Weather service name is not allowed to modify");

        return false;
    }

    private Set<String> findChangeSet(V3WeatherServiceContext currRecord, V3WeatherServiceContext oldRecord) throws Exception{
        Map<String, Object> curr = FieldUtil.getAsJSON(currRecord);
        Map<String, Object> old = FieldUtil.getAsJSON(oldRecord);
        Set<String> changeSet = new HashSet<>();
        for(String key : old.keySet()) {
            if(curr.get(key) == old.get(key) || curr.get(key).equals(old.get(key))) {
                continue;
            }
            changeSet.add(key);
        }
        return changeSet;
    }

}