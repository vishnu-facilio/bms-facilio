Map cardLayout(Map params) {
    result = {};

    moduleName = params.moduleName;
    assetCategoryId = params.assetCategoryId;
    buildingId = params.buildingId;
  	filterCriteria = params.filterCriteria;
    columns = params.columns;
    excludeEmptyReadings = true;
    if (params.excludeEmptyReadings != null) {
        excludeEmptyReadings = params.excludeEmptyReadings;
    }
    dateRange = "Current Month";
    if (params.dateRange != null) {
        dateRange = params.dateRange;
    }
  	sorting = params.sorting;
  	if (sorting == null) {
      sorting = {};
      sorting["fieldName"] = "name";
	  sorting["order"] = "asc";
    }
  	perPage = params.perPage;
  	if (perPage == null) {
      perPage = 100;
    }

    date = NameSpace("date");
    month = date.getDateRange(dateRange);

  	criteriaObj = null;
  	assetCategoryCriteria = [category == assetCategoryId];
  	if (buildingId != null && buildingId > 0) {
      bs = NameSpace("resource").getBaseSpace(buildingId);
      assetCategoryCriteria = [category == assetCategoryId && space == bs];
    }
  	if (filterCriteria == null) {
      criteriaObj = assetCategoryCriteria;
    }
	else {
      criteriaObj = NameSpace("criteria").get(filterCriteria);
      criteriaObj.and(assetCategoryCriteria);
    }
  
  	assetDb = null;
  	if (sorting.order == "asc") {
      assetDb = {
          criteria : criteriaObj,
          orderBy : sorting.fieldName asc,
          limit : perPage
      };
    }
  	else {
      assetDb = {
          criteria : criteriaObj,
          orderBy : sorting.fieldName desc,
          limit : perPage
      };
    }
    assetModule = Module(moduleName);
    assetList = assetModule.fetch(assetDb);

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
                    if (fieldMapInfo.mainField != null) {
                        linkTo = {};
                        linkTo.id = asset.id;
                        linkTo.module = moduleName;
                        linkTo.linkType = "SUMMARY";
                        cell["linkTo"] = linkTo;
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

                    readingObj = {};
                    readingObj["fieldId"] = fieldObj.id();
                    readingObj["parentId"] = asset.id;
                    readingObj["moduleName"] = column.moduleName;
                    readings = [];
                    readings.add(readingObj);
                    
                    linkTo = {};
                    linkTo.readings = readings;
                    linkTo.linkType = "ANALYTICS";
                    cell["linkTo"] = linkTo;
                }
                
                if (fieldMapInfo != null) {
                    cell["unit"] = fieldMapInfo.get("unit");
                    cell["dataType"] = fieldMapInfo.get("dataTypeEnum");
                }
                cell["parentId"] = asset.id;
                row.add(cell);
            }
            if (excludeEmptyReadings != null) {
                if (excludeEmptyReadings == true && readingColumnCount > 0 && readingColumnCount != nullReadingCount) {
                    rows.add(row);
                }
                else if (excludeEmptyReadings == false || readingColumnCount == 0) {
                    rows.add(row);
                }
            }
            else {
                rows.add(row);
            }
        }
    }
    result["title"] = params.title;
    result["period"] = dateRange;
    result["columns"] = columns;
    result["rows"] = rows;
    return result;
}