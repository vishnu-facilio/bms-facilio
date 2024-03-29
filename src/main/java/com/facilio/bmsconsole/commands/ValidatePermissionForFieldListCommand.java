package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext.PermissionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class ValidatePermissionForFieldListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fieldList = (List<FacilioField>)context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		String moduleName= (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PermissionType fieldPermissionType = (PermissionType)context.get(FacilioConstants.ContextNames.PERMISSION_TYPE);
		Boolean validateFieldPermissions = (Boolean) context
				.getOrDefault(FacilioConstants.ContextNames.DO_FIELD_PERMISSIONS_VALIDATION, true);

		try {
			if(StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(fieldList) && AccountUtil.isFeatureEnabled(FeatureLicense.FIELD_PERMISSIONS)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				List<FacilioField> restrictedFields = FieldUtil.getPermissionRestrictedFields(module, fieldPermissionType, validateFieldPermissions);
				if(CollectionUtils.isNotEmpty(restrictedFields)) {
					for(FacilioField field : fieldList) {
					     if(restrictedFields.contains(field.getName())) {
				        	 fieldList.remove(field);
				         }
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
