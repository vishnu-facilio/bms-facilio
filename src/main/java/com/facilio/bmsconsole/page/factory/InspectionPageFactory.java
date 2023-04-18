package com.facilio.bmsconsole.page.factory;

import static com.facilio.bmsconsole.page.factory.AssetPageFactory.addRelatedListWidget;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMCreationType;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.ResponseContext;

public class InspectionPageFactory extends PageFactory {

	public static Page getInspectionTemplatePage(InspectionTemplateContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
		
        Page.Tab summaryTab = page.new Tab("summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
        secondaryDetailsWidget.addToLayoutParams(SummarySec, 24, 7);
        JSONObject widgetParams = getdetailsWidgetParam1(record,module);
        widgetParams.put("isConfigurationWidget", true);
        secondaryDetailsWidget.setWidgetParams(widgetParams);
        
        if(AccountUtil.getCurrentOrg().getOrgId() == 17l) {
        	if(record!=null && record.getData()!=null && record.getData().get("picklist") != null) {
        		long actionTypeValue = Long.parseLong(record.getData().get("picklist").toString());
        		if(actionTypeValue == 1) {
        			SummarySec.addWidget(secondaryDetailsWidget);
        		}
        	}
        }
        else {
        	SummarySec.addWidget(secondaryDetailsWidget);
        }
        
        PageWidget secondaryDetailsWidget1 = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
        secondaryDetailsWidget1.addToLayoutParams(SummarySec, 24, 7);
        widgetParams = getdetailsWidgetParam2(record,module);
        widgetParams.put("isConfigurationWidget", false);
        secondaryDetailsWidget1.setWidgetParams(widgetParams);
        SummarySec.addWidget(secondaryDetailsWidget1);
        
        PageWidget inspectionDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionDetails);
        inspectionDetails.addToLayoutParams(SummarySec, 24, 6);
        SummarySec.addWidget(inspectionDetails);
        
        PageWidget inspectionPageDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionPageDetails);
        inspectionPageDetails.addToLayoutParams(SummarySec, 24, 12);
        SummarySec.addWidget(inspectionPageDetails);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        Page.Tab triggerTab = page.new Tab("trigger");
        Page.Section triggerTabSec = page.new Section();
        
        PageWidget inspectionTriggers = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionTriggers);
        inspectionTriggers.addToLayoutParams(triggerTabSec, 24, 24);
        
        triggerTabSec.addWidget(inspectionTriggers);
        
        triggerTab.addSection(triggerTabSec);
        page.addTab(triggerTab);
        
        Page.Tab insightTab = page.new Tab("insight");
        Page.Section insightTabSec = page.new Section();
        
