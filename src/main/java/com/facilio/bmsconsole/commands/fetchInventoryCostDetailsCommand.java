package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.InventryContext.CostType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class fetchInventoryCostDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			InventoryCostContext ic = (InventoryCostContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (ic != null && ic.getId() > 0) {
				if (ic.getInventory().getId() != -1) {
					InventryContext inventry = getInventory(ic.getInventory().getId());
					ic.setInventory(inventry);
				}
			}
			context.put(FacilioConstants.ContextNames.INVENTORY_COST, ic);
		}
		return false;
	}
	
	public static InventryContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);
		
		SelectRecordsBuilder<InventryContext> selectBuilder = new SelectRecordsBuilder<InventryContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(InventryContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<InventryContext> inventories = selectBuilder.get();
		
		if(inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}

}
