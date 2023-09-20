package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ClientContactModuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class ClientContactModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.CLIENT_CONTACT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("contactDetails", "Contact Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, ClientContactModuleUtil.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0,  null, ClientContactModuleUtil.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

}
