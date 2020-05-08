package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
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
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class ValidateFieldPermissionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ModuleBaseWithCustomFields> recordList = (List<ModuleBaseWithCustomFields>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PermissionType fieldPermissionType = (PermissionType) context
				.get(FacilioConstants.ContextNames.PERMISSION_TYPE);
		try {
			if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(recordList)
					&& AccountUtil.isFeatureEnabled(FeatureLicense.SCOPING)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				List<FacilioField> restrictedFields = FieldUtil.getPermissionRestrictedFields(module,
						fieldPermissionType);
				if (CollectionUtils.isNotEmpty(restrictedFields)) {
					for (ModuleBaseWithCustomFields rec : recordList) {
						for (FacilioField field : restrictedFields) {
							if(fieldPermissionType == PermissionType.READ_WRITE) {
								throw new IllegalArgumentException("Not permitted to add/update the field - "+ field.getName());
							}
							if(field.isDefault()) {
								PropertyUtils.setProperty(rec, field.getName(), null);
							}
							else {
								PropertyUtils.setMappedProperty(rec, "data", field.getName(), null);//custom field handling
							}
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
