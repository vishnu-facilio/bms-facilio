package com.facilio.relation.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationRequestContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VirtualRelationAction extends FacilioAction {
    @Getter
    private RelationRequestContext relation;
    private String moduleName;
    private int relationType = -1;
    public String addOrUpdateVirtualRelation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateVirtualRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, relation);
        chain.execute();
        setResult(FacilioConstants.ContextNames.RESULT, SUCCESS);
        return SUCCESS;
    }
    public String fetchFilteredRelationList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getFetchFilteredRelationListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.Relationship.RELATION_TYPE, relationType);
        chain.execute();
        setResult(FacilioConstants.ContextNames.RELATION_LIST, context.get(FacilioConstants.ContextNames.RELATION_LIST));
        return SUCCESS;
    }
}
