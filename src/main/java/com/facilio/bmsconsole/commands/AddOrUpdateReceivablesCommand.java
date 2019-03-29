package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddOrUpdateReceivablesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<ReceivableContext> receivables = (List<ReceivableContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (!CollectionUtils.isEmpty(receivables)  && StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			List<ReceivableContext> saveReceivables = new ArrayList<ReceivableContext>();
			List<ReceivableContext> updateReceivables = new ArrayList<ReceivableContext>();
			
			for (ReceivableContext receivableContext : receivables) {
				if (receivableContext.getId() > 0) {
					updateReceivables.add(receivableContext);
				} else {
					saveReceivables.add(receivableContext);
				}
			}
			if(!CollectionUtils.isEmpty(updateReceivables)) {
				updateRecords(updateReceivables, module, fields);
			}
			if(!CollectionUtils.isEmpty(saveReceivables)) {
				saveRecords(saveReceivables, module, fields);
			}
		}
		return false;
	}

	private void saveRecords(List<ReceivableContext> receivableContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		insertRecordBuilder.addRecords(receivableContext);
		insertRecordBuilder.save();
	}
	
	public void updateRecords(List<ReceivableContext> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		for (ReceivableContext data : list) {
			UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
			updateRecordBuilder.update(data);
		}
	}

}
