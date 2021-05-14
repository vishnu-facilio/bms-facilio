Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        date = new NameSpace("date");
        dateRangeObj = date.getDateRange("Today");
        period = "Last Value";
        db = {};
         if (params.dateRange == null) {
             db = {
                criteria: [parentId == (params.reading.parentId) && ttime == dateRangeObj],
                field: params.reading.fieldName,
                aggregation: params.reading.yAggr
            };
        }
        else {
            if (params.dateRange != "none") {
                date = new NameSpace("date");
                dateRangeObj = date.getDateRange(params.dateRange);
                period = params.dateRange;
                db = {
                    criteria: [parentId == (params.reading.parentId)],
                    field: params.reading.fieldName,
                    aggregation: params.reading.yAggr
                };
            } else {
                db = {
                    criteria: [parentId == (params.reading.parentId)],
                    field: params.reading.fieldName,
                    aggregation: params.reading.yAggr
                };
            }
        }
        
       
        fetchModule = Module(params.reading.moduleName);
        cardValue = fetchModule.fetch(db);
        enumMap = Reading(fieldid, params.reading.parentId).getEnumMap();
        valueMap = {};
        valueMap["value"] = cardValue;
        valueMap["actualValue"] = cardValue;
        if (enumMap != null) {
            enumValue = cardValue;
          	if (cardValue == true) {
              enumValue = 1;
            }
          	else if (cardValue == false) {
              enumValue = 0;
            }
            if (enumValue != null && enumMap.get(enumValue) != null) {
                valueMap["value"] = enumMap.get(enumValue);
            }
        }
        if (fieldMapInfo != null) {
            valueMap["unit"] = fieldMapInfo.get("unit");
            valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
            valueMap["label"] = fieldMapInfo.get("displayName");
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