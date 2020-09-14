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
    period =null;
    dateRangeObj=null;

    
  	date = new NameSpace("date");

    if(params.cardFilters!=null)
    {
      cardFilters=params.cardFilters;
      
      startTime=cardFilters.startTime;
      endTime=cardFilters.endTime;
      period=cardFilters.dateLabel;
      dateRangeObj= new NameSpace("dateRange").create(startTime,endTime);
      
    }
    else if(params.dateRange != null) 
    {

        period = params.dateRange;
        dateRangeObj=date.getDateRange(period);
       

    }
    else 
    {
      period = "Current Month";
      dateRangeObj=date.getDateRange(period);
      
    }

  	sorting = params.sorting;
  	if (sorting == null) {
      sorting = {};
      sorting["fieldName"] = "name";
	  sorting["order"] = "asc";
    }
  	page = params.page;
    if (page == null) {
        page = 1;
    }
    perPage = params.perPage;
  	if (perPage == null) {
      perPage = 100;
    }

    offset = ((page-1) * perPage);
    if (offset < 0) {
        offset = 0;
    }
    offset = new NameSpace("number").intValue(offset+"");

  	criteriaObj = null;
  	assetCategoryCriteria = [category == assetCategoryId];
  	if (buildingId != null && buildingId > 0) {
      bs = new NameSpace("resource").getBaseSpace(buildingId);
      assetCategoryCriteria = [category == assetCategoryId && space == bs];
    }
  	if (filterCriteria == null) {
      criteriaObj = assetCategoryCriteria;
    }
	else {
      criteriaObj = new NameSpace("criteria").get(filterCriteria);
      criteriaObj.and(assetCategoryCriteria);
    }
  
  	assetDb = {};
  	assetDb.put("criteria", criteriaObj);
    if (sorting.fieldName != null) {
        assetDb.put("sortByFieldName", sorting.fieldName);
    }
    if (sorting.order != null) {
        assetDb.put("sortOrder", sorting.order);
    }
    assetDb.put("offset", offset);
    assetDb.put("limit", perPage);

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
                    fieldObj = new NameSpace("module").getField(column.fieldName, moduleName);
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
                    fieldObj = new NameSpace("module").getField(column.fieldName, column.moduleName);
                    fieldMapInfo = fieldObj.asMap();
                    db = {
                        criteria : [parentId == asset.id && ttime == dateRangeObj],
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
                                    enumValue = readingValue;

                                 if (readingValue == true) {
                        enumValue = 1;
                        }
                        else if (readingValue == false) {
                        enumValue = 0;
                        }
                        if (enumValue != null && enumMap.get(enumValue) != null) {
                            cell["value"] = enumMap.get(enumValue);
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