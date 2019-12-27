package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.agentv2.device.Device;
import com.facilio.bmsconsole.context.FormSiteRelationContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddFormSiteRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		if (form.getSiteIds() != null && form.getSiteIds().size() > 0) {
			List<FormSiteRelationContext> propList = new ArrayList<FormSiteRelationContext>();
			
			for (int i = 0; i < form.getSiteIds().size(); i++) {
				FormSiteRelationContext prop = new FormSiteRelationContext();
				prop.setFormId(form.getId());
				prop.setSiteId(form.getSiteIds().get(i));
				propList.add(prop);
			}
			
			FacilioModule formSiteRelationModule = ModuleFactory.getFormSiteRelationModule();
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(formSiteRelationModule.getTableName())
					.fields(FieldFactory.getFormSiteRelationFields())
					.addRecords(FieldUtil.getAsMapList(propList,FormSiteRelationContext.class));
			
			
			if(!propList.isEmpty()) {
				insertRecordBuilder.save();
			}
		}
		return false;
	}

	

}
