package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.modules.FacilioModule;

public class InspectionPageFactory extends PageFactory {

	public static Page getInspectionTemplatePage(InspectionTemplateContext record, FacilioModule module) {
		
		Page page = new Page();
		
		PageWidget secondaryDetailsWidget = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_secondaryDetailsWidget);
        PageWidget inspectionDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionDetails);
        PageWidget inspectionPageDetails = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionPageDetails);
        PageWidget inspectionTriggers = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionTriggers);
        PageWidget inspectionInsights = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionInsights);
        PageWidget inspectionQuestions = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_inspectionQuestions);
        PageWidget history = new PageWidget(PageWidget.WidgetType.INSPECTION_TEMPLATE_history);

        Page.Tab summaryTab = page.new Tab("summary");
        Page.Section SummarySec = page.new Section();
        
        SummarySec.addWidget(secondaryDetailsWidget);
        SummarySec.addWidget(inspectionDetails);
        SummarySec.addWidget(inspectionPageDetails);
        
        summaryTab.addSection(SummarySec);
        page.addTab(summaryTab);
        
        
        Page.Tab triggerTab = page.new Tab("trigger");
        Page.Section triggerTabSec = page.new Section();
        
        triggerTabSec.addWidget(inspectionTriggers);
        
        triggerTab.addSection(triggerTabSec);
        page.addTab(triggerTab);
        
        Page.Tab insightTab = page.new Tab("insight");
        Page.Section insightTabSec = page.new Section();
        
        insightTabSec.addWidget(inspectionInsights);
        insightTabSec.addWidget(inspectionQuestions);
        
        insightTab.addSection(insightTabSec);
        page.addTab(insightTab);

        Page.Tab historyTab = page.new Tab("history");
        Page.Section historyTabSec = page.new Section();
        
        historyTabSec.addWidget(history);
        
        historyTab.addSection(historyTabSec);
        page.addTab(historyTab);
        

        return page;
	}

}
