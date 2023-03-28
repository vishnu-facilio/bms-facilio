package com.facilio.weather.commands;

import com.facilio.chain.FacilioChain;

public class WeatherTransactionChainFactory {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain addWeatherStationMigratinChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddWeatherStationMigrationCommand());
        return c;
    }

}
