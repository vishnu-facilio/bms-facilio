package com.facilio.bmsconsole.page.factory;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.HashMap;

public class SensorAlarmPageFactory extends PageFactory {
	public static Page getSensorAlarmPage(SensorRollUpAlarmContext alarms, FacilioModule module) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Page page = new Page();
     // Summary Tab
        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);
        Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        if (alarms.getLastWoId() > 0 || (alarms.getLastOccurrence() != null && alarms.getLastOccurrence().getAcknowledged() != null && alarms.getLastOccurrence().getAcknowledged())) {
            addTimeLineWidget(tab1Sec1, alarms);
        }
        addAlarmDetailsWidget(tab1Sec1);
        addAssetAlarmDetailsWidget(tab1Sec1);
//		addAlarmReport(tab1Sec1,alarms.getLastOccurrence());
//		addAlarmDetail(tab1Sec1);
		HashMap<String, String> titles = new HashMap<>();
		titles.put("notes", "Comment");
		addCommonSubModuleWidget(tab1Sec1, module, alarms, titles, false, WidgetType.COMMENT);
        
        
        
        
     // History Tab
        Page.Tab tab2 = page.new Tab("occurrenceHistory", "occurrenceHistory");
        page.addTab(tab2);

        Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        addOccurrenceHistoryWidget(tab2Sec1);
        
        
        return  page;
    }
	 protected static void addAlarmDetailsWidget (Page.Section section) {
	        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ALARM_DETAILS);
//		 PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
//	        PageWidget pageWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
	        pageWidget.addToLayoutParams(section, 24, 4);
	        section.addWidget(pageWidget);
	    }
	 protected static void addAssetAlarmDetailsWidget (Page.Section section) {
	        PageWidget pageWidget = new PageWidget(PageWidget.WidgetType.ANOMALY_DETAILS_WIDGET);
	        pageWidget.addToLayoutParams(section, 24, 4);
	        section.addWidget(pageWidget);
	    }
//	 protected static PageWidget addAlarmReport(Section section,AlarmOccurrenceContext lastOccurrence) {
//	        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.ALARM_REPORT);
//	        alarmReport.addToLayoutParams(section, 24, 15);
//	        section.addWidget(alarmReport);
//	        return alarmReport;
//	    }
//	 protected static PageWidget addAlarmDetail(Section section) {
//	        PageWidget alarmReport = new PageWidget(PageWidget.WidgetType.SENSOR_ALARM_DETAIL);
//	        alarmReport.addToLayoutParams(section, 24, 7);
//	        section.addWidget(alarmReport);
//	        return alarmReport;
//	    }
	 private static PageWidget addOccurrenceHistoryWidget(Section section) {
	        PageWidget occurrenceListWidget = new PageWidget(PageWidget.WidgetType.OCCURRENCE_HISTORY);
	        section.addWidget(occurrenceListWidget);
	        return occurrenceListWidget;
	    }
	 
	 protected  static  void  addTimeLineWidget(Page.Section section, SensorRollUpAlarmContext alarms) throws Exception {
	        JSONObject activities = new JSONObject();
	        int widgetHeight = 0;
	        if (alarms.getLastOccurrence() != null) {
	            if (alarms.getLastWoId() > 0) {
	                WorkOrderContext wo = WorkOrderAPI.getWorkOrder(alarms.getLastWoId());
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

}
