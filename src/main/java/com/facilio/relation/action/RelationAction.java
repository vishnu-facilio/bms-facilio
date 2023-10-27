package com.facilio.relation.action;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        JSONObject pagination = new JSONObject();
        pagination.put("page", getPage());
        pagination.put("perPage", getPerPage());
        if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
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

    private long fromModuleId = -1;
    public long getFromModuleId() {
        return fromModuleId;
    }
    public void setFromModuleId(long fromModuleId) {
        this.fromModuleId = fromModuleId;
    }

    private long toModuleId = -1;
    public long getToModuleId() {
        return toModuleId;
    }
    public void setToModuleId(long toModuleId) {
        this.toModuleId = toModuleId;
    }

    private int relationType = -1;
    public int getRelationType() {
        return relationType;
    }
    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }

    private int relationCategory = -1;
    public int getRelationCategory() {
        return relationCategory;
    }
    public void setRelationCategory(int relationCategory) {
        this.relationCategory = relationCategory;
    }

    public String fetchRelations() throws Exception {
        FacilioChain fetchExistingRelationships = TransactionChainFactoryV3.fetchExistingRelationsChain();
        FacilioContext context = fetchExistingRelationships.getContext();
        context.put("fromModuleId", fromModuleId);
        context.put("toModuleId", toModuleId);
        context.put("relationType", relationType);
        context.put("relationCategory", relationCategory);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        fetchExistingRelationships.execute();
        List<RelationRequestContext> relationList = new ArrayList<>();
        if(context.get(FacilioConstants.ContextNames.RELATION_LIST) != null) {
            relationList = (List<RelationRequestContext>) context.get(FacilioConstants.ContextNames.RELATION_LIST);
        }
        setResult(FacilioConstants.ContextNames.RELATION_LIST, relationList);
        return SUCCESS;
    }

    public String fetchMeterRelations() throws Exception {
        FacilioChain fetchMeterRelationships = TransactionChainFactoryV3.fetchMeterRelationsChain();
        FacilioContext context = fetchMeterRelationships.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        JSONObject pagination = new JSONObject();
        pagination.put("page", getPage());
        pagination.put("perPage", getPerPage());
        if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        fetchMeterRelationships.execute();

        setResult(FacilioConstants.ContextNames.RELATION_LIST, context.get(FacilioConstants.ContextNames.RELATION_LIST));
        return SUCCESS;
    }
}
