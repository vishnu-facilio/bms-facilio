package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.Arrays;

public class BureauInhibitReasonListModule  extends SignUpData {

    public static final String MODULE_NAME = "bureauInhibitReasonList";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Bureau Inhibit Reason List");
        module.setDescription("Bureau Inhibit Reason List");
        module.setTableName("Bureau_Inhibit_Reason_List");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.SUB_ENTITY);
        module.setTrashEnabled(false);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField flaggedEventBureauEvaluation = new LookupField();
        flaggedEventBureauEvaluation.setName("flaggedEventBureauEvaluation");
        flaggedEventBureauEvaluation.setModule(mod);
        flaggedEventBureauEvaluation.setDisplayName("Flagged Event Bureau Evaluation");
        flaggedEventBureauEvaluation.setColumnName("FLAGGED_EVENT_BUREAU_EVALUATION");
        flaggedEventBureauEvaluation.setDataType(FieldType.LOOKUP);
        flaggedEventBureauEvaluation.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        flaggedEventBureauEvaluation.setLookupModule(modBean.getModule(FlaggedEventBureauEvaluationModule.MODULE_NAME));
        flaggedEventBureauEvaluation.setDefault(true);
        modBean.addField(flaggedEventBureauEvaluation);

        StringField inhibitReason = new StringField();
        inhibitReason.setName("inhibitReason");
        inhibitReason.setDisplayName("Inhibit Reason");
        inhibitReason.setModule(mod);
        inhibitReason.setColumnName("INHIBIT_REASON");
        inhibitReason.setDataType(FieldType.STRING);
        inhibitReason.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        inhibitReason.setDefault(true);
        modBean.addField(inhibitReason);

        NumberField maxInhibitTime = new NumberField();
        maxInhibitTime.setModule(mod);
        maxInhibitTime.setName("maxInhibitTime");
        maxInhibitTime.setDisplayName("Max Inhibit Time");
        maxInhibitTime.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        maxInhibitTime.setDataType(FieldType.NUMBER);
        maxInhibitTime.setDefault(true);
        maxInhibitTime.setColumnName("MAX_INHIBIT_TIME");
        modBean.addField(maxInhibitTime);

        BooleanField hideInBureau = new BooleanField();
        hideInBureau.setDisplayName("Hide In Bureau");
        hideInBureau.setName("hideInBureau");
        hideInBureau.setColumnName("HIDE_IN_BUREAU");
        hideInBureau.setDataType(FieldType.BOOLEAN);
        hideInBureau.setDisplayType(FacilioField.FieldDisplayType.RADIO);
        hideInBureau.setDefault(true);
        hideInBureau.setModule(mod);
        modBean.addField(hideInBureau);

        LookupField inhibitReasonForBureauAction = new LookupField();
        inhibitReasonForBureauAction.setDefault(true);
        inhibitReasonForBureauAction.setName("inhibitReason");
        inhibitReasonForBureauAction.setDisplayName("Inhibit Reason");
        inhibitReasonForBureauAction.setModule(modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME));
        inhibitReasonForBureauAction.setDataType(FieldType.LOOKUP);
        inhibitReasonForBureauAction.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        inhibitReasonForBureauAction.setColumnName("INHIBIT_REASON");
        inhibitReasonForBureauAction.setLookupModule(modBean.getModule(BureauInhibitReasonListModule.MODULE_NAME));
        modBean.addField(inhibitReasonForBureauAction);
    }
}