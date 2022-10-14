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
}
