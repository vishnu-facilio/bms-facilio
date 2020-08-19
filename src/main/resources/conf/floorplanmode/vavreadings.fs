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

    fieldMapInfo = temperture.asMap();
  	setpointMap = setPointObj.asMap();
   	returnAirTempMap = returnAirTemp.asMap();
  	sensorMapTemp = temperture.asMap();

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
                enumMap = Reading(fieldId, assetId).getEnumMap();
                enumMap2 = Reading(fieldId2, assetId).getEnumMap();
                avgValue1 = avgValue1 + readingValue;
                avgValue2 = avgValue2 + readingValue2;
              valueMap = {};
              if (setpointMap != null) {
                if(assetName != null){
                  valueMap["name"] = assetName[0];
                }
                valueMap["value"] = readingValue;
                valueMap["unit"] = setpointMap.get("unit");
                valueMap["dataType"] = setpointMap.get("dataTypeEnum");
                resultData[assetId] = valueMap;
              }
            }
            resultAssets.add(resultData);
            val1 = avgValue1 / assetIds.size(); // setpoint temperature avg value
            val2 = avgValue2 / assetIds.size(); // space temperature avg value
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
        enumMap3 = null;

        rreadingValue = null;
        if (rassetIds != null) {
          resultData = {};
            avgValue4 = 0;
            for each dx, ass in rassetIds {
              	assetNameDb = {
            		criteria: [id == ass],
                  	field: "name",
        		};
              	assetName = assetModule.fetch(assetNameDb);
                rreadingValue = Reading(fieldId4, ass).getLastValue();
                enumMap3 = Reading(fieldId4, ass).getEnumMap();
                avgValue4 = avgValue4 + rreadingValue;
                       valueMap = {};
                if (sensorMapTemp != null) {
                   if(assetName != null){
                  valueMap["name"] = assetName[0];
                }
                valueMap["value"] = rreadingValue;
                valueMap["unit"] = sensorMapTemp.get("unit");
                valueMap["dataType"] = sensorMapTemp.get("dataTypeEnum");
                resultData[ass] = valueMap;
              }
            }
             resultAssets.add(resultData);
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
        areas.add(area);
    }
    result = {};
    result.areas = areas;
    result.layer = "vav-reading";
    return result;
}