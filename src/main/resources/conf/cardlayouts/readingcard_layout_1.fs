Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();        
        dateRangeObj = null;
        period = null;

        if (params.cardFilters != null ) {
            cardFilters=params.cardFilters;
            startTime = cardFilters.startTime;
            endTime = cardFilters.endTime;
            period=cardFilters.dateLabel;
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
        }

        else if (params.dateRange != null) {
            date = new NameSpace("date");
            dateRangeObj = date.getDateRange(params.dateRange);
            period = params.dateRange;
        } else {
            dateRangeObj = date.getDateRange("Current Month");
            period = "Last Value";
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
        valueMap["actualValue"] = cardValue;
        if (fieldMapInfo.dataTypeEnum == "BOOLEAN") {
            if (cardValue == true && fieldMapInfo.trueVal != null) {
                valueMap["value"] = fieldMapInfo.trueVal;
            }
            else if (cardValue == false && fieldMapInfo.falseVal != null) {
                valueMap["value"] = fieldMapInfo.falseVal;
            }
        }
        else if (enumMap != null) {
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
    result["title"] = params.title;
    result["image"] = params.imageId;
    result["period"] = period;
    return result;
}