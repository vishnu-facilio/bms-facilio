package com.facilio.bmsconsole.page.factory;

import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class VisitorLogPageFactory extends PageFactory {

    public static Page getVisitsPage(VisitorLogContextV3 visitorLog, FacilioModule module) throws Exception {
    	
        Page page = new Page();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule visitorLogModule = modBean.getModule(visitorLog.getModuleId());
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);

		PageWidget detailsWidget = new PageWidget(WidgetType.VISITS_PRIMARY_FIELDS);
		detailsWidget.addToLayoutParams(tab1Sec1, 24, 4);
		detailsWidget.setTitle("Basic Info");
		tab1Sec1.addWidget(detailsWidget);

		addSecondaryDetailsWidget(tab1Sec1);

		PageWidget attachmentWidget = new PageWidget(WidgetType.ATTACHMENTS_PREVIEW);
		attachmentWidget.addToLayoutParams(tab1Sec1, 24, 6);
		attachmentWidget.setTitle("Attachments");
		tab1Sec1.addWidget(attachmentWidget);

		PageWidget notesWidget = new PageWidget(WidgetType.COMMENT);
		notesWidget.addToLayoutParams(tab1Sec1, 24, 6);
		notesWidget.setTitle("Comments");
		tab1Sec1.addWidget(notesWidget);
	    
		return page;
    }

	private static void addSecondaryDetailsWidget(Section section) {
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 4);
		detailsWidget.setTitle("Other Info");
		section.addWidget(detailsWidget);
	}

}
