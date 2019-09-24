package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class AssociateDefaultTermsToPoCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Integer eventType = (Integer)context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		if(eventType != null && eventType == EventType.CREATE.getValue()) {
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
