package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class DeleteGatePassCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			DeleteRecordBuilder<GatePassContext> deleteRecordBuilder = new DeleteRecordBuilder<GatePassContext>()
					.moduleName(moduleName)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			int updatedCount = deleteRecordBuilder.markAsDelete();
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updatedCount);
		}
		return false;
	}

}
