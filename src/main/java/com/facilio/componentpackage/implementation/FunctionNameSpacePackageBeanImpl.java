package com.facilio.componentpackage.implementation;

import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.workflowv2.util.UserFunctionAPI;
import org.apache.commons.collections4.MapUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.bean.ScriptBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionNameSpacePackageBeanImpl implements PackageBean<WorkflowNamespaceContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getNameSpaceIdMap();
    }

    @Override
    public Map<Long, WorkflowNamespaceContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, WorkflowNamespaceContext> nameSpaceIdVsNameSpace = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            Map<Long, WorkflowNamespaceContext> nameSpacesForIds = UserFunctionAPI.getNameSpacesForIds(idsSubList);
            if (MapUtils.isNotEmpty(nameSpacesForIds)) {
                nameSpaceIdVsNameSpace.putAll(nameSpacesForIds);
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return nameSpaceIdVsNameSpace;
    }

    @Override
    public void convertToXMLComponent(WorkflowNamespaceContext nameSpace, XMLBuilder element) throws Exception {
        // TODO - Handle SOURCE_BUNDLE
        element.element(PackageConstants.NAME).text(nameSpace.getLinkName());
        element.element(PackageConstants.DISPLAY_NAME).text(nameSpace.getName());
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
//        UserFunctionAPI.getNameSpacesForLinkName() can be used to validate
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

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();

            WorkflowNamespaceContext workflowNamespaceContext = constructWorkflowNamespaceContextFromBuilder(element);
            workflowNamespaceContext = scriptBean.addNameSpace(workflowNamespaceContext);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), workflowNamespaceContext.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ScriptBean scriptBean = Constants.getScriptBean();

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder element = idVsData.getValue();

            WorkflowNamespaceContext workflowNamespaceContext = constructWorkflowNamespaceContextFromBuilder(element);
            scriptBean.updateNameSpace(workflowNamespaceContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            Constants.getScriptBean().deleteNameSpacesForIds(ids);
        }
    }

    private Map<Long, Long> getNameSpaceIdMap() throws Exception {
        Map<Long, Long> functionNameSpaceIdMap = new HashMap<>();
        FacilioModule workflowNamespaceModule = ModuleFactory.getWorkflowNamespaceModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(workflowNamespaceModule));
        }};

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(workflowNamespaceModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

        List<Map<String, Object>> propsList = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                functionNameSpaceIdMap.put((Long) prop.get("id"), -1L);
            }
        }
        return functionNameSpaceIdMap;
    }

    private WorkflowNamespaceContext constructWorkflowNamespaceContextFromBuilder(XMLBuilder element) {
        String linkName = element.getElement(PackageConstants.NAME).getText();
        String displayName = element.getElement(PackageConstants.DISPLAY_NAME).getText();

        WorkflowNamespaceContext workflowNamespaceContext = new WorkflowNamespaceContext();
        workflowNamespaceContext.setLinkName(linkName);
        workflowNamespaceContext.setName(displayName);

        return workflowNamespaceContext;
    }
}
