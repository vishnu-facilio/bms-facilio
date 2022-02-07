package com.facilio.emailtemplate.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;

public class EMailTemplateAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.EMAIL_STRUCTURES, context.get(FacilioConstants.ContextNames.EMAIL_STRUCTURES));
        return SUCCESS;
    }

    private EMailStructure emailStructure;
    public EMailStructure getEmailStructure() {
        return emailStructure;
    }
    public void setEmailStructure(EMailStructure emailStructure) {
        this.emailStructure = emailStructure;
    }

    public String addOrUpdate() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, emailStructure);

        chain.execute();
        setResult(FacilioConstants.ContextNames.EMAIL_STRUCTURE, context.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE));
        return SUCCESS;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String viewEmailStructure() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getEmailStructureChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.EMAIL_STRUCTURE, context.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE));

        return SUCCESS;
    }

    public String deleteEmailStructure() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);

        chain.execute();
        setResult("message", "Email Template is deleted successfully");
        return SUCCESS;
    }
}
