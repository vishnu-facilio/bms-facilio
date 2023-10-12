package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class FetchPortfolioCardsDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> cardData = new HashMap<>();
        cardData.put("sites",SpaceAPI.getSitesCount());
        cardData.put("buildings",SpaceAPI.getBuildingsCount());
        cardData.put("floors",SpaceAPI.getFloorsCount());
        cardData.put("spaces",SpaceAPI.getSpacesCount());
        context.put("cardData",cardData);
        return false;
    }
}
