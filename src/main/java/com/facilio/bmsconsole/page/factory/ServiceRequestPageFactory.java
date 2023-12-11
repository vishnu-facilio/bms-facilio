package com.facilio.bmsconsole.page.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
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
            if (!AccountUtil.getCurrentUser().isPortalUser() && isSurveyAvailable(record.getId(),module.getModuleId())) {
                addMetricandTimelogTab(page, record.getId(), module.getModuleId());
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
    private static void addMetricandTimelogTab(Page page, long serviceRequestId,long moduleId) throws Exception {
        Page.Tab metricandTimelogTab = page.new Tab("timelog and metrics");
        page.addTab(metricandTimelogTab);

        addWorkOrderSurveyPageWidget(page, metricandTimelogTab,serviceRequestId,moduleId);

//        Page.Section metrictimelogSection = page.new Section();
//        metricandTimelogTab.addSection(metrictimelogSection);
//
//        // metric and timelog widget
//        PageWidget stateTransitionTimelogWidget = new PageWidget(PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG);
//        stateTransitionTimelogWidget.addToLayoutParams(metrictimelogSection, 24, 8);
//        metrictimelogSection.addWidget(stateTransitionTimelogWidget);

    }

    private static void addWorkOrderSurveyPageWidget(Page page, Page.Tab metricandTimelogTab, long serviceRequestId, long moduleId) throws Exception{

        if(isSurveyAvailable(serviceRequestId,moduleId)){

            Page.Section surveyTimeLogSection = page.new Section();
            metricandTimelogTab.addSection(surveyTimeLogSection);

            PageWidget surveyTimelogWidget = new PageWidget(PageWidget.WidgetType.SURVEY_RESPONSE_WIDGET);
            surveyTimelogWidget.addToLayoutParams(surveyTimeLogSection, 24, 8);
            surveyTimeLogSection.addWidget(surveyTimelogWidget);
        }
    }

    private static boolean isSurveyAvailable(long serviceRequestId,long moduleId) throws Exception{

        ModuleBean bean = Constants.getModBean();
        FacilioModule module = bean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE);
        List<FacilioField> fields  = bean.getAllFields(module.getName());

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SurveyResponseContext> builder = new SelectRecordsBuilder<SurveyResponseContext>()
                .select(fields)
                .moduleName(module.getName())
                .beanClass(SurveyResponseContext.class)
                .andCondition(CriteriaAPI.getCondition(module.getTableName()+".SERVICE_REQUEST_ID",module.getTableName()+".serviceRequestId",String.valueOf(serviceRequestId), StringOperators.IS));

        if(!AccountUtil.getCurrentUser().isSuperAdmin()){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assignedTo"),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()), NumberOperators.EQUALS));
        }

        List<SurveyResponseContext> surveyResponseRecords = builder.get();

        return surveyResponseRecords.size() > 0;
    }

}
