package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GlobalVariablePackageBeanImpl implements PackageBean<GlobalVariableContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getGlobalVariableIdVsModuleId();
    }

    @Override
    public Map<Long, GlobalVariableContext> fetchComponents(List<Long> ids) throws Exception {
        List<GlobalVariableContext> globalVariables  = getGlobalVariableForIds(ids);
        Map<Long, GlobalVariableContext> globalVariableIdVsGlobalVariableMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(globalVariables)) {
            globalVariables.forEach(globalVariable -> globalVariableIdVsGlobalVariableMap.put(globalVariable.getId(), globalVariable));
        }
        return globalVariableIdVsGlobalVariableMap;
    }

    @Override
    public void convertToXMLComponent(GlobalVariableContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.GlobalVariableConstants.VARIABLE_NAME).text(component.getName());
        element.element(PackageConstants.GlobalVariableConstants.LINK_NAME).text(component.getLinkName());
        element.element(PackageConstants.GlobalVariableConstants.TYPE).text(String.valueOf(component.getType()));
        element.element(PackageConstants.GlobalVariableConstants.VALUE_STRING).text(component.getValueString());
        String groupLinkName = GlobalVariableUtil.getVariableGroup(component.getGroupId()).getLinkName();
        element.element(PackageConstants.GlobalVariableConstants.GROUP_LINK_NAME).text(groupLinkName);
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
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<GlobalVariableContext> globalVariables = GlobalVariableUtil.getAllGlobalVariables();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder globalVariableElement = idVsData.getValue();
            GlobalVariableContext globalVariable = constructGlobalVariableFromBuilder(globalVariableElement);
            boolean containsName = false;
            Long id = -1L;
            for(GlobalVariableContext variable : globalVariables) {
                if (variable.getLinkName().equals(globalVariable.getLinkName())) {
                    id = variable.getId();
                    containsName = true;
                    break;
                }
            }
            if (!containsName) {
                long globalVariableId = addGlobalVariable(globalVariable);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), globalVariableId);
            }else{
                globalVariable.setId(id);
                updateGlobalVariable(globalVariable);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long globalVariableId = idVsData.getKey();
            XMLBuilder globalVariableElement = idVsData.getValue();
            GlobalVariableContext globalVariable = constructGlobalVariableFromBuilder(globalVariableElement);
            globalVariable.setId(globalVariableId);
            updateGlobalVariable(globalVariable);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for(Long id : ids) {
            GlobalVariableUtil.deleteVariable(id);
        }
    }
    private Map<Long, Long> getGlobalVariableIdVsModuleId() throws Exception {
        Map<Long, Long> globalVariableIdVsModuleId = new HashMap<>();
        FacilioModule globalVariableModule = ModuleFactory.getGlobalVariableModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("id","ID", globalVariableModule));
            add(FieldFactory.getNumberField("moduleId", "MODULEID", globalVariableModule));
        }};
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(globalVariableModule.getTableName());
        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                globalVariableIdVsModuleId.put((Long) prop.get("id"), (Long) prop.get("moduleId"));
            }
        }
        return globalVariableIdVsModuleId;
    }
    public List<GlobalVariableContext> getGlobalVariableForIds(Collection<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .select(FieldFactory.getGlobalVariableFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getGlobalVariableModule()));
        List<GlobalVariableContext> globalVariables = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableContext.class);
        return globalVariables;
    }
    public static GlobalVariableContext constructGlobalVariableFromBuilder(XMLBuilder globalVariableElement) throws Exception {
        String variableName = globalVariableElement.getElement(PackageConstants.GlobalVariableConstants.VARIABLE_NAME).getText();
        String linkName = globalVariableElement.getElement(PackageConstants.GlobalVariableConstants.LINK_NAME).getText();
        int type = Integer.parseInt((globalVariableElement.getElement(PackageConstants.GlobalVariableConstants.TYPE).getText()));
        String valueString = globalVariableElement.getElement(PackageConstants.GlobalVariableConstants.VALUE_STRING).getText();
        GlobalVariableContext globalVariable = new GlobalVariableContext();
        globalVariable.setName(variableName);
        globalVariable.setLinkName(linkName);
        globalVariable.setType(type);
        globalVariable.setValueString(valueString);
        String groupLinkName = globalVariableElement.getElement(PackageConstants.GlobalVariableConstants.GROUP_LINK_NAME).getText();
        long groupId = GlobalVariableUtil.getVariableGroupForLinkName(groupLinkName).getId();
        globalVariable.setGroupId(groupId);
        return globalVariable;
    }
    private long addGlobalVariable(GlobalVariableContext globalVariable) throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE, globalVariable);
        chain.execute();
        GlobalVariableContext globalVariableContext = (GlobalVariableContext) context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE);
        long globalVariableContextId = globalVariableContext.getId();
        return globalVariableContextId;
    }
    private void updateGlobalVariable(GlobalVariableContext globalVariable) throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE, globalVariable);
        chain.execute();
    }
}
