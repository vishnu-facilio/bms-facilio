package com.facilio.bmsconsole.page.factory;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.ResponseContext;

public class SurveyPageFactory extends PageFactory {

	public static Page getSurveyTemplatePage(SurveyTemplateContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
		
        Page.Tab summaryTab = page.new Tab("summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
        secondaryDetailsWidget.addToLayoutParams(SummarySec, 24, 7);
        secondaryDetailsWidget.setWidgetParams(getSurveyTemplateSummaryParams());
        SummarySec.addWidget(secondaryDetailsWidget);
        
        PageWidget SurveyDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionDetails);
        SurveyDetails.addToLayoutParams(SummarySec, 24, 6);
        SummarySec.addWidget(SurveyDetails);
        
        PageWidget SurveyPageDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionPageDetails);
        SurveyPageDetails.addToLayoutParams(SummarySec, 24, 12);
        SummarySec.addWidget(SurveyPageDetails);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        Page.Tab triggerTab = page.new Tab("trigger");
        Page.Section triggerTabSec = page.new Section();
        
        PageWidget SurveyTriggers = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionTriggers);
        SurveyTriggers.addToLayoutParams(triggerTabSec, 24, 24);
        
        triggerTabSec.addWidget(SurveyTriggers);
        
        triggerTab.addSection(triggerTabSec);
        page.addTab(triggerTab);
        
        Page.Tab insightTab = page.new Tab("insight");
        Page.Section insightTabSec = page.new Section();
        
//        PageWidget SurveyInsights = new PageWidget(PageWidget.WidgetType.SURVEY_TEMPLATE_SurveyInsights);
//        SurveyInsights.addToLayoutParams(insightTabSec, 24, 8);
//        insightTabSec.addWidget(SurveyInsights);
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Survey.SURVEY_RESPONSE));
        
        PageWidget SurveyCountInsight = new PageWidget(PageWidget.WidgetType.CHART,"SurveyCountInsight");
        SurveyCountInsight.addToLayoutParams(insightTabSec, 24, 8);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("responseStatus"), ResponseContext.ResponseStatus.DISABLED.getIndex()+"", NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), record.getId()+"", NumberOperators.EQUALS));
        
		addChartParams(SurveyCountInsight, "donut",null, "responseStatus", null,  null , null, DateOperators.CURRENT_YEAR, null, criteria, "createdTime", FacilioConstants.Survey.SURVEY_RESPONSE);
		SurveyCountInsight.addToWidgetParams("type", "inspectionChart");
		insightTabSec.addWidget(SurveyCountInsight);
		
        PageWidget SurveyQuestions = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionQuestions);
        SurveyQuestions.addToLayoutParams(insightTabSec, 24, 6);
        insightTabSec.addWidget(SurveyQuestions);
        
        insightTab.addSection(insightTabSec);
        page.addTab(insightTab);

        Page.Tab historyTab = page.new Tab("History");
        Page.Section historyTabSec = page.new Section();
        
        PageWidget history = new PageWidget(PageWidget.WidgetType.SURVEY_TEMPLATE_history);
        history.addToLayoutParams(historyTabSec, 24, 3);
        
        historyTabSec.addWidget(history);
        
        historyTab.addSection(historyTabSec);
        page.addTab(historyTab);
        

        return page;
	}
	
	private static JSONObject getSurveyResponseSummaryParams() {
		
		JSONObject returnObj = new JSONObject();
		
		JSONArray fieldList = new JSONArray();
		fieldList.add("description");
		fieldList.add("siteId");
		fieldList.add("responseStatus");
		fieldList.add("moduleState");
		fieldList.add("totalAnswered");
		
		fieldList.add("parent");
		fieldList.add("vendor");
		fieldList.add("people");
		fieldList.add("scheduledWorkStart");
		fieldList.add("scheduledWorkEnd");
		fieldList.add("actualWorkStart");
		fieldList.add("actualWorkEnd");
		
		fieldList.add("createdTime");
		fieldList.add("sysCreatedBy");
		fieldList.add("sysModifiedTime");
		fieldList.add("sysModifiedBy");
		
		returnObj.put("fields", fieldList);
		return returnObj;
	}
	
	
	private static JSONObject getSurveyTemplateSummaryParams() {
		JSONObject returnObj = new JSONObject();
		
		JSONArray fieldList = new JSONArray();
		fieldList.add("description");
		fieldList.add("siteApplyTo");
		fieldList.add("sites");
		fieldList.add("totalPages");
		fieldList.add("totalQuestions");
		fieldList.add("moduleState");
		fieldList.add("sysCreatedTime");
		fieldList.add("sysCreatedBy");
		fieldList.add("sysModifiedTime");
		fieldList.add("sysModifiedBy");
		returnObj.put("fields", fieldList);
		return returnObj;
	}


	public static Page getSurveyResponsePage(SurveyResponseContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
        Page.Tab summaryTab = page.new Tab("Summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget summaryWidget = new PageWidget(PageWidget.WidgetType.INSPECTION_RESPONSE_WIDGET);
        summaryWidget.addToLayoutParams(SummarySec, 24, 24);
        SummarySec.addWidget(summaryWidget);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        ApplicationContext currentApp = AccountUtil.getCurrentApp();
        if(currentApp == null || currentApp.getAppCategoryEnum() != ApplicationContext.AppCategory.PORTALS) {
        	
        	Page.Tab notesAndAttachmentTab = page.new Tab("Notes & Information");
            Page.Section notesAndAttachmentSec = page.new Section();
            
            PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
            secondaryDetailsWidget.addToLayoutParams(notesAndAttachmentSec, 24, 7);
            secondaryDetailsWidget.setWidgetParams(getSurveyResponseSummaryParams());
            notesAndAttachmentSec.addWidget(secondaryDetailsWidget);
            
            PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT,"Notes");
            notesWidget.setTitle("Notes");
            notesWidget.addToLayoutParams(notesAndAttachmentSec, 24, 8);
            notesAndAttachmentSec.addWidget(notesWidget);
            
            PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT,"Documents");
            attachmentWidget.addToLayoutParams(notesAndAttachmentSec, 24, 6);
            notesAndAttachmentSec.addWidget(attachmentWidget);
            
            notesAndAttachmentTab.addSection(notesAndAttachmentSec);
            page.addTab(notesAndAttachmentTab);
            
            
            Tab relatedList = page.new Tab("Related");
            boolean isRelationshipAdded = addRelationshipSection(page, relatedList, module.getModuleId());
    		Section relatedListSec = getRelatedListSectionObj(page);
    		addRelatedListWidgets(relatedListSec, module.getModuleId());
    		relatedList.addSection(relatedListSec);
    		if(CollectionUtils.isNotEmpty(relatedListSec.getWidgets()) || isRelationshipAdded) {
    			page.addTab(relatedList);
    		}
        }
        
        Page.Tab activityTab = page.new Tab("History");;
        Page.Section activitySec = page.new Section();
        
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        
        JSONObject widgetParams = new JSONObject();
        
        widgetParams.put("activityModuleName", FacilioConstants.Survey.SURVEY_RESPONSE_ACTIVITY);
        
        activityWidget.setWidgetParams(widgetParams);
        activityWidget.addToLayoutParams(activitySec, 24, 3);
        activitySec.addWidget(activityWidget);
        
        activityTab.addSection(activitySec);
        
        page.addTab(activityTab);
        
        return page;
	}
}
