package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BMSAlarmPageFactory extends PageFactory {
    public static Page getBmsAlarmPage(BaseAlarmContext alarm, FacilioModule module) throws Exception {
        return getDefaultBmsAlarmSummaryPage(alarm, module);
    }

    private static Page getDefaultBmsAlarmSummaryPage(BaseAlarmContext alarm, FacilioModule module) throws Exception {
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
        addCommonSubModuleWidget(tab1Sec1, module, alarm, titles, false, WidgetType.COMMENT);

        //Related Tab
        Page.Tab tab2 = page.new Tab("Related");

        boolean isRelationshipAdded = addRelationshipSection(page, tab2, alarm.getModuleId());

        Section tab2Sec1 = getRelatedListSectionObj(page);
        tab2.addSection(tab2Sec1);
        addRelatedListWidgets(tab2Sec1, alarm.getModuleId());

        if (CollectionUtils.isNotEmpty(tab2Sec1.getWidgets()) || isRelationshipAdded) {
            page.addTab(tab2);
        }

        // More Information Tab
        Page.Tab tab3 = page.new Tab("moreinfo");
        page.addTab(tab3);
        Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);
        addFieldDetailsWidget(tab3Sec1,module);

        // History Tab
        Page.Tab tab4 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab4);

        Section tab4Sec1 = page.new Section();
        tab4.addSection(tab4Sec1);

        addOccurrenceHistoryWidget(tab4Sec1);

        return page;
    }

    protected static void addAssetAlarmDetailsWidget(Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ANOMALY_DETAILS_WIDGET);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }

    protected static void addAlarmDetailsWidget(Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.BMS_ALARM_DETAILS);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }

    private static PageWidget addOccurrenceHistoryWidget(Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }

    public static void addFieldDetailsWidget (Page.Section section, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        PageWidget pageWidget = new PageWidget(WidgetType.ALARM_SECONDARY_DETAILS);
        pageWidget.setTitle("Fields");
        pageWidget.addToLayoutParams(section, 24, 12);
        JSONObject widgetParams = new JSONObject();
        JSONArray fieldList = new JSONArray();
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        fieldList.addAll(fields.stream().map(FacilioField::getName).collect(Collectors.toList()));
        widgetParams.put("fields", fieldList);
        pageWidget.setWidgetParams(widgetParams);
        section.addWidget(pageWidget);
    }

}
