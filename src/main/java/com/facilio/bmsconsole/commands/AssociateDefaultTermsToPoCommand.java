package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;

public class AssociateDefaultTermsToPoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean eventType = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_EDIT, false);
		if(!eventType) {
			PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
			List<TermsAndConditionContext> terms = PurchaseOrderAPI.fetchPoDefaultTerms();
			if(CollectionUtils.isNotEmpty(terms)) {
				List<PoAssociatedTermsContext> poAssociatedTerms = new ArrayList<PoAssociatedTermsContext>();
				for(TermsAndConditionContext term : terms) {
					PoAssociatedTermsContext associatedTerm = new PoAssociatedTermsContext();
					associatedTerm.setPoId(purchaseOrderContext.getId());
					associatedTerm.setTerms(term);
					poAssociatedTerms.add(associatedTerm);
				}
				PurchaseOrderAPI.updateTermsAssociated(purchaseOrderContext.getId(), poAssociatedTerms );
			}
		}
		return false;
	}

	
}
