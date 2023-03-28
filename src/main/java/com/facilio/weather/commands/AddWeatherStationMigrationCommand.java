package com.facilio.weather.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

@Log4j
public class AddWeatherStationMigrationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {
            List<SiteContext> sites = WeatherUtil.getAllSites(true);
            if (sites == null || sites.isEmpty()) {
                context.put("message", "No sites to be found");
                return false;
            }

            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
            LOGGER.info("Sites counts ::" + sites.size());
            V3WeatherServiceContext facilioWeatherService = WeatherAPI.getWeatherServiceByName("facilioweather");

            for (SiteContext site : sites) {
                JSONObject stationData = WeatherAPI.getStationCode(site.getLocation(), site.getName());
                if (stationData == null) {
                    continue;
                }
                String stationCode = (String) stationData.get("stationCode");
                LOGGER.info("stationCode :: " + stationCode);
                V3WeatherStationContext weatherStationByCode = WeatherAPI.getExistingWeatherStation(stationCode, facilioWeatherService.getId());
                LOGGER.info("weatherStationByCode ::" + weatherStationByCode);
                if (weatherStationByCode != null) {
                    continue;
                }
                stationData.put("serviceId", facilioWeatherService.getId());
                stationData.put("service", facilioWeatherService);
                stationData.put("name", stationData.get("identifier"));
                stationData.put("description", stationData.get("identifier") + " location");
                LOGGER.info("facilioWeatherService ::" + facilioWeatherService);
                FacilioContext ctx = V3Util.createRecord(module, stationData);
                long stationId = (long) ((List) ctx.get("recordIds")).get(0);
                WeatherAPI.associateSiteWithWeatherStation(site.getId(), stationId);
            }
            context.put("message", "Migrated successfully");
        } catch (Exception e ){
            context.put("message", "Migration failed with exception. "+e);
            LOGGER.error("Migration failed with exception. ", e);
        }
        return false;
    }
}
