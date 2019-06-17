package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateUsedQuanityForPoServiceItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PurchaseOrderLineItemContext> poLineItems = (List<PurchaseOrderLineItemContext>)context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		// TODO Auto-generated method stub
		if (CollectionUtils.isNotEmpty(poLineItems)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule poLineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			
			for (PurchaseOrderLineItemContext lItem : poLineItems) {
			    if((lItem.getQuantityUsed() + lItem.getQuantity()) <= lItem.getQuantityReceived()) {
					Map<String, Object> updateMap = new HashMap<>();
					FacilioField quantityUsedField = modBean.getField("quantityUsed", poLineItemModule.getName());
					updateMap.put("quantityUsed", lItem.getQuantityUsed() + lItem.getQuantity());
					List<FacilioField> updatedfields = new ArrayList<FacilioField>();
					updatedfields.add(quantityUsedField);
					UpdateRecordBuilder<PurchaseOrderLineItemContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderLineItemContext>()
									.module(poLineItemModule)
									.fields(updatedfields)
									.andCondition(CriteriaAPI.getIdCondition(lItem.getId(),poLineItemModule));
				     updateBuilder.updateViaMap(updateMap);
			
			    }
			    else {
			    	throw new IllegalArgumentException("Quantity required is greater than the received quantity available");
			    }
			
			}
		}
		
		return false;
	}
	
	
}
