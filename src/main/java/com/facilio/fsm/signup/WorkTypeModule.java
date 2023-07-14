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
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class WorkTypeModule extends BaseModuleConfig {

    public WorkTypeModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE);
    }

    @Override
    public void addData() throws Exception {
        addWorkTypeModule();
        addWorkTypeSkillsField();
    }

    private void addWorkTypeModule() throws Exception{
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE, "Work Type", "Work_Type", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("estimatedDuration","Estimated Duration","ESTIMATED_DURATION",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        module.setFields(fields);
        addModule(module);
    }
    private void addWorkTypeSkillsField() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule workType = bean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE);
        FacilioModule workTypeSkillsRelMod = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.WORK_TYPE_SKILLS, "Work Type Skills", "Work_Type_Skills", FacilioModule.ModuleType.SUB_ENTITY);
        FacilioModule skillsMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);

        MultiLookupField skillsMultiLookup = FieldFactory.getDefaultField("skills","Skills","SKILLS", FieldType.MULTI_LOOKUP);
        skillsMultiLookup.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        skillsMultiLookup.setModule(workType);
        skillsMultiLookup.setRequired(false);
        skillsMultiLookup.setDisabled(false);
        skillsMultiLookup.setDefault(true);
        skillsMultiLookup.setRelModule(workTypeSkillsRelMod);
        skillsMultiLookup.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        skillsMultiLookup.setLookupModule(skillsMod);
        bean.addField(skillsMultiLookup);
    }
    private void addModule(FacilioModule module) throws Exception{
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.Module.USE_PEOPLE_LOOKUP, true);
        addModuleChain.execute();
    }
}
