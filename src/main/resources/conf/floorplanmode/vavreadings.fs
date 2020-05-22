Map floorPlanMode(Map params) {
 floorId = params.floorId;
spaceIds = params.spaceId;
 assetCategoryId = 11692;
 readingModule = "spacetemperature";
 readingFieldName = "setpoint";
 readingFieldName2 = "spacetemperature";
  
  setPointObj = NameSpace("module").getField(readingFieldName, readingModule);
  fieldId = setPointObj.id();
  
  spaceTemp = NameSpace("module").getField(readingFieldName2, readingModule);
  fieldId2 = spaceTemp.id();
  
  areas = [];
  for each index, spaceId in spaceIds {
    assetModule = Module("asset");    
    db = {
      criteria: [space == spaceId && category == assetCategoryId],
      field: "id"
    };
  	assetIds = assetModule.fetch(db);
        
    readingValue = null;
    readingValue2 = null;
    enumMap = null;
    enumMap2 = null;
    val1 = 0;
    val2 = 0;
    if (assetIds != null) {
      avgValue1 = 0;
      avgValue2 = 0;
      for each aidx, assetId in assetIds {
        readingValue = Reading(fieldId, assetId).getLastValue();
        readingValue2 = Reading(fieldId2, assetId).getLastValue();
        enumMap = Reading(fieldId, assetId).getEnumMap();
        enumMap2 = Reading(fieldId2, assetId).getEnumMap();
        avgValue1 = avgValue1 + readingValue;
        avgValue2 = avgValue2 + readingValue2;
      }
      val1 = avgValue1 / assetIds.size();
      val2 = avgValue2 / assetIds.size();
    }
    unitData = "";
    comp1 = val2 - val1;
     area = {};

    if (assetIds != null) {
      styles = {};
      styles.fill = "#b0b0b0";
      if (comp1 > -0.5 && comp1 < 0.5) {
      styles["fill"] = "#48ba61"; // green zone
    }
    else if (comp1 > 0.6 && comp1 < 2.9) {
      styles["fill"] = "#ff9900"; // orange zone
    }
    else if (comp1 > 3) {
      styles["fill"] = "#ff0000"; // red zone
    }   
    else if (comp1 < -0.6) {
      styles["fill"] = "#0000ff"; // Blue zone
    }  
      area.styles = styles;
    }
    area.spaceId = spaceId;
    areas.add(area);
  }
  result = {};
  result.areas = areas;
  result.layer = "vav-reading";
  return result;
}