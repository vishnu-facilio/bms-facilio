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
import lombok.Getter;
import lombok.Setter;

public class HomePageFactoryAction extends V3Action {

    public HomePage getHomepage() {
        return homepage;
    }

    public void setHomepage(HomePage homepage) {
        this.homepage = homepage;
    }

    private HomePage homepage;

    @Getter @Setter
    private String linkName;

    public String getHomePage() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getHomepageChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.LINK_NAME, AccountUtil.getCurrentApp().getLinkName());
        chain.execute();
        setData(FacilioConstants.ContextNames.HOME_PAGE, context.get(FacilioConstants.ContextNames.HOME_PAGE));

        return SUCCESS;
    }

    public  String getWidgetData() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getWidgetDataChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.HomePage.WIDGET_LINKNAME, getLinkName());
        chain.execute();
        setData(FacilioConstants.ContextNames.HomePage.WIDGET_DATA, context.get(FacilioConstants.ContextNames.HomePage.WIDGET_DATA));


        return SUCCESS;
    }


}
