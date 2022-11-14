package com.facilio.bmsconsoleV3.context.autocadfileimport;


import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

@Getter
@Setter
public class AutoCadImportLayerContext{
    private static final long serialVersionUID = 1L;
    Long orgId;
    Long Id;
    Long importId;
    String name;
    String geojsonString;
    JSONObject geojson;
    Long createdTime;
    Long createdBy;




}
