package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.forms.FacilioForm;

public interface FormInterface {
    FacilioForm getForm();
    void setForm(FacilioForm facilioForm);

    long getFormId();

    void setFormId(long formId);

    long getId();

    long getModuleId() throws Exception;
}
