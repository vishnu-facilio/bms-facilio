package com.facilio.bmsconsole.commands;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
		Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PermissionType fieldPermissionType = (PermissionType) context
				.get(FacilioConstants.ContextNames.PERMISSION_TYPE);
		Boolean validateFieldPermissions = (Boolean) context
				.getOrDefault(FacilioConstants.ContextNames.DO_FIELD_PERMISSIONS_VALIDATION, true);

		if(StringUtils.isEmpty(moduleName)){
			throw new IllegalArgumentException("Module name cannot be null");
		}

		if(MapUtils.isNotEmpty(recordMap)) {
			try {
				List<ModuleBaseWithCustomFields> recordList = recordMap.get(moduleName);
				if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(recordList)
						&& AccountUtil.isFeatureEnabled(FeatureLicense.FIELD_PERMISSIONS)) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(moduleName);
					List<FacilioField> restrictedFields = FieldUtil.getPermissionRestrictedFields(module,
							fieldPermissionType, validateFieldPermissions);
					if (CollectionUtils.isNotEmpty(restrictedFields)) {
						for (ModuleBaseWithCustomFields rec : recordList) {
							for (FacilioField field : restrictedFields) {
								try {
									if (fieldPermissionType == PermissionType.READ_WRITE) {
										throw new IllegalArgumentException("Not permitted to add/update the field - " + field.getName());
									}
									if (field.isDefault()) {
										PropertyUtils.setProperty(rec, field.getName(), null);
									} else {
										PropertyUtils.setMappedProperty(rec, "data", field.getName(), null);//custom field handling
									}
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
									//boolean fields will not work if the naming convention is wrong
								}

							}

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
