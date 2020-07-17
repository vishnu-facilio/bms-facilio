Map floorPlanMode(Map params) {
    floorId = params.floorId;
    spaceIds = params.spaceId;
    
    
    assetCategoryId = 11692;
    assetCategoryId2 = 11693;
    readingModule = "spacetemperature";
    readingFieldName = "setpoint";
    readingFieldName2 = "spacetemperature";
    readingFieldName3 = "returnairtemperaturesp";
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
    areas = [];
    for each index, spaceId in spaceIds {

        assetModule = Module("asset");
        db = {
            criteria: [space == spaceId && category == 11692],
            field: "id"
        };
        db1 = {
            criteria: [space == spaceId && category == 11693],
            field: "id"
        };
        db2 = {
            criteria: [space == spaceId && category == 11766],
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
        if (assetIds != null) {
            avgValue1 = 0;
            avgValue2 = 0;
            cavgValue1 = 0;
            for each aidx, assetId in assetIds {
                readingValue = Reading(fieldId, assetId).getLastValue();
                readingValue2 = Reading(fieldId2, assetId).getLastValue();
                enumMap = Reading(fieldId, assetId).getEnumMap();
                enumMap2 = Reading(fieldId2, assetId).getEnumMap();
                avgValue1 = avgValue1 + readingValue;
                avgValue2 = avgValue2 + readingValue2;
            }
            val1 = avgValue1 / assetIds.size(); // setpoint temperature avg value
            val2 = avgValue2 / assetIds.size(); // space temperature avg value
        }
        if (cscassetIds != null) {
            avgValue3 = 0;
            for each idx, cassetId in cscassetIds {
                creadingValue = Reading(fieldId3, cassetId).getLastValue();
                cenumMap = Reading(fieldId3, cassetId).getEnumMap();
                avgValue3 = avgValue3 + creadingValue;
            }
            val3 = avgValue3 / cscassetIds.size();
        }
        val4 = 0;
        enumMap3 = null;

        rreadingValue = null;
        if (rassetIds != null) {
            avgValue4 = 0;
            for each dx, ass in rassetIds {
                rreadingValue = Reading(fieldId4, ass).getLastValue();
                enumMap3 = Reading(fieldId4, ass).getEnumMap();
                avgValue4 = avgValue4 + rreadingValue;
            }
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

        if (assetIds != null) {
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
        if (icons.size() > 0) {
            area.icons = icons;
        }
        areas.add(area);
    }
    result = {};
    result.areas = areas;
    result.layer = "vav-reading";
    return result;
}