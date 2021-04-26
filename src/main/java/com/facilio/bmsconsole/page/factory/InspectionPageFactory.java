package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.modules.FacilioModule;

public class InspectionPageFactory extends PageFactory {

	public static Page getInspectionTemplatePage(InspectionTemplateContext record, FacilioModule module) {
		
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
        
        PageWidget inspectionInsights = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionInsights);
        inspectionInsights.addToLayoutParams(insightTabSec, 24, 8);
        insightTabSec.addWidget(inspectionInsights);
        
        PageWidget inspectionQuestions = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionQuestions);
        inspectionQuestions.addToLayoutParams(insightTabSec, 24, 6);
        insightTabSec.addWidget(inspectionQuestions);
        
        insightTab.addSection(insightTabSec);
        page.addTab(insightTab);

        Page.Tab historyTab = page.new Tab("history");
        Page.Section historyTabSec = page.new Section();
        
        PageWidget history = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_history);
        history.addToLayoutParams(historyTabSec, 24, 3);
        
        historyTabSec.addWidget(history);
        
        historyTab.addSection(historyTabSec);
        page.addTab(historyTab);
        

        return page;
	}

}
