
package com.facilio.bmsconsole.page.factory;

        import java.util.Map;

        import com.facilio.agent.alarms.AgentAlarmContext;
        import com.facilio.beans.ModuleBean;
        import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
        import com.facilio.bmsconsole.context.BaseAlarmContext;
        import com.facilio.bmsconsole.context.ReadingAlarm;
        import com.facilio.bmsconsole.context.WorkOrderContext;
        import com.facilio.bmsconsole.page.Page;
        import com.facilio.bmsconsole.util.WorkOrderAPI;
        import com.facilio.constants.FacilioConstants;
        import com.facilio.db.criteria.operators.DateOperators;
        import com.facilio.modules.BmsAggregateOperators;
        import org.json.simple.JSONObject;
        import com.facilio.bmsconsole.page.PageWidget;
        import com.facilio.bmsconsole.page.WidgetGroup;
        import com.facilio.db.criteria.Criteria;
        import com.facilio.db.criteria.CriteriaAPI;
        import com.facilio.db.criteria.operators.NumberOperators;
        import com.facilio.fw.BeanFactory;
        import com.facilio.modules.FieldFactory;
        import com.facilio.modules.fields.FacilioField;

public class AgentAlarmPageFactory extends PageFactory  {
    public static Page getAgentAlarmPage(BaseAlarmContext alarms) throws Exception {
        return getDefaultAgentAlarmSummaryPage(alarms);
    }
    private static Page getDefaultAgentAlarmSummaryPage(BaseAlarmContext alarms) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();
        // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (alarms.getLastOccurrence().getWoId() > 0 || (alarms.getLastOccurrence().getAcknowledged() != null && alarms.getLastOccurrence().getAcknowledged())) {
            addTimeLineWidget(tab1Sec1, alarms);
        }
        addAlarmDetailsWidget(tab1Sec1);
        addAgentDetailsWidget(tab1Sec1);
        // addAlarmReport(tab1Sec1,alarms.getLastOccurrence());
        addCommonSubModuleGroup(tab1Sec1);

        Page.Tab tab2 = page.new Tab("insight");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        // addAlarmRankCard(tab2Sec1);
        addAlarmDuration(tab2Sec1);
        addDefaultAlarmDuration(tab2Sec1);
        addDefaultMaxDuration(tab2Sec1);
//        addMeanTimeBetweenCard(tab2Sec1);
//        addMeanTimeToClearCard(tab2Sec1);

        // History Tab
        Page.Tab tab3 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab3);

        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addOccurrenceHistoryWidget(tab3Sec1);
        return  page;
    }
    protected  static  void  addTimeLineWidget(Page.Section section, BaseAlarmContext alarms) throws Exception {
        JSONObject activities = new JSONObject();
        int widgetHeight = 0;
        if (alarms.getLastOccurrence() != null) {
            if (alarms.getLastOccurrence().getWoId() > 0) {
                WorkOrderContext wo = WorkOrderAPI.getWorkOrder(alarms.getLastOccurrence().getWoId());
                activities.put("workOrder", wo);
                widgetHeight = widgetHeight + 2;
            }
            if (alarms.getLastOccurrence().getAcknowledged() != null && alarms.getLastOccurrence().getAcknowledged()) {
                activities.put("acknowledge", alarms.getLastOccurrence().getAcknowledgedBy());
                widgetHeight = widgetHeight + 2;
            }
        }
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_TIME_LINE);
        pageWidget.addToLayoutParams(section, 24, widgetHeight);
        pageWidget.setRelatedList(activities);
        section.addWidget(pageWidget);
    }
    protected static void addAgentDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.AGENT_DETAILS_WIDGET);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static void addAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_DETAILS);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static PageWidget addCommonSubModuleGroup(Page.Section section) {

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

    protected static PageWidget addAlarmReport(Page.Section section, AlarmOccurrenceContext lastOccurrence) {
        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.ALARM_REPORT);
//        int widgetHeight = 12 + (lastOccurrence.getPossibleCauses() != null ? 3 : 0) + (lastOccurrence.getRecommendation() != null ? 1 : 0);
        alarmReport.addToLayoutParams(section, 24, 15);
        section.addWidget(alarmReport);
        return alarmReport;
    }
    private static PageWidget addOccurrenceHistoryWidget(Page.Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }
//    private static PageWidget addActivityWidget (Section section) {
//        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY_WIDGET);
//        section.addWidget(activityWidget);
//        return activityWidget;
//    }

    private static PageWidget addAlarmRankCard(Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD);
        cardWidget.addToLayoutParams(section, 24, 2);
        cardWidget.addToWidgetParams("type", PageWidget.CardType.RANK_RULE.getName());
        section.addWidget(cardWidget);
        return cardWidget;
    }

    private static PageWidget addMeanTimeBetweenCard(Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mtba");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTBA);
        section.addWidget(cardWidget);
        return  cardWidget;

    }
    private static PageWidget addMeanTimeToClearCard(Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mttc");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTTC);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private  static  PageWidget addAlarmDuration (Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "duration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ALARM_DUARTION);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private  static  PageWidget addDefaultAlarmDuration (Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "duration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.setTitle("Offline Duration");
        cardWidget.addToWidgetParams("primaryTitle", "This Month");
        cardWidget.addToWidgetParams("primaryKey", "durationCurrentMonth");
        cardWidget.addToWidgetParams("secondaryTitle", "Last Month");
        cardWidget.addToWidgetParams("secondaryKey", "durationLastMonth");
        cardWidget.addToWidgetParams("dataType", "duration");
        cardWidget.addCardType(PageWidget.CardType.DEFAULT_COMPARISION_CARD);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private  static  PageWidget addDefaultMaxDuration (Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "maxDuration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.setTitle("Max Outage Duration");
        cardWidget.addToWidgetParams("primaryTitle", "This Month");
        cardWidget.addToWidgetParams("primaryKey", "maxDurationThisMonth");
        cardWidget.addToWidgetParams("secondaryTitle", "Last Month");
        cardWidget.addToWidgetParams("secondaryKey", "maxDurationLastMonth");
        cardWidget.addToWidgetParams("dataType", "duration");
        cardWidget.addCardType(PageWidget.CardType.DEFAULT_COMPARISION_CARD);
        section.addWidget(cardWidget);
        return  cardWidget;
    }

}

