package com.facilio.bmsconsoleV3.signup.craft;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class LabourCraftAndSkillsModules extends BaseModuleConfig {
	public LabourCraftAndSkillsModules() throws Exception {
		setModuleName(FacilioConstants.CraftAndSKills.LABOUR_CRAFT);
	}

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

		LookupField labour = FieldFactory.getDefaultField("labour","Labour","LABOUR_ID", FieldType.LOOKUP,true);
		labour.setLookupModule(labourModule);

		fields.add(labour);

		LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID",FieldType.LOOKUP);
		craft.setLookupModule(bean.getModule(FacilioConstants.CraftAndSKills.CRAFT));

		fields.add(craft);

		LookupField skill = FieldFactory.getDefaultField("skill","Skills","SKILL_ID",FieldType.LOOKUP);
		skill.setLookupModule(bean.getModule(FacilioConstants.CraftAndSKills.SKILLS));

		fields.add(skill);

		fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));
		fields.add(FieldFactory.getDefaultField("isDefault","Is Default","IS_DEFAULT",FieldType.BOOLEAN));
		module.setFields(fields);

		return module;
	}
	@Override
	public List<Map<String, Object>> getViewsAndGroups() {
		List<Map<String, Object>> groupVsViews = new ArrayList<>();
		Map<String, Object> groupDetails;

		int order = 1;
		ArrayList<FacilioView> labourcraft = new ArrayList<FacilioView>();
		labourcraft.add(getLabourCraftsViews().setOrder(order++));

		groupDetails = new HashMap<>();
		groupDetails.put("name", "systemviews");
		groupDetails.put("displayName", "System Views");
		groupDetails.put("moduleName", FacilioConstants.CraftAndSKills.LABOUR_CRAFT);
		groupDetails.put("views", labourcraft);
		groupVsViews.add(groupDetails);

		return groupVsViews;
	}

	private FacilioView getLabourCraftsViews() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "LabourCraftSkills.ID", FieldType.NUMBER), true));

		FacilioView labourCraftsView = new FacilioView();
		labourCraftsView.setName("all");
		labourCraftsView.setDisplayName("Labour crafts skill");

		labourCraftsView.setModuleName(FacilioConstants.CraftAndSKills.LABOUR_CRAFT);
		labourCraftsView.setSortFields(sortFields);

		List<ViewField> LabourCraftViewFields = new ArrayList<>();

		LabourCraftViewFields.add(new ViewField("labour","Labour"));
		LabourCraftViewFields.add(new ViewField("craft","Craft"));
		LabourCraftViewFields.add(new ViewField("skill","Skill"));
		LabourCraftViewFields.add(new ViewField("rate","Rate"));
		LabourCraftViewFields.add(new ViewField("isDefault","Is Default Craft?"));


		labourCraftsView.setFields(LabourCraftViewFields);

		return labourCraftsView;
	}
}

