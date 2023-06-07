package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class UtilityIntegrationCustomerAttachmentsModule extends BaseModuleConfig{
    public UtilityIntegrationCustomerAttachmentsModule() throws Exception {
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ATTACHMENTS);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule attachmentsModule = addAttachmentsModule();
        modules.add(attachmentsModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, customerModule.getName());
        addModuleChain.execute();

    }

    public FacilioModule addAttachmentsModule() throws Exception{

        FacilioModule module = new FacilioModule("utilityIntegrationCustomerAttachments", "Utility Customer Attachments", "Utility_Integration_Customer_Attachments", FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fileId = new NumberField(module, "fileId", "File ID", FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
        fields.add(fileId);

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        NumberField createdTime = new NumberField(module, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, null);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
    }
}

