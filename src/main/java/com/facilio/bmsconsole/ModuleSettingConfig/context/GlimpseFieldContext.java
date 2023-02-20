package com.facilio.bmsconsole.ModuleSettingConfig.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlimpseFieldContext {

    private long id;
    private long orgId;
    private long glimpseId;
    private long fieldId;
    private long lookupFieldId;
    private String fieldName;
    private FacilioField facilioField;
    private FacilioField lookupField;
    private long sequenceNumber;

}
