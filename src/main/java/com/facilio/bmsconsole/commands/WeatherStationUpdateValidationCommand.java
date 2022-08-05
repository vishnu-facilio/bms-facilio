package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
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
public class WeatherStationUpdateValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherStationContext> weatherStationContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherStationContexts)) {
            return true;
        }

        Map<String, Map> oldRecordMap = (Map<String, Map>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long, V3WeatherStationContext>  oldWeatherStations = oldRecordMap.get(moduleName);

        V3WeatherStationContext currRecord = weatherStationContexts.get(0);
        V3WeatherStationContext oldRecord = oldWeatherStations.get(currRecord.getId());
        Set<String> changeSet = findChangeSet(currRecord, oldRecord);

        FacilioUtil.throwIllegalArgumentException(changeSet.contains("stationCode"), "Station code can't be modified");
        FacilioUtil.throwIllegalArgumentException(changeSet.contains("service")
                && currRecord.getService().getId() != oldRecord.getService().getId(), "Weather service can't be changed");
        FacilioUtil.throwIllegalArgumentException(changeSet.contains("serviceId"), "Weather service Id can't be changed");

        return false;
    }

    private Set<String> findChangeSet(V3WeatherStationContext currRecord, V3WeatherStationContext oldRecord) throws Exception{
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