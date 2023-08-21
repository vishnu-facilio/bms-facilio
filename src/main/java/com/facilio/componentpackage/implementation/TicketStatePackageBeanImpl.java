package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketStatePackageBeanImpl implements PackageBean<FacilioStatus> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> stateIdVsParentModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketModule = moduleBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(ticketModule));
            add(FieldFactory.getStringField("status", "STATUS", ticketModule));
            add(FieldFactory.getNumberField("parentModuleId","PARENT_MODULEID",ticketModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(ModuleFactory.getTicketStatusModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_MODULEID","parentModuleId", String.valueOf(-1), NumberOperators.NOT_EQUALS));

        List<Map<String, Object>> statusProps = builder.get();

        if (CollectionUtils.isNotEmpty(statusProps)) {
            long parentModuleId;
            long statusId;
            for (Map<String, Object> prop : statusProps) {
                parentModuleId = prop.containsKey("parentModuleId") ? (Long) prop.get("parentModuleId") : -1;
                statusId = prop.containsKey("id") ? (Long) prop.get("id") : -1;
                stateIdVsParentModuleId.put(statusId, parentModuleId);
            }
            addPickListConf(statusProps);
        }

        return stateIdVsParentModuleId;
    }

    @Override
    public Map<Long, FacilioStatus> fetchComponents(List<Long> ids) throws Exception {

        Map<Long,FacilioStatus> facilioStatusMap = new HashMap<>();

        for (Long id : ids){
            FacilioStatus status = TicketAPI.getStatus(id);
            facilioStatusMap.put(id,status);
        }
        return facilioStatusMap;
    }

    @Override
    public void convertToXMLComponent(FacilioStatus status, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        if (status.getParentModuleId() > 0) {
            FacilioModule module = moduleBean.getModule(status.getParentModuleId());
            element.element("parentModuleName").text(module.getName());
        }
        element.element("displayName").text(status.getDisplayName());
        element.element("recordLocked").text(String.valueOf(status.getRecordLocked()));
        element.element("requestedState").text(String.valueOf(status.getRequestedState()));
        element.element("status").text(status.getStatus());
        element.element("timerEnabled").text(String.valueOf(status.getTimerEnabled()));
        element.element("typeCode").text(String.valueOf(status.getTypeCode()));
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

        Map<String,Long> uniqueNameVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        FacilioStatus status = new FacilioStatus();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            status = constructComponentFromXML(builder, moduleBean);

            long statusId = -1l;
            if (status.getParentModuleId() > 0) {
               statusId  = addOrUpdateStatus(status);
            }
            if (statusId > 0) {
                uniqueNameVsComponentId.put(idVsData.getKey(), statusId);
            }
        }

        return uniqueNameVsComponentId;
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("ticketstatus");
        List<Long> targetTicketStateIds = new ArrayList<>(idVsXMLComponents.keySet());
        Map<String, Long> ticketStatusUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.TICKET_CATEGORY);
        if(PackageUtil.isInstallThread()) {
            PackageBeanUtil.deleteV3OldRecordFromTargetOrg(module.getName(), ticketStatusUIdVsIdsFromPackage,targetTicketStateIds);
        }
    }

    private long addOrUpdateStatus(FacilioStatus status) throws Exception{

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(status.getParentModuleId());

        FacilioStatus existingStatus = new FacilioStatus();
        existingStatus = TicketAPI.getStatus(module, status.getStatus());
        if (existingStatus != null){
            status.setId(existingStatus.getId());
        }

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TICKET_STATUS, status);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE, module.getName());
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
        chain.execute(context);

        long statusId = (long) context.getOrDefault(FacilioConstants.ContextNames.ID, -1L);

        return statusId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioStatus status;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long statusId = idVsData.getKey();
            if (statusId == null || statusId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            status = constructComponentFromXML(builder, moduleBean);
            status.setId(statusId);

            addOrUpdateStatus(status);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addTicketStatusConfForContext(FacilioConstants.ContextNames.TICKET_STATUS, "status", FacilioStatus.class);
    }

    private FacilioStatus constructComponentFromXML(XMLBuilder builder, ModuleBean moduleBean) throws Exception{
        String displayName = builder.getElement("displayName").getText();
        Boolean recordLocked = Boolean.parseBoolean(builder.getElement("recordLocked").getText());
        Boolean requestedState = Boolean.parseBoolean(builder.getElement("requestedState").getText());
        Boolean timerEnabled = Boolean.parseBoolean(builder.getElement("timerEnabled").getText());
        Integer typeCode = Integer.parseInt(builder.getElement("typeCode").getText());
        String statusName = builder.getElement("status").getText();

        FacilioStatus status = new FacilioStatus();

        if (builder.getElement("parentModuleName") != null ) {
            String parentModuleName = builder.getElement("parentModuleName").getText();
            FacilioModule module = moduleBean.getModule(parentModuleName);
            status.setParentModuleId(module.getModuleId());
        }

        status.setStatus(statusName);
        status.setDisplayName(displayName);
        status.setRecordLocked(recordLocked);
        status.setTypeCode(typeCode);
        status.setRequestedState(requestedState);
        status.setTimerEnabled(timerEnabled);

        return status;
    }

    private static void addPickListConf(List<Map<String, Object>> statusProps) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioStatus> facilioStatusList = FieldUtil.getAsBeanListFromMapList(statusProps, FacilioStatus.class);

        if (CollectionUtils.isNotEmpty(facilioStatusList)) {
            Map<String, List<FacilioStatus>> moduleNameVsFacilioStatusList = new HashMap<>();
            for (FacilioStatus facilioStatus : facilioStatusList) {
                long parentModuleId = facilioStatus.getParentModuleId();
                FacilioModule module = parentModuleId > 0 ? moduleBean.getModule(parentModuleId) : null;

                if (module != null) {
                    if (!moduleNameVsFacilioStatusList.containsKey(module.getName())) {
                        moduleNameVsFacilioStatusList.put(module.getName(), new ArrayList<>());
                    }
                    moduleNameVsFacilioStatusList.get(module.getName()).add(facilioStatus);
                }
            }

            for (String moduleName : moduleNameVsFacilioStatusList.keySet()) {
                PackageBeanUtil.addPickListConfForXML(moduleName, "status", moduleNameVsFacilioStatusList.get(moduleName), FacilioStatus.class, true);
            }
        }
    }
}
