package com.facilio.bmsconsoleV3.signup.workOrder;

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
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddWorkOrderLabourFields extends SignUpData {
    @Override
    public void addData() throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);

        Objects.requireNonNull(module,"WorkOrder Labour module doesn't exist.");

        List<FacilioField> fields = new ArrayList<>();

        LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID", FieldType.LOOKUP);
        craft.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.CraftAndSKills.CRAFT),"Craft module doesn't exists."));
        fields.add(craft);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.CraftAndSKills.SKILLS),"Skill module doesn't exists."));
        fields.add(skill);

        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }
}
