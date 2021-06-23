package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateReceivablesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
				saveRecords(true, saveReceivables, module, fields);
			}
		}
		return false;
	}

	private void saveRecords(boolean isLocalIdNeeded, List<ReceivableContext> receivableContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
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
