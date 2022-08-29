package com.facilio.bmsconsoleV3.signup.labour;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddLabourModuleFields extends SignUpData {
    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LABOUR);

        FacilioUtil.throwIllegalArgumentException(module==null,"Labour module should not be null..");

        List<FacilioField> fields = new ArrayList<>();

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
        approvalFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(approvalFlowIdField);


        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }
}
