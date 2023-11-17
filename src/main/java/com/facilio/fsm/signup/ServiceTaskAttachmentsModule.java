package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceTaskAttachmentsModule extends BaseModuleConfig {
    public ServiceTaskAttachmentsModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_ATTACHMENTS);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        FacilioModule serviceTaskModule = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        if(serviceTaskModule!=null && serviceTaskModule.getModuleId()>0) {
            FacilioModule serviceTaskAttachmentsModule = constructServiceTaskAttachmentsModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceTaskAttachmentsModule));
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
            addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, serviceTaskModule.getName());
            addModuleChain.execute();
            bean.addSubModule(serviceTaskModule.getModuleId(), serviceTaskAttachmentsModule.getModuleId());
        }
    }
    private FacilioModule constructServiceTaskAttachmentsModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_ATTACHMENTS, "Task Attachments", "Service_Task_Attachments", FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField fileId = FieldFactory.getDefaultField("fileId","File ID","FILE_ID",FieldType.NUMBER);
        fileId.setRequired(true);
        fileId.setMainField(true);
        fields.add(fileId);

        FacilioField parent = FieldFactory.getDefaultField("parentId","Parent","PARENT",FieldType.NUMBER);
        fields.add(parent);

        FacilioField createdTime = FieldFactory.getDefaultField("createdTime","Created Time","CREATED_TIME",FieldType.NUMBER);
        createdTime.setRequired(true);
        fields.add(createdTime);

        FacilioField type = FieldFactory.getDefaultField("type","Type","ATTACHMENT_TYPE",FieldType.NUMBER);
        type.setRequired(true);
        fields.add(type);

        module.setFields(fields);

        return module;
    }
}
