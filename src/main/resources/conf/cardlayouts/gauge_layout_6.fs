Map cardLayout(Map params) {
    result = {};
    date = new NameSpace("date");
    dateRangeObj = null;
    period = null;
    if(params.cardFilters!=null){
            cardFilters=params.cardFilters;
            startTime=cardFilters.startTime;
            endTime=cardFilters.endTime;
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
            period=cardFilters.dateLabel;
    }
    else if (params.dateRange != null) {
        dateRangeObj = date.getDateRange(params.dateRange);
        period = params.dateRange;
    } else {
        dateRangeObj = date.getDateRange("Current Month");
        period = "Last Value";
    }
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        db = {
            criteria: [parentId == (params.reading.parentId) && ttime == dateRangeObj],
            field: params.reading.fieldName,
            aggregation: params.reading.yAggr
        };
        fetchModule = Module(params.reading.moduleName);
        cardValue = fetchModule.fetch(db);
        enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();
        valueMap = {};
        valueMap["value"] = cardValue;
        if (enumMap != null) {
            if (cardValue != null && enumMap.get(cardValue) != null) {
                valueMap["value"] = enumMap.get(cardValue);
            }
        }
        if (fieldMapInfo != null) {
            valueMap["unit"] = fieldMapInfo.get("unit");
            valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
        }
        result["value"] = valueMap;
    } else {
        valueMap = {};
        valueMap["value"] = null;
        result["value"] = valueMap;
    }
    if (params.minSafeLimitType != null) {
        if (params.minSafeLimitType == "constant") {
            targetValueMap = {};
            targetValueMap["value"] = params.minSafeLimitConstant;
            result["minValue"] = targetValueMap;
        } else {
            targetFieldObj = new NameSpace("module").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);
            targetFieldId = targetFieldObj.id();
            targetFieldMap = targetFieldObj.asMap();
            db = {
                criteria: [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],
                field: params.minSafeLimitReading.fieldName,
                aggregation: params.minSafeLimitReading.yAggr
            };
            fetchModule = Module(params.minSafeLimitReading.moduleName);
            cardValue = fetchModule.fetch(db);
            enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();
            valueMap = {};
            valueMap["value"] = cardValue;
            if (enumMap != null) {
                if (cardValue != null && enumMap.get(cardValue) != null) {
                    valueMap["value"] = enumMap.get(cardValue);
                }
            }
            if (fieldMapInfo != null) {
                valueMap["unit"] = targetFieldMap.get("unit");
                valueMap["dataType"] = targetFieldMap.get("dataTypeEnum");
            }
            result["minValue"] = valueMap;
        }
    } else {
        result["minValue"] = null;
    }
    if (params.maxSafeLimitType != null) {
        if (params.maxSafeLimitType == "constant") {
            targetValueMap = {};
            targetValueMap["value"] = params.maxSafeLimitConstant;
            result["maxValue"] = targetValueMap;
        } else {
            targetFieldObj = new NameSpace("module").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);
            targetFieldId = targetFieldObj.id();
            targetFieldMap = targetFieldObj.asMap();
            db = {
                criteria: [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],
                field: params.maxSafeLimitReading.fieldName,
                aggregation: params.maxSafeLimitReading.yAggr
            };
            fetchModule = Module(params.maxSafeLimitReading.moduleName);
            cardValue = fetchModule.fetch(db);
            enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();
            valueMap = {};
            valueMap["value"] = cardValue;
            if (enumMap != null) {
                if (cardValue != null && enumMap.get(cardValue) != null) {
                    valueMap["value"] = enumMap.get(cardValue);
                }
            }
            if (fieldMapInfo != null) {
                valueMap["unit"] = targetFieldMap.get("unit");
                valueMap["dataType"] = targetFieldMap.get("dataTypeEnum");
            }
            result["maxValue"] = valueMap;
        }
    } else {
        result["maxValue"] = null;
    }
    result["title"] = params.title;
    result["period"] = period;
    return result;
}