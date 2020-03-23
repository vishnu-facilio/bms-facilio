Map cardLayout(Map params) {
    result = {};

    moduleName = params.moduleName;
    assetCategoryId = params.assetCategoryId;
    columns = params.columns;
    excludeEmptyReadings = true;
    if (params.excludeEmptyReadings != null) {
        excludeEmptyReadings = params.excludeEmptyReadings;
    }
    dateRange = "Current Month";
    if (params.dateRange != null) {
        dateRange = params.dateRange;
    }

    date = NameSpace("date");
    month = date.getDateRange(dateRange);

    assetModule = Module(moduleName);
    assetList = assetModule.fetch([category == assetCategoryId]);

    rows = [];
    if (assetList != null) {
        for each index, asset in assetList {
            row = [];
            nullReadingCount = 0;
            readingColumnCount = 0;
            for each ci, column in columns {
                cell = {};
                fieldObj = null;
                fieldMapInfo = null;
                if (column.type == "field") {
                    fieldObj = NameSpace("module").getField(column.fieldName, moduleName);
                    fieldMapInfo = fieldObj.asMap();
                    
                    fieldValue = asset.get(column.fieldName);
                    if (fieldMapInfo.get("dataTypeEnum") == "LOOKUP" && fieldValue != null && fieldValue.id != null) {
                        cell["id"] = fieldValue.id;
                        cell["module"] = fieldMapInfo.lookupModule.name;
                        lookupModule = Module(fieldMapInfo.lookupModule.name);
                        lookupEntryList = lookupModule.fetch([id == fieldValue.id]);
                        if (lookupEntryList != null) {
                            lookupEntry = lookupEntryList[0];
                            if (lookupEntry != null && lookupEntry.displayName != null) {
                                cell["value"] = lookupEntry.displayName;
                            }
                            else if (lookupEntry != null && lookupEntry.name != null) {
                                cell["value"] = lookupEntry.name;
                            }
                            else {
                                cell["value"] = "---";
                            }
                        }
                        else {
                            cell["value"] = "---";
                        }
                    }
                    else {
                        cell["value"] = fieldValue;
                    }
                }
                else if (column.type == "reading") {
                    readingColumnCount = readingColumnCount + 1;
                    fieldObj = NameSpace("module").getField(column.fieldName, column.moduleName);
                    fieldMapInfo = fieldObj.asMap();
                    db = {
                        criteria : [parentId == asset.id && ttime == month],
                        field : column.fieldName,
                        aggregation : column.yAggr
                    };
                    
                    fetchModule = Module(column.moduleName);
                    readingValue = fetchModule.fetch(db);
                    if (readingValue == null) {
                        nullReadingCount = nullReadingCount + 1;
                    }
                    enumMap = Reading(fieldObj.id(), asset.id).getEnumMap();
                    
                    cell = {};
                    cell["value"] = readingValue;
                    if (enumMap != null) {
                        if (readingValue != null && enumMap.get(readingValue) != null) {
                            cell["value"] = enumMap.get(readingValue);
                        }
                    }
                }
                
                if (fieldMapInfo != null) {
                    cell["unit"] = fieldMapInfo.get("unit");
                    cell["dataType"] = fieldMapInfo.get("dataTypeEnum");
                }
                row.add(cell);
            }
            if (excludeEmptyReadings != null && excludeEmptyReadings) {
                if (readingColumnCount > 0 && readingColumnCount != nullReadingCount) {
                    rows.add(row);
                }
            }
            else {
                rows.add(row);
            }
        }
    }
    result["columns"] = columns;
    result["rows"] = rows;
    return result;
}