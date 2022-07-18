package com.facilio.bmsconsoleV3.signup.weather;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddWeatherModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        addWeatherModules();
        addWeatherSubModules();
        addDefaultWeatherService();
        LOGGER.info("Weather related modules and config added successfully");
    }

    private void addWeatherModules() throws Exception {
        List< FacilioModule > modules = new ArrayList<>();
        modules.add(constructWeatherServiceModule());
        modules.add(constructWeatherStationModule());

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }

    private void addWeatherSubModules() throws Exception {
        List< FacilioModule > subModules = new ArrayList<>();
        subModules.add(constructWeatherReadingModule());
        subModules.add(constructPsychrometricModule());

        FacilioChain addSubModuleChain = TransactionChainFactory.addSystemModuleChain();
        addSubModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, subModules);
        addSubModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ModuleNames.WEATHER_STATION);
        addSubModuleChain.execute();
    }

    private void addDefaultWeatherService() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_SERVICE);

        Map<String, Object> addDefaultWeather = new HashMap<>();
        addDefaultWeather.put("name", "facilioweather");
        addDefaultWeather.put("dataInterval", 30);

        V3Util.createRecord(module, addDefaultWeather);
    }

    private FacilioModule constructWeatherServiceModule() {
        FacilioModule module = new FacilioModule("weatherservice", "Weather Service", "Weather_Service", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("dataInterval", "Data Interval", "DATA_INTERVAL", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructWeatherStationModule() {
        FacilioModule module = new FacilioModule("weatherstation", "Weather Station", "Weather_Station", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("stationCode", "Station Code", "STATION_CODE", FieldType.NUMBER, true));
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("lat", "Latitude", "LAT", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("lng", "Longtitude", "LNG", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("serviceId", "Service ID", "SERVICE_ID", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructWeatherReadingModule() {
        FacilioModule module = new FacilioModule("newWeather", "New Weather Reading", "New_Weather_Reading",
                FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("summary", "Summary", "SUMMARY", FieldType.STRING, true));

        EnumField iconField = FieldFactory.getDefaultField("icon", "Icon", "ICON", FieldType.ENUM);
        List<String> icons = Arrays.asList(
                "clear-day", "clear-night",
                "rain", "snow", "sleet", "wind",
                "fog", "cloudy",
                "partly-cloudy-day", "partly-cloudy-night",
                "hail", "thunderstorm", "tornado");
        List<EnumFieldValue<Integer>> iconValues = icons.stream().map( val -> {
            int index = icons.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        iconField.setValues(iconValues);
        fields.add(iconField);

        fields.add(FieldFactory.getDefaultField("temperature", "Temperature", "TEMPERATURE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("apparentTemperature", "Apparent Temperature", "APPARENT_TEMPERATURE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("dewPoint", "Dew Point", "DEW_POINT", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("pressure", "Pressure", "Pressure", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("humidity", "Humidity", "HUMIDITY", FieldType.DECIMAL));
        fields.addAll(FieldFactory.getDefaultReadingFields(module));
        fields.add(FieldFactory.getDefaultField("precipitationIntensity", "Precipitation Intensity", "PRECIPITATION_INTENSITY", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("precipitationIntensityError", "Precipitation Intensity Error", "PRECIPITATION_INTENSITY_ERROR", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("precipitationProbability", "Precipitation Probability", "PRECIPITATION_PROBABILITY", FieldType.DECIMAL));

        EnumField precipitationTypeField = FieldFactory.getDefaultField("precipitationType", "Precipitation Type", "PRECIPITATION_TYPE", FieldType.ENUM);
        List<String> precipitations = Arrays.asList("rain", "snow", "sleet");
        List<EnumFieldValue<Integer>> precipitationValues = precipitations.stream().map( val -> {
            int index = precipitations.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        precipitationTypeField.setValues(precipitationValues);
        fields.add(precipitationTypeField);

        fields.add(FieldFactory.getDefaultField("windSpeed", "Wind Speed", "WIND_SPEED", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("windDegree", "Wind Degree", "WIND_DEGREE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("windGust", "Wind Gust", "WIND_GUST", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("windBearing", "Wind Bearing", "WIND_BEARING", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("cloudCover", "Cloud Cover", "CLOUD_COVER", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("uvIndex", "UV Index", "UV_INDEX", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("visibility", "Visibility", "VISIBILITY", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("ozone", "Ozone", "OZONE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("nearestStormDistance", "Nearest Storm Distance", "NEAREST_STORM_DISTANCE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("nearestStormBearing", "Nearest Storm Bearing", "NEAREST_STORM_BEARING", FieldType.DECIMAL));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructPsychrometricModule() {
        FacilioModule module = new FacilioModule("newpsychrometric", "New Psychrometric", "New_Psychrometric_Reading",
                FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);
        module.setDataInterval(30);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("dewPointTemperature", "Dew Point Temperature", "DEWPOINT_TEMPERATURE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("wetBulbTemperature", "Wet Bulb Temperature", "WETBULB_TEMPERATURE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("enthalpy", "Enthalpy", "ENTHALPY", FieldType.DECIMAL));
        fields.addAll(FieldFactory.getDefaultReadingFields(module));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructCDD() {
        FacilioModule module = new FacilioModule("cdd", "CDD", "CDD_Reading", FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);
        module.setDataInterval(24 * 60);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("cdd", "Cooling Degree Days", "CDD", FieldType.DECIMAL));
        fields.addAll(FieldFactory.getDefaultReadingFields(module));
        module.setFields(fields);
        return module;
    }

    private FacilioModule constructWDD() {
        FacilioModule module = new FacilioModule("wdd", "WDD", "WDD_Reading", FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);
        module.setDataInterval(24 * 60);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("wdd", "Wet Degree Days", "WDD", FieldType.DECIMAL));
        fields.addAll(FieldFactory.getDefaultReadingFields(module));
        module.setFields(fields);
        return module;
    }

    private FacilioModule constructHDD() {
        FacilioModule module = new FacilioModule("hdd", "HDD", "HDD_Reading", FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);
        module.setDataInterval(24 * 60);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("hdd", "Heating Degree Days", "HDD", FieldType.DECIMAL));
        fields.addAll(FieldFactory.getDefaultReadingFields(module));
        module.setFields(fields);
        return module;
    }

}