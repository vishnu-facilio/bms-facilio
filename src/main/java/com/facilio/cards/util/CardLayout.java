package com.facilio.cards.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public enum CardLayout {
	
	READINGCARD_LAYOUT_1 ("readingcard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
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
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
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
	
	READINGCARD_LAYOUT_2 ("readingcard_layout_2") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("baseline", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("value", true);
			returnValue.put("baselineValue", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		date = NameSpace(\"date\");"
					+ "		dateRangeObj = date.getDateRange(params.dateRange);"
					+ "		baselineDateRangeObj = date.getDateRange(params.dateRange, params.baseline);"
					+ "		period = params.dateRange;"
					+ "		db = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		baselineDb = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == baselineDateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		baselineCardValue = fetchModule.fetch(baselineDb);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		baselineValueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		baselineValueMap[\"value\"] = baselineCardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "			if (baselineCardValue != null) {"
					+ "				baselineValueMap[\"value\"] = enumMap[baselineCardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "			baselineValueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			baselineValueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "		result[\"baselineValue\"] = baselineValueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "		baselineValueMap = {};"
					+ "		baselineValueMap[\"value\"] = null;"
					+ "		result[\"baselineValue\"] = baselineValueMap;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	READINGCARD_LAYOUT_3 ("readingcard_layout_3") {
		
		public JSONObject getParameters () {
			return null;
		}
		
		public JSONObject getReturnValue () {
			return null;
		}
		
		public String getScript () {
			return null;
		}
	},
	
	READINGCARD_LAYOUT_4 ("readingcard_layout_4") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("values", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		values = [];"
					+ "		for each index, yAggr in params.reading.yAggr {"
					+ "			db = {"
					+ "				criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.reading.fieldName,"
					+ "				aggregation : yAggr"
					+ "			};"
					
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			valueMap[\"aggregation\"] = yAggr;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "		result[\"values\"] = values;"
					+ "}"
					+ "else {"
					+ "		result[\"values\"] = [];"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	READINGCARD_LAYOUT_5 ("readingcard_layout_5") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("values", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		values = [];"
					+ "		for each index, parentId in params.reading.parentId {"
					+ "			enumMap = Reading(fieldid, parentId).getEnumMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (parentId) && ttime == dateRangeObj],"
					+ "				field : params.reading.fieldName,"
					+ "				aggregation : params.reading.yAggr"
					+ "			};"
					
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			assetModule = Module(\"asset\");"
					+ "			assetDb = {criteria : [id == parentId], field : \"name\"};"
					+ "			assetRecord = assetModule.fetch(assetDb);"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			valueMap[\"name\"] = assetRecord[0];"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "		result[\"values\"] = values;"
					+ "}"
					+ "else {"
					+ "		result[\"values\"] = [];"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	READINGCARD_LAYOUT_6 ("readingcard_layout_6") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("readings", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("values", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("values = [];"
					+ "for each index, reading in params.readings {"
					+ "		valueMap = {};"
					+ "		valueMap[\"title\"] = reading.title;"
					+ "		fieldObj = NameSpace(\"module\").getField(reading.fieldName, reading.moduleName);\n"
					+ "		if (fieldObj != null) {"
					+ "			fieldid = fieldObj.id();"
					+ "			fieldMapInfo = fieldObj.asMap();"
					+ "			date = NameSpace(\"date\");"
					+ "			dateRangeObj = null;"
					+ "			period = null;"
					+ "			if (params.dateRange != null) {"
					+ "				dateRangeObj = date.getDateRange(params.dateRange);"
					+ "				period = params.dateRange;"
					+ "			} else {"
					+ "				dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "				period = \"Last Value\";"
					+ "			}"
					+ "			db = {"
					+ "				criteria : [parentId == (reading.parentId) && ttime == dateRangeObj],"
					+ "				field : reading.fieldName,"
					+ "				aggregation : reading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(reading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(fieldid, reading.parentId).getEnumMap();"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "		else {"
					+ "			valueMap[\"value\"] = null;"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "}"
					+ "result[\"values\"] = values;"
					+ "result[\"period\"] = period;");
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	GAUGE_LAYOUT_1 ("gauge_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("minSafeLimitType", true);
			params.put("minSafeLimitConstant", true);
			params.put("minSafeLimitReading", true);
			params.put("maxSafeLimitType", true);
			params.put("maxSafeLimitConstant", true);
			params.put("maxSafeLimitReading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("currentValue", true);
			returnValue.put("minValue", true);
			returnValue.put("maxValue", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("date = NameSpace(\"date\");"
					+ "dateRangeObj = null;"
					+ "period = null;"
					+ "if (params.dateRange != null) {"
					+ "		dateRangeObj = date.getDateRange(params.dateRange);"
					+ "		period = params.dateRange;"
					+ "} else {"
					+ "		dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "		period = \"Last Value\";"
					+ "}"
					+ "fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		db = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.minSafeLimitType != null) {"
					+ "		if (params.minSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.minSafeLimitConstant;"
					+ "			result[\"minValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.minSafeLimitReading.fieldName,"
					+ "				aggregation : params.minSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.minSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"minValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"minValue\"] = null;"
					+ "}"
					+ "if (params.maxSafeLimitType != null) {"
					+ "		if (params.maxSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.maxSafeLimitConstant;"
					+ "			result[\"maxValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.maxSafeLimitReading.fieldName,"
					+ "				aggregation : params.maxSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.maxSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"maxValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"maxValue\"] = null;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	GAUGE_LAYOUT_2 ("gauge_layout_2") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("minSafeLimitType", true);
			params.put("minSafeLimitConstant", true);
			params.put("minSafeLimitReading", true);
			params.put("maxSafeLimitType", true);
			params.put("maxSafeLimitConstant", true);
			params.put("maxSafeLimitReading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("currentValue", true);
			returnValue.put("minValue", true);
			returnValue.put("maxValue", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("date = NameSpace(\"date\");"
					+ "dateRangeObj = null;"
					+ "period = null;"
					+ "if (params.dateRange != null) {"
					+ "		dateRangeObj = date.getDateRange(params.dateRange);"
					+ "		period = params.dateRange;"
					+ "} else {"
					+ "		dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "		period = \"Last Value\";"
					+ "}"
					+ "fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		db = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.minSafeLimitType != null) {"
					+ "		if (params.minSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.minSafeLimitConstant;"
					+ "			result[\"minValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.minSafeLimitReading.fieldName,"
					+ "				aggregation : params.minSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.minSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"minValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"minValue\"] = null;"
					+ "}"
					+ "if (params.maxSafeLimitType != null) {"
					+ "		if (params.maxSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.maxSafeLimitConstant;"
					+ "			result[\"maxValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.maxSafeLimitReading.fieldName,"
					+ "				aggregation : params.maxSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.maxSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"maxValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"maxValue\"] = null;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	GAUGE_LAYOUT_3 ("gauge_layout_3") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("minSafeLimitType", true);
			params.put("minSafeLimitConstant", true);
			params.put("minSafeLimitReading", true);
			params.put("maxSafeLimitType", true);
			params.put("maxSafeLimitConstant", true);
			params.put("maxSafeLimitReading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("currentValue", true);
			returnValue.put("minValue", true);
			returnValue.put("maxValue", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("date = NameSpace(\"date\");"
					+ "dateRangeObj = null;"
					+ "period = null;"
					+ "if (params.dateRange != null) {"
					+ "		dateRangeObj = date.getDateRange(params.dateRange);"
					+ "		period = params.dateRange;"
					+ "} else {"
					+ "		dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "		period = \"Last Value\";"
					+ "}"
					+ "fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		db = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.minSafeLimitType != null) {"
					+ "		if (params.minSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.minSafeLimitConstant;"
					+ "			result[\"minValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.minSafeLimitReading.fieldName,"
					+ "				aggregation : params.minSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.minSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"minValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"minValue\"] = null;"
					+ "}"
					+ "if (params.maxSafeLimitType != null) {"
					+ "		if (params.maxSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.maxSafeLimitConstant;"
					+ "			result[\"maxValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.maxSafeLimitReading.fieldName,"
					+ "				aggregation : params.maxSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.maxSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"maxValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"maxValue\"] = null;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	GAUGE_LAYOUT_4 ("gauge_layout_4") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("reading", true);
			params.put("minSafeLimitType", true);
			params.put("minSafeLimitConstant", true);
			params.put("minSafeLimitReading", true);
			params.put("maxSafeLimitType", true);
			params.put("maxSafeLimitConstant", true);
			params.put("maxSafeLimitReading", true);
			params.put("dateRange", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("currentValue", true);
			returnValue.put("minValue", true);
			returnValue.put("maxValue", true);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("date = NameSpace(\"date\");"
					+ "dateRangeObj = null;"
					+ "period = null;"
					+ "if (params.dateRange != null) {"
					+ "		dateRangeObj = date.getDateRange(params.dateRange);"
					+ "		period = params.dateRange;"
					+ "} else {"
					+ "		dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "		period = \"Last Value\";"
					+ "}"
					+ "fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		db = {"
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.minSafeLimitType != null) {"
					+ "		if (params.minSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.minSafeLimitConstant;"
					+ "			result[\"minValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.minSafeLimitReading.fieldName,"
					+ "				aggregation : params.minSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.minSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"minValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"minValue\"] = null;"
					+ "}"
					+ "if (params.maxSafeLimitType != null) {"
					+ "		if (params.maxSafeLimitType == \"constant\") {"
					+ "			targetValueMap = {};"
					+ "			targetValueMap[\"value\"] = params.maxSafeLimitConstant;"
					+ "			result[\"maxValue\"] = targetValueMap;"
					+ "		}"
					+ "		else {"
					+ "			targetFieldObj = NameSpace(\"module\").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);\n"
					+ "			targetFieldId = targetFieldObj.id();"
					+ "			targetFieldMap = targetFieldObj.asMap();"
					+ "			db = {"
					+ "				criteria : [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],"
					+ "				field : params.maxSafeLimitReading.fieldName,"
					+ "				aggregation : params.maxSafeLimitReading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(params.maxSafeLimitReading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();"
					+ "			valueMap = {};"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = targetFieldMap.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = targetFieldMap.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			result[\"maxValue\"] = valueMap;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"maxValue\"] = null;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	ENERGYCARD_LAYOUT_1 ("energycard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("imageSpaceId", true);
			params.put("reading", true);
			params.put("dateRange", false);
			params.put("baseline", false);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("value", true);
			returnValue.put("baselineValue", false);
			returnValue.put("period", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.imageSpaceId != null) {"
					+ "		basespaceModule = Module(\"basespace\");"
					+ "		basespaceDb = {"
					+ "			criteria : [id == params.imageSpaceId]"
					+ "		};"
					+ "		baseSpaceList = basespaceModule.fetch(basespaceDb);"
					+ "		baseSpace = baseSpaceList[0];"
					+ "		if (baseSpace != null && baseSpace.photoId != null) {"
					+ "			result[\"image\"] = \"/api/v2/files/preview/\" + baseSpace.photoId;"
					+ "		}"
					+ "		else {"
					+ "			result[\"image\"] = null;"
					+ "		}"
					+ "}"
					+ "else {"
					+ "		result[\"image\"] = null;"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	ENERGYCOST_LAYOUT_1 ("energycost_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("multiplier", true);
			params.put("reading", true);
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
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.multiplier != null && result.value.value != null) {"
					+ "		result.value.value = result.value.value * params.multiplier;"
					+ "}"
					+ "result.value.unit = \"$\";"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},

	CARBONCARD_LAYOUT_1 ("carboncard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("multiplier", true);
			params.put("reading", true);
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
			
			sb.append("fieldObj = NameSpace(\"module\").getField(params.reading.fieldName, params.reading.moduleName);\n"
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
					+ "			criteria : [parentId == (params.reading.parentId) && ttime == dateRangeObj],"
					+ "			field : params.reading.fieldName,"
					+ "			aggregation : params.reading.yAggr"
					+ "		};"
					+ "		fetchModule = Module(params.reading.moduleName);"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = cardValue;"
					+ "		if (enumMap != null) {"
					+ "			if (cardValue != null) {"
					+ "				valueMap[\"value\"] = enumMap[cardValue];"
					+ "			}"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "if (params.multiplier != null && result.value.value != null) {"
					+ "		result.value.value = result.value.value * params.multiplier;"
					+ "}"
					+ "result.value.unit = \"kg\";"
					+ "result[\"title\"] = params.title;"
					+ "result[\"period\"] = period;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	WEATHERCARD_LAYOUT_1 ("weathercard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("baseSpaceId", true);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("value", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fieldObj = NameSpace(\"module\").getField(\"temperature\", \"weather\");\n"
					+ "if (fieldObj != null) {"
					+ "		fieldid = fieldObj.id();"
					+ "		fieldMapInfo = fieldObj.asMap();"
					+ "		date = NameSpace(\"date\");"
					+ "		dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "		db = {"
					+ "			criteria : [parentId == params.baseSpaceId && ttime == dateRangeObj],"
					+ "			limit : 1,"
					+ "			orderBy : \"ttime\" desc"
					+ "		};"
					+ "		fetchModule = Module(\"weather\");"
					+ "		cardValue = fetchModule.fetch(db);"
					+ "		valueMap = {};"
					+ "		if (cardValue != null) {"
					+ "			valueMap[\"value\"] = cardValue[0];"
					+ "		} else {"
					+ "			valueMap[\"value\"] = null;"
					+ "		}"
					+ "		if (fieldMapInfo != null) {"
					+ "			valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "			valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "		}"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "else {"
					+ "		valueMap = {};"
					+ "		valueMap[\"value\"] = null;"
					+ "		result[\"value\"] = valueMap;"
					+ "}"
					+ "result[\"title\"] = params.title;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	GRAPHICALCARD_LAYOUT_1 ("graphicalcard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("readings", true);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("values", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("values = [];"
					+ "for each index, readingEntry in params.readings {"
					+ "		reading = readingEntry.reading;"
					+ "		valueMap = {};"
					+ "		valueMap[\"label\"] = readingEntry.label;"
					+ "		valueMap[\"icon\"] = readingEntry.icon;"
					+ "		fieldObj = NameSpace(\"module\").getField(reading.fieldName, reading.moduleName);\n"
					+ "		if (fieldObj != null) {"
					+ "			fieldid = fieldObj.id();"
					+ "			fieldMapInfo = fieldObj.asMap();"
					+ "			date = NameSpace(\"date\");"
					+ "			dateRangeObj = null;"
					+ "			period = null;"
					+ "			if (params.dateRange != null) {"
					+ "				dateRangeObj = date.getDateRange(params.dateRange);"
					+ "				period = params.dateRange;"
					+ "			} else {"
					+ "				dateRangeObj = date.getDateRange(\"Current Month\");"
					+ "				period = \"Last Value\";"
					+ "			}"
					+ "			db = {"
					+ "				criteria : [parentId == (reading.parentId) && ttime == dateRangeObj],"
					+ "				field : reading.fieldName,"
					+ "				aggregation : reading.yAggr"
					+ "			};"
					+ "			fetchModule = Module(reading.moduleName);"
					+ "			cardValue = fetchModule.fetch(db);"
					+ "			enumMap = Reading(fieldid, reading.parentId).getEnumMap();"
					+ "			valueMap[\"value\"] = cardValue;"
					+ "			if (enumMap != null) {"
					+ "				if (cardValue != null) {"
					+ "					valueMap[\"value\"] = enumMap[cardValue];"
					+ "				}"
					+ "			}"
					+ "			if (fieldMapInfo != null) {"
					+ "				valueMap[\"unit\"] = fieldMapInfo.get(\"unit\");"
					+ "				valueMap[\"dataType\"] = fieldMapInfo.get(\"dataTypeEnum\");"
					+ "			}"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "		else {"
					+ "			valueMap[\"value\"] = null;"
					+ "			values.push(valueMap);"
					+ "		}"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"values\"] = values;");
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	},
	
	MAPCARD_LAYOUT_1 ("mapcard_layout_1") {
		
		public JSONObject getParameters () {
			JSONObject params = new JSONObject();
			params.put("title", true);
			params.put("module", true);
			params.put("field", true);
			params.put("criteria", true);
			params.put("marker", true);
			return params;
		}
		
		public JSONObject getReturnValue () {
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", true);
			returnValue.put("values", true);
			return returnValue;
		}
		
		public String getScript () {
			StringBuilder sb = new StringBuilder();
			
			sb.append("fetchModule = Module(params.module);"
					+ "records = [];"
					+ "if (params.module == \"asset\") {"
					+ "		records = fetchModule.fetch([geoLocation != null]);"
					+ "} else {"
					+ "		records = fetchModule.fetch([location != null]);"
					+ "}"
					+ "values = [];"
					+ "if (records != null) {"
					+ "		for each index, record in records {"
					+ "			 locationEntry = {};"
					+ "			 locationEntry[\"id\"] = record.id;"
					+ "			 locationEntry[\"name\"] = record.name;"
					+ "			 if (record.photoId != null && record.photoId > 0) {"
					+ "			 	locationEntry[\"image\"] = \"/api/v2/files/preview/\" + record.photoId;"
					+ "			 } else {"
					+ "			 	locationEntry[\"image\"] = null;"
					+ "			 }"
					+ "			 if (params.module == \"asset\") {"
					+ "			 	locationEntry[\"location\"] = record.geoLocation;"
					+ "			 }"
					+ "			 else {"
					+ "			 	locationObj = Module(\"location\").fetch([id == record.location.id])[0];"
					+ "			 	if (locationObj != null && locationObj.lat != null && locationObj.lng != null) {"
					+ "			 		locationEntry[\"location\"] = locationObj.lat + \",\" + locationObj.lng;"
					+ "			 	}"
					+ "			 	else {"
					+ "			 		locationEntry[\"location\"] = null;"
					+ "			 	}"
					+ "			 }"
					+ "			 locationEntry[\"icon\"] = params.marker.icon;"
					+ "			 if (params.marker.type == \"noOfAlarms\") {"
					+ "			 	occurenceCount = {"
					+ "			 		criteria: [resource == record.id],"
		            + "			 		field: \"id\","
		            + "			 		aggregation: \"count\""
		            + "			 	};"
		            + "			 	locationEntry[\"value\"] = Module(\"alarm\").fetch(occurenceCount);"
					+ "			 }"
					+ "			 else if (params.marker.type == \"noOfWorkorders\") {"
					+ "			 	woCount = {"
					+ "			 		criteria: [resource == record.id],"
		            + "			 		field: \"id\","
		            + "			 		aggregation: \"count\""
		            + "			 	};"
		            + "			 	locationEntry[\"value\"] = Module(\"workorder\").fetch(woCount);"
					+ "			 }"
					+ "			 values.push(locationEntry);"
					+ "		}"
					+ "}"
					+ "result[\"title\"] = params.title;"
					+ "result[\"values\"] = values;");
					
			
			return CardUtil.appendCardPrefixSuffixScript(sb.toString());
		}
	};
	
	private String name;
	
	private CardLayout (String name) {
		this.name = name;
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
