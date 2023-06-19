package com.facilio.fsm.signup;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.List;

public class ServiceSkillModule  extends BaseModuleConfig {

    public ServiceSkillModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule serviceSkillModule = constructServiceSkillModule();

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(serviceSkillModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    private FacilioModule constructServiceSkillModule(){
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL, "Skills", "Sevice_Skills", FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME",FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("ratePerHour","Rate Per Hour","RATE_PER_HOUR",FieldType.NUMBER, FacilioField.FieldDisplayType.DURATION));
        fields.add(FieldFactory.getDefaultField("localId","Local Id","LOCAL_ID",FieldType.NUMBER));
        module.setFields(fields);

        return module;
    }
}
