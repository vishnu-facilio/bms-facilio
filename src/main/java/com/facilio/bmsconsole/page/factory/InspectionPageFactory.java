package com.facilio.bmsconsole.page.factory;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.constants.FacilioConstants;
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
        
        PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_secondaryDetailsWidget);
        secondaryDetailsWidget.addToLayoutParams(SummarySec, 24, 6);
        SummarySec.addWidget(secondaryDetailsWidget);
        
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
	
	
	public static Page getInspectionResponsePage(InspectionResponseContext record, FacilioModule module) throws Exception {
		
		Page page = new Page();
		
		
        Page.Tab summaryTab = page.new Tab("summary");
        Page.Section SummarySec = page.new Section();
        
        PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.INSPECTION_RESPONSE_WIDGET);
        secondaryDetailsWidget.addToLayoutParams(SummarySec, 24, 24);
        SummarySec.addWidget(secondaryDetailsWidget);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        return page;
	}

}
