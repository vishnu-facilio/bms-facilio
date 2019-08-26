package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.fs.FileInfo;
import com.facilio.modules.fields.FileField;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

public class AddSystemFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if (module != null) {
			// add only for custom module
			if (module.getTypeEnum() != ModuleType.CUSTOM) {
				return false;
			}
			
			if (fields == null) {
				fields = new ArrayList<>();
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField mainField = FieldFactory.getField("name", "Name", "NAME", module, FieldType.STRING);
			mainField.setDisplayType(FieldDisplayType.TEXTBOX);
			mainField.setMainField(true);
			mainField.setRequired(true);
			mainField.setDefault(true);
			fields.add(mainField);

			FileField photoField = new FileField();
			photoField.setName("photo");
			photoField.setDisplayName("Photo");
			photoField.setColumnName("PHOTO_ID");
			photoField.setModule(module);
			photoField.setDataType(FieldType.FILE);
			photoField.setDisplayType(FieldDisplayType.IMAGE);
			photoField.setFormat(FileInfo.FileFormat.IMAGE);
			photoField.setMainField(false);
			photoField.setRequired(false);
			photoField.setDefault(true);
			fields.add(photoField);
			
			LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
			moduleStateField.setDefault(true);
			moduleStateField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
			moduleStateField.setLookupModule(modBean.getModule("ticketstatus"));
			fields.add(moduleStateField);

			FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
			stateFlowIdField.setDefault(true);
			stateFlowIdField.setDisplayType(FieldDisplayType.NUMBER);
			fields.add(stateFlowIdField);
		}
		return false;
	}

}
