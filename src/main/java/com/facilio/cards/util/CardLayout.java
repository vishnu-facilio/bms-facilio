package com.facilio.cards.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public enum CardLayout {
	
	READINGCARD_LAYOUT_1 (1, "readingcard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("moduleName", true);
			params.put("parentId", true);
			params.put("fieldName", true);
			params.put("yAggr", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("value", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.fieldName, params.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		date = NameSpace(\"date\");"
					+ "		dateRangeObj = null;"
					+ "		period = null;"
					+ "		if (params.dateRange != null) {"
					+ "			dateRangeObj = date.getDateRange(params.dateRange);"
					+ "			period = params.dateRange;"
					+ "		} else {"
					+ "			dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "			period = \"Last Value\";"
					+ "		}"
					+ "		db = {"
					+ "			criteria : [parentId == (params.parentId) && ttime == dateRangeObj],"
					+ "			field : params.fieldName,"
					+ "			aggregation : params.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	READINGCARD_LAYOUT_2 (1, "readingcard_layout_2") {
		
		public JSONObject getParameters () {
			return null;
		}
		
		public JSONObject getReturnValue () {
			return null;
		}
		
		public String getScript () {
			return null;
		}
	};
	
	private int id;
	private String name;
	
	private CardLayout (int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public abstract JSONObject getParameters ();
	
	public abstract JSONObject getReturnValue ();
	
	public abstract String getScript ();
	
	private static final Map<String, CardLayout> cardLayoutMap = Collections.unmodifiableMap(initCardLayoutMap());
	private static Map<String, CardLayout> initCardLayoutMap() {
		Map<String, CardLayout> cardLayoutMap = new HashMap<>();
		for(CardLayout cardLayout : values()) {
			cardLayoutMap.put(cardLayout.getName(), cardLayout);
		}
		return cardLayoutMap;
	}
	
	public static CardLayout getCardLayout(String layoutName) {
		return cardLayoutMap.get(layoutName);
	}
	
	public static Map<String, CardLayout> getAllCardLayouts() {
		return cardLayoutMap;
	}
}
