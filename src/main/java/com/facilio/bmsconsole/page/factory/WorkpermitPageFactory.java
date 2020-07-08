package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class WorkpermitPageFactory extends PageFactory {
	private static final Logger LOGGER = LogManager.getLogger(WorkpermitPageFactory.class.getName());

	public static Page getWorkPermitPage(WorkPermitContext workpermit) throws Exception {

		if (Arrays.asList(75,155,173).contains(AccountUtil.getCurrentOrg().getOrgId())) {
			return getNewWorkPermitPage(workpermit);
		}

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
		addRelatedListWidgets(tab1Sec3, module.getModuleId());
		addCommonSubModuleWidget(tab1Sec3, module, workpermit);

		return page;
	}

	public static Page getNewWorkPermitPage(WorkPermitContext record) throws Exception {
		Page page = new Page();

		Page.Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Page.Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.WORKPERMIT_PREVIEW);
		previewWidget.addToLayoutParams(tab1Sec1, 24, 24);
		tab1Sec1.addWidget(previewWidget);


		Page.Tab tab2 = page.new Tab("Notes & Attachments");
		page.addTab(tab2);
		Page.Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
		notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
		tab2Sec1.addWidget(notesWidget);

		PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
		attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
		attachmentWidget.setTitle("Attachments");
		tab2Sec1.addWidget(attachmentWidget);


		Page.Tab tab4 = page.new Tab("Activity");
		page.addTab(tab4);
		Page.Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
		activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
		activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY);
		tab4Sec1.addWidget(activityWidget);

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
