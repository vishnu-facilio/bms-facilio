package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GetStateTransitionIdFromSessionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String sessionString = (String) context.get(FacilioConstants.ContextNames.SESSION);
        if (StringUtils.isNotEmpty(sessionString)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sessionString);

            context.put(FacilioConstants.ContextNames.ID, jsonObject.get("transitionId"));
        }
        return false;
    }
}
