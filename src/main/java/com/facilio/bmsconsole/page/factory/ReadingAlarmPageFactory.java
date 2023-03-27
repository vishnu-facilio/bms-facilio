package com.facilio.bmsconsole.page.factory;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FacilioModule;

import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;

public class ReadingAlarmPageFactory extends PageFactory  {
    public static Page getReadingAlarmPage(ReadingAlarm alarms, FacilioModule module) throws Exception {
        return getDefaultReadingAlarmSummaryPage(alarms, module);
    }
    private static Page getDefaultReadingAlarmSummaryPage(ReadingAlarm alarms, FacilioModule module) throws Exception {
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();
        // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (alarms.getLastOccurrence().getWoId() > 0 || (alarms.getLastOccurrence().getAcknowledged() != null && alarms.getLastOccurrence().getAcknowledged())) {
            addTimeLineWidget(tab1Sec1, alarms);
        }
        addAlarmDetailsWidget(tab1Sec1);
        addAssetAlarmDetailsWidget(tab1Sec1);
        addAlarmReport(tab1Sec1,alarms.getLastOccurrence());

        HashMap<String, String> titles = new HashMap<>();
        titles.put("notes", "Comment");
        addCommonSubModuleWidget(tab1Sec1, module, alarms, titles, false, WidgetType.COMMENT);


        Page.Tab tab2 = page.new Tab("insight");
        page.addTab(tab2);
        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
//        addAlarmRankCard(tab2Sec1);
        addMeanTimeBetweenCard(tab2Sec1);
        addMeanTimeToClearCard(tab2Sec1);
        addAlarmDuration(tab2Sec1);

        if(alarms.getRule() != null && alarms.getIsNewReadingRule()) {
            Long ruleId = alarms.getRule().getId();
            NewReadingRuleContext rule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(ruleId)).get(0);
            if (rule != null && rule.getImpact() != null) {
                addImpactInfoCard(tab2Sec1);
                addEnergyImpactCard(tab2Sec1);
                addCostImpactCard(tab2Sec1);
            }
        }
        addImpactReportCard(tab2Sec1, alarms);

//        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.BASE_EVENT));
//        Criteria criteria = new Criteria();
//		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("baseAlarm"), String.valueOf(alarms.getId()), NumberOperators.EQUALS));
//        addImpactDetails(tab2Sec1, criteria, alarms.getLastOccurrence());

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

        Page.Tab tab5 = page.new Tab("alarmRca", "alarmRca");
        page.addTab(tab5);

        Section tab5Sec1 = page.new Section();
        tab5.addSection(tab5Sec1);

        return  page;
    }
    protected  static  void  addTimeLineWidget(Page.Section section, ReadingAlarm alarms) throws Exception {
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
    protected static void addAssetAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ANOMALY_DETAILS_WIDGET);
        pageWidget.addToLayoutParams(section, 24, 4);
        section.addWidget(pageWidget);
    }
    protected static void addAlarmDetailsWidget (Page.Section section) {
        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_DETAILS);
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

    protected static PageWidget addAlarmReport(Section section,AlarmOccurrenceContext lastOccurrence) {
        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.ALARM_REPORT);
//        int widgetHeight = 12 + (lastOccurrence.getPossibleCauses() != null ? 3 : 0) + (lastOccurrence.getRecommendation() != null ? 1 : 0);
        alarmReport.addToLayoutParams(section, 24, 15);
        section.addWidget(alarmReport);
        return alarmReport;
    }
    protected static PageWidget addImpactDetails(Section section, Criteria criteria, AlarmOccurrenceContext lastOccurrence) {
        PageWidget alarmDetails = new PageWidget(WidgetType.CHART, "impactDetails");
        alarmDetails.addToLayoutParams(section, 24, 12);
        alarmDetails.addCardType(PageWidget.CardType.IMPACT_DETAILS);
        if (lastOccurrence != null && lastOccurrence.getAdditionInfo() != null && lastOccurrence.getAdditionInfo().containsKey("impact")) {
            addChartParams(alarmDetails, null, DateAggregateOperator.HOURSOFDAYONLY , "createdTime",null,NumberAggregateOperator.SUM , "cost" ,null,null, DateOperators.CURRENT_N_DAY, String.valueOf(lastOccurrence.getCreatedTime()) ,criteria);
        } else  {
            alarmDetails.addToWidgetParams("isEmpty", true);
        }

        section.addWidget(alarmDetails);
        return alarmDetails;
    }
    private static PageWidget addOccurrenceHistoryWidget(Section section) {
        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
        section.addWidget(occurrenceListWidget);
        return occurrenceListWidget;
    }

    public static PageWidget addAlarmRankCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD);
        cardWidget.addToLayoutParams(section, 24, 2);
        cardWidget.addToWidgetParams("type", PageWidget.CardType.RANK_RULE.getName());
        section.addWidget(cardWidget);
        return cardWidget;
    }

    public static PageWidget addMeanTimeBetweenCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mtba");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTBA);
        section.addWidget(cardWidget);
        return  cardWidget;

    }
    public static PageWidget addMeanTimeToClearCard(Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "mttc");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ML_MTTC);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    public  static  PageWidget addAlarmDuration (Section section) {
        PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.CARD, "duration");
        cardWidget.addToLayoutParams(section, 8, 4);
        cardWidget.addCardType(PageWidget.CardType.ALARM_DUARTION);
        section.addWidget(cardWidget);
        return  cardWidget;
    }
    public static  PageWidget  addImpactCard (Section section) {
        PageWidget impactCard = new PageWidget(PageWidget.WidgetType.IMPACTS);
        impactCard.addToLayoutParams(section, 24, 8);
        section.addWidget(impactCard);
        return impactCard;
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

    private static PageWidget addImpactReportCard(Section section, ReadingAlarm alarm) throws Exception {
        PageWidget impactReport = new PageWidget(WidgetType.IMPACT_REPORT);
        impactReport.addToLayoutParams(section, 24, 15);
        JSONObject widgetParams = getWidgetParamsForImpactReport(alarm);
        impactReport.setWidgetParams(widgetParams);
        section.addWidget(impactReport);
        return impactReport;
    }
    private static PageWidget addImpactInfoCard(Section section) {
        PageWidget impactInfo = new PageWidget(WidgetType.IMPACT_INFO);
        impactInfo.addToLayoutParams(section, 8, 4);
        section.addWidget(impactInfo);
        return impactInfo;
    }
    private static PageWidget addEnergyImpactCard(Section section){
        PageWidget energyImpact = new PageWidget(WidgetType.ENERGY_IMPACT);
        energyImpact.addToLayoutParams(section, 8, 4);
        section.addWidget(energyImpact);
        return energyImpact;
    }
    private static PageWidget addCostImpactCard(Section section){
        PageWidget costImpact = new PageWidget(WidgetType.COST_IMPACT);
        costImpact.addToLayoutParams(section, 8, 4);
        section.addWidget(costImpact);
        return costImpact;
    }
    private static JSONObject getWidgetParamsForImpactReport(ReadingAlarm alarm) throws Exception {
        JSONObject widgetParams = new JSONObject();
        if (alarm.getRule() != null && alarm.getIsNewReadingRule()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Long ruleId = alarm.getRule().getId();
            NewReadingRuleContext rule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(ruleId)).get(0);
            FacilioModule module = modBean.getModule(rule.getReadingModuleId());
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            widgetParams.put("costImpactId", fieldMap.get("costImpact").getFieldId());
            widgetParams.put("energyImpactId", fieldMap.get("energyImpact").getFieldId());
        }
        return widgetParams;
    }
}