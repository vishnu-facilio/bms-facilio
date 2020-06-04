package com.facilio.bmsconsole.page.factory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class VisitorLoggingPageFactory extends PageFactory{
	private static final Logger LOGGER = LogManager.getLogger(VisitorLoggingPageFactory.class.getName());
	public static Page getVisitorLoggingPage(VisitorLoggingContext visitorLogging) throws Exception {
		Page page = new Page();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(visitorLogging.getModuleId());
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		addVisitDetailsWidget(tab1Sec1);
		
		if (visitorLogging.getVisitedSpace() != null) {
			Section tab1Sec2 = page.new Section();
			tab1.addSection(tab1Sec2);
			addSpaceDetailWidget(tab1Sec2);
		}

		Section tab1Sec3 = page.new Section();
		tab1.addSection(tab1Sec3);
		addCommonSubModuleWidget(tab1Sec3, module, visitorLogging);
		
		return page;
	}
	
	private static void addVisitDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.TENANT_PORTAL_VISITS_DETAILS, "tenantPortalVisitsDetails");
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}

	private static void addSpaceDetailWidget(Section section) {
		PageWidget recurringInfoWidget = new PageWidget(WidgetType.SPACE_WIDGET, "spaceWidget");
		recurringInfoWidget.addToLayoutParams(section, 24, 3);
		section.addWidget(recurringInfoWidget);
	}

}
