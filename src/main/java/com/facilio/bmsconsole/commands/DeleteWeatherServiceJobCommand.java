package com.facilio.bmsconsole.commands;

import com.facilio.weather.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class DeleteWeatherServiceJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> serviceIds = (List<Long>) context.get("recordIds");
        if(CollectionUtils.isNotEmpty(serviceIds)) {
            List<V3WeatherServiceContext> serviceList = V3RecordAPI.getRecordsList(moduleName,serviceIds,V3WeatherServiceContext.class);
            if (CollectionUtils.isNotEmpty(serviceList)) {
                for(V3WeatherServiceContext service : serviceList) {
                    WeatherAPI.deleteWeatherServiceJob(service);
                }
            }
        }
        return false;
    }
}