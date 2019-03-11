package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.WorkorderItemsAction;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetDeleteWorkorderItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<WorkorderItemContext> deletedWorkorderItems = (List<WorkorderItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if(deletedWorkorderItems!=null && !deletedWorkorderItems.isEmpty()) {
			for(WorkorderItemContext workorderItem : deletedWorkorderItems) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
				List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
				
				SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
						.select(fields).table(module.getTableName()).moduleName(module.getName())
						.beanClass(PurchasedItemContext.class).andCustomWhere(module.getTableName() + ".ID = ?", workorderItem.getPurchasedItem().getId());

				List<PurchasedItemContext> purchasedItems = selectBuilder.get();

				if (purchasedItems != null && !purchasedItems.isEmpty()) {
					PurchasedItemContext purchasedItem = purchasedItems.get(0);
					purchasedItem.setIsUsed(false);
					updatePurchasedItem(purchasedItem);
				}
			}
		}
		
		return false;
	}
	
	private void updatePurchasedItem(PurchasedItemContext purchasedItem) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		UpdateRecordBuilder<PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
		updateBuilder.update(purchasedItem);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
