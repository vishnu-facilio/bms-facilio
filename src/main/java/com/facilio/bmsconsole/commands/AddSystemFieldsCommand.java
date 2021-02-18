package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

public class AddSystemFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if (module != null) {
			// add only for custom module
			if (!module.isCustom()) {
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
			
			FacilioField siteField = FieldFactory.getSiteIdField(module);
			siteField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
			siteField.setMainField(true);
			siteField.setDisplayName("Site");
			siteField.setRequired(true);
			siteField.setDefault(true);
			fields.add(siteField);
			
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

			LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
			approvalStateField.setDefault(true);
			approvalStateField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
			approvalStateField.setLookupModule(modBean.getModule("ticketstatus"));
			fields.add(approvalStateField);

			FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
			approvalFlowIdField.setDefault(true);
			approvalFlowIdField.setDisplayType(FieldDisplayType.NUMBER);
			fields.add(approvalFlowIdField);
		}
		return false;
	}

	// temp migration code.. remove it
	public static void addApprovalFlowFieldsForCustomModule() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> customModuleList = modBean.getModuleList(ModuleType.BASE_ENTITY, true);
		if (CollectionUtils.isNotEmpty(customModuleList)) {
			for (FacilioModule module : customModuleList) {
				FacilioField approvalStatus = modBean.getField("approvalStatus", module.getName());
				if (approvalStatus != null) {
					// approval fields already exists
					continue;
				}

				LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
				approvalStateField.setDefault(true);
				approvalStateField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
				approvalStateField.setLookupModule(modBean.getModule("ticketstatus"));
				modBean.addField(approvalStateField);

				FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
				approvalFlowIdField.setDefault(true);
				approvalFlowIdField.setDisplayType(FieldDisplayType.NUMBER);
				modBean.addField(approvalFlowIdField);
			}
		}
	}

}
