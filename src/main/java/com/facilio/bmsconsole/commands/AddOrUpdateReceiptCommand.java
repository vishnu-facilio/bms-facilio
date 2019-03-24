package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
				lineitemIds.add(receiptContext.getLineItemId());
				if (receiptContext.getId() > 0) {
					updateReceipts.add(receiptContext);
				} else {
					saveReceipts.add(receiptContext);
				}
			}
			updateRecords(updateReceipts, module, fields);
			saveRecords(saveReceipts, module, fields);
		}
		context.put(FacilioConstants.ContextNames.RECEIVABLE_ID, receivableIds);
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID, lineitemIds);
		return false;
	}

	private void saveRecords(List<ReceiptContext> receiptContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
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
