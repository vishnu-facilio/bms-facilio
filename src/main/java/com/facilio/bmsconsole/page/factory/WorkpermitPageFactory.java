package com.facilio.bmsconsole.page.factory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class WorkpermitPageFactory extends PageFactory {
	private static final Logger LOGGER = LogManager.getLogger(WorkpermitPageFactory.class.getName());

	public static Page getWorkPermitPage(WorkPermitContext workpermit) throws Exception {
		Page page = new Page();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(workpermit.getModuleId());

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		if (AccountUtil.getCurrentUser().isPortalUser()) {
			addPortalDetailsWidget(tab1Sec1);
		} else {
			addSecondaryDetailsWidget(tab1Sec1);
		}
		if (workpermit.isRecurring()) {
			Section tab1Sec2 = page.new Section("Recurring Info");
			tab1.addSection(tab1Sec2);
			addRecurringInfoWidget(tab1Sec2);
		}
		Section tab1Sec3 = page.new Section();
		tab1.addSection(tab1Sec3);
		addCommonSubModuleWidget(tab1Sec3, module, workpermit);

//		Tab tab2 = page.new Tab("related list");
//		page.addTab(tab2);
//		Section tab2Sec1 = page.new Section("Insurance");
//		tab2.addSection(tab2Sec1);
//		addInsuranceWidget(tab2Sec1);

		return page;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}

	private static void addRecurringInfoWidget(Section section) {
		PageWidget recurringInfoWidget = new PageWidget(WidgetType.RECURRING_INFO, "recurringInfo");
		recurringInfoWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(recurringInfoWidget);
	}

	private static void addPortalDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.PORTAL_WORKPERMIT, "portalWorkPermit");
		detailsWidget.addToLayoutParams(section, 24, 7);
		section.addWidget(detailsWidget);
	}
}
