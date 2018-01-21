package com.facilio.bmsconsole.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ViewField;

public class ColumnFactory {
	
	private static Map<String, List<ViewField>> columns = Collections.unmodifiableMap(initColumns());
	public static List<ViewField> getColumns (String moduleName, String viewName) {
		return columns.get(moduleName + "-" +viewName);
	}
	
	private static Map<String, List<ViewField>> initColumns() {
		Map<String, List<ViewField>> columnMap = new HashMap<>();
		columnMap.put("workorder-open", getDefaultViewColumns());
		columnMap.put("workorder-overdue", getDefaultViewColumns());
		columnMap.put("workorder-duetoday", getDefaultViewColumns());
		columnMap.put("workorder-myopen", getDefaultViewColumns());
		columnMap.put("workorder-myteamopen", getDefaultViewColumns());
		columnMap.put("workorder-unassigned", getDefaultViewColumns());
		columnMap.put("workorder-closed", getDefaultViewColumns());
		columnMap.put("workorder-openfirealarms", getDefaultViewColumns());
		columnMap.put("workorder-myoverdue", getDefaultViewColumns());
		columnMap.put("workorder-myduetoday", getDefaultViewColumns());
		columnMap.put("workorder-my", getDefaultViewColumns());
		columnMap.put("workorder-all", getDefaultViewColumns());
		columnMap.put("workorder-planned", getDefaultViewColumns());
		columnMap.put("workorder-unplanned", getDefaultViewColumns());
		
		columnMap.put("alarm-active", getDefaultAlarmColumns());
		columnMap.put("alarm-cleared", getDefaultAlarmColumns());
		columnMap.put("alarm-critical", getDefaultAlarmColumns());
		columnMap.put("alarm-major", getDefaultAlarmColumns());
		columnMap.put("alarm-minor", getDefaultAlarmColumns());
		columnMap.put("alarm-myalarms", getDefaultAlarmColumns());
		columnMap.put("alarm-unassigned", getDefaultAlarmColumns());
		columnMap.put("alarm-unacknowledged", getDefaultAlarmColumns());
		columnMap.put("alarm-fire", getDefaultAlarmColumns());
		columnMap.put("alarm-energy", getDefaultAlarmColumns());
		columnMap.put("alarm-hvac", getDefaultAlarmColumns());
		
		// For getting default values for a column - view not shown in ui
		columnMap.put("workorder-default", getDefaultViewColumns());
		columnMap.put("alarm-default", getDefaultAlarmColumns());
		
		return columnMap;
	}
	
	private static List<ViewField> getDefaultViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("space", "Space / Asset"));
		columns.add(new ViewField("assignedTo", "Assigned To"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("noOfNotes", "Comments"));
		columns.add(new ViewField("noOfTasks", "Tasks"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("alarmType", "Category"));
		columns.add(new ViewField("node", "Entity"));
		columns.add(new ViewField("modifiedTime", "Time Since Report"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged"));
		
		return columns;
	}

}
