package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class RollUpReceivableDeliveryTimeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(purchaseOrderContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
			FacilioField requiredtimeField = modBean.getField("requiredTime", module.getName());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("requiredTime", purchaseOrderContext.getRequiredTime());
			PurchaseOrderAPI.updatePoReceivable(purchaseOrderContext.getId(), Collections.singletonList(requiredtimeField), map);
		}
		return false;
	}

}
