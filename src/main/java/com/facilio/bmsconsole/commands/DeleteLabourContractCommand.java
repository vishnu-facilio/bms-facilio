package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeleteLabourContractCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			DeleteRecordBuilder<LabourContractContext> deleteRecordBuilder = new DeleteRecordBuilder<LabourContractContext>()
					.moduleName(moduleName)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			int updatedCount = deleteRecordBuilder.markAsDelete();
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updatedCount);
		}
//		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTRACTS);
		context.put(FacilioConstants.ContextNames.PREFERENCE_NAMES, "expireDateNotification,paymentNotification");

		return false;
	}

}
