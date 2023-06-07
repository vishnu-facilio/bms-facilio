package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class UtilityIntegrationBillActivityModule extends BaseModuleConfig{

    public UtilityIntegrationBillActivityModule() throws Exception {
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_BILL_ACTIVITY);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule utilityIntegrationBillModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationbillActivityModule = addActivityModule();
        modules.add(utilityIntegrationbillActivityModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, utilityIntegrationBillModule.getName());
        addModuleChain.execute();


    }

    public FacilioModule addActivityModule() throws Exception{

        FacilioModule module = new FacilioModule("utilityIntegrationBillActivity", "Utility Integration Bill Activity", "Utility_Integration_Bill_Activity", FacilioModule.ModuleType.ACTIVITY,true);

        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        DateField ttime = new DateField(module, "ttime", "Timestamp", FacilioField.FieldDisplayType.NUMBER, "TTIME", FieldType.DATE_TIME, true, false, true, null);
        fields.add(ttime);

        NumberField type = new NumberField(module, "type", "Type", FacilioField.FieldDisplayType.NUMBER, "ACTIVITY_TYPE", FieldType.NUMBER, true, false, true, null);
        fields.add(type);

        LookupField doneBy = new LookupField(module, "doneBy", "Done By", FacilioField.FieldDisplayType.LOOKUP_POPUP, "DONE_BY_ID", FieldType.LOOKUP, false, false, true, null, "users");
        fields.add(doneBy);

        StringField infoJsonStr = new StringField(module, "infoJsonStr", "Info", FacilioField.FieldDisplayType.TEXTAREA, "INFO", FieldType.STRING, false, false, true, null);
        fields.add(infoJsonStr);

        module.setFields(fields);
        return module;
    }
}


