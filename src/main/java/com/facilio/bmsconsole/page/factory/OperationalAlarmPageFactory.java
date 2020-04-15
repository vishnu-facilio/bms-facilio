package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.OperationAlarmContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.Map;

public class OperationalAlarmPageFactory extends PageFactory {
    public static Page getOperationalAlarmPage(OperationAlarmContext alarms) throws Exception {
        return getDefaultOperationalAlarmSummaryPage(alarms);
    }
    private static Page getDefaultOperationalAlarmSummaryPage(OperationAlarmContext alarms) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();
        // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        ReadingAlarmPageFactory.addAlarmDetailsWidget(tab1Sec1);
        ReadingAlarmPageFactory.addAssetAlarmDetailsWidget(tab1Sec1);

        addAlarmReport(tab1Sec1);
        addCommonSubModuleGroup(tab1Sec1);

        Page.Tab tab2 = page.new Tab("insight");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        addDefaultAlarmRank(tab2Sec1);
        addDefaultAlarmDuration(tab2Sec1);
        ReadingAlarmPageFactory.addAlarmDuration(tab2Sec1);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarms.getId()), NumberOperators.EQUALS));
        addOutOfScheduleReport(tab2Sec1, criteria, alarms.getLastOccurrence());
        // History Tab
        Page.Tab tab3 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab3);

        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addOccurrenceHistoryWidget(tab3Sec1);
        return  page;
    }

    private static PageWidget addOccurrenceHistoryWidget(Page.Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }
    protected static PageWidget addAlarmReport(Page.Section section) {
        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.ALARM_REPORT);
        alarmReport.addToLayoutParams(section, 24, 13);
        section.addWidget(alarmReport);
        return alarmReport;
    }
    protected static PageWidget addOutOfScheduleReport(Page.Section section, Criteria criteria, AlarmOccurrenceContext lastOccurrence) {
        PageWidget alarmDetails = new PageWidget(PageWidget.WidgetType.CHART, "outOfSchedule");
        alarmDetails.addToLayoutParams(section, 24, 12);
        alarmDetails.addCardType(PageWidget.CardType.OUT_OF_SCHEDULE);
        addChartParams(alarmDetails, null, BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR , "createdTime", BmsAggregateOperators.NumberAggregateOperator.SUM , "duration" ,null, DateOperators.CURRENT_YEAR, String.valueOf(lastOccurrence.getCreatedTime()) ,criteria);
        section.addWidget(alarmDetails);
        return alarmDetails;
    }

    private  static  PageWidget addDefaultAlarmRank (Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "rank");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.setTitle("Rank");
        cardWidget.addToWidgetParams("primaryTitle", "This Month");
        cardWidget.addToWidgetParams("primaryKey", "ranking");
        cardWidget.addToWidgetParams("primaryKeyOutOf", "outOfRule");
        cardWidget.addToWidgetParams("secondaryTitle", "Last Month");
        cardWidget.addToWidgetParams("secondaryKey", "lastranking");
        cardWidget.addToWidgetParams("secondaryKeyOutOf", "outOfRuleLastMonth");
        cardWidget.addToWidgetParams("dataType", "rank");
        cardWidget.addToWidgetParams("workflowId", "110");
        cardWidget.addCardType(PageWidget.CardType.DEFAULT_COMPARISION_CARD);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    private  static  PageWidget addDefaultAlarmDuration (Page.Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "duration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.setTitle("Duration");
        cardWidget.addToWidgetParams("primaryTitle", "This Year");
        cardWidget.addToWidgetParams("primaryKey", "durationCurrentYear");
        cardWidget.addToWidgetParams("secondaryTitle", "Last Year");
        cardWidget.addToWidgetParams("secondaryKey", "durationLastYear");
        cardWidget.addToWidgetParams("dataType", "duration");
        cardWidget.addToWidgetParams("workflowId", "109");
        cardWidget.addCardType(PageWidget.CardType.DEFAULT_COMPARISION_CARD);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
}

