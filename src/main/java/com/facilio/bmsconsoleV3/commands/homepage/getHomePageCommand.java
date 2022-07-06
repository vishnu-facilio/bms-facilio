package com.facilio.bmsconsoleV3.commands.homepage;

import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.bmsconsole.homepage.factory.HomePageFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class getHomePageCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String appLinkName = (String) context.get(FacilioConstants.ContextNames.LINK_NAME);

        HomePage homePage = HomePageFactory.getPage(appLinkName);
        if (homePage == null) {

        }
        context.put(FacilioConstants.ContextNames.HOME_PAGE, homePage);
        return false;
    }
}
