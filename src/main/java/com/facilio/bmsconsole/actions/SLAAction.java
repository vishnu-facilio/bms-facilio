package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SLAContext;
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

    private SLAContext sla;
    public SLAContext getSla() {
        return sla;
    }
    public void setSla(SLAContext sla) {
        this.sla = sla;
    }

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

        setResult(FacilioConstants.ContextNames.SLA_LIST, context.get(FacilioConstants.ContextNames.SLA_LIST));
        return SUCCESS;
    }

    public String addOrUpdateSLA() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SLA, sla);

        chain.execute();
        return SUCCESS;
    }

    public String getSLA() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSLAChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());

        chain.execute();

        setResult(FacilioConstants.ContextNames.SLA, context.get(FacilioConstants.ContextNames.SLA));
        return SUCCESS;
    }
}
