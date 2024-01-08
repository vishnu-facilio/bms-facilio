package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

public class BureauCloseIssueReasonOptionListModule extends SignUpData {

    public static final String MODULE_NAME = "bureauCloseIssueReasonOption";

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Bureau Close Issue Reason Option");
        module.setDescription("Bureau Close Issue Reason Option");
        module.setTableName("Bureau_Close_Issue_Reason_Option_List");
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
        nameField.setMainField(true);
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


        MultiLookupField bureauCloseIssues = new MultiLookupField();
        bureauCloseIssues.setDefault(true);
        bureauCloseIssues.setName("bureauCloseIssues");
        bureauCloseIssues.setDisplayName("Bureau Close Issues");
        bureauCloseIssues.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        bureauCloseIssues.setDataType(FieldType.MULTI_LOOKUP);
        bureauCloseIssues.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        bureauCloseIssues.setLookupModule(mod);
        modBean.addField(bureauCloseIssues);
    }
}
