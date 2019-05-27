package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeleteResourceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			ResourceAPI.getExtendedResources(recordIds, true);
			
			DeleteRecordBuilder<? extends ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
																				.module(module)
																				.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
																				;
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, deleteBuilder.markAsDelete());
			context.put(FacilioConstants.ContextNames.RECORD_LIST, recordIds);
		}
		return false;
	}

}
