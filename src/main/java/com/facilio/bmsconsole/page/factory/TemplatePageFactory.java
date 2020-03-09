package com.facilio.bmsconsole.page.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class TemplatePageFactory extends PageFactory{
    public static Page getTemplatePage(DefaultTemplate template) throws Exception {
        Page page = new Page();

        Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        addTemplateSummary(tab1Sec1);

        Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        addTemplateCondition(tab1Sec2);

        Section tab1Sec3 = page.new Section();
        tab1.addSection(tab1Sec3);
        addTemplateAlarmDetails(tab1Sec3);

        

        return page;
    }
    private static void addTemplateSummary(Section section) {
        PageWidget detailsWidget = new PageWidget(WidgetType.TEMPLATE_SUMMARY_DETAILS);
        detailsWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(detailsWidget);
    }
    private static void addTemplateCondition(Section section) {
        PageWidget detailsWidget = new PageWidget(WidgetType.TEMPALTE_CONDITIONS);
        detailsWidget.addToLayoutParams(section, 24, 12);
        section.addWidget(detailsWidget);
    }
    private static void addTemplateAlarmDetails(Section section) {
        PageWidget detailsWidget = new PageWidget(WidgetType.TEMPLATE_ALARM_DETAILS);
        detailsWidget.addToLayoutParams(section, 24, 8);
        section.addWidget(detailsWidget);
    }
    
  

}