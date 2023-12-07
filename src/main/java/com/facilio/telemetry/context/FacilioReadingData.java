package com.facilio.telemetry.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilioReadingData implements Serializable {

    Long orgId;

    long resourceId;

    long fieldId;

    Object value;

    long ttime; //millisecond

    ResourceContext resource;

    FacilioField field;

    @JsonIgnore
    public FacilioField getField() throws Exception {
        if (field != null) {
            return field;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
        FacilioField f = modBean.getField(fieldId);
        if (f != null) {
            setField(f);
        }
        return field;
    }

    public double doubleValue() {
        String v = String.valueOf(getValue());
        double tv;
        if (v.equals("true") || v.equals("True")) {
            tv = 1;
        } else if (v.equals("false") || v.equals("False")) {
            tv = 0;
        } else {
            tv = Double.valueOf(v);
        }
        return tv;
    }
}

