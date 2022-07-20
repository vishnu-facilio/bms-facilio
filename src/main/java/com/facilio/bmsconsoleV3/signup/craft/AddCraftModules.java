package com.facilio.bmsconsoleV3.signup.craft;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.ArrayList;
import java.util.List;

public class AddCraftModules extends SignUpData{
	@Override
	public void addData() throws Exception{

		List<FacilioModule> modules = new ArrayList<>();

		FacilioModule craftModule = constructCraftModule();
		modules.add(craftModule);

		FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
		addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
		addModuleChain.execute();

		List<FacilioModule> modules1 = new ArrayList<>();

		FacilioModule skillModule = constructSkillModule(craftModule);
		modules1.add(skillModule);

		FacilioChain addSkillModuleChain = TransactionChainFactory.addSystemModuleChain();
		addSkillModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
		addSkillModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,craftModule.getName());
		addSkillModuleChain.execute();

	}

	private FacilioModule 	constructSkillModule(FacilioModule craftModule){
		FacilioModule module = new FacilioModule("craftSkill", "Craft Skill", "CraftSkills", FacilioModule.ModuleType.SUB_ENTITY,true);

		List<FacilioField> fields = new ArrayList<>();

		fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
		fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
		LookupField parent = FieldFactory.getDefaultField("parentId","Parent Id","PARENT_ID",FieldType.LOOKUP);
		parent.setLookupModule(craftModule);
		fields.add(parent);
		fields.add(FieldFactory.getDefaultField("skillLevelRank","Skill Level Rank","SKILL_LEVEL_RANK",FieldType.NUMBER));
		module.setFields(fields);

		return module;
	}

	private FacilioModule constructCraftModule(){

		FacilioModule module = new FacilioModule("crafts", "Crafts", "Crafts", FacilioModule.ModuleType.BASE_ENTITY, true);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
		fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
		fields.add(FieldFactory.getDefaultField("standardRate","Standard Rate","STANDARD_RATE",FieldType.DECIMAL));

		module.setFields(fields);

		return module;
	}
}
