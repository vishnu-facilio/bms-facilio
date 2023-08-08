package com.facilio.bmsconsole.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.weather.util.WeatherAPI;
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
        if(weatherServiceContext.getName() == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Weather service name can't be empty");
        }
        String serviceName = weatherServiceContext.getName().toString();

        if(!FacilioProperties.getConfig("weather.service").equals(serviceName) && StringUtils.isEmpty(weatherServiceContext.getApiKey())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "API key can't be empty for custom weather service");
        }

        V3WeatherServiceContext existingService =  WeatherAPI.getWeatherServiceByDisplayName(weatherServiceContext.getDisplayName());
        if(existingService != null) { // normal check
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Given weather service display name already exists");
        }

        return false;
    }

}