package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class ValidateFieldPermissionCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Map<String, Object>> recordMapList = (List<Map<String, Object>>)context.get(FacilioConstants.ContextNames.RECORD_LIST_MAP);
		String moduleName= (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PermissionType fieldPermissionType = (PermissionType)context.get(FacilioConstants.ContextNames.PERMISSION_TYPE);
		try {
			if(StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(recordMapList) && AccountUtil.isFeatureEnabled(FeatureLicense.SCOPING)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				List<String> permissableFields = FieldUtil.getFieldPermission(module, fieldPermissionType);
				for(Map<String, Object> rec : recordMapList) {
					Iterator<Map.Entry<String, Object>> itr = rec.entrySet().iterator(); 
					while(itr.hasNext()) 
			        { 
						 Map.Entry<String, Object> entry = itr.next(); 
				         if(!permissableFields.contains(entry.getKey())) {
				        	 rec.remove(entry.getKey());
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
