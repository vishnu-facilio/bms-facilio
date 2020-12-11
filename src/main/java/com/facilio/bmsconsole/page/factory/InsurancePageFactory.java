package com.facilio.bmsconsole.page.factory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class InsurancePageFactory extends PageFactory{
	private static final Logger LOGGER = LogManager.getLogger(InsurancePageFactory.class.getName());
	public static Page getInsurancePage(InsuranceContext insurance) throws Exception {
		Page page = new Page();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(insurance.getModuleId());
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		addSecondaryDetailsWidget(tab1Sec1);
		
		Section tab1Sec3 = page.new Section();
		tab1.addSection(tab1Sec3);
		addNotesModule(tab1Sec3);
		
		return page;
	}
	
	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}

	private static PageWidget addNotesModule(Section section) {

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(section, 24, 8);
		subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
		section.addWidget(subModuleGroup);
		
		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		subModuleGroup.addToWidget(notesWidget);

		return subModuleGroup;
	}
}
