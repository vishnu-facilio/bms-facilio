package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class ValidateWeatherStationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherStationContext> weatherStationContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherStationContexts)) {
            return true;
        }

        V3WeatherStationContext weatherStationContext = weatherStationContexts.get(0);
        V3WeatherStationContext existingStationContext =  WeatherAPI.getWeatherStationByCode(weatherStationContext.getStationCode());
        if(existingStationContext!=null) {
            throw new Exception("Given weather station code already exists");
        }
        return false;
    }
}