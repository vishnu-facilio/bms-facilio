package com.facilio.weather.commands;

import com.facilio.chain.FacilioChain;

public class WeatherReadOnlyChainFactory {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getWeatherStationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchWeatherStationCommand());
        return c;
    }

    public static FacilioChain getCurrentWeatherChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetCurrentWeatherDataCommand());
        return c;
    }

    public static FacilioChain getSiteWeatherStationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSiteStationCommand());
        return c;
    }

    public static FacilioChain getBuildingWeatherFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetSiteIdForGivenBuildingIdCommand());
        c.addCommand(new GetSiteStationCommand());
        c.addCommand(new GetWeatherReadingFieldsCommand());
        return c;
    }

    public static FacilioChain getAllWeatherFieldsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetAllStationDetailsCommand());
        c.addCommand(new GetWeatherReadingFieldsCommand());
        return c;
    }
}
