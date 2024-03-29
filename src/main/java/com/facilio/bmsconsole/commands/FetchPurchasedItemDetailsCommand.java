package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchPurchasedItemDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			PurchasedItemContext ic = (PurchasedItemContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (ic != null && ic.getId() > 0) {
				if (ic.getItem().getId() != -1) {
					ItemContext inventry = getInventory(ic.getItem().getId());
					ic.setItem(inventry);
				}
			}
			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, ic);
		}
		return false;
	}
	
	public static ItemContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(ItemContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<ItemContext> inventories = selectBuilder.get();
		
		if(inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}

}
