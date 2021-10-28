package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class ServicePageFactory extends PageFactory {

    public static Page getServicePage(V3ServiceContext services, FacilioModule module) throws Exception {
        Page page = new Page();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
        tab1Sec1.addWidget(previewWidget);



        Page.Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        PageWidget vendorsWidget = new PageWidget(PageWidget.WidgetType.SERVICE_VENDORS);
        vendorsWidget.addToLayoutParams(tab1Sec2, 24, 6);
        tab1Sec2.addWidget(vendorsWidget);



        return page;
    }

}