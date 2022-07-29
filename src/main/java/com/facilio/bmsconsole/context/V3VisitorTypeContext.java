package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class V3VisitorTypeContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private String name;
    private Boolean enabled;
    private FacilioForm form;

    public Boolean getFormEnabled() {
        return formEnabled;
    }

    public void setFormEnabled(Boolean formEnabled) {
        this.formEnabled = formEnabled;
    }

    private Boolean formEnabled = false;

    public FacilioForm getForm() {
        return form;
    }

    public void setForm(FacilioForm form) {
        this.form = form;
    }


    public Long getVisitorFormId() {
        return visitorFormId;
    }

    public void setVisitorFormId(Long visitorFormId) {
        this.visitorFormId = visitorFormId;
    }

    private Long visitorFormId;



    private V3VisitorTypeContext.VisitorLogo visitorLogo;
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum VisitorLogo implements FacilioIntEnum {
        GUEST("kiosk-guest"),
        VENDOR("kiosk-vendor"),
        EMPLOYEE("kiosk-employee"),
        CUSTOM("kiosk-custom"),
        DELIVERY("kiosk-delivery"),
        EMPLOYEE_2("employee"),
        ELECTRICIAN("electrician"),
        STUDENT("student"),
        DOCTOR("doc"),
        MESSAGE("kiosk-message")
        ;

        private String name;
        VisitorLogo (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static V3VisitorTypeContext.VisitorLogo valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }


    public V3VisitorTypeContext.VisitorLogo getVisitorLogoEnum() {
        return visitorLogo;
    }

    public int getVisitorLogo() {
        if(visitorLogo!=null)
        {
            return visitorLogo.getIndex();
        }
        return -1;
    }
    @JsonIgnore
    public Map<String, Object> getVisitorLogoObj() throws Exception {
        return FieldUtil.getAsProperties(visitorLogo);
    }


    public void setVisitorLogo(int visitorLogo) {
        this.visitorLogo = V3VisitorTypeContext.VisitorLogo.valueOf(visitorLogo);
    }
    public void setVisitorLog(V3VisitorTypeContext.VisitorLogo visitorLogo) {
        this.visitorLogo = visitorLogo;
    }


    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public VisitorTypeFormsContext getVisitorTypeForm() {
        return visitorTypeForm;
    }

    public void setVisitorTypeForm(VisitorTypeFormsContext visitorTypeForm) {
        this.visitorTypeForm = visitorTypeForm;
    }

    VisitorTypeFormsContext visitorTypeForm;


}
