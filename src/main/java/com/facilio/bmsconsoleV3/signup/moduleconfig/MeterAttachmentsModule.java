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

public class MeterAttachmentsModule extends BaseModuleConfig{
    public MeterAttachmentsModule() throws Exception {
        setModuleName(FacilioConstants.Meter.METER_ATTACHMENTS);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule meterModule = modBean.getModule("meter");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule meterAttachmentsModule = addMeterAttachmentsModule();
        modules.add(meterAttachmentsModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, meterModule.getName());
        addModuleChain.execute();

    }

    public FacilioModule addMeterAttachmentsModule() throws Exception{

        FacilioModule module = new FacilioModule("meterattachments", "Meter Attachments", "Meter_Attachments", FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<>();

        NumberField fileId = new NumberField(module, "fileId", "File ID", FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
        fields.add(fileId);

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_METER", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        NumberField createdTime = new NumberField(module, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, null);
        fields.add(createdTime);

        module.setFields(fields);
        return module;
    }
}
