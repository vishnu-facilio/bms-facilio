 package com.facilio.bmsconsole.commands;

 import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class LoadMainFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the module");
		}
 		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
			//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			//Transaction trans = FacilioTransactionManager.INSTANCE.getTransactionManager().suspend();
			
		ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		//	modBean.
			
			//FacilioTransactionManager.INSTANCE.getTransactionManager().resume(trans);
		FacilioField defaultField = modBean.getPrimaryField(moduleName);
			
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, defaultField.getModule().getTableName());
		context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, defaultField);
		
		return false;
		
	}

}
