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
        else if (params.dateRange != null) {
            dateRangeObj = date.getDateRange(params.dateRange);
            period = params.dateRange;
        } else {
            dateRangeObj = date.getDateRange("Current Month");
            period = "Last Value";
        }
        fetchModule = Module(params.reading.moduleName);
        values = [];
        for each index, parentId in params.reading.parentId {
            enumMap = Reading(fieldid, parentId).getEnumMap();
            db = {
                criteria: [parentId == (parentId) && ttime == dateRangeObj],
                field: params.reading.fieldName,
                aggregation: params.reading.yAggr
            };
            cardValue = fetchModule.fetch(db);
            assetModule = Module("asset");
            assetDb = {
                criteria: [id == parentId],
                field: "name"
            };
            assetRecord = assetModule.fetch(assetDb);
            valueMap = {};
            valueMap["value"] = cardValue;
            valueMap["actualValue"] = cardValue;
            valueMap["name"] = assetRecord[0];
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
        }
        result["values"] = values;
    } else {
        result["values"] = [];
    }
    result["title"] = params.title;
    result["period"] = period;
    return result;
}