//        PageWidget inspectionInsights = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionInsights);
//        inspectionInsights.addToLayoutParams(insightTabSec, 24, 8);
//        insightTabSec.addWidget(inspectionInsights);
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));
        
        PageWidget inspectionCountInsight = new PageWidget(PageWidget.WidgetType.CHART,"inspectionCountInsight");
        inspectionCountInsight.addToLayoutParams(insightTabSec, 24, 8);
        
        Criteria criteria = new Criteria();
        
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("responseStatus"), ResponseContext.ResponseStatus.DISABLED.getIndex()+"", NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), record.getId()+"", NumberOperators.EQUALS));
        
		addChartParams(inspectionCountInsight, "donut",null, "responseStatus", null,  null , null, DateOperators.CURRENT_YEAR, null, criteria, "sysCreatedTime", FacilioConstants.Inspection.INSPECTION_RESPONSE);
		inspectionCountInsight.addToWidgetParams("type", "inspectionChart");
		insightTabSec.addWidget(inspectionCountInsight);
		
        PageWidget inspectionQuestions = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionQuestions);
        inspectionQuestions.addToLayoutParams(insightTabSec, 24, 6);
        insightTabSec.addWidget(inspectionQuestions);
        
        insightTab.addSection(insightTabSec);
        page.addTab(insightTab);

        Page.Tab historyTab = page.new Tab("History");
        Page.Section historyTabSec = page.new Section();
        
        PageWidget history = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_history);
        history.addToLayoutParams(historyTabSec, 24, 3);
        
        historyTabSec.addWidget(history);
        
        historyTab.addSection(historyTabSec);
        page.addTab(historyTab);
        

        return page;
	}
	
	
	private static JSONObject getdetailsWidgetParam2(InspectionTemplateContext record, FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject widgetParam = new JSONObject();
		
		JSONArray fieldList = new JSONArray();
		
		fieldList.add("vendor");
		fieldList.add("tenant");
		fieldList.add("category");
		fieldList.add("priority");
		fieldList.add("assignmentGroup");
		fieldList.add("assignedTo");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> customFields = modBean.getAllCustomFields(module.getName());
		if(customFields != null) {
			fieldList.addAll(customFields.stream().filter(f -> !f.getName().equals("formId")).map(FacilioField::getName).collect(Collectors.toList()));
		}
		
		widgetParam.put("fields", fieldList);
		
		return widgetParam;
		
	}
	
	private static JSONObject getInspectionResponseWidgetParams(FacilioModule inspectionResponse) throws Exception {
		JSONObject widgetParam = new JSONObject();
		
		JSONArray fieldList = new JSONArray();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		fieldList.add("description");
		fieldList.add("siteId");
		fieldList.add("responseStatus");
		fieldList.add("moduleState");
		fieldList.add("totalAnswered");
		fieldList.add("resource");
		
		fieldList.add("vendor");
		fieldList.add("tenant");
		fieldList.add("category");
		fieldList.add("priority");
		fieldList.add("assignmentGroup");
		fieldList.add("assignedTo");
		
		fieldList.add("scheduledWorkStart");
		fieldList.add("scheduledWorkEnd");
		fieldList.add("actualWorkStart");
		fieldList.add("actualWorkEnd");
		
		fieldList.add("createdTime");
		fieldList.add("sysCreatedBy");
		fieldList.add("sysModifiedTime");
		fieldList.add("sysModifiedBy");
		
		List<FacilioField> customFields = modBean.getAllCustomFields(inspectionResponse.getName());
		if(customFields != null) {
			fieldList.addAll(customFields.stream().filter(f -> !f.getName().equals("formId")).map(FacilioField::getName).collect(Collectors.toList()));
		}
		
		widgetParam.put("fields", fieldList);
		
		return widgetParam;
	}


	private static JSONObject getdetailsWidgetParam1(InspectionTemplateContext record, FacilioModule module) {
		// TODO Auto-generated method stub
		
		JSONObject widgetParam = new JSONObject();
		
		JSONArray fieldList = new JSONArray();
		
		fieldList.add("creationType");
		fieldList.add("sysCreatedTime");
		fieldList.add("sysCreatedBy");
		fieldList.add("sysModifiedTime");
		fieldList.add("sysModifiedBy");
		
		if(record.getCreationType() == InspectionTemplateContext.CreationType.SINGLE.getIndex()) {
			fieldList.add("siteId");
			fieldList.add("resource");
		}
		else {
			fieldList.add("sites");
			fieldList.add("buildings");
			
			if(record.getAssignmentTypeEnum() == PMAssignmentType.ASSET_CATEGORY) {
				fieldList.add("assetCategory");
			}
			if(record.getAssignmentTypeEnum() == PMAssignmentType.SPACE_CATEGORY) {
				fieldList.add("spaceCategory");
			}
			
			fieldList.add("assignmentType");
		}
		
		widgetParam.put("fields", fieldList);
		
		return widgetParam;
	}


	public static Page getInspectionResponsePage(InspectionResponseContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
        Page.Tab summaryTab = page.new Tab("Summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget summaryWidget = new PageWidget(PageWidget.WidgetType.INSPECTION_RESPONSE_WIDGET);
        summaryWidget.addToLayoutParams(SummarySec, 24, 24);
        SummarySec.addWidget(summaryWidget);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        ApplicationContext currentApp = AccountUtil.getCurrentApp();
        //if(currentApp == null || currentApp.getAppCategoryEnum() != ApplicationContext.AppCategory.PORTALS) {
        	
        	Page.Tab notesAndAttachmentTab = page.new Tab("Notes & Information");
            Page.Section notesAndAttachmentSec = page.new Section();
            
            PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.Q_AND_A_SECONDARY_DETAILS_WIDGET);
            secondaryDetailsWidget.addToLayoutParams(notesAndAttachmentSec, 24, 7);
            secondaryDetailsWidget.setWidgetParams(getInspectionResponseWidgetParams(module));
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
			boolean isRelationshipNeeded = addRelationshipSection(page, relatedList, module.getModuleId());
    		Section relatedListSec = getRelatedListSectionObj(page);
    		addRelatedListWidgets(relatedListSec, module.getModuleId());
    		relatedList.addSection(relatedListSec);
    		
    		if(CollectionUtils.isNotEmpty(relatedListSec.getWidgets()) || isRelationshipNeeded) {
    			page.addTab(relatedList);
    		}
        //}
        
        Page.Tab activityTab = page.new Tab("History");;
        Page.Section activitySec = page.new Section();
        
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        
        JSONObject widgetParams = new JSONObject();
        
        widgetParams.put("activityModuleName", FacilioConstants.Inspection.INSPECTION_RESPONSE_ACTIVITY);
        
        activityWidget.setWidgetParams(widgetParams);
        activityWidget.addToLayoutParams(activitySec, 24, 3);
        activitySec.addWidget(activityWidget);
        
        activityTab.addSection(activitySec);
        
        page.addTab(activityTab);
        
        return page;
	}


}
