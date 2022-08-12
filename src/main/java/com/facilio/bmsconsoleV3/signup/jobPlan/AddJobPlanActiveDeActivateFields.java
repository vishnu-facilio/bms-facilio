package com.facilio.bmsconsoleV3.signup.jobPlan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;

public class AddJobPlanActiveDeActivateFields extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlan = modBean.getModule("jobplan");

        addJobPlanActiveDeActivateFields(jobPlan, modBean);
    }

    private void addJobPlanActiveDeActivateFields(FacilioModule jobPlan, ModuleBean modBean) throws Exception {
        BooleanField isActiveField = (BooleanField) FieldFactory.getBooleanField("isActive", "IS_ACTIVE", jobPlan);
        isActiveField.setDefault(true);
        isActiveField.setMainField(false);
        isActiveField.setRequired(true);
        modBean.addField(isActiveField);

        BooleanField isDisabledField = (BooleanField) FieldFactory.getBooleanField("isDisabled", "IS_DISABLED", jobPlan);
        isDisabledField.setDefault(true);
        isDisabledField.setMainField(false);
        isDisabledField.setRequired(true);
        modBean.addField(isDisabledField);
    }
}
