package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReceiptsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long receivableId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (StringUtils.isNotEmpty(moduleName) && receivableId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			
			SelectRecordsBuilder<ReceiptContext> builder = new SelectRecordsBuilder<ReceiptContext>()
					.module(module)
					.select(fields)
					.andCondition(CriteriaAPI.getCondition("RECEIVABLE_ID", "receivableId", String.valueOf(receivableId), NumberOperators.EQUALS));
			List<ReceiptContext> list = builder.get();
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		}
		return false;
	}

}
