package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.forms.FacilioForm;

public interface FormInterface {
    FacilioForm getForm();

    long getFormId();

    void setFormId(long formId);

    long getId();

    long getModuleId() throws Exception;
}
