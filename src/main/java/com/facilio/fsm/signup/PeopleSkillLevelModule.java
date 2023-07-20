package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeopleSkillLevelModule extends BaseModuleConfig{
    public PeopleSkillLevelModule(){setModuleName(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL);}

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule peopleSkillLevelModule = addPeopleSkillLevelModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(peopleSkillLevelModule));
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private FacilioModule addPeopleSkillLevelModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL,
                "People Skill Level",
                "PeopleSkillLevel",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField peopleId = FieldFactory.getDefaultField("people", "People", "PEOPLE_ID", FieldType.LOOKUP,true);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_SKILL));
        fields.add(skill);

        SystemEnumField level = FieldFactory.getDefaultField("level", "Level", "LEVEL", FieldType.SYSTEM_ENUM);
        level.setEnumName("Level");
        fields.add(level);

        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));

        module.setFields(fields);
        return module;

    }
}
