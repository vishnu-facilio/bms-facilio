package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.OperationAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
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
//        if (alarms.getLastOccurrence().getWoId() > 0 || (alarms.getLastOccurrence().getAcknowledged() != null && alarms.getLastOccurrence().getAcknowledged())) {
//            ReadingAlarmPageFactory.addTimeLineWidget(tab1Sec1, alarms);
//        }
        ReadingAlarmPageFactory.addAlarmDetailsWidget(tab1Sec1);
        ReadingAlarmPageFactory.addAssetAlarmDetailsWidget(tab1Sec1);

//        Criteria breakdownCriteria = new Criteria();
//        breakdownCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
        addOperationalReport(tab1Sec1, alarms);
//        addAlarmReport(tab1Sec1,alarms.getLastOccurrence());
        addCommonSubModuleGroup(tab1Sec1);

//        Page.Tab tab4 = page.new Tab("alarmRca", "alarmRca");
//        page.addTab(tab4);
//
//        Page.Section tab4Sec1 = page.new Section();
//        tab4.addSection(tab4Sec1);


        Page.Tab tab2 = page.new Tab("insight");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        ReadingAlarmPageFactory.addAlarmRankCard(tab2Sec1);
        ReadingAlarmPageFactory.addMeanTimeBetweenCard(tab2Sec1);
        ReadingAlarmPageFactory.addMeanTimeToClearCard(tab2Sec1);
        ReadingAlarmPageFactory.addAlarmDuration(tab2Sec1);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.BASE_EVENT));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("baseAlarm"), String.valueOf(alarms.getId()), NumberOperators.EQUALS));
        ReadingAlarmPageFactory.addImpactDetails(tab2Sec1, criteria, alarms.getLastOccurrence());

        // History Tab
        Page.Tab tab3 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab3);

        Page.Section tab3Sec1 = page.new Section();
        tab3.addSection(tab3Sec1);

        addOccurrenceHistoryWidget(tab3Sec1);
//        Page.Tab tab5 = page.new Tab("activity", "activity");
//        page.addTab(tab5);
//
//        Section tab5Sec1 = page.new Section();
//        tab5.addSection(tab5Sec1);
//
//        addActivityWidget(tab5Sec1);
        return  page;
    }

    private static PageWidget addOccurrenceHistoryWidget(Page.Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }

    private static void addOperationalReport(Page.Section section, OperationAlarmContext alarm) {
        Criteria breakdownCriteria = new Criteria();
        breakdownCriteria.addAndCondition(CriteriaAPI.getCondition(alarm.getReadingField(), String.valueOf(alarm.getResource().getId()), NumberOperators.EQUALS));

        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CHART, "operationAlarm");
        cardWidget.addToLayoutParams(section, 24, 6);
        cardWidget.addCardType(PageWidget.CardType.OPERATION_ALARM_REPORT);
        addChartParams(cardWidget, "fromtime", "duration",  breakdownCriteria);

        section.addWidget(cardWidget);
    }
}

