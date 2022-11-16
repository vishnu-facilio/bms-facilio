package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WeatherAPI;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherServiceContext;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import lombok.var;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
                long stationCode = (long) stationData.get("stationCode");
                V3WeatherStationContext weatherStationByCode = WeatherAPI.getWeatherStationByCode(stationCode);
                if (weatherStationByCode != null) {
                    WeatherAPI.associateSiteWithWeatherStation(site.getId(), weatherStationByCode.getId());
                    continue;
                }
                V3WeatherServiceContext facilioWeatherService = WeatherAPI.getWeatherServiceByName("facilioweather");
                String locationName = site.getLocation().getName();
                if(locationName!=null) {
                    locationName = locationName.split("_")[0];
                }
                stationData.put("name", locationName);
                stationData.put("serviceId", facilioWeatherService.getId());
                stationData.put("service", facilioWeatherService);
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
