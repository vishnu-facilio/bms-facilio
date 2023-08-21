package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
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

public class GlobalVariableGroupPackageBeanImpl implements PackageBean<GlobalVariableGroupContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getGlobalVariableGroupIdVsModuleId();
    }

    @Override
    public Map<Long, GlobalVariableGroupContext> fetchComponents(List<Long> ids) throws Exception {
        List<GlobalVariableGroupContext> globalVariableGroups  = getGlobalVariableGroupForIds(ids);
        Map<Long, GlobalVariableGroupContext> globalVariableGroupIdVsGlobalVariableGroupMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(globalVariableGroups)) {
            globalVariableGroups.forEach(globalVariableGroup -> globalVariableGroupIdVsGlobalVariableGroupMap.put(globalVariableGroup.getId(), globalVariableGroup));
        }
        return globalVariableGroupIdVsGlobalVariableGroupMap;
    }

    @Override
    public void convertToXMLComponent(GlobalVariableGroupContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.GlobalVariableGroupConstants.GROUP_NAME).text(component.getName());
        element.element(PackageConstants.GlobalVariableGroupConstants.LINK_NAME).text(component.getLinkName());
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
        List<GlobalVariableGroupContext> globalVariableGroups = GlobalVariableUtil.getAllGlobalVariableGroups();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder globalGroupVariableElement = idVsData.getValue();
            GlobalVariableGroupContext globalVariableGroup = constructGlobalVariableGroupFromBuilder(globalGroupVariableElement);
            boolean containsName = false;
            Long id = -1L;
            for(GlobalVariableGroupContext variableGroup : globalVariableGroups) {
                if (variableGroup.getLinkName().equals(globalVariableGroup.getLinkName())) {
                    id = variableGroup.getId();
                    containsName = true;
                    break;
                }
            }
            if (!containsName) {
                long globalVariableGroupId = addGlobalVariableGroup(globalVariableGroup);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), globalVariableGroupId);
            }else{
                globalVariableGroup.setId(id);
                updateGlobalVariableGroup(globalVariableGroup);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long globalVariableGroupId = idVsData.getKey();
            XMLBuilder globalVariableGroupElement = idVsData.getValue();
            GlobalVariableGroupContext globalVariableGroup = constructGlobalVariableGroupFromBuilder(globalVariableGroupElement);
            globalVariableGroup.setId(globalVariableGroupId);
            updateGlobalVariableGroup(globalVariableGroup);
        }
    }
    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }
    private Map<Long, Long> getGlobalVariableGroupIdVsModuleId() throws Exception {
        Map<Long, Long> globalVariableGroupIdVsModuleId = new HashMap<>();
        FacilioModule globalVariableGroupModule = ModuleFactory.getGlobalVariableGroupModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("id","ID", globalVariableGroupModule));
            add(FieldFactory.getNumberField("moduleId", "MODULEID", globalVariableGroupModule));
        }};
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(globalVariableGroupModule.getTableName());
        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                globalVariableGroupIdVsModuleId.put((Long) prop.get("id"), (Long) prop.get("moduleId"));
            }
        }
        return globalVariableGroupIdVsModuleId;
    }
    public List<GlobalVariableGroupContext> getGlobalVariableGroupForIds(Collection<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getGlobalVariableGroupModule()));
        List<GlobalVariableGroupContext> globalVariableGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableGroupContext.class);
        return globalVariableGroups;
    }
    public static GlobalVariableGroupContext constructGlobalVariableGroupFromBuilder(XMLBuilder globalVariableGroupElement) throws Exception {
        String groupName = globalVariableGroupElement.getElement(PackageConstants.GlobalVariableGroupConstants.GROUP_NAME).getText();
        String linkName = globalVariableGroupElement.getElement(PackageConstants.GlobalVariableGroupConstants.LINK_NAME).getText();
        GlobalVariableGroupContext globalVariableGroup = new GlobalVariableGroupContext();
        globalVariableGroup.setName(groupName);
        globalVariableGroup.setLinkName(linkName);
        return globalVariableGroup;
    }
    private long addGlobalVariableGroup(GlobalVariableGroupContext globalVariableGroup) throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP, globalVariableGroup);
        chain.execute();
        GlobalVariableGroupContext globalVariableGroupContext = (GlobalVariableGroupContext) context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP);
        long globalVariableGroupContextId = globalVariableGroupContext.getId();
        return globalVariableGroupContextId;
    }
    private void updateGlobalVariableGroup(GlobalVariableGroupContext globalVariableGroup) throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP, globalVariableGroup);
        chain.execute();
    }
}
