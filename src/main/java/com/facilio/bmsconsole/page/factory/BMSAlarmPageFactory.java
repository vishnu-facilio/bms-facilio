package com.facilio.bmsconsole.page.factory;

import java.util.HashMap;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.modules.FacilioModule;

import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.fw.BeanFactory;

public class BMSAlarmPageFactory extends PageFactory  {
    public static Page getBmsAlarmPage(BaseAlarmContext alarms, FacilioModule module) throws Exception {
        return getDefaultBmsAlarmSummaryPage(alarms, module);
    }
    private static Page getDefaultBmsAlarmSummaryPage(BaseAlarmContext alarms, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();
        // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

        addAlarmDetailsWidget(tab1Sec1);
        addAssetAlarmDetailsWidget(tab1Sec1);

        HashMap<String, String> titles = new HashMap<>();
        titles.put("notes", "Comment");
        addCommonSubModuleWidget(tab1Sec1, module, alarms, titles, false, WidgetType.COMMENT);


        // History Tab
        Page.Tab tab3 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab3);

        Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addOccurrenceHistoryWidget(tab3Sec1);

        return  page;
    }

    protected static void addAssetAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ANOMALY_DETAILS_WIDGET);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static void addAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.BMS_ALARM_DETAILS);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static PageWidget addCommonSubModuleGroup(Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        section.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setTitle("Comment");
        notesWidget.setWidgetType(PageWidget.WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

        return subModuleGroup;
    }


    private static PageWidget addOccurrenceHistoryWidget(Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }
}

