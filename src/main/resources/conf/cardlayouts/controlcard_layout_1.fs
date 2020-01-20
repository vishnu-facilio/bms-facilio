Map cardLayout(Map params) {
    result = {};
    fieldObj = NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = NameSpace("date");
        dateRangeObj = date.getDateRange("Today");
        period = "Last Value";
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
    result["title"] = params.title;
    result["control"] = params.control;
    return result;
}