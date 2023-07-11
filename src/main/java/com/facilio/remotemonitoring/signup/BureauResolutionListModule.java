
package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class BureauResolutionListModule extends SignUpData {

    public static final String MODULE_NAME = "bureauResolutionList";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Bureau Resolution List");
        module.setDescription("Bureau Resolution List");
        module.setTableName("Bureau_Resolution_List");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        StringField nameField = new StringField();
        nameField.setName("name");
        nameField.setDisplayName("Name");
        nameField.setModule(mod);
        nameField.setColumnName("NAME");
        nameField.setDataType(FieldType.STRING);
        nameField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        nameField.setDefault(true);
        modBean.addField(nameField);

        NumberField bureauEvalId = new NumberField();
        bureauEvalId.setName("bureauEvaluationId");
        bureauEvalId.setDisplayName("Bureau Evaluation ID");
        bureauEvalId.setColumnName("BUREAU_EVALUATION_ID");
        bureauEvalId.setModule(mod);
        bureauEvalId.setDataType(FieldType.NUMBER);
        bureauEvalId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        bureauEvalId.setDefault(true);
        modBean.addField(bureauEvalId);
    }
}
