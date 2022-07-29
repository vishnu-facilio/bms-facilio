package com.facilio.bmsconsoleV3.signup.craft;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
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
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LabourCraftAndSkillsModules extends SignUpData{
	@Override
	public void addData() throws Exception{

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean",AccountUtil.getCurrentOrg().getOrgId());

		FacilioModule labourModule = bean.getModule(FacilioConstants.ContextNames.LABOUR);

		List<FacilioModule> modules = new ArrayList<>();

		FacilioModule labourCraftAndSkills = constructLabourCraftAndSkills(labourModule, bean);

		modules.add(labourCraftAndSkills);

		FacilioChain addSkillModuleChain = TransactionChainFactory.addSystemModuleChain();
		addSkillModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
		addSkillModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,labourModule.getName());
		addSkillModuleChain.execute();

		addPeopleLookUpFieldForLabour(labourModule,bean);

		FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, labourModule.getName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, labourModule.getFields());
		chain.execute();

	}

	private void addPeopleLookUpFieldForLabour(FacilioModule labourModule, ModuleBean bean) throws Exception{

		LookupField people = FieldFactory.getDefaultField("people","People","PEOPLE_ID",FieldType.LOOKUP);
		people.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));

		labourModule.setFields(Collections.singletonList(people));
	}

	private FacilioModule constructLabourCraftAndSkills(FacilioModule labourModule, ModuleBean bean) throws Exception{
		FacilioModule module = new FacilioModule("labourCraftSkill", "Labour Craft Skill", "LabourCraftSkills", FacilioModule.ModuleType.SUB_ENTITY,true);

		List<FacilioField> fields =new ArrayList<>();

		LookupField labour = FieldFactory.getDefaultField("labour","Labour","LABOUR_ID", FieldType.LOOKUP);
		labour.setLookupModule(labourModule);

		fields.add(labour);

		LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID",FieldType.LOOKUP);
		craft.setLookupModule(bean.getModule(FacilioConstants.CraftAndSKills.CRAFT));

		fields.add(craft);

		LookupField skill = FieldFactory.getDefaultField("skill","Skills","SKILL_ID",FieldType.LOOKUP);
		skill.setLookupModule(bean.getModule(FacilioConstants.CraftAndSKills.SKILLS));

		fields.add(skill);

		fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));

		module.setFields(fields);

		return module;
	}
}
