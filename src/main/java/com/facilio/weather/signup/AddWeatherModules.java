package com.facilio.weather.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
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
        addRelationships();
        LOGGER.info("Weather related modules and config added successfully");
    }

    private void addWeatherModules() throws Exception {
        FacilioModule serviceModule = constructWeatherServiceModule();
        FacilioModule stationModule = constructWeatherStationModule(serviceModule);

        List< FacilioModule > modules = new ArrayList<>();
        modules.add(serviceModule);
        modules.add(stationModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }
    
    private void addWeatherSubModules() throws Exception {
        List< FacilioModule> subModules = new ArrayList<>();
        subModules.add(constructWeatherReadingModule());
        subModules.add(constructPsychrometricModule());
        subModules.add(constructDegreeDayModule());

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
        addDefaultWeather.put("displayName", "Facilio Weather");
        addDefaultWeather.put("dataInterval", 30);
        addDefaultWeather.put("apiKey", "default");
        addDefaultWeather.put("status", true);

        V3Util.createRecord(module, addDefaultWeather);
    }

    private FacilioModule constructWeatherServiceModule() {
        FacilioModule module = new FacilioModule("weatherservice", "Weather Service", "Weather_Service", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        StringSystemEnumField nameField =  FieldFactory.getDefaultField("name", "Name", "NAME",
                FieldType.STRING_SYSTEM_ENUM);
        nameField.setEnumName("ServiceType");
        fields.add(nameField);

        fields.add(FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("dataInterval", "Data Interval", "DATA_INTERVAL", FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("apiKey", "API Key", "API_KEY", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructWeatherStationModule(FacilioModule serviceModule) {
        FacilioModule module = new FacilioModule("weatherstation", "Weather Station", "Weather_Station", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("stationCode", "Station Code", "STATION_CODE", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("lat", "Latitude", "LAT", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("lng", "Longtitude", "LNG", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("serviceId", "Service ID", "SERVICE_ID", FieldType.NUMBER));

        NumberField cddTempField = FieldFactory.getDefaultField("cddBaseTemperature", "Cooling Degree Days Base Temperature", "CDD_BASE_TEMPERATURE", FieldType.DECIMAL);
        cddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        cddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(cddTempField);

        NumberField hddTempField = FieldFactory.getDefaultField("hddBaseTemperature", "Heading Degree Days Base Temperature", "HDD_BASE_TEMPERATURE", FieldType.DECIMAL);
        hddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        hddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(hddTempField);

        NumberField wddTempField = FieldFactory.getDefaultField("wddBaseTemperature", "Wet Degree Days Base Temperature", "WDD_BASE_TEMPERATURE", FieldType.DECIMAL);
        wddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        wddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(wddTempField);

        LookupField service = FieldFactory.getDefaultField("service", "Service", "SERVICE", FieldType.LOOKUP);
        service.setLookupModule(serviceModule);
        fields.add(service);

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

        NumberField temperatureField = FieldFactory.getDefaultField("temperature", "Temperature", "TEMPERATURE", FieldType.DECIMAL);
        temperatureField.setMetric(Metric.TEMPERATURE.getMetricId());
        temperatureField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(temperatureField);

        NumberField apparentTempField = FieldFactory.getDefaultField("apparentTemperature", "Apparent Temperature", "APPARENT_TEMPERATURE", FieldType.DECIMAL);
        apparentTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        apparentTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(apparentTempField);

        NumberField dewPointField = FieldFactory.getDefaultField("dewPoint", "Dew Point", "DEW_POINT", FieldType.DECIMAL);
        dewPointField.setMetric(Metric.TEMPERATURE.getMetricId());
        dewPointField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(dewPointField);

        NumberField pressureField = FieldFactory.getDefaultField("pressure", "Pressure", "PRESSURE", FieldType.DECIMAL);
        pressureField.setMetric(Metric.PRESSURE.getMetricId());
        pressureField.setUnitId(Unit.HECTOPASCAL.getUnitId());
        fields.add(pressureField);

        NumberField coField = FieldFactory.getDefaultField("co", "Carbon monoxide", "CO", FieldType.DECIMAL);
        coField.setMetric(Metric.CONCENTRATION.getMetricId());
        coField.setUnitId(Unit.MICROGRAMPERCUBICMETER.getUnitId());
        fields.add(coField);

        NumberField noField = FieldFactory.getDefaultField("no", "Nitrogen monoxide", "NO", FieldType.DECIMAL);
        noField.setMetric(Metric.CONCENTRATION.getMetricId());
        noField.setUnitId(Unit.MICROGRAMPERCUBICMETER.getUnitId());
        fields.add(noField);

        NumberField nh3Field = FieldFactory.getDefaultField("nh3", "Ammonia", "NH3", FieldType.DECIMAL);
        nh3Field.setMetric(Metric.CONCENTRATION.getMetricId());
        nh3Field.setUnitId(Unit.MICROGRAMPERCUBICMETER.getUnitId());
        fields.add(nh3Field);

        NumberField pm2_5Field = FieldFactory.getDefaultField("pm2_5", "Particulate 2.5", "PM2_5", FieldType.DECIMAL);
        pm2_5Field.setMetric(Metric.CONCENTRATION.getMetricId());
        pm2_5Field.setUnitId(Unit.MICROGRAMPERCUBICMETER.getUnitId());
        fields.add(pm2_5Field);

        NumberField pm10Field = FieldFactory.getDefaultField("pm10", "Particulate 10", "PM10", FieldType.DECIMAL);
        pm10Field.setMetric(Metric.CONCENTRATION.getMetricId());
        pm10Field.setUnitId(Unit.MICROGRAMPERCUBICMETER.getUnitId());
        fields.add(pm10Field);

        NumberField humidityField = FieldFactory.getDefaultField("humidity", "Humidity", "HUMIDITY", FieldType.DECIMAL);
        humidityField.setMetric(Metric.PERCENTAGE.getMetricId());
        humidityField.setUnitId(Unit.PERCENTAGE.getUnitId());
        fields.add(humidityField);

        fields.addAll(FieldFactory.getDefaultReadingFields(module));
        NumberField precipitationIntensityField = FieldFactory.getDefaultField("precipitationIntensity",
                "Precipitation Intensity", "PRECIPITATION_INTENSITY", FieldType.DECIMAL);
        precipitationIntensityField.setMetric(Metric.PRECIPITATION_INTENSITY.getMetricId());
        precipitationIntensityField.setUnitId(Unit.MILLIPERHOUR.getUnitId());
        fields.add(precipitationIntensityField);

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

        NumberField windSpeedField = FieldFactory.getDefaultField("windSpeed", "Wind Speed", "WIND_SPEED", FieldType.DECIMAL);
        windSpeedField.setMetric(Metric.SPEED.getMetricId());
        windSpeedField.setUnitId(Unit.METERPERSECOND.getUnitId());
        fields.add(windSpeedField);

        NumberField windDegreeField = FieldFactory.getDefaultField("windDegree", "Wind Degree", "WIND_DEGREE", FieldType.DECIMAL);
        windDegreeField.setMetric(Metric.ANGLE.getMetricId());
        windDegreeField.setUnitId(Unit.DEGREE.getUnitId());
        fields.add(windDegreeField);

        NumberField windGustField = FieldFactory.getDefaultField("windGust", "Wind Gust", "WIND_GUST", FieldType.DECIMAL);
        windGustField.setMetric(Metric.SPEED.getMetricId());
        windGustField.setUnitId(Unit.METERPERSECOND.getUnitId());
        fields.add(windGustField);

        NumberField windBearingField = FieldFactory.getDefaultField("windBearing", "Wind Bearing", "WIND_BEARING", FieldType.DECIMAL);
        windBearingField.setMetric(Metric.ANGLE.getMetricId());
        windBearingField.setUnitId(Unit.DEGREE.getUnitId());
        fields.add(windBearingField);

        NumberField cloudCoverField = FieldFactory.getDefaultField("cloudCover", "Cloud Cover", "CLOUD_COVER", FieldType.DECIMAL);
        cloudCoverField.setMetric(Metric.PERCENTAGE.getMetricId());
        cloudCoverField.setUnitId(Unit.PERCENTAGE.getUnitId());
        fields.add(cloudCoverField);

        fields.add(FieldFactory.getDefaultField("uvIndex", "UV Index", "UV_INDEX", FieldType.DECIMAL));

        NumberField visibilityField = FieldFactory.getDefaultField("visibility", "Visibility", "VISIBILITY", FieldType.DECIMAL);
        visibilityField.setMetric(Metric.LENGTH.getMetricId());
        visibilityField.setUnitId(Unit.METER.getUnitId());
        fields.add(visibilityField);

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

        NumberField dewPointTempField = FieldFactory.getDefaultField("dewPointTemperature", "Dew Point Temperature", "DEWPOINT_TEMPERATURE", FieldType.DECIMAL);
        dewPointTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        dewPointTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(dewPointTempField);

        NumberField webBulbTempField = FieldFactory.getDefaultField("wetBulbTemperature", "Wet Bulb Temperature", "WETBULB_TEMPERATURE", FieldType.DECIMAL);
        webBulbTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        webBulbTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(webBulbTempField);

        NumberField enthalpyField = FieldFactory.getDefaultField("enthalpy", "Enthalpy", "ENTHALPY", FieldType.DECIMAL);
        enthalpyField.setMetric(Metric.TEMPERATURE.getMetricId());
        enthalpyField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(enthalpyField);

        fields.addAll(FieldFactory.getDefaultReadingFields(module));

        module.setFields(fields);
        return module;
    }

    private void addRelationships() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule weatherStation = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
        FacilioModule site = modBean.getModule(FacilioConstants.ContextNames.SITE);
        RelationRequestContext siteVsStationRelation = new RelationRequestContext();
        siteVsStationRelation.setName("weatherstation");
        siteVsStationRelation.setDescription("Site Vs Weatherstation relationship");
        siteVsStationRelation.setFromModuleId(site.getModuleId());
        siteVsStationRelation.setToModuleId(weatherStation.getModuleId());
        siteVsStationRelation.setRelationType(RelationRequestContext.RelationType.MANY_TO_ONE);
        siteVsStationRelation.setRelationName("belongs to");
        siteVsStationRelation.setReverseRelationName("being used by");

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, siteVsStationRelation);
        chain.execute();
    }

    private FacilioModule constructDegreeDayModule() {
        FacilioModule module = new FacilioModule("degreeDayReading", "Degree Day Reading", "Degree_Day_Reading",
                FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);
        module.setDataInterval(24 * 60);
        List<FacilioField> fields = new ArrayList<>();

        NumberField cddTempField = FieldFactory.getDefaultField("cdd", "Cooling Degree Days", "CDD", FieldType.DECIMAL);
        cddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        cddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(cddTempField);

        NumberField hddTempField = FieldFactory.getDefaultField("hdd", "Heading Degree Days", "HDD", FieldType.DECIMAL);
        hddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        hddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(hddTempField);

        NumberField wddTempField = FieldFactory.getDefaultField("wdd", "Wet Degree Days", "WDD", FieldType.DECIMAL);
        wddTempField.setMetric(Metric.TEMPERATURE.getMetricId());
        wddTempField.setUnitId(Unit.CELSIUS.getUnitId());
        fields.add(wddTempField);

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