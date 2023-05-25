package com.facilio.bmsconsole.page.factory;

import java.util.HashMap;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class ServiceRequestPageFactory extends PageFactory {
    public static Page getServiceRequestPage(V3ServiceRequestContext record, FacilioModule module) throws Exception {

            Page page = new Page();

            Page.Tab tab1 = page.new Tab("summary", "serviceRequestDetails");
            page.addTab(tab1);

            boolean hideCommentsAndInformationSectionInSR = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.HIDE_COMMENTS_AND_INFORMATION_SECTION_IN_SR, Boolean.FALSE));

            //hide comments section for portal users except for atre org
            boolean hideComments = AccountUtil.getCurrentUser().isPortalUser() && AccountUtil.getCurrentOrg().getOrgId() != 418l;

            if(hideCommentsAndInformationSectionInSR){
                hideComments=true;
            }


            String tab2Title = hideComments ? "More Information" : "Comments & Information";
            Page.Tab tab2 = page.new Tab(tab2Title);
            page.addTab(tab2);

            Page.Section tab2sec1 = page.new Section();
            tab2.addSection(tab2sec1);
            HashMap<String, String> titleMap = new HashMap<>();
            titleMap.put("notes", "Comment");
            titleMap.put("documents", "Attachment");
            if (hideComments) {
                PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
                secondaryDetailsWidget.addToLayoutParams(tab2sec1, 24, 7);
                tab2sec1.addWidget(secondaryDetailsWidget);

                addCommonSubModuleWidget(tab2sec1, module, record, titleMap, false, PageWidget.WidgetType.ATTACHMENT);
            } else {
            	if(AccountUtil.getCurrentUser().isPortalUser() && AccountUtil.getCurrentOrg().getOrgId() == 418l){
                    PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
                    secondaryDetailsWidget.addToLayoutParams(tab2sec1, 24, 7);
                    tab2sec1.addWidget(secondaryDetailsWidget);
                    addCommonSubModuleWidget(tab2sec1, module, record, titleMap, false, PageWidget.WidgetType.COMMENT);
                }
                else{
                    PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
                    secondaryDetailsWidget.addToLayoutParams(tab2sec1, 24, 7);
                    tab2sec1.addWidget(secondaryDetailsWidget);
                    addCommonSubModuleWidget(tab2sec1, module, record, titleMap, false);
                }
            }
            // Showing Related list for Main app and in portal for nmdp org
            if (AccountUtil.getCurrentOrg().getOrgId() == 429l || !AccountUtil.getCurrentUser().isPortalUser()) {
                Page.Tab tab3 = page.new Tab("Related");
                boolean isRelationshipNeeded = addRelationshipSection(page, tab3, record.getModuleId());
                Page.Section tab3Sec1 = getRelatedListSectionObj(page);
                tab3.addSection(tab3Sec1);
                addRelatedListWidgets(tab3Sec1, record.getModuleId());
                if (CollectionUtils.isNotEmpty(tab3Sec1.getWidgets()) || isRelationshipNeeded) {
                    page.addTab(tab3);
                }
            }
            
            if (!AccountUtil.getCurrentUser().isPortalUser()) {
                Page.Tab tab4 = page.new Tab("History");;
                page.addTab(tab4);
                Page.Section tab4Sec1 = page.new Section();
                tab4.addSection(tab4Sec1);
                PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
                activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
                activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);
                tab4Sec1.addWidget(activityWidget);
            }

            return page;
    }
}
