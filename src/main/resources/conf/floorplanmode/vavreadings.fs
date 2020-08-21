Map floorPlanMode(Map params) {
    floorId = params.floorId;
    spaceIds = params.spaceId;
    
    vavcategoryid = 11692;
    csucatid = 11693;
  	sensorCatid = 11766;
  	
    readingModule = "spacetemperature";
    readingFieldName = "setpoint";
    readingFieldName2 = "spacetemperature";
    readingFieldName3 = "returnairtemperature";
    readingFieldName4 = "temperture";
    readingFieldName5 = "temperture";
    readigModule3 = "runcommand";
  	humidityFieldName = "humidity";
	  occupancyName = "occupancystatus";
    readigModule4 = "temperture";
    readigModule5 = "temperture";
  
    setPointObj = new NameSpace("module").getField(readingFieldName, readingModule);
    fieldId = setPointObj.id();

    spaceTemp = new NameSpace("module").getField(readingFieldName2, readingModule);
    fieldId2 = spaceTemp.id();

    returnAirTemp = new NameSpace("module").getField(readingFieldName3, readigModule3);
    fieldId3 = returnAirTemp.id();

    temperture = new NameSpace("module").getField(readingFieldName4, readigModule4);
    fieldId4 = temperture.id();
  
  	humidity = new NameSpace("module").getField(humidityFieldName, readigModule4);
  	humidityFieldId = humidity.id();
  
  	occupancy = new NameSpace("module").getField(occupancyName, occupancyName);
  	occupancyFieldId = occupancy.id();

    fieldMapInfo = temperture.asMap();
  	setpointMap = setPointObj.asMap();
   	returnAirTempMap = returnAirTemp.asMap();
  	sensorMapTemp = temperture.asMap();
  	sensorHumidityMap = humidity.asMap();
  	occupancyMap = occupancy.asMap();

    areas = [];
    for each index, spaceId in spaceIds {

        assetModule = Module("asset");
        db = {
            criteria: [space == spaceId && category == vavcategoryid],
            field: "id"
        };
        db1 = {
            criteria: [space == spaceId && category == csucatid],
            field: "id"
        };
        db2 = {
            criteria: [space == spaceId && category == sensorCatid],
            field: "id"
        };
      
        assetIds = assetModule.fetch(db);
        cscassetIds = assetModule.fetch(db1);
        rassetIds = assetModule.fetch(db2);
        readingValue = null;
        readingValue2 = null;
        enumMap = null;
        enumMap2 = null;
        val1 = 0;
        val2 = 0;
        val3 = 0;
      	humidityCount = 0;
      	occupancyVal = false;
      	resultAssets = [];
      	resultData = {};
        if (assetIds != null) {
            avgValue1 = 0;
            avgValue2 = 0;
            cavgValue1 = 0;
            for each aidx, assetId in assetIds {
              	assetNameDb = {
            		criteria: [id == assetId],
                  	field: "name",
        		};
              	assetName = assetModule.fetch(assetNameDb);
                readingValue = Reading(fieldId, assetId).getLastValue();
                readingValue2 = Reading(fieldId2, assetId).getLastValue();
              	occupancyValue = Reading(occupancyFieldId, assetId).getLastValue();
              	if(occupancyValue != false){
                  occupancyVal = true;
                }
                enumMap = Reading(fieldId, assetId).getEnumMap();
                enumMap2 = Reading(fieldId2, assetId).getEnumMap();
                avgValue1 = avgValue1 + readingValue;
                avgValue2 = avgValue2 + readingValue2;
              valueMap = {};
              if (setpointMap != null) {
                if(assetName != null){
                  valueMap["name"] = assetName[0];
                }
                valueMap["categoryId"] = vavcategoryid;
                valueMap["value"] = readingValue;
                valueMap["unit"] = setpointMap.get("unit");
                valueMap["dataType"] = setpointMap.get("dataTypeEnum");
                resultData[assetId] = valueMap;
              }
            }
            resultAssets.add(resultData);
            val1 = avgValue1 / assetIds.size(); // setpoint temperature avg value
            val2 = avgValue2 / assetIds.size(); // space temperature avg value
          	if(occupancyVal != false){
              valueMap={};
              valueMap["value"] = occupancyVal;
              valueMap["label"] = occupancyMap.get("displayName");
              if(sensorHumidityMap != null){
                valueMap["unit"] = occupancyMap.get("unit");
                valueMap["dataType"] = occupancyMap.get("dataTypeEnum");
              }
              markerReadings["OCCUPANCY"] = valueMap;
            }
        }
        if (cscassetIds != null) {
            avgValue3 = 0;
            for each idx, cassetId in cscassetIds {
              	assetNameDb = {
            		criteria: [id == cassetId],
                  	field: "name",
        		};
              	assetName = assetModule.fetch(assetNameDb);
                resultData = {};
                creadingValue = Reading(fieldId3, cassetId).getLastValue();
                cenumMap = Reading(fieldId3, cassetId).getEnumMap();
                avgValue3 = avgValue3 + creadingValue;
                 valueMap = {};
                 if (returnAirTempMap != null) {
                   if(assetName != null){
                  valueMap["name"] = assetName[0];
                }
                valueMap["categoryId"] = csucatid;
                valueMap["value"] = creadingValue;
                valueMap["unit"] = returnAirTempMap.get("unit");
                valueMap["dataType"] = returnAirTempMap.get("dataTypeEnum");
                resultData[cassetId] = valueMap;
              }
            }
            resultAssets.add(resultData);
            val3 = avgValue3 / cscassetIds.size();
        }
        val4 = 0;
      	humidityVal=0;
        enumMap3 = null;

        rreadingValue = null;
        if (rassetIds != null) {
          resultData = {};
            avgValue4 = 0;
          	avgHumidity = 0;
            for each dx, ass in rassetIds {
              	assetNameDb = {
            		criteria: [id == ass],
                  	field: "name",
        		};
              	assetName = assetModule.fetch(assetNameDb);
                rreadingValue = Reading(fieldId4, ass).getLastValue();
                enumMap3 = Reading(fieldId4, ass).getEnumMap();
              	humidityVal = Reading(humidityFieldId,ass).getLastValue();
              	if(humidityVal != null && hunmidityVal > 0){
                  avgHumidity = avgHumidity + humidityVal;
                  humidityCount = humidityCount + 1;
                }
                avgValue4 = avgValue4 + rreadingValue;
                       valueMap = {};
                if (sensorMapTemp != null) {
                   if(assetName != null){
                  valueMap["name"] = assetName[0];
                }
                valueMap["categoryId"] = sensorCatid;
                valueMap["value"] = rreadingValue;
                valueMap["unit"] = sensorMapTemp.get("unit");
                valueMap["dataType"] = sensorMapTemp.get("dataTypeEnum");
                resultData[ass] = valueMap;
              }
            }
             resultAssets.add(resultData);
          	if(humidityCount > 0){
          	humidityVal = avgHumidity / humidityCount;
            }
          	humidityVal = math().ceil(humidityVal);
            val4 = avgValue4 / rassetIds.size();
            val4 = math().ceil(val4);
        }
        unitData = "";
        comp1 = val2;
        if (assetIds != null && cscassetIds != null && cscassetIds.size() > 0 && assetIds.size() > 0) {
            comp1 = (val2 + val3) / 2;
        } else if (assetIds != null && cscassetIds == null && assetIds.size() > 0) {
            comp1 = val2;
        }
        if (assetIds == null && cscassetIds != null && cscassetIds.size() > 0) {
            comp1 = val3;
        }

        icons = [];
        area = {};
      	markerReadings = {};
        icon = {};
        if (rassetIds != null) {
            valueMap = {};
            valueMap["value"] = val4;
            valueMap["label"] = fieldMapInfo.get("displayName");
            if (fieldMapInfo != null) {
                valueMap["unit"] = fieldMapInfo.get("unit");
                valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
            }
            icon["type"] = "READINGS";
            icon["position"] = "center";
            icon["value"] = valueMap;
            unitData = "";
            if (fieldMapInfo.get("unit") != null) {
                unitData = fieldMapInfo.get("unit");
            }
            icons.add(icon);
          	if(humidityVal > 0){
              valueMap={};
              valueMap["value"] = humidityVal;
              valueMap["label"] = sensorHumidityMap.get("displayName");
              if(sensorHumidityMap != null){
                valueMap["unit"] = sensorHumidityMap.get("unit");
                valueMap["dataType"] = sensorHumidityMap.get("dataTypeEnum");
              }
              markerReadings["HUMIDITY"] = valueMap;
            }
          	if(val4  > 0){
              valueMap={};
              valueMap["value"] = val4;
              valueMap["label"] = fieldMapInfo.get("displayName");
              if(sensorHumidityMap != null){
                valueMap["unit"] = fieldMapInfo.get("unit");
                valueMap["dataType"] = fieldMapInfo.get("dataTypeEnum");
              }
              markerReadings["TEMPERATURE"] = valueMap;
            }
        }


        styles = {};
        condition = true;
    if (assetIds == null && cscassetIds == null) {
        condition = false;
       }

        if (condition) {
            styles.fill = "#b0b0b0";
            if (comp1 > -1 && comp1 < 22.1) {
                styles["fill"] = "#0000ff"; // blue zone
            } else if (comp1 > 22.1 && comp1 < 24.1) {
                styles["fill"] = "#48ba61"; // green zone
            } else if (comp1 > 24) {
                styles["fill"] = "#ff0000"; // red zone
            }
            area.styles = styles;
        }
        area.spaceId = spaceId;
      	area.assets = resultAssets;
      	area.markerReadings = markerReadings;
        areas.add(area);
    }
    result = {};
    result.areas = areas;
    result.layer = "vav-reading";
    return result;
}