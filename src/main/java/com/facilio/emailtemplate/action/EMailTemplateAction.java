package com.facilio.emailtemplate.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class EMailTemplateAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private Boolean fetchAll;

    public Boolean getFetchAll() {
        return fetchAll;
    }

    public void setFetchAll(Boolean fetchAll) {
        this.fetchAll = fetchAll;
    }
    @Getter @Setter
    private long templateId = -1;
    @Getter @Setter
    private long recordId = -1;
    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.FETCH_ALL,fetchAll);

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

    private List<Map<String, Object>> attachmentList;
    public List<Map<String, Object>> getAttachmentList() {
        return attachmentList;
    }
    public void setAttachmentList(List<Map<String, Object>> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String addOrUpdate() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, emailStructure);
        context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachmentList);

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

    public String publishEmailStructure() throws Exception{
        FacilioChain chain = TransactionChainFactory.getPublishEmailStructureChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);

        chain.execute();
        setResult(FacilioConstants.ContextNames.PUBLISH_SUCCESS,"Email Template published successfully");
        return SUCCESS;
    }

    public String testMailTemplate() throws Exception{
        FacilioChain chain= TransactionChainFactory.getTestEmailChain();
        FacilioContext context=chain.getContext();
        context.put(FacilioConstants.ContextNames.TEMPLATE_ID,templateId);
        context.put(FacilioConstants.ContextNames.RECORD_ID,recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();
        return SUCCESS;

    }
}
