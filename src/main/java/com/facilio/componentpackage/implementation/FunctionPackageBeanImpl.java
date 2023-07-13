package com.facilio.componentpackage.implementation;

import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import org.apache.commons.collections4.MapUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.bean.ScriptBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.lang3.StringUtils;
import com.facilio.v3.context.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionPackageBeanImpl implements PackageBean<WorkflowUserFunctionContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getFunctionIdVsNameSpaceId();
    }

    @Override
    public Map<Long, WorkflowUserFunctionContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, WorkflowUserFunctionContext> functionIdVsFunctionMap = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            Map<Long, WorkflowUserFunctionContext> functionsForIds = UserFunctionAPI.getFunctionsForIds(idsSubList, false);
            if (MapUtils.isNotEmpty(functionsForIds)) {
                // Fetch NameSpace
                List<Long> nameSpaceIds = functionsForIds.values().stream().map(ScriptContext::getNameSpaceId)
                                            .filter(nameSpaceId -> nameSpaceId > 0).collect(Collectors.toList());
                Map<Long, WorkflowNamespaceContext> nameSpacesForIds = UserFunctionAPI.getNameSpacesForIds(nameSpaceIds);

                // Add NameSpaceLinkName to WorkflowUserFunctionContext
                if (MapUtils.isNotEmpty(nameSpacesForIds)) {
                    functionsForIds.values().forEach(workflowUserFunction -> {
                        long nameSpaceId = workflowUserFunction.getNameSpaceId();
                        if (nameSpacesForIds.containsKey(nameSpaceId)) {
                            workflowUserFunction.setNameSpaceName(nameSpacesForIds.get(nameSpaceId).getLinkName());
                        }
                    });
                }

                functionIdVsFunctionMap.putAll(functionsForIds);

            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return functionIdVsFunctionMap;
    }

    @Override
    public void convertToXMLComponent(WorkflowUserFunctionContext functionContext, XMLBuilder element) throws Exception {
        // TODO - Handle SOURCE_BUNDLE
        element.element(PackageConstants.NAME).text(functionContext.getLinkName());
        element.element(PackageConstants.DISPLAY_NAME).text(functionContext.getName());
        element.element(PackageConstants.FunctionConstants.NAMESPACE_LINK_NAME).text(functionContext.getNameSpaceName());
        element.element(PackageConstants.FunctionConstants.UI_MODE)
                .text(functionContext.getWorkflowUIMode() > -1 ? String.valueOf(functionContext.getWorkflowUIMode()) : String.valueOf(-1));
        element.element(PackageConstants.FunctionConstants.TYPE)
                .text(functionContext.getLogType() != null ? functionContext.getLogType().name() : null);
        element.element(PackageConstants.FunctionConstants.RETURN_TYPE)
                .text(functionContext.getReturnTypeEnum() != null ? functionContext.getReturnTypeEnum().name() : null);
        element.element(PackageConstants.FunctionConstants.IS_LOG_NEEDED).text(String.valueOf(functionContext.isLogNeeded()));
        element.element(PackageConstants.FunctionConstants.IS_V2).text(String.valueOf(functionContext.isV2Script()));
        element.element(PackageConstants.FunctionConstants.RUN_AS_ADMIN).text(String.valueOf(functionContext.isRunAsAdmin()));
        element.element(PackageConstants.FunctionConstants.WORKFLOW_STRING).cData(functionContext.getWorkflowV2String());
        element.element(PackageConstants.FunctionConstants.WORKFLOW_XML_STRING).cData(functionContext.getWorkflowString());
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
//        UserFunctionAPI.getFunctionsForLinkNames() can be used to validate
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
        ScriptBean scriptBean = Constants.getScriptBean();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, Long> linkNameVsNameSpaceId = getNameSpaceLinkNameVsId();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();

            WorkflowUserFunctionContext userFunction = constructFunctionFromBuilder(element, linkNameVsNameSpaceId);
            // create empty function & update function body on updateComponentFromXML
            constructEmptyWorkFlowStr(userFunction);
            FacilioContext context = scriptBean.addFunction(userFunction);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), userFunction.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents, boolean isReUpdate) throws Exception {
        ScriptBean scriptBean = Constants.getScriptBean();
        Map<String, Long> linkNameVsNameSpaceId = getNameSpaceLinkNameVsId();

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder element = idVsData.getValue();

            WorkflowUserFunctionContext userFunction = constructFunctionFromBuilder(element, linkNameVsNameSpaceId);
            userFunction.setId(idVsData.getKey());
            FacilioContext context = scriptBean.updateFunction(userFunction);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            Constants.getScriptBean().deleteFunctionsForIds(ids);
        }
    }

    private Map<Long, Long> getFunctionIdVsNameSpaceId() throws Exception {
        Map<Long, Long> functionIdVsNameSpaceIdMap = new HashMap<>();
        FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(module));
            add(FieldFactory.getNumberField("nameSpaceId", "NAMESPACE_ID", module));
        }};

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(module.getTableName())
                .innerJoin(ModuleFactory.getWorkflowModule().getTableName())
                .on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

        List<Map<String, Object>> propsList = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                Long nameSpaceId = prop.containsKey("nameSpaceId") ? (Long) prop.get("nameSpaceId") : -1;
                functionIdVsNameSpaceIdMap.put((Long) prop.get("id"), nameSpaceId);
            }
        }
        return functionIdVsNameSpaceIdMap;
    }

    private WorkflowUserFunctionContext constructFunctionFromBuilder(XMLBuilder element, Map<String, Long> linkNameVsNameSpace) {
        String linkName = element.getElement(PackageConstants.NAME).getText();
        String displayName = element.getElement(PackageConstants.DISPLAY_NAME).getText();
        String nameSpaceLinkName = element.getElement(PackageConstants.FunctionConstants.NAMESPACE_LINK_NAME).getText();
        int uiModeStr = Integer.parseInt(element.getElement(PackageConstants.FunctionConstants.UI_MODE).getText());
        String typeStr = element.getElement(PackageConstants.FunctionConstants.TYPE).getText();
        String returnTypeStr = element.getElement(PackageConstants.FunctionConstants.RETURN_TYPE).getText();
        boolean isLogRequired = Boolean.parseBoolean(element.getElement(PackageConstants.FunctionConstants.IS_LOG_NEEDED).getText());
        boolean isV2 = Boolean.parseBoolean(element.getElement(PackageConstants.FunctionConstants.IS_V2).getText());
        boolean runAsAdmin = Boolean.parseBoolean(element.getElement(PackageConstants.FunctionConstants.RUN_AS_ADMIN).getText());
        String workflowV2Str = element.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING).getCData();
        String workflowXMLStr = element.getElement(PackageConstants.FunctionConstants.WORKFLOW_XML_STRING).getCData();

        ScriptContext.WorkflowUIMode workflowUIMode = uiModeStr > 0 ? ScriptContext.WorkflowUIMode.valueOf(uiModeStr) : null;
        WorkflowFieldType returnType = StringUtils.isNotEmpty(returnTypeStr) ? WorkflowFieldType.valueOf(returnTypeStr) : null;
        WorkflowLogContext.WorkflowLogType workflowLogType = StringUtils.isNotEmpty(typeStr) ? WorkflowLogContext.WorkflowLogType.valueOf(typeStr) : null;
        long nameSpaceId = (StringUtils.isNotEmpty(nameSpaceLinkName) && linkNameVsNameSpace.containsKey(nameSpaceLinkName)) ? linkNameVsNameSpace.get(nameSpaceLinkName) : -1;

        WorkflowUserFunctionContext workflowUserFunctionContext = new WorkflowUserFunctionContext();
        workflowUserFunctionContext.setLinkName(linkName);
        workflowUserFunctionContext.setName(displayName);
        workflowUserFunctionContext.setIsV2Script(isV2);
        workflowUserFunctionContext.setRunAsAdmin(runAsAdmin);
        workflowUserFunctionContext.setLogType(workflowLogType);
        workflowUserFunctionContext.setLogNeeded(isLogRequired);
        workflowUserFunctionContext.setReturnTypeEnum(returnType);
        workflowUserFunctionContext.setWorkflowUIMode(workflowUIMode);
        workflowUserFunctionContext.setWorkflowString(workflowXMLStr);
        workflowUserFunctionContext.setWorkflowV2String(workflowV2Str);
        workflowUserFunctionContext.setNameSpaceName(nameSpaceLinkName);
        workflowUserFunctionContext.setNameSpaceId(nameSpaceId);

        return workflowUserFunctionContext;
    }

    private Map<String, Long> getNameSpaceLinkNameVsId() throws Exception {
        List<WorkflowNamespaceContext> allNameSpace = UserFunctionAPI.getAllNameSpace();

        Map<String, Long> linkNameVsNameSpaceId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allNameSpace)) {
            linkNameVsNameSpaceId = allNameSpace.stream().collect(Collectors.toMap(WorkflowNamespaceContext::getLinkName, WorkflowNamespaceContext::getId));
        }
        return linkNameVsNameSpaceId;
    }

    private void constructEmptyWorkFlowStr(WorkflowUserFunctionContext workflowUserFunctionContext) {
        StringBuilder resultWorkFlowString = new StringBuilder();
        String workflowV2String = workflowUserFunctionContext.getWorkflowV2String();
        WorkflowFieldType returnTypeEnum = workflowUserFunctionContext.getReturnTypeEnum();

        Pattern pattern = Pattern.compile("^[^{]*");
        Matcher matcher = pattern.matcher(workflowV2String);
        String functionCall = matcher.find() ? matcher.group().trim() : null;

        if (StringUtils.isNotEmpty(functionCall)) {
            resultWorkFlowString.append(functionCall);
            resultWorkFlowString.append(" {\n");
            if (returnTypeEnum != WorkflowFieldType.VOID) {
                resultWorkFlowString.append("return null;");
            }
            resultWorkFlowString.append("\n}");

            workflowUserFunctionContext.setWorkflowV2String(resultWorkFlowString.toString());
        }
    }
}
