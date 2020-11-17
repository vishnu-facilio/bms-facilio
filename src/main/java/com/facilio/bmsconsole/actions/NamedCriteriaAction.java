package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.manager.NamedCriteria;

public class NamedCriteriaAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    public String getAllNamedCriteria() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA_LIST, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA_LIST));
        return SUCCESS;
    }

    public String addOrUpdateNamedCriteria() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddNamedCriteriaChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.NAMED_CRITERIA, namedCriteria);

        chain.execute();
        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA));

        return SUCCESS;
    }

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String deleteNamedCriteria() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        return SUCCESS;
    }

    private long recordId = -1;
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String evaluateCriteria() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getEvaluateNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA_RESULT, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA_RESULT));

        return SUCCESS;
    }
}
