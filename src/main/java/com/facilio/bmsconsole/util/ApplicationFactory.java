package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.fw.BeanFactory;

import java.util.*;

public class ApplicationFactory {
    private static List<ApplicationContext> applications = Collections.unmodifiableList(initApplication());
    private static List<ApplicationContext> initApplication() {
        try {
            applications.add(facilioApp()); // default facilio app
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applications;
    }

    private static ApplicationContext facilioApp() throws Exception {
        List<WebTabGroupContext> webTabGroups = new ArrayList<>();
        List<WebTabContext> webTabs = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ApplicationContext applicationContext = ApplicationApi.getDefaultApplication();
//            FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
//            FacilioContext chainContext = chain.getContext();
//            chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroups);
        return null;
    }
}
