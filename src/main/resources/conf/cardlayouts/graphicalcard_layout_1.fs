Map cardLayout(Map params) {
    result = {};
    values = [];
    lastRecord = null;
    for each index, readingEntry in params.readings {
        reading = readingEntry.reading;
        valueMap = {};
        valueMap["label"] = readingEntry.label;
        valueMap["icon"] = readingEntry.icon;
        fieldObj = new NameSpace("module").getField(reading.fieldName, reading.moduleName);
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
                dateRangeObj = date.getDateRange("Today");
                period = "Last Value";
            }
            db = {
                criteria: [parentId == (reading.parentId) && ttime == dateRangeObj],
                field: reading.fieldName,
                aggregation: reading.yAggr
            };
            db1 = {
                criteria: [parentId == (reading.parentId) && ttime == dateRangeObj],
                field: "ttime",
                limit: 1,
                orderBy: "ttime" desc
            };
            fetchModule = Module(reading.moduleName);
            cardValue = fetchModule.fetch(db);
            if (lastRecord == null) {
                lastRow = fetchModule.fetch(db1);
                if (lastRow != null) {
                    lastRecord = lastRow[0];
                }
            }
            enumMap = Reading(fieldid, reading.parentId).getEnumMap();
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
            values.push(valueMap);
        } else {
            valueMap["value"] = null;
            values.push(valueMap);
        }
    }
    result["title"] = params.title;
    result["lastUpdated"] = lastRecord;
    result["values"] = values;
    return result;
}