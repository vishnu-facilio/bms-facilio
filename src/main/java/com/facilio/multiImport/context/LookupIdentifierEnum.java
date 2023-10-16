package com.facilio.multiImport.context;

import com.facilio.modules.FacilioIntEnum;

import java.util.HashMap;
import java.util.Map;

public enum LookupIdentifierEnum implements FacilioIntEnum {
    PRIMARY_FIELD,
    ID;
    private static final Map<Integer,LookupIdentifierEnum>  IDENTIFIER_ENUM_MAP = initMap();
    private static Map<Integer,LookupIdentifierEnum> initMap(){
        Map<Integer,LookupIdentifierEnum> map = new HashMap<>();
        for(LookupIdentifierEnum value:LookupIdentifierEnum.values()){
            map.put(value.getIndex(),value);
        }
        return map;
    }
    public static LookupIdentifierEnum getLookupIdentifier(int lookupIdentifier){
        return IDENTIFIER_ENUM_MAP.get(lookupIdentifier);
    }
}
