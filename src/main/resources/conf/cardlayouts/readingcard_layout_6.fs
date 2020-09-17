Map cardLayout(Map params) {
    result = {};
    values = [];
    for each index, reading in params.readings {
        valueMap = {};
        valueMap["title"] = reading.title;
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
                dateRangeObj = date.getDateRange("Current Month");
                period = "Last Value";
            }
            db = {
                criteria: [parentId == (reading.parentId) && ttime == dateRangeObj],
                field: reading.fieldName,
                aggregation: reading.yAggr
            };
            fetchModule = Module(reading.moduleName);
            cardValue = fetchModule.fetch(db);
            enumMap = Reading(fieldid, reading.parentId).getEnumMap();
            valueMap["value"] = cardValue;
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
            values.push(valueMap);
        } else {
            valueMap["value"] = null;
            values.push(valueMap);
        }
    }
    result["values"] = values;
    result["period"] = period;
    return result;
}