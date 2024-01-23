package com.facilio.modules;

import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class SupplementsExtraMetaData implements Serializable {
    private Map<BaseLookupField, List<FacilioField>> lookupFieldVsSelectableFieldsMap;
    private Map<BaseLookupField,Map<BaseLookupField, List<FacilioField>>> lookupFieldVsOneLevelSelectableFieldsMap;
    public Map<BaseLookupField, List<FacilioField>> getOneLevelSelectableFieldsMap(BaseLookupField baseLookupField){
        if(MapUtils.isNotEmpty(lookupFieldVsOneLevelSelectableFieldsMap)){
            return lookupFieldVsOneLevelSelectableFieldsMap.get(baseLookupField);
        }
        return null;
    }
    public List<FacilioField> getSelectableFields(BaseLookupField lookupField){
        if(MapUtils.isNotEmpty(lookupFieldVsSelectableFieldsMap)){
            return lookupFieldVsSelectableFieldsMap.get(lookupField);
        }
        return null;
    }
}
