package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;



public class MyAppsAction extends V3Action {

    private Long ouId;
    public Long getAppId() {
        return ouId;
    }
    public void setAppId(Long appId) {
        this.ouId = appId;
    }

    public String listMyApps() throws Exception {


        FacilioChain chain = TransactionChainFactoryV3.getListMyApps();
        FacilioContext context = chain.getContext();
        HttpServletRequest request = ServletActionContext.getRequest();

        context.put(FacilioConstants.ContextNames.APP_DOMAIN, request.getServerName());
        context.put(FacilioConstants.ContextNames.ORG_USER_ID, AccountUtil.getCurrentUser().getOuid());
        chain.execute();


        setData("myApps", context.get(FacilioConstants.ContextNames.MY_APPS));
        return SUCCESS;
    }
}
