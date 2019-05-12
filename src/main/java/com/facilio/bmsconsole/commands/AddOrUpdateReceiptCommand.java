package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddOrUpdateReceiptCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<ReceiptContext> receipts = (List<ReceiptContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Set<Long> receivableIds = new HashSet<>();
		Set<Long> lineitemIds = new HashSet<>();
		if (!CollectionUtils.isEmpty(receipts)  && StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			List<ReceiptContext> saveReceipts = new ArrayList<ReceiptContext>();
			List<ReceiptContext> updateReceipts = new ArrayList<ReceiptContext>();
			for (ReceiptContext receiptContext : receipts) {
				receivableIds.add(receiptContext.getReceivableId());
				lineitemIds.add(receiptContext.getLineItem().getId());
				if(receiptContext.getReceiptTime() == -1) {
					receiptContext.setReceiptTime(System.currentTimeMillis());
				}
			
				if (receiptContext.getId() > 0) {
					updateReceipts.add(receiptContext);
				} else {
					saveReceipts.add(receiptContext);
				}
			}
			updateRecords(updateReceipts, module, fields);
			saveRecords(true, saveReceipts, module, fields);
			context.put(FacilioConstants.ContextNames.RECEIPTS, saveReceipts.size() > 0 ? saveReceipts : updateReceipts);

		}
		context.put(FacilioConstants.ContextNames.RECEIVABLE_ID, receivableIds);
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID, lineitemIds);
	
		return false;
	}

	private void saveRecords(boolean isLocalIdNeeded, List<ReceiptContext> receiptContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(receiptContext);
		insertRecordBuilder.save();
	}
	
	public void updateRecords(List<ReceiptContext> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		for (ReceiptContext data : list) {
			UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
			updateRecordBuilder.update(data);
		}
	}

}
