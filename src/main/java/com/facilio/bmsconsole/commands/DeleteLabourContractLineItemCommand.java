package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeleteLabourContractLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			DeleteRecordBuilder<LabourContractLineItemContext> deleteRecordBuilder = new DeleteRecordBuilder<LabourContractLineItemContext>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			int delete = deleteRecordBuilder.delete();
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, delete);
		}
		return false;
	}

}
