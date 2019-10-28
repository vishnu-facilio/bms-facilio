package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import org.json.simple.JSONObject;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;

public class ReadingAlarmPageFactory extends PageFactory  {
    public static Page getReadingAlarmPage(ReadingAlarm alarms) throws Exception {
        return getDefaultReadingAlarmSummaryPage(alarms);
    }
    private static Page getDefaultReadingAlarmSummaryPage(ReadingAlarm alarms) throws Exception {
        Page page = new Page();
        // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (alarms.getLastOccurrence().getWoId() > 0) {
            addTimeLineWidget(tab1Sec1, alarms);
        }
        addAlarmDetailsWidget(tab1Sec1);
        addAssetAlarmDetailsWidget(tab1Sec1);
        addAlarmReport(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);


        Page.Tab tab4 = page.new Tab("alarmRca", "alarmRca");
        page.addTab(tab4);

        Section tab4Sec1 = page.new Section();
        tab4.addSection(tab4Sec1);


        Page.Tab tab2 = page.new Tab("insight");
        page.addTab(tab2);
        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addAlarmRankCard(tab2Sec1);
        addMeanTimeBetweenCard(tab2Sec1);
        addMeanTimeToClearCard(tab2Sec1);
        addAlarmDuration(tab2Sec1);
        addImpactCard(tab2Sec1);
        
        // History Tab
        Page.Tab tab3 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab3);

        Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addOccurrenceHistoryWidget(tab3Sec1);
        return  page;
    }
    protected  static  void  addTimeLineWidget(Page.Section section, ReadingAlarm alarms) throws Exception {
        JSONObject activities = new JSONObject();
        if (alarms.getLastOccurrence() != null) {
            if (alarms.getLastOccurrence().getWoId() > 0) {
                WorkOrderContext wo = WorkOrderAPI.getWorkOrder(alarms.getLastOccurrence().getWoId());
                activities.put("workOrder", wo);
            }
        }
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_TIME_LINE);
        pageWidget.addToLayoutParams(section, 24, 2);
        pageWidget.setRelatedList(activities);
        section.addWidget(pageWidget);
    }
    protected static void addAssetAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ANOMALY_DETAILS_WIDGET);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static void addAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_DETAILS);
        pageWidget.addToLayoutParams(section, 24, 5);
        section.addWidget(pageWidget);
    }
    protected static PageWidget addCommonSubModuleGroup(Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        section.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setWidgetType(PageWidget.WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

        return subModuleGroup;
    }

    protected static PageWidget addAlarmReport(Section section) {
        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.ALARM_REPORT);
        alarmReport.addToLayoutParams(section, 24, 13);
        section.addWidget(alarmReport);
        return alarmReport;
    }
    private static PageWidget addOccurrenceHistoryWidget(Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }

    private static PageWidget addAlarmRankCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD);
        cardWidget.addToLayoutParams(section, 24, 2);
        cardWidget.addToWidgetParams("type", PageWidget.CardType.RANK_RULE.getName());
        section.addWidget(cardWidget);
        return cardWidget;
    }

    private static PageWidget addMeanTimeBetweenCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mtba");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTBA);
        section.addWidget(cardWidget);
        return  cardWidget;

    }
    private static PageWidget addMeanTimeToClearCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mttc");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTTC);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private  static  PageWidget addAlarmDuration (Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "duration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ALARM_DUARTION);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private static  PageWidget  addImpactCard (Section section) {
        PageWidget impactCard = new PageWidget(PageWidget.WidgetType.IMPACTS);
        impactCard.addToLayoutParams(section, 24, 8);
        section.addWidget(impactCard);
        return impactCard;
    }
}

