package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

import java.util.ArrayList;
import java.util.List;

public class UtilityTypeModule extends BaseModuleConfig {

	public UtilityTypeModule() throws Exception {
		setModuleName(FacilioConstants.Meter.UTILITY_TYPE);
	}

	@Override
	public void addData() throws Exception{

		List<FacilioModule> modules = new ArrayList<>();

		FacilioModule utilityTypeModule = addUtilityTypeModule();
		modules.add(utilityTypeModule);

		FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
		addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.execute();

	}

	public FacilioModule addUtilityTypeModule(){
		FacilioModule module = new FacilioModule("utilitytype", "Utility Type", "Utility_Type", FacilioModule.ModuleType.PICK_LIST,true);

		List<FacilioField> fields = new ArrayList<>();

		StringField name = new StringField(module, "name", "Name", FacilioField.FieldDisplayType.TEXTBOX, "NAME", FieldType.STRING, true, false, true, null);
		fields.add(name);

		StringField description = new StringField(module, "description", "Description", FacilioField.FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING, false, false, true, null);
		fields.add(description);

		NumberField parentUtilityTypeId = new NumberField(module, "parentUtilityTypeId", "Parent Utility Type Id", FacilioField.FieldDisplayType.NUMBER, "PARENT_UTILITY_TYPE_ID", FieldType.NUMBER, false, false, true, null);
		fields.add(parentUtilityTypeId);

		NumberField meterModuleID = new NumberField(module, "meterModuleID", "Meter Module ID", FacilioField.FieldDisplayType.NUMBER, "METER_MODULEID", FieldType.NUMBER, false, false, true, null);
		fields.add(meterModuleID);

		StringField displayName = new StringField(module, "displayName", "Display Name", FacilioField.FieldDisplayType.TEXTBOX, "DISPLAY_NAME", FieldType.STRING, true, false, true, true);
		fields.add(displayName);

		BooleanField isDefault = new BooleanField(module, "isDefault", "Is Default", FacilioField.FieldDisplayType.DECISION_BOX, "IS_DEFAULT", FieldType.BOOLEAN, true, false, true, null);
		fields.add(isDefault);

		module.setFields(fields);

		return module;
	}

}
