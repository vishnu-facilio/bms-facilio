package com.facilio.bmsconsole.commands;

import com.facilio.weather.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class WeatherStationCreateValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherStationContext> weatherStationContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherStationContexts)) {
            return true;
        }

        V3WeatherStationContext weatherStationContext = weatherStationContexts.get(0);
        V3WeatherStationContext existingStationContext =  WeatherAPI.getExistingWeatherStation(weatherStationContext.getStationCode(), weatherStationContext.getServiceId());

        if(existingStationContext!=null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Given weather station code already exists for given service");
        }
        return false;
    }
}