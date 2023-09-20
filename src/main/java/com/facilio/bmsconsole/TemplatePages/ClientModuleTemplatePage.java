package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ClientModulePageUtil;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ClientModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.CLIENT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientModule=modBean.getModule(getModuleName());
        List<String> moduleToRemove=new ArrayList<>();
        moduleToRemove.add(FacilioConstants.ContextNames.WORK_ORDER);
        moduleToRemove.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
        moduleToRemove.add(FacilioConstants.ContextNames.SITE);
        moduleToRemove.add("contact");


        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("clientDetails", "Client Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, ClientModulePageUtil.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("workorderFields", null, null)
                .addWidget("workorderDetails", "Workorders", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"workorder","client"))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0,  null, ClientModulePageUtil.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("sites", "Sites", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("siteFields", null, null)
                .addWidget("siteDetails", "Sites", PageWidget.WidgetType.SITE_LIST_WIDGET, "flexiblewebsitelistwidget_29", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"site","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("clientContacts", "Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientconatactfields", null, null)
                .addWidget("clientcontactdetails", "Contacts", PageWidget.WidgetType.CLIENT_CONTACT_LIST_WIDGET, "flexibleclientcontactlistwidget_29", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(clientModule,"clientcontact","client"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("clientRelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("clientBulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("clientRelatedlist", "Related List", "List of related records across modules")
                .addWidget("clientBulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_29", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
}
