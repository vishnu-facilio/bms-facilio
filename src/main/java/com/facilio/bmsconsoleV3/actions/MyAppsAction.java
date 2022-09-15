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

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
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

    public String listMyAppsForUser() throws Exception {


        FacilioChain chain = TransactionChainFactoryV3.getListMyAppsForUser();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ORG_USER_ID,ouId );
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId );
        chain.execute();


        setData("myApps", context.get(FacilioConstants.ContextNames.MY_APPS));
        return SUCCESS;
    }
}
