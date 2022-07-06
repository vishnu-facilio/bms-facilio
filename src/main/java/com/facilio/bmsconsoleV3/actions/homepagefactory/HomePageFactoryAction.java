package com.facilio.bmsconsoleV3.actions.homepagefactory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.urjanet.entity.Account;
import com.facilio.v3.V3Action;

public class HomePageFactoryAction extends V3Action {

    public HomePage getHomepage() {
        return homepage;
    }

    public void setHomepage(HomePage homepage) {
        this.homepage = homepage;
    }

    private HomePage homepage;

    public String getHomePage() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getHomepageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.LINK_NAME, AccountUtil.getCurrentApp().getLinkName());
        chain.execute();
        setData(FacilioConstants.ContextNames.HOME_PAGE, context.get(FacilioConstants.ContextNames.HOME_PAGE));

        return SUCCESS;
    }


}
