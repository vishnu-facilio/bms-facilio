Map cardLayout(Map params) {
    result = {};
    fetchModule = Module(params.moduleName);
    records = [];
    if (params.moduleName == "asset") {
        records = fetchModule.fetch([geoLocation != null && category == params.assetCategory]);
    } else {
        records = fetchModule.fetch([location != null]);
    }
    values = [];
    if (records != null) {
        clearSeverity = {
            criteria: [severity == "Clear"]
        };
        clearSeverityId = Module("alarmseverity").fetch(clearSeverity)[0];
        for each index, record in records {
            locationEntry = {};
            locationEntry["id"] = record.id;
            locationEntry["name"] = record.name;
            locationEntry["moduleName"] = params.moduleName;
            if (record.photoId != null && record.photoId > 0) {
                locationEntry["image"] = "/api/v2/files/preview/" + record.photoId;
            } else {
                locationEntry["image"] = null;
            }
            if (params.moduleName == "asset") {
                locationEntry["location"] = record.geoLocation;
            } else {
                locationObj = Module("location").fetch([id == record.location.id])[0];
                if (locationObj != null && locationObj.lat != null && locationObj.lng != null) {
                    locationEntry["location"] = locationObj.lat + "," + locationObj.lng;
                } else {
                    locationEntry["location"] = null;
                }
            }
            locationEntry["icon"] = params.marker.icon;
            valueMap = {};
            if (params.marker.type == "noOfAlarms") {
                spaceIds = Module("basespace").getSubordinates(record.id);
                occurenceCount = {
                    criteria: [resource == spaceIds && severity != clearSeverityId.id],
                    field: "id",
                    aggregation: "count"
                };
                valueMap["label"] = "No of Active Alarms";
                valueMap["value"] = Module("alarmoccurrence").fetch(occurenceCount);
            } else if (params.marker.type == "noOfWorkorders") {
                spaceIds = Module("basespace").getSubordinates(record.id);
                woCount = {
                    criteria: [resource == spaceIds],
                    field: "id",
                    aggregation: "count"
                };
                valueMap["label"] = "No of Workorders";
                valueMap["value"] = Module("workorder").fetch(woCount);
            } else if (params.moduleName == "asset" && params.marker.type == "reading") {
                date = NameSpace("date");
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
                    criteria: [parentId == (record.id) && ttime == dateRangeObj],
                    field: params.marker.reading.fieldName,
                    aggregation: params.marker.reading.yAggr
                };
                fetchModule = Module(params.marker.reading.moduleName);
                cardValue = fetchModule.fetch(db);
                fieldObj = NameSpace("module").getField(params.marker.reading.fieldName, params.marker.reading.moduleName);
                fieldid = fieldObj.id();
                fieldMapInfo = fieldObj.asMap();
                enumMap = Reading(fieldid, record.id).getEnumMap();
                valueMap["label"] = fieldMapInfo.displayName;
                valueMap["value"] = cardValue;
                valueMap["period"] = period;
                if (enumMap != null) {
                    if (cardValue != null && enumMap.get(cardValue) != null) {
                        valueMap["value"] = enumMap.get(cardValue);
                    }
                }
                if (fieldMapInfo != null) {
                    valueMap["unit"] = fieldMapInfo.get("unit");
                    valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
                }
            }
            locationEntry["value"] = valueMap;
            values.push(locationEntry);
        }
    }
    result["title"] = params.title;
    result["values"] = values;
    return result;
}