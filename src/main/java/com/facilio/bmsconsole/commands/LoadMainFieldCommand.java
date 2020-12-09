 package com.facilio.bmsconsole.commands;

 import com.facilio.util.FacilioUtil;
 import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
 import org.apache.commons.lang3.StringUtils;

 import java.text.MessageFormat;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;

 public class LoadMainFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Module Name is not set for the module");
		ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField defaultField = modBean.getPrimaryField(moduleName);
		FacilioUtil.throwIllegalArgumentException(defaultField == null, MessageFormat.format("Invalid default field for the given module {0}", moduleName));
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, defaultField.getModule().getTableName());
		context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, defaultField);
		
		return false;
		
	}

}
