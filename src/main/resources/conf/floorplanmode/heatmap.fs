Map floorPlanMode(Map params) {
  floorId = params.floorId;
  spaceIds = params.spaceId;
  assetCategoryId = params.viewParams.assetCategoryId;
  readingModule = params.viewParams.readingModule;
  readingFieldName = params.viewParams.readingFieldName;
  
  fieldObj = NameSpace("module").getField(readingFieldName, readingModule);
  fieldId = fieldObj.id();
  fieldMapInfo = fieldObj.asMap();
  
  areas = [];
  for each index, spaceId in spaceIds {
    assetModule = Module("asset");
    
    db = {
      criteria: [space == spaceId && category == assetCategoryId],
      field: "id"
    };
  	assetIds = assetModule.fetch(db);
    
    readingValue = null;
    enumMap = null;
    if (assetIds != null) {
      for each aidx, assetId in assetIds {
        readingValue = Reading(fieldId, assetId).getLastValue();
        enumMap = Reading(fieldId, assetId).getEnumMap();
      }
    }
    
    valueMap = {};
    valueMap["value"] = readingValue;
    valueMap["label"] = fieldMapInfo.get("displayName");
    
    if (enumMap != null) {
      if (readingValue != null && enumMap.get(readingValue) != null) {
        valueMap["value"] = enumMap.get(readingValue);
      }
    }
    if (fieldMapInfo != null) {
      valueMap["unit"] = fieldMapInfo.get("unit");
      valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
    }
    
    area = {};
    area.spaceId = spaceId;
    area.value = valueMap;
    areas.add(area);
  }
  
  result = {};
  result.areas = areas;
  result.layer = "heatmap";
  return result;
}