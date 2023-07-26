
package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.emailtemplate.action.EMailTemplateAction;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class EmailTemplatePackageBeanImpl implements PackageBean<EMailStructure> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getEmailStructureIdVsModuleId();
    }

    @Override
    public Map<Long, EMailStructure> fetchComponents(List<Long> ids) throws Exception {
        List<EMailStructure> eMailStructures  = getEmailStructureForIds(ids);
        Map<Long, EMailStructure> eMailStructureIdVsEMailStructureMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(eMailStructures)) {
            eMailStructures.forEach(eMailStructure -> eMailStructureIdVsEMailStructureMap.put(eMailStructure.getId(), eMailStructure));
        }
        return eMailStructureIdVsEMailStructureMap;
    }

    @Override
    public void convertToXMLComponent(EMailStructure component, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        element.element(PackageConstants.EmailConstants.NAME).text(component.getName());
        element.element(PackageConstants.EmailConstants.SUBJECT).text(component.getSubject());
        element.element(PackageConstants.EmailConstants.MESSAGE).cData(component.getMessage());
        element.element(PackageConstants.EmailConstants.HTML).text(String.valueOf(component.getHtml()));
        element.element(PackageConstants.EmailConstants.DRAFT).text(String.valueOf(component.getDraft()));

        if (CollectionUtils.isNotEmpty(Collections.singleton(component.getModuleId()))) {
            FacilioModule module = moduleBean.getModule(component.getModuleId());
            element.element(PackageConstants.MODULENAME).text(module.getName());
        }

        XMLBuilder workFlowElementsList = element.element(PackageConstants.EmailConstants.WORKFLOW);
        if (component.getWorkflow()!=null) {
            XMLBuilder parametersElement = workFlowElementsList.element(PackageConstants.WorkFlowConstants.PARAMETERS);
            if (CollectionUtils.isNotEmpty(component.getWorkflow().getParameters())) {
                XMLBuilder parameterElement = parametersElement.element(PackageConstants.WorkFlowConstants.PARAMETER);
                for (ParameterContext parameter : component.getWorkflow().getParameters()) {
                    parameterElement.element(PackageConstants.NAME).text(parameter.getName());
                    parameterElement.element(PackageConstants.TYPE_STRING).text(parameter.getTypeString());
                    parametersElement.addElement(parameterElement);
                }
            }

            XMLBuilder expressionsElement = workFlowElementsList.element(PackageConstants.WorkFlowConstants.EXPRESSIONS);
            if (CollectionUtils.isNotEmpty(component.getWorkflow().getExpressions())) {
                XMLBuilder expressionElement = expressionsElement.element(PackageConstants.WorkFlowConstants.EXPRESSION);
                for (WorkflowExpression expression : component.getWorkflow().getExpressions()) {
                    ExpressionContext expressionContext = (ExpressionContext) expression;
                    expressionElement.element(PackageConstants.NAME).text(expressionContext.getName());
                    expressionElement.element(PackageConstants.CONSTANT).text((String) expressionContext.getConstant());
                    expressionsElement.addElement(expressionElement);
                }
            }
        }
        XMLBuilder userWorkflowElements = element.element(PackageConstants.EmailConstants.USER_WORKFLOW);
        if (component.getUserWorkflow()!=null) {
            if (CollectionUtils.isNotEmpty(Collections.singleton(component.getUserWorkflow().isV2Script()))) {
                userWorkflowElements.element(PackageConstants.EmailConstants.IS_V2_SCRIPT).text(String.valueOf(component.getUserWorkflow().isV2Script()));
            }
            if (CollectionUtils.isNotEmpty(Collections.singleton(component.getUserWorkflow().getWorkflowV2String()))) {
                userWorkflowElements.element(PackageConstants.EmailConstants.WORKFLOW_V2_STRING).cData(component.getUserWorkflow().getWorkflowV2String());
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<EMailStructure> eMailTemplates = FieldUtil.getAsBeanListFromMapList(getEmailTemplatesProps(), EMailStructure.class);
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder emailElement = idVsData.getValue();
            String moduleName = new String();
            EMailStructure eMailStructure = constructEmailFromBuilder(emailElement);
            long eMailStructureModuleId = eMailStructure.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(eMailStructureModuleId))) {
                FacilioModule module = moduleBean.getModule(eMailStructureModuleId);
                moduleName = module.getName();
            }
            boolean containsName = false;
            Long id = -1L;
            for(EMailStructure eMailTemplate : eMailTemplates) {
                if (eMailStructure.getName().equals(eMailTemplate.getName())) {
                    id = eMailTemplate.getId();
                    containsName = true;
                    break;
                }
            }
            if (!containsName) {
                long emailStructureId = addEMailStructure(moduleName, eMailStructure);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), emailStructureId);
            }else{
                eMailStructure.setId(id);
                updateEmailStructure(moduleName,eMailStructure);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    private List<Map<String, Object>> getEmailTemplatesProps() throws Exception {
        FacilioModule eMailStructureModule = ModuleFactory.getEMailStructureModule();
        FacilioModule templateModule = ModuleFactory.getTemplatesModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("id", "ID", eMailStructureModule));
            add(FieldFactory.getStringField("name", "NAME", templateModule));
        }};
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(eMailStructureModule.getTableName())
                .select(selectableFields)
                .innerJoin(templateModule.getTableName()).on("EMail_Structure.ID = Templates.ID");
        List<Map<String, Object>> props = builder.get();
        return props;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long emailStructureId = idVsData.getKey();
            XMLBuilder emailElement = idVsData.getValue();
            EMailStructure eMailStructure = constructEmailFromBuilder(emailElement);
            eMailStructure.setId(emailStructureId);
            String moduleName = new String();
            long eMailStructureModuleId = eMailStructure.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(eMailStructureModuleId))) {
                FacilioModule module = moduleBean.getModule(eMailStructureModuleId);
                moduleName = module.getName();
            }
            updateEmailStructure(moduleName, eMailStructure);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            FacilioChain chain = TransactionChainFactory.getDeleteEmailStructureChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.ID, id);
            chain.execute();
        }
    }

    public Map<Long, Long> getEmailStructureIdVsModuleId() throws Exception {
        Map<Long, Long> EMailStructureIdVsModuleId = new HashMap<>();
        FacilioModule emailStructureModule = ModuleFactory.getEMailStructureModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("eMailStructureId","ID", emailStructureModule ));
            add(FieldFactory.getNumberField("moduleId", "MODULE_ID", emailStructureModule));
        }};

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(emailStructureModule.getTableName());

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                EMailStructureIdVsModuleId.put((Long) prop.get("eMailStructureId"), (Long) prop.get("moduleId"));
            }

        }
        return EMailStructureIdVsModuleId;
    }

    public static EMailStructure constructEmailFromBuilder(XMLBuilder EmailElement) throws Exception {
        XMLBuilder workFlowElementLists = EmailElement.getElement(PackageConstants.EmailConstants.WORKFLOW);
        XMLBuilder userWorkFlowElementLists = EmailElement.getElement(PackageConstants.EmailConstants.USER_WORKFLOW);
        String name = EmailElement.getElement(PackageConstants.EmailConstants.NAME).getText();
        String subject = EmailElement.getElement(PackageConstants.EmailConstants.SUBJECT).getText();
        String message = EmailElement.getElement(PackageConstants.EmailConstants.MESSAGE).getCData();
        boolean html = Boolean.parseBoolean(EmailElement.getElement(PackageConstants.EmailConstants.HTML).getText());
        boolean draft = Boolean.parseBoolean(EmailElement.getElement(PackageConstants.EmailConstants.DRAFT).getText());
        EMailStructure eMailStructure = new EMailStructure();
        eMailStructure.setName(name);
        eMailStructure.setSubject(subject);
        eMailStructure.setMessage(message);
        eMailStructure.setHtml(html);
        eMailStructure.setDraft(draft);

        WorkflowContext workflowContext = new WorkflowContext();

        Map<String, Object> parameterEmail = new HashMap<>();
        List<Object> parameterKeyEmail = new ArrayList<>();

        if(workFlowElementLists!=null) {
            List<XMLBuilder> parametersLists =  workFlowElementLists.getElementList(PackageConstants.WorkFlowConstants.PARAMETERS);
            List<XMLBuilder> expressionsLists =  workFlowElementLists.getElementList(PackageConstants.WorkFlowConstants.EXPRESSIONS);
            if(parametersLists!=null) {
                for (XMLBuilder paramList : parametersLists) {
                    XMLBuilder param = paramList.getElement(PackageConstants.WorkFlowConstants.PARAMETER);
                    String paramName = param.getElement(PackageConstants.NAME).getText();
                    String paramTypeString = param.getElement(PackageConstants.TYPE_STRING).getText();
                    parameterEmail.put(PackageConstants.NAME, paramName);
                    parameterEmail.put(PackageConstants.TYPE_STRING, paramTypeString);
                    parameterKeyEmail.add(parameterEmail);
                }
            }

            JSONArray expressionKeyEmail = new JSONArray();
            JSONObject expEmail = new JSONObject();
            if(expressionsLists!=null) {
                for (XMLBuilder expList : expressionsLists) {
                    XMLBuilder exp = expList.getElement(PackageConstants.WorkFlowConstants.EXPRESSION);
                    String expressionName = exp.getElement(PackageConstants.NAME).getText();
                    String expressionConstant = exp.getElement(PackageConstants.CONSTANT).getText();
                    expEmail.put(PackageConstants.NAME, expressionName);
                    expEmail.put(PackageConstants.CONSTANT, expressionConstant);
                    expressionKeyEmail.add(expEmail);
                }
            }

            workflowContext.setParams(parameterKeyEmail);
            workflowContext.setExpressions(expressionKeyEmail);
            eMailStructure.setWorkflow(workflowContext);
        }

        WorkflowContext userWorkflowContext = new WorkflowContext();

        if(userWorkFlowElementLists!=null) {
            XMLBuilder isV2ScriptElement = userWorkFlowElementLists.getElement(PackageConstants.EmailConstants.IS_V2_SCRIPT);
            XMLBuilder workflowV2StringElement = userWorkFlowElementLists.getElement(PackageConstants.EmailConstants.WORKFLOW_V2_STRING);
            if (isV2ScriptElement != null) {
                boolean isv2script = Boolean.parseBoolean(userWorkFlowElementLists.getElement(PackageConstants.EmailConstants.IS_V2_SCRIPT).getText());
                userWorkflowContext.setIsV2Script(isv2script);
                eMailStructure.setUserWorkflow(userWorkflowContext);
            }
            if (workflowV2StringElement != null) {
                String workflowV2string = userWorkFlowElementLists.getElement(PackageConstants.EmailConstants.WORKFLOW_V2_STRING).getCData();
                userWorkflowContext.setWorkflowV2String(workflowV2string);
                eMailStructure.setUserWorkflow(userWorkflowContext);
            }
        }
        XMLBuilder modulesBuilder = EmailElement.getElement(PackageConstants.MODULENAME);
        if (modulesBuilder != null) {
            ModuleBean moduleBean = Constants.getModBean();
            String moduleName = modulesBuilder.getText();
            FacilioModule module = moduleBean.getModule(moduleName);
            eMailStructure.setModuleId(module.getModuleId());
        }
        return eMailStructure;

    }
    public List<EMailStructure> getEmailStructureForIds(Collection<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getEMailStructureModule().getTableName())
                .select(FieldFactory.getEMailStructureFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getEMailStructureModule()));

        List<EMailStructure> eMailStructures = FieldUtil.getAsBeanListFromMapList(builder.get(), EMailStructure.class);
        if (CollectionUtils.isNotEmpty(eMailStructures)) {
            for (EMailStructure eMailStructure:eMailStructures){
                EMailStructure emailTemplate = ( EMailStructure) TemplateAPI.getTemplate(eMailStructure.getId());
                eMailStructure.setName(emailTemplate.getName());
                eMailStructure.setMessage(emailTemplate.getMessage());
                eMailStructure.setWorkflow(emailTemplate.getWorkflow());

                eMailStructure.setUserWorkflow(emailTemplate.getUserWorkflow());
            }
        }
        return eMailStructures;
    }
    private long addEMailStructure(String module, EMailStructure eMailStructure) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext context = chain.getContext();
        EMailTemplateAction eMailTemplateAction = new EMailTemplateAction();
        List<Map<String, Object>> attachmentList = new ArrayList<>();
        eMailTemplateAction.setAttachmentList(attachmentList);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module);
        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, eMailStructure);
        context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, eMailTemplateAction.getAttachmentList());
        chain.execute();
        EMailStructure emailStructureContext1 = (EMailStructure) context.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE);
        long emailStructureContextId = emailStructureContext1.getId();

        return emailStructureContextId;
    }
    public void updateEmailStructure(String module, EMailStructure eMailStructure) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext context = chain.getContext();
        EMailTemplateAction eMailTemplateAction = new EMailTemplateAction();
        List<Map<String, Object>> attachmentList = new ArrayList<>();
        eMailTemplateAction.setAttachmentList(attachmentList);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module);
        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, eMailStructure);
        context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, eMailTemplateAction.getAttachmentList());
        chain.execute();
    }
}
