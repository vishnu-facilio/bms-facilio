package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import java.util.List;

public class DefaultAppUserAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private List<Long> ouIds;
    public List<Long> getOuIds() {
        return ouIds;
    }
    public void setOuIds(List<Long> ouIds) {
        this.ouIds = ouIds;
    }

    private Long appId;
    public Long getAppId() {
        return appId;
    }
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String setDefaultAppUser() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.setDefaultAppForUser();

        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ORG_USER_ID, ouIds);
        context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);

        chain.execute();
        setData("message","success");

        return SUCCESS;
    }

}
