Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = new NameSpace("date");
        dateRangeObj = null;
        period = null;
        if(params.cardFilters!=null)
        {
            cardFilters=params.cardFilters;
            startTime=cardFilters.startTime;
            endTime=cardFilters.endTime;
            period=cardFilters.dateLabel;
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);

        }
        else if (params.dateRange!=null) {
            dateRangeObj = date.getDateRange(params.dateRange);
            period = params.dateRange;
        } else {
            dateRangeObj = date.getDateRange("Current Month");
            period = "Last Value";
        }
        fetchModule = Module(params.reading.moduleName);
        enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();
        values = [];
        for each index, yAggr in params.reading.yAggr {
            db = {
                criteria: [parentId == (params.reading.parentId) && ttime == dateRangeObj],
                field: params.reading.fieldName,
                aggregation: yAggr
            };
            cardValue = fetchModule.fetch(db);
            valueMap = {};
            valueMap["value"] = cardValue;
            valueMap["aggregation"] = yAggr;
            if (enumMap != null) {
                if (cardValue != null && enumMap.get(cardValue) != null) {
                    valueMap["value"] = enumMap.get(cardValue);
                }
            }
            if (fieldMapInfo != null) {
                valueMap["unit"] = fieldMapInfo.get("unit");
                valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
            }
            values.push(valueMap);
        }
        result["values"] = values;
    } else {
        result["values"] = [];
    }
    result["title"] = params.title;
    result["period"] = period;
    return result;
}