package com.facilio.bmsconsole.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ViewField;

public class ColumnFactory {
	
	private static Map<String, List<ViewField>> columns = Collections.unmodifiableMap(initColumns());
	public static List<ViewField> getColumns (String viewName) {
		return columns.get(viewName);
	}
	
	private static Map<String, List<ViewField>> initColumns() {
		Map<String, List<ViewField>> columnMap = new HashMap<>();
		columnMap.put("workorder-open", getDefaultViewColumns());
		columnMap.put("workorder-overdue", getDefaultViewColumns());
		columnMap.put("workorder-duetoday", getDefaultViewColumns());
		columnMap.put("workorder-myopen", getMyOpenWorkOrderColumns());
		columnMap.put("workorder-myteamopen", getDefaultViewColumns());
		columnMap.put("workorder-unassigned", getDefaultViewColumns());
		columnMap.put("workorder-closed", getDefaultViewColumns());
		columnMap.put("workorder-openfirealarms", getDefaultViewColumns());
		columnMap.put("workorder-myoverdue", getDefaultViewColumns());
		columnMap.put("workorder-myduetoday", getDefaultViewColumns());
		columnMap.put("workorder-my", getDefaultViewColumns());
		columnMap.put("workorder-all", getDefaultViewColumns());
		
		//TODO remove
		columnMap.put("workorder-default", getDefaultViewColumns());
		
		return columnMap;
	}
	
	private static List<ViewField> getDefaultViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("space", "Space"));
		columns.add(new ViewField("assignedTo", "Assigned To"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		
		return columns;
	}
	
	private static List<ViewField> getMyOpenWorkOrderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("space", "Space"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		
		return columns;
	}

}
