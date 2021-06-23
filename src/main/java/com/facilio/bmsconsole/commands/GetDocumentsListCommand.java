package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DocumentContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;

public class GetDocumentsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long parentId = (long)context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if(parentId <= 0) {
			throw new IllegalArgumentException("ParentId cannot be null");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		boolean fetchCount = (boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		List<FacilioField> fields = null;
		FacilioField parentField = modBean.getField("parentId", moduleName);
		if(!fetchCount) {
			fields = modBean.getAllFields(moduleName);
		}
		
		SelectRecordsBuilder<DocumentContext> builder = new SelectRecordsBuilder<DocumentContext>()
				.module(module)
				.beanClass(DocumentContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition(parentField.getColumnName(), parentField.getName(), String.valueOf(parentId),NumberOperators.EQUALS));
				;
		if(fetchCount) {
			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
		}
		
		
		List<DocumentContext> list = builder.get();
		if (fetchCount) {
			if (CollectionUtils.isNotEmpty(list)) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, list.get(0).getId());
			}
		}
		else {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		}


		return false;
	}

}
