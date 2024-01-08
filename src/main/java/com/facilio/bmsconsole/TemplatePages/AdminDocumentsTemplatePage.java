package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.signup.moduleconfig.AdminDocumentsModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class AdminDocumentsTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ADMIN_DOCUMENTS;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary","Summary", PageTabContext.TabType.SIMPLE,true,null)
                        .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                             .addSection("summary","",null)
                               .addWidget("summaryFieldsWidget", "Admin Documents",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_6", 0, 0, null, AdminDocumentsModule.getSummaryWidgetDetails(module.getName(),app))
                               .widgetDone()
                            .sectionDone()
                       .columnDone()
                    .tabDone()
                .layoutDone();
    }
}

