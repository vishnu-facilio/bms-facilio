package com.facilio.bmsconsole.page.factory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class TenantWorkRequestPageFactory extends PageFactory{
	private static final Logger LOGGER = LogManager.getLogger(TenantWorkRequestPageFactory.class.getName());
	public static Page getWorkorderPage(WorkOrderContext workorder) throws Exception {
		Page page = new Page();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(workorder.getModuleId());
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		if (AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			
			addRelatedListWidgets(tab1Sec1, module.getModuleId());
		}
		else {

		addWorkrequestDetailsWidget(tab1Sec1);
		
		
		Section tab1Sec2 = page.new Section("portalWorkrequestFormDetails");
		tab1.addSection(tab1Sec2);
		addSecondaryDetailsWidget(tab1Sec2);
		
		Section tab1Sec3 = page.new Section();
		tab1.addSection(tab1Sec3);
		addCommentsAttachmentSubModuleGroup(tab1Sec3);
		
		Tab tab2 = page.new Tab("activity");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addActivityWidget(tab2Sec1);
		}
		
		return page;
	}

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET, "portalWorkrequestFormDetails");
		detailsWidget.addToLayoutParams(section, 24, 4);
		// Temp..needs to move to db
		if (AccountUtil.getCurrentOrg().getOrgId() == 406) {
			detailsWidget.addToWidgetParams("sort", "form");
		}
		section.addWidget(detailsWidget);
	}

	private static void addWorkrequestDetailsWidget(Section section) {
		PageWidget recurringInfoWidget = new PageWidget(WidgetType.WORK_REQUEST_DETAILS_WIDGET, "workRequestDetails");
		recurringInfoWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(recurringInfoWidget);
	}
	
	private static void addActivityWidget(Section section) {
		PageWidget historyWidget = new PageWidget(WidgetType.PORTAL_ACTIVITY);
		historyWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(historyWidget);
	}
	
	private static PageWidget addCommentsAttachmentSubModuleGroup(Section section) {

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(section, 24, 8);
		subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
		section.addWidget(subModuleGroup);
		
		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		notesWidget.setTitle("Comment");
		subModuleGroup.addToWidget(notesWidget);
		
		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		attachmentWidget.setTitle("Attachment");
		subModuleGroup.addToWidget(attachmentWidget);

		return subModuleGroup;
	}

	
}
