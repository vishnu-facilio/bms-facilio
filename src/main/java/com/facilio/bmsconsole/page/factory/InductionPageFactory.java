package com.facilio.bmsconsole.page.factory;

import static com.facilio.bmsconsole.page.factory.AssetPageFactory.addRelatedListWidget;

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
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
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

import io.jsonwebtoken.lang.Collections;

public class InductionPageFactory extends PageFactory {

	public static Page getInductionTemplatePage(InductionTemplateContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
		
        Page.Tab summaryTab = page.new Tab("summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
        secondaryDetailsWidget.addToLayoutParams(SummarySec, 24, 7);
        secondaryDetailsWidget.setWidgetParams(getInductionTemplateSummaryParams());
        SummarySec.addWidget(secondaryDetailsWidget);
        
        PageWidget InductionDetails = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_inductionDetails);
        InductionDetails.addToLayoutParams(SummarySec, 24, 6);
        SummarySec.addWidget(InductionDetails);
        
        PageWidget InductionPageDetails = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_inductionPageDetails);
        InductionPageDetails.addToLayoutParams(SummarySec, 24, 12);
        SummarySec.addWidget(InductionPageDetails);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        Page.Tab triggerTab = page.new Tab("trigger");
        Page.Section triggerTabSec = page.new Section();
        
        PageWidget InductionTriggers = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_inductionTriggers);
        InductionTriggers.addToLayoutParams(triggerTabSec, 24, 24);
        
        triggerTabSec.addWidget(InductionTriggers);
        
        triggerTab.addSection(triggerTabSec);
        page.addTab(triggerTab);
        
        Page.Tab insightTab = page.new Tab("insight");
        Page.Section insightTabSec = page.new Section();
        
//        PageWidget InductionInsights = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_InductionInsights);
//        InductionInsights.addToLayoutParams(insightTabSec, 24, 8);
//        insightTabSec.addWidget(InductionInsights);
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_RESPONSE));
        
        PageWidget InductionCountInsight = new PageWidget(PageWidget.WidgetType.CHART,"InductionCountInsight");
        InductionCountInsight.addToLayoutParams(insightTabSec, 24, 8);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("responseStatus"), ResponseContext.ResponseStatus.DISABLED.getIndex()+"", NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), record.getId()+"", NumberOperators.EQUALS));
        
		addChartParams(InductionCountInsight, "donut",null, "responseStatus", null,  null , null, DateOperators.CURRENT_YEAR, null, criteria, "createdTime", FacilioConstants.Induction.INDUCTION_RESPONSE);
		InductionCountInsight.addToWidgetParams("type", "inductionChart");
		insightTabSec.addWidget(InductionCountInsight);
		
        PageWidget InductionQuestions = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_inductionQuestions);
        InductionQuestions.addToLayoutParams(insightTabSec, 24, 6);
        insightTabSec.addWidget(InductionQuestions);
        
        insightTab.addSection(insightTabSec);
        page.addTab(insightTab);

        Page.Tab historyTab = page.new Tab("History");
        Page.Section historyTabSec = page.new Section();
        
        PageWidget history = new PageWidget(PageWidget.WidgetType.INDUCTION_TEMPLATE_history);
        history.addToLayoutParams(historyTabSec, 24, 3);
        
        historyTabSec.addWidget(history);
        
        historyTab.addSection(historyTabSec);
        page.addTab(historyTab);
        

        return page;
	}
	
	private static JSONObject getInductionResponseSummaryParams() {
		
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
	
	
	private static JSONObject getInductionTemplateSummaryParams() {
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


	public static Page getInductionResponsePage(InductionResponseContext record, FacilioModule module) throws Exception {
		
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
            secondaryDetailsWidget.setWidgetParams(getInductionResponseSummaryParams());
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
            
            
            Tab relatedList = page.new Tab("Related Records");
    		Section relatedListSec = page.new Section();
    		addRelatedListWidgets(relatedListSec, module.getModuleId());
    		relatedList.addSection(relatedListSec);
    		if(CollectionUtils.isNotEmpty(relatedListSec.getWidgets())) {
    			page.addTab(relatedList);
    		}
        }
        
        Page.Tab activityTab = page.new Tab("Activity");
        Page.Section activitySec = page.new Section();
        
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        
        JSONObject widgetParams = new JSONObject();
        
        widgetParams.put("activityModuleName", FacilioConstants.Induction.INDUCTION_RESPONSE_ACTIVITY);
        
        activityWidget.setWidgetParams(widgetParams);
        activityWidget.addToLayoutParams(activitySec, 24, 3);
        activitySec.addWidget(activityWidget);
        
        activityTab.addSection(activitySec);
        
        page.addTab(activityTab);
        
        return page;
	}



}
