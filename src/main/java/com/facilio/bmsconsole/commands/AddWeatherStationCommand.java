package com.facilio.bmsconsole.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import lombok.var;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Log4j
public class AddWeatherStationCommand extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record: records) {
            var site = (V3SiteContext) record;
            try {
                JSONObject stationData = WeatherAPI.getStationCode(site.getLocation(), site.getName());
                if (stationData == null) {
                    continue;
                }
                String stationCode = (String) stationData.get("stationCode");
                V3WeatherServiceContext defaultWeatherService = WeatherAPI.getWeatherServiceByName(FacilioProperties.getConfig("weather.service"));
                V3WeatherStationContext weatherStationByCode = WeatherAPI.getExistingWeatherStation(stationCode, defaultWeatherService.getId());
                if (weatherStationByCode != null) {
                    WeatherAPI.associateSiteWithWeatherStation(site.getId(), weatherStationByCode.getId());
                    continue;
                }

                String locationName = site.getLocation().getName();
                if(locationName!=null) {
                    locationName = locationName.split("_")[0];
                }
                stationData.put("name", locationName);
                stationData.put("serviceId", defaultWeatherService.getId());
                stationData.put("service", defaultWeatherService);
                FacilioContext ctx = V3Util.createRecord(module, stationData);
                long stationId = (long) ((List) ctx.get("recordIds")).get(0);
                WeatherAPI.associateSiteWithWeatherStation(site.getId(), stationId);
            }catch (Exception e) {
                LOGGER.info("Failed to add weather station for site id :: "+site.getId());
            }
        }
        return false;
    }
}
