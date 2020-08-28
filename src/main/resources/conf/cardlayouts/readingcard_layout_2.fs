Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = new NameSpace("date");
        
        if(params.cardFilters!=null)
        {
            cardFilters=params.cardFilters;
            startTime = cardFilters.startTime;
            endTime = cardFilters.endTime;
            period=cardFilters.dateLabel;
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
        }
        else
        {        
        dateRangeObj = date.getDateRange(params.dateRange);
        period = params.dateRange;
        }

        baselineDateRangeObj = date.getDateRange(params.dateRange, params.baseline);
        


        db = {
            criteria: [parentId == (params.reading.parentId) && ttime == dateRangeObj],
            field: params.reading.fieldName,
            aggregation: params.reading.yAggr
        };
        fetchModule = Module(params.reading.moduleName);
        cardValue = fetchModule.fetch(db);
        baselineDb = {
            criteria: [parentId == (params.reading.parentId) && ttime == baselineDateRangeObj],
            field: params.reading.fieldName,
            aggregation: params.reading.yAggr
        };
        baselineCardValue = fetchModule.fetch(baselineDb);
        enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();
        valueMap = {};
        baselineValueMap = {};
        valueMap["value"] = cardValue;
        baselineValueMap["value"] = baselineCardValue;
        if (enumMap != null) {
            if (cardValue != null && enumMap.get(cardValue) != null) {
                valueMap["value"] = enumMap.get(cardValue);
            }
            if (baselineCardValue != null && enumMap.get(baselineCardValue) != null) {
                baselineValueMap["value"] = enumMap.get(baselineCardValue);
            }
        }
        if (fieldMapInfo != null) {
            valueMap["unit"] = fieldMapInfo.get("unit");
            valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
            baselineValueMap["unit"] = fieldMapInfo.get("unit");
            baselineValueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
        }
        result["value"] = valueMap;
        result["baselineValue"] = baselineValueMap;
    } else {
        valueMap = {};
        valueMap["value"] = null;
        result["value"] = valueMap;
        baselineValueMap = {};
        baselineValueMap["value"] = null;
        result["baselineValue"] = baselineValueMap;
    }
    result["title"] = params.title;
    result["image"] = params.imageId;
    result["period"] = period;
    result["baselinePeriod"] = params.baseline;
    return result;
}