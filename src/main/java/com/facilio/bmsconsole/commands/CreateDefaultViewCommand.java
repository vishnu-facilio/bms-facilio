package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateDefaultViewCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
		ViewGroups viewGroup = new ViewGroups();
		viewGroup.setName("allViews");
		viewGroup.setDisplayName("All Views");
		viewGroup.setModuleId(module.getModuleId());
		long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), module.getName());
		
		FacilioView defaultView = ViewFactory.getCustomModuleAllView(module);
		defaultView.setGroupId(groupId);
		defaultView.setModuleId(module.getModuleId());
		defaultView.setType(1);
		defaultView.setStatus(true);
		defaultView.setModuleName(module.getName());
		defaultView.setFields(getDefaultViewFields(module));
		long viewId = ViewAPI.addView(defaultView, AccountUtil.getCurrentOrg().getOrgId());
		defaultView.setId(viewId);

		return false;
	}

	public static List<ViewField> getDefaultViewFields(FacilioModule module) throws Exception {
		// Set all module fields as viewFields
		ModuleBean moduleBean = Constants.getModBean();
		List<FacilioField> allFields = moduleBean.getAllFields(module.getName());

		List<String> customModuleFieldNames = Arrays.asList("name", "siteId", "sysCreatedTime", "sysModifiedTime");

		List<ViewField> viewFieldList = new ArrayList<>();
		for (FacilioField field : allFields) {
			if (customModuleFieldNames.contains(field.getName())) {
				ViewField viewField = new ViewField();
				viewField.setColumnDisplayName(field.getDisplayName());
				viewField.setFieldId(field.getFieldId());
				viewField.setFieldName(field.getName());
				viewField.setField(field);

				viewFieldList.add(viewField);
			}
		}
		return viewFieldList;
	}
}
