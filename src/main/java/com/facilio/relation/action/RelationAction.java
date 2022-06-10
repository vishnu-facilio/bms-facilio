package com.facilio.relation.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationRequestContext;

public class RelationAction extends FacilioAction {

    private RelationRequestContext relation;
    public RelationRequestContext getRelation() {
        return relation;
    }
    public void setRelation(RelationRequestContext relation) {
        this.relation = relation;
    }

    public String addOrUpdateRelation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RELATION, relation);

        chain.execute();
        setResult(FacilioConstants.ContextNames.RELATION, context.get(FacilioConstants.ContextNames.RELATION));
        return SUCCESS;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String viewRelation() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getViewRelationChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.RELATION, context.get(FacilioConstants.ContextNames.RELATION));
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getRelationListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.RELATION_LIST, context.get(FacilioConstants.ContextNames.RELATION_LIST));
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);

        chain.execute();
        setResult(FacilioConstants.ContextNames.MESSAGE, "Relation deleted successfully");
        return SUCCESS;
    }
}
