package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AssociateTermsToPOCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<V3PoAssociatedTermsContext> terms = (List<V3PoAssociatedTermsContext>)context.get(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		V3PurchaseOrderContext po = new V3PurchaseOrderContext();
		po.setId(recordId);
		if (CollectionUtils.isNotEmpty(terms)) {
			for (V3PoAssociatedTermsContext poterm: terms) {
				poterm.setPurchaseOrder(po);
			}
		}
		PurchaseOrderAPI.updateTermsAssociatedV3(terms);
			
		return false;
		
	}

}
