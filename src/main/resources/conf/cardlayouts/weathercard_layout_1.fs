Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField("temperature", "weather");
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = new NameSpace("date");
        dateRangeObj = date.getDateRange("Current Month");
        db = {
            criteria: [parentId == params.baseSpaceId && ttime == dateRangeObj],
            limit: 1,
            orderBy: "ttime" desc
        };
        fetchModule = Module("weather");
        cardValue = fetchModule.fetch(db);
        valueMap = {};
        if (cardValue != null) {
            valueMap["value"] = cardValue[0];
        } else {
            valueMap["value"] = null;
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
    return result;
}