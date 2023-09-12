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
    private boolean onlyRestrictedWorkflows;
    private Boolean isSubFormRecord = false;

    public static void addConfigParams(FacilioContext context, ConfigParams configParams) throws Exception {
         if (configParams == null){
             return;
         }
         context.put(FacilioConstants.ContextNames.SELECTABLE_FIELD_NAMES,configParams.getSelectableFieldNames());
         context.put(FacilioConstants.ContextNames.IS_SUB_FORM_RECORD,configParams.getIsSubFormRecord());

    }

}
