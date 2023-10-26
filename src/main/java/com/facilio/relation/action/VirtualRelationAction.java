package com.facilio.relation.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationRequestContext;

public class VirtualRelationAction extends FacilioAction {
    public RelationRequestContext getRelation() {
        return relation;
    }

    public void setRelation(RelationRequestContext relation) {
        this.relation = relation;
    }

    private RelationRequestContext relation;
    public String addVirtualRelation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddVirtualRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, relation);
        chain.execute();
        setResult(FacilioConstants.ContextNames.RESULT, SUCCESS);
        return SUCCESS;
    }
}
