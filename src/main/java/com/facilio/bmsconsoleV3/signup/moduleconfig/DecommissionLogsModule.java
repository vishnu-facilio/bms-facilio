package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecommissionLogsModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        FacilioModule decommissionLog = addDecommissionLogModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(decommissionLog));
        addModuleChain.execute();
    }
    private FacilioModule addDecommissionLogModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("decommissionLog",
                "Decommission Log",
                "Decommission_Log",
                FacilioModule.ModuleType.CUSTOM,
                false
        );
        List<FacilioField> fields = new ArrayList<>();

        NumberField idField = (NumberField) FieldFactory.getDefaultField("id", "ID", "ID", FieldType.NUMBER, true);
        fields.add(idField);
        NumberField resourceIdField = (NumberField) FieldFactory.getDefaultField("resourceId", "Resource Id", "RESOURCE_ID", FieldType.NUMBER);
        fields.add(resourceIdField);

        BooleanField decommissionField = (BooleanField) FieldFactory.getDefaultField("decommission", "Decommission", "IS_DECOMMISSION", FieldType.BOOLEAN);
        fields.add(decommissionField);

        DateField decommissionTImeField = (DateField) FieldFactory.getDefaultField("commissionedTime","commissioned Time","DECOMMISSIONED_TIME",FieldType.DATE_TIME);
        fields.add(decommissionTImeField);

        LookupField userField = (LookupField) FieldFactory.getDefaultField("commissionedBy","Commissione By","COMMISSIONED_BY",module, FieldType.LOOKUP);
        userField.setDisplayType(11);
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(userField);

        StringField logJsonField = FieldFactory.getDefaultField("logValue","Log Value","LOG_VALUE",FieldType.STRING);
        fields.add(logJsonField);

        StringField remarksField = FieldFactory.getDefaultField("remarks","Remark","REMARKS",FieldType.STRING);
        fields.add(remarksField);

        module.setFields(fields);
        return module;
    }
}
