package com.facilio.bmsconsoleV3.signup.jobPlan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

public class AddJobPlanTaskSectionFields extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanTaskSectionModule = modBean.getModule("jobplansection");

        addJobTaskSectionFields(jobPlanTaskSectionModule, modBean);
    }

    private void addJobTaskSectionFields(FacilioModule jobPlanTaskSectionModule, ModuleBean moduleBean) throws Exception {
        // adding INPUT_TYPE field
        FacilioField inputTypeField = FieldFactory.getField("inputType", "Input Type", "INPUT_TYPE", jobPlanTaskSectionModule, FieldType.NUMBER);
        inputTypeField.setDefault(true);
        inputTypeField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        inputTypeField.setRequired(false);
        inputTypeField.setDefault(false);
        inputTypeField.setDisabled(false);
        moduleBean.addField(inputTypeField);

        // adding ADDITIONAL_INFO field
        FacilioField additionalInfoField = FieldFactory.getField("additionalInfoJsonStr", "Additional Info", "ADDITIONAL_INFO", jobPlanTaskSectionModule, FieldType.STRING);
        additionalInfoField.setDefault(true);
        additionalInfoField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        additionalInfoField.setRequired(false);
        additionalInfoField.setDefault(false);
        additionalInfoField.setDisabled(false);
        moduleBean.addField(additionalInfoField);
    }
}
