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
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServiceTaskTemplateModule extends BaseModuleConfig {
    public ServiceTaskTemplateModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlanModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioModule serviceTaskTemplateModule = constructServiceTaskTemplateModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceTaskTemplateModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        modBean.addSubModule(servicePlanModule.getModuleId(), serviceTaskTemplateModule.getModuleId(),2);

        addTaskSkillsField();
        addServicePlanField();
    }
    private FacilioModule constructServiceTaskTemplateModule() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE, "Service Task Template", "Service_Task_Template", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();
        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("taskCode","Task Code","TASK_CODE",FieldType.STRING));
        LookupField parent = FieldFactory.getDefaultField("workType","Work Type","WORK_TYPE",FieldType.LOOKUP);
        parent.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE),"Work Type module doesn't exist."));
        fields.add(parent);
        fields.add(FieldFactory.getDefaultField("sequence","Sequence","SEQUENCE",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("isPhotoMandatory","Photo Mandatory","IS_PHOTO_MANDATORY", FieldType.BOOLEAN));
        module.setFields(fields);
        return module;
    }
    private void addTaskSkillsField() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule serviceTaskTemplate = bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);
        FacilioModule serviceTaskSkillsRelMod = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE_SKILLS, "Service Task Template Skills", "Service_Task_Template_Skills", FacilioModule.ModuleType.SUB_ENTITY);
        FacilioModule skillsMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);

        MultiLookupField skillsMultiLookup = FieldFactory.getDefaultField("skills","Skills","SKILLS", FieldType.MULTI_LOOKUP);
        skillsMultiLookup.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        skillsMultiLookup.setModule(serviceTaskTemplate);
        skillsMultiLookup.setRequired(false);
        skillsMultiLookup.setDisabled(false);
        skillsMultiLookup.setDefault(true);
        skillsMultiLookup.setRelModule(serviceTaskSkillsRelMod);
        skillsMultiLookup.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        skillsMultiLookup.setLookupModule(skillsMod);
        bean.addField(skillsMultiLookup);
    }
    private void addServicePlanField()throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlanModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioModule serviceTaskTemplateModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);

        LookupField parent = FieldFactory.getDefaultField("servicePlan","Service Plan","SERVICE_PLAN", FieldType.LOOKUP);
        parent.setRequired(true);
        parent.setLookupModule(servicePlanModule);
        parent.setModule(serviceTaskTemplateModule);
        modBean.addField(parent);

    }
}
