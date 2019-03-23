package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReceivablesListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			SelectRecordsBuilder<ReceivableContext> builder = new SelectRecordsBuilder<ReceivableContext>()
					.module(module)
					.select(fields)
					.beanClass(ReceivableContext.class);
		
			Long poId = (Long)context.get(FacilioConstants.ContextNames.PO_ID);
			Long receivableId = (Long)context.get(FacilioConstants.ContextNames.ID);
			
			if(poId != null) {
				builder.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(poId),NumberOperators.EQUALS ));
			}
			if(receivableId != null) {
				builder.andCondition(CriteriaAPI.getIdCondition(receivableId, module));
			}
			
			List<ReceivableContext> list = builder.get();
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		}
		return false;
	}

}
