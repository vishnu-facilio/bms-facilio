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
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceTaskModule extends BaseModuleConfig {

    public ServiceTaskModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceTaskModule = constructServiceTaskModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceTaskModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addTaskSkillsField();
    }

    private FacilioModule constructServiceTaskModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, "Service Task", "Service_Task", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);

        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("remarks","Remarks","REMARKS",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));

        LookupField parent = FieldFactory.getDefaultField("workType","Work Type","WORK_TYPE",FieldType.LOOKUP);
        parent.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE),"Work Type module doesn't exist."));
        fields.add(parent);

        LookupField serviceOrder = FieldFactory.getDefaultField("serviceOrder","Service Order","SERVICE_ORDER",FieldType.LOOKUP);
        serviceOrder.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER),"Service Order module doesn't exist."));
        fields.add(serviceOrder);

        fields.add(FieldFactory.getDefaultField("sequence","Sequence","SEQUENCE",FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("actualStartTime","Actual Start Time","ACTUAL_START_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));
        fields.add(FieldFactory.getDefaultField("actualEndTime","Actual End Time","ACTUAL_END_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME));

        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
        status.setEnumName("ServiceTaskStatus");
        fields.add(status);

        module.setFields(fields);

        return module;
    }
    private void addTaskSkillsField() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceTask = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule serviceTaskSkillsRelMod = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_SKILLS, "Service Task Skills", "Service_Task_Skills", FacilioModule.ModuleType.SUB_ENTITY);
        FacilioModule skillsMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);

        MultiLookupField skillsMultiLookup = FieldFactory.getDefaultField("skills","Skills","SKILLS", FieldType.MULTI_LOOKUP);
        skillsMultiLookup.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        skillsMultiLookup.setModule(serviceTask);
        skillsMultiLookup.setRequired(false);
        skillsMultiLookup.setDisabled(false);
        skillsMultiLookup.setDefault(true);
        skillsMultiLookup.setRelModule(serviceTaskSkillsRelMod);
        skillsMultiLookup.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        skillsMultiLookup.setLookupModule(skillsMod);
        bean.addField(skillsMultiLookup);
    }
}
