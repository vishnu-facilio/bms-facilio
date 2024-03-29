Map cardLayout(Map params) {
    result = {};
    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
        dateRangeObj = null;
        period = null;
        date = new NameSpace("date");

        if (params.cardFilters != null ) {
            cardFilters=params.cardFilters;
            startTime = cardFilters.startTime;
            endTime = cardFilters.endTime;            
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
            period=cardFilters.dateLabel;
        }
        else{
        
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
        valueMap["actualValue"] = cardValue;
        baselineValueMap["value"] = baselineCardValue;
        currentVal = 0;
        previousVal = 0;
        if (fieldMapInfo.dataTypeEnum == "BOOLEAN") {
            if (cardValue == true && fieldMapInfo.trueVal != null) {
                valueMap["value"] = fieldMapInfo.trueVal;
            }
            else if (cardValue == false && fieldMapInfo.falseVal != null) {
                valueMap["value"] = fieldMapInfo.falseVal;
            }
            if (baselineCardValue == true && fieldMapInfo.trueVal != null) {
                baselineValueMap["value"] = fieldMapInfo.trueVal;
            }
            else if (baselineCardValue == false && fieldMapInfo.falseVal != null) {
                baselineValueMap["value"] = fieldMapInfo.falseVal;
            }
        }
        else if (enumMap != null) {
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
    if (params.imageSpaceId != null) {
        basespaceModule = Module("basespace");
        basespaceDb = {
            criteria: [id == params.imageSpaceId]
        };
        baseSpaceList = basespaceModule.fetch(basespaceDb);
        baseSpace = baseSpaceList[0];
        if (baseSpace != null && baseSpace.photoId != null) {
            result["image"] = "/api/v2/files/preview/" + baseSpace.photoId;
        } else {
            result["image"] = null;
        }
    } else {
        result["image"] = null;
    }
    result["title"] = params.title;
    result["period"] = period;
    return result;
}