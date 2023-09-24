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
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MeterPageFactory extends PageFactory {

	private static final Logger LOGGER = LogManager.getLogger(MeterPageFactory.class.getName());

	private static void composeSummaryRightPanel(Page.Section section, V3MeterContext meter) throws Exception {
		int yOffset = 0;
		int xOffset = 18;
		int widgetWidth = 6;

		if (meter.getUtilityType() != null) {
			V3UtilityTypeContext type = V3RecordAPI.getRecord(FacilioConstants.Meter.UTILITY_TYPE, meter.getUtilityType().getId(), V3UtilityTypeContext.class);
			if(type.getName().equals("Gas Meter") || type.getName().equals("Water Meter") || type.getName().equals("Electricity Meter") || type.getName().equals("Heat Meter") || type.getName().equals("BTU Meter")) {
				PageWidget monthlyConsumption = new PageWidget(WidgetType.MONTHLY_CONSUMPTION);
				monthlyConsumption.addToLayoutParams(xOffset, yOffset, widgetWidth, 5);
				section.addWidget(monthlyConsumption);
				yOffset += 5;

				PageWidget totalConsumption = new PageWidget(WidgetType.TOTAL_CONSUMPTION);
				totalConsumption.addToLayoutParams(xOffset, yOffset, widgetWidth, 5);
				section.addWidget(totalConsumption);
				yOffset += 5;

				PageWidget peakDemand = new PageWidget(WidgetType.PEAK_DEMAND);
				peakDemand.addToLayoutParams(xOffset, yOffset, widgetWidth, 5);
				section.addWidget(peakDemand);
			}
		}
	}

	private static void addSummaryTab(Page page, V3MeterContext meter) throws Exception {
		Page.Tab summaryTab = page.new Tab("summary");
		page.addTab(summaryTab);

		Page.Section summarySection = page.new Section();
		summaryTab.addSection(summarySection);

		int yOffset = 0;

		if(meter.getMeterTypeEnum() != null && meter.getMeterTypeEnum().equals(V3MeterContext.MeterType.VIRTUAL)) {
			PageWidget detailsWidget = new PageWidget(WidgetType.VIRTUAL_METER_DETAILS_WIDGET);
			detailsWidget.addToLayoutParams(0, 0, 18, 8);
			summarySection.addWidget(detailsWidget);
			yOffset += 8;
		}
		else{
			PageWidget detailsWidget = new PageWidget(WidgetType.PHYSICAL_METER_DETAILS_WIDGET);
			detailsWidget.addToLayoutParams(0, 0, 18, 10);
			summarySection.addWidget(detailsWidget);
			yOffset += 10;
		}

		//PageWidget childMeterWidget = new PageWidget(WidgetType.CHILD_METERS);
		//childMeterWidget.addToLayoutParams(0, yOffset, 18, 8);
		//summarySection.addWidget(childMeterWidget);
		//yOffset += 8;

		PageWidget readingsWidget = new PageWidget(WidgetType.METER_READINGS);
		readingsWidget.addToLayoutParams(0, yOffset, 18, 10);
		summarySection.addWidget(readingsWidget);
		yOffset += 10;

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(0, yOffset, 18, 8);
		subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
		summarySection.addWidget(subModuleGroup);

		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		notesWidget.setTitle("Comment");

		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		attachmentWidget.setTitle("Attachment");

		subModuleGroup.addToWidget(notesWidget);
		subModuleGroup.addToWidget(attachmentWidget);

		composeSummaryRightPanel(summarySection, meter);

	}


	public static Page getMeterPage(V3MeterContext meter) throws Exception {
		Page page = new Page();
		addSummaryTab(page, meter);
		page.addTab(composeHistoryTab(page));

		return page;
	}

	private static Page.Tab composeHistoryTab(Page page) {
		Page.Tab historyTab = page.new Tab("History");
		Page.Section primarySection = page.new Section();
		PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		historyWidget.addToWidgetParams("activityModuleName", FacilioConstants.Meter.METER_ACTIVITY);
		historyWidget.addToLayoutParams(primarySection, 24, 3);
		primarySection.addWidget(historyWidget);
		historyTab.addSection(primarySection);
		return historyTab;
	}

}
