package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class SLAAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private SLAWorkflowRuleContext sla;

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getAllSLA() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST));
        return SUCCESS;
    }

    public String addOrUpdateSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_RULE_MODULE, sla);

        chain.execute();
        return SUCCESS;
    }

    public String getSLA() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_RULE_MODULE, context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE));
        return SUCCESS;
    }

    private SLAEntityContext slaEntity;
    public SLAEntityContext getSlaEntity() {
        return slaEntity;
    }
    public void setSlaEntity(SLAEntityContext slaEntity) {
        this.slaEntity = slaEntity;
    }

    public String addOrUpdateSLAEntity() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA_ENTITY, slaEntity);
        chain.execute();

        return SUCCESS;
    }

    public String listSLAEntity() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA_ENTITY_LIST, context.get(FacilioConstants.ContextNames.SLA_ENTITY_LIST));

        return SUCCESS;
    }

    public String deleteSLAEntity() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteSLAEntityChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        return SUCCESS;
    }
}
