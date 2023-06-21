package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
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

public class PeopleSkillLevelModule extends SignUpData {
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
        FacilioModule module = new FacilioModule("peopleskilllevel",
                "People Skill Level",
                "PeopleSkillLevel",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField peopleId = FieldFactory.getDefaultField("peopleId", "People ID", "PEOPLE_ID", FieldType.LOOKUP);
        peopleId.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(peopleId);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(modBean.getModule(FacilioConstants.CraftAndSKills.CRAFT));
        fields.add(skill);

        SystemEnumField level = FieldFactory.getDefaultField("level", "Level", "LEVEL", FieldType.SYSTEM_ENUM);
        fields.add(level);

        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));

        module.setFields(fields);
        return module;

    }
}
