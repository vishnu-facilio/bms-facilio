package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GenericDeleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		if(recordId > 0 && moduleName != null) {
			GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(recordId, module));
			
			delete.delete();
		}
		return false;
	}

}
