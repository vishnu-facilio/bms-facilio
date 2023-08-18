Map cardLayout(Map params) {
    result = {};
    date = new NameSpace("date");
    dateRangeObj = null;
    period = null;

    kpiType = {};
    kpiType["live"] = 1;
    kpiType["schedule"] = 2;
    kpiType["dynamic"] = 3;

    if(params.cardFilters!=null){
            cardFilters=params.cardFilters;
            startTime=cardFilters.startTime;
            endTime=cardFilters.endTime;
            dateRangeObj = new NameSpace("dateRange").create(startTime, endTime);
            period=cardFilters.dateLabel;
        }    
    else if (params.dateRange != null) {
        dateRangeObj = date.getDateRange(params.dateRange);
        period = params.dateRange;
    } else {
        dateRangeObj = date.getDateRange("Current Month");
        period = "Last Value";
    }

    newStartTime = null;
    newEndTime = null;
    if(dateRangeObj !=null){
        newStartTime = dateRangeObj.getStartTime();
        newEndTime = dateRangeObj.getEndTime();
    }

    fieldObj = new NameSpace("module").getField(params.reading.fieldName, params.reading.moduleName);
    if (fieldObj != null) {
        fieldid = fieldObj.id();
        fieldMapInfo = fieldObj.asMap();
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
    }
    else if(params.reading!=null && params.reading.kpiType == "DYNAMIC"){
         readingObj = params.reading;
         valueMap = {};
         dynamicResult = new NameSpace("readingkpi").getResult(kpiType.dynamic,readingObj.fieldId,readingObj.parentId,newStartTime,newEndTime);
         valueMap["actualValue"] = dynamicResult.value;
         valueMap["dataType"] = dynamicResult.dataType;
         valueMap["unit"] = dynamicResult.unit;
         valueMap["value"] = dynamicResult.value;
         result["value"] = valueMap;
    }
    else {
        valueMap = {};
        valueMap["value"] = null;
        result["value"] = valueMap;
    }
    if (params.minSafeLimitType != null) {
        minSafeLimitObj =  params.minSafeLimitReading;
        if (params.minSafeLimitType == "constant") {
            targetValueMap = {};
            targetValueMap["value"] = params.minSafeLimitConstant;
            result["minValue"] = targetValueMap;
        }
        else if (minSafeLimitObj !=null && minSafeLimitObj.kpiType =="DYNAMIC"){
            valueMap = {};
            minDynamicResult = new NameSpace("readingkpi").getResult(kpiType.dynamic,minSafeLimitObj.fieldId,minSafeLimitObj.parentId,newStartTime,newEndTime);
            valueMap["actualValue"] = minDynamicResult.value;
            valueMap["dataType"] = minDynamicResult.dataType;
            valueMap["unit"] = minDynamicResult.unit;
            valueMap["value"] = minDynamicResult.value;
            result["minValue"] = valueMap;
        }
        else {
            targetFieldObj = new NameSpace("module").getField(params.minSafeLimitReading.fieldName, params.minSafeLimitReading.moduleName);
            targetFieldId = targetFieldObj.id();
            targetFieldMap = targetFieldObj.asMap();
            db = {
                criteria: [parentId == (params.minSafeLimitReading.parentId) && ttime == dateRangeObj],
                field: params.minSafeLimitReading.fieldName,
                aggregation: params.minSafeLimitReading.yAggr
            };
            fetchModule = Module(params.minSafeLimitReading.moduleName);
            cardValue = fetchModule.fetch(db);
            enumMap = Reading(targetFieldId, params.minSafeLimitReading.parentId).getEnumMap();
            valueMap = {};
            valueMap["value"] = cardValue;
            if (fieldMapInfo!=null && fieldMapInfo.dataTypeEnum == "BOOLEAN") {
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
                valueMap["unit"] = targetFieldMap.get("unit");
                valueMap["dataType"] = targetFieldMap.get("dataTypeEnum");
            }
            result["minValue"] = valueMap;
        }
    }
    else {
        result["minValue"] = null;
    }
    if (params.maxSafeLimitType != null) {
        maxSafeLimitObj = params.maxSafeLimitReading;
        if (params.maxSafeLimitType == "constant") {
            targetValueMap = {};
            targetValueMap["value"] = params.maxSafeLimitConstant;
            result["maxValue"] = targetValueMap;
        }
        else if(maxSafeLimitObj!=null && maxSafeLimitObj.kpiType == "DYNAMIC"){
             valueMap = {};
             maxDynamicResult = new NameSpace("readingkpi").getResult(kpiType.dynamic,maxSafeLimitObj.fieldId,maxSafeLimitObj.parentId,newStartTime,newEndTime);
             valueMap["actualValue"] = maxDynamicResult.value;
             valueMap["dataType"] = maxDynamicResult.dataType;
             valueMap["unit"] = maxDynamicResult.unit;
             valueMap["value"] = maxDynamicResult.value;
             result["maxValue"] = valueMap;
        }
        else {
            targetFieldObj = new NameSpace("module").getField(params.maxSafeLimitReading.fieldName, params.maxSafeLimitReading.moduleName);
            targetFieldId = targetFieldObj.id();
            targetFieldMap = targetFieldObj.asMap();
            db = {
                criteria: [parentId == (params.maxSafeLimitReading.parentId) && ttime == dateRangeObj],
                field: params.maxSafeLimitReading.fieldName,
                aggregation: params.maxSafeLimitReading.yAggr
            };
            fetchModule = Module(params.maxSafeLimitReading.moduleName);
            cardValue = fetchModule.fetch(db);
            enumMap = Reading(targetFieldId, params.maxSafeLimitReading.parentId).getEnumMap();
            valueMap = {};
            valueMap["value"] = cardValue;
            if (fieldMapInfo!=null && fieldMapInfo.dataTypeEnum == "BOOLEAN") {
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
                valueMap["unit"] = targetFieldMap.get("unit");
                valueMap["dataType"] = targetFieldMap.get("dataTypeEnum");
            }
            result["maxValue"] = valueMap;
        }
    } else {
        result["maxValue"] = null;
    }
    result["title"] = params.title;
    result["period"] = period;
    return result;
}