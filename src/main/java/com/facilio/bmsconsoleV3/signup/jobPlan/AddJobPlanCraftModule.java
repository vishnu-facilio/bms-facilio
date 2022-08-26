package com.facilio.bmsconsoleV3.signup.jobPlan;

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

public class AddJobPlanCraftModule extends SignUpData {
    @Override
    public void addData() throws Exception {

        ModuleBean bean = Constants.getModBean();
        FacilioModule parentModule = bean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

        Objects.requireNonNull(parentModule,"JobPlan module doesn't exists.");

        FacilioModule jobPlanCraftModule = constructJobPlanCraftModule(parentModule,bean);

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(jobPlanCraftModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,parentModule.getName());
        addModuleChain.execute();

    }

    private FacilioModule constructJobPlanCraftModule(FacilioModule jobPlanModule,ModuleBean bean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN_CRAFTS, "Job Plan Crafts", "Job_Plan_Crafts", FacilioModule.ModuleType.SUB_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("jobPlan","Job Plan","JOB_PLAN_ID",FieldType.LOOKUP,true);
        parent.setLookupModule(jobPlanModule);
        fields.add(parent);

        LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID",FieldType.LOOKUP);
        craft.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.CRAFT),"Craft module doesn't exists."));
        fields.add(craft);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.SKILLS),"Skill module doesn't exists."));
        fields.add(skill);

        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalPrice","Total Price","TOTAL_PRICE",FieldType.DECIMAL));

        module.setFields(fields);

        return module;
    }
}
