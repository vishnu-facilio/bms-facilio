package com.facilio.v3.context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ConfigParams implements Serializable {
    private String selectableFieldNames;

    public static void addConfigParams(FacilioContext context, ConfigParams configParams) throws Exception {
         if (configParams == null){
             return;
         }
         context.put(FacilioConstants.ContextNames.SELECTABLE_FIELD_NAMES,configParams.getSelectableFieldNames());
    }

}
