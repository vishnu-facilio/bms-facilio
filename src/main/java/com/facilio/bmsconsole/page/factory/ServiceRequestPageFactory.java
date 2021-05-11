package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;

public class ServiceRequestPageFactory extends PageFactory {
    public static Page getServiceRequestPage(ServiceRequestContext record, FacilioModule module) throws Exception {

        if (FacilioProperties.isDevelopment() || (AccountUtil.getCurrentOrg().getId() == 204l || AccountUtil.getCurrentOrg().getId() == 75l || AccountUtil.getCurrentOrg().getId() == 1l)) {
            Page page = new Page();

            Page.Tab tab1 = page.new Tab("summary");
            page.addTab(tab1);

            Page.Section tab1Sec1 = page.new Section();
            tab1.addSection(tab1Sec1);
            PageWidget emailThreadWidget = new PageWidget(PageWidget.WidgetType.SR_EMAIL_THREAD);
            emailThreadWidget.addToLayoutParams(tab1Sec1, 18, 24);
            tab1Sec1.addWidget(emailThreadWidget);

            PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SR_DETAILS_WIDGET);
            detailsWidget.addToLayoutParams(tab1Sec1, 6, 24);
            detailsWidget.addToWidgetParams("hideBg", true);
            tab1Sec1.addWidget(detailsWidget);
            String tab2Title = AccountUtil.getCurrentUser().isPortalUser() ? "Attachments" : "Comments & Attachments";
            Page.Tab tab2 = page.new Tab(tab2Title);
            page.addTab(tab2);

            Page.Section tab2sec1 = page.new Section();
            tab2.addSection(tab2sec1);
            HashMap<String, String> titleMap = new HashMap<>();
            titleMap.put("notes", "Comment");
            titleMap.put("documents", "Attachment");
            if (AccountUtil.getCurrentUser().isPortalUser()) {
                addCommonSubModuleWidget(tab2sec1, module, record, titleMap, false, PageWidget.WidgetType.ATTACHMENT);
            } else {
                addCommonSubModuleWidget(tab2sec1, module, record, titleMap, false);
            }
            if (!AccountUtil.getCurrentUser().isPortalUser()) {
                Page.Tab tab3 = page.new Tab("related list");
                Page.Section tab3Sec1 = page.new Section();
                tab3.addSection(tab3Sec1);
                addRelatedListWidgets(tab3Sec1, record.getModuleId());
                if (CollectionUtils.isNotEmpty(tab3Sec1.getWidgets())) {
                    page.addTab(tab3);
                }
                Page.Tab tab4 = page.new Tab("Activity");
                page.addTab(tab4);
                Page.Section tab4Sec1 = page.new Section();
                tab4.addSection(tab4Sec1);
                PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
                activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
                activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);
                tab4Sec1.addWidget(activityWidget);
            }

            return page;
        } else {
            return CustomModulePageFactory.getCustomModulePage(record, module);
        }
    }
}
