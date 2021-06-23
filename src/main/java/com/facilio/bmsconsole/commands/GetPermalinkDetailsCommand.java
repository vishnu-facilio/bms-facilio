package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;

public class GetPermalinkDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String token = (String) context.get(FacilioConstants.ContextNames.TOKEN);

        if (StringUtils.isNotEmpty(token)) {
    		Object sessionInfo = IAMUserUtil.getPermalinkDetails(token);
            context.put(FacilioConstants.ContextNames.SESSION, sessionInfo);
        }
        return false;
    }
}
