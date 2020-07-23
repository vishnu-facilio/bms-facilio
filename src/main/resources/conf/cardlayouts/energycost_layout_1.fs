Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = new NameSpace("date");
        dateRangeObj = null;
        period = null;
        if (params.dateRange != null) {
            dateRangeObj = date.getDateRange(params.dateRange);
            period = params.dateRange;
        } else {
            dateRangeObj = date.getDateRange("Current Month");
            period = "Last Value";
        }
        if (params.filters != null && params.filters.ttime != null) {
            startTimeStr = params.filters.ttime.value[0];
            endTimeStr = params.filters.ttime.value[1];
            startTime = new NameSpace("number").longValue(startTimeStr);
            endTime = new NameSpace("number").longValue(endTimeStr);

            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
  
			dateNs = new NameSpace("date");
  			period = dateNs.getFormattedTime(startTime,"dd-MMM-yyyy") + " to " + dateNs.getFormattedTime(startTime,"dd-MMM-yyyy");
        }
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
    if (params.multiplier != null && result.value.value != null) {
        result.value.value = result.value.value * params.multiplier;
    }
    result.value.unit = "$";
    result["title"] = params.title;
    result["period"] = period;
    return result;
}