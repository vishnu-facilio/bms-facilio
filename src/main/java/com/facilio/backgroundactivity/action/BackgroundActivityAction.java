package com.facilio.backgroundactivity.action;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class BackgroundActivityAction extends FacilioAction {

    @Getter @Setter
    private Long activityId;

    public String fetchParentActivities() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getParentActivitiesChain();
        FacilioContext context = chain.getContext();
        context.put(Constants.ACTIVITY_ID, activityId);
        chain.execute();
        setResult(Constants.PARENT_ACTIVITY_LIST,context.get(Constants.PARENT_ACTIVITY_LIST));
        return SUCCESS;
    }

}
