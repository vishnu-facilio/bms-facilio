package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class WeatherServiceCreateValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherServiceContext> weatherServiceContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherServiceContexts)) {
            return true;
        }

        V3WeatherServiceContext weatherServiceContext = weatherServiceContexts.get(0);
        if(StringUtils.isEmpty(weatherServiceContext.getName())) {
            if(StringUtils.isEmpty(weatherServiceContext.getDisplayName())) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Both service name and displayName can't be empty");
            }
            String serviceName = weatherServiceContext.getDisplayName();
            serviceName = serviceName.toLowerCase();
            serviceName = serviceName.replaceAll("[^a-z0-9]", "");
            weatherServiceContext.setName(serviceName);
        }

        V3WeatherServiceContext existingService =  WeatherAPI.getWeatherServiceByName(weatherServiceContext.getName());
        if(existingService != null) { // normal check
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Given weather service name already exists");
        }

        return false;
    }

}