package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class VirtualMeterTemplatePageFactory extends PageFactory {

	private static final Logger LOGGER = LogManager.getLogger(VirtualMeterTemplatePageFactory.class.getName());

	private static void addSummaryTab(Page page, VirtualMeterTemplateContext virtualMeterTemplate) throws Exception {
		Page.Tab summaryTab = page.new Tab("summary");
		page.addTab(summaryTab);

		Page.Section summarySection = page.new Section();
		summaryTab.addSection(summarySection);

		int yOffset = 0;

		PageWidget detailsWidget = new PageWidget(WidgetType.VIRTUAL_METER_TEMPLATE_DETAILS);
		detailsWidget.addToLayoutParams(0, 0, 24, 6);
		summarySection.addWidget(detailsWidget);
		yOffset += 6;

		if(virtualMeterTemplate.getVmTemplateStatusEnum() != null && virtualMeterTemplate.getVmTemplateStatusEnum() == VirtualMeterTemplateContext.VMTemplateStatus.PUBLISHED ) {
			PageWidget vmTemplateReadingsWidget = new PageWidget(WidgetType.VIRTUAL_METER_TEMPLATE_READINGS);
			vmTemplateReadingsWidget.addToLayoutParams(0, yOffset, 24, 8);
			summarySection.addWidget(vmTemplateReadingsWidget);
			yOffset += 8;

			PageWidget relatedVMListWidget = new PageWidget(WidgetType.RELATED_VIRTUAL_METERS_LIST);
			relatedVMListWidget.addToLayoutParams(0, yOffset, 24, 8);
			summarySection.addWidget(relatedVMListWidget);
		}

	}


	public static Page getVirtualMeterTemplatePage(VirtualMeterTemplateContext virtualMeterTemplate) throws Exception {
		Page page = new Page();
		addSummaryTab(page, virtualMeterTemplate);
		page.addTab(composeHistoryTab(page));

		return page;
	}

	private static Page.Tab composeHistoryTab(Page page) {
		Page.Tab historyTab = page.new Tab("History");
		Page.Section primarySection = page.new Section();
		PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		historyWidget.addToWidgetParams("activityModuleName", FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ACTIVITY);
		historyWidget.addToLayoutParams(primarySection, 24, 3);
		primarySection.addWidget(historyWidget);
		historyTab.addSection(primarySection);
		return historyTab;
	}

}
