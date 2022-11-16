package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class AddWeatherServiceJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WeatherServiceContext> weatherServiceContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(weatherServiceContexts)) {
            return true;
        }

        V3WeatherServiceContext weatherServiceContext = weatherServiceContexts.get(0);
        WeatherAPI.addWeatherJob(weatherServiceContext);
        return false;
    }
}