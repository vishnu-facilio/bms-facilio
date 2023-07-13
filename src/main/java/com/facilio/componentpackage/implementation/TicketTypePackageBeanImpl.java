package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class TicketTypePackageBeanImpl implements PackageBean<TicketTypeContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getTicketTypeIdVsModuleId();
    }

    @Override
    public Map<Long, TicketTypeContext> fetchComponents(List<Long> ids) throws Exception {
        List<TicketTypeContext> ticketTypes  = getTicketTypeForIds(ids);
        Map<Long, TicketTypeContext> ticketTypeIdVsTicketTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketTypes)) {
            ticketTypes.forEach(ticketTypeContext -> ticketTypeIdVsTicketTypeMap.put(ticketTypeContext.getId(), ticketTypeContext));
        }
        return ticketTypeIdVsTicketTypeMap;
    }

    @Override
    public void convertToXMLComponent(TicketTypeContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.TicketTypeConstants.TICKET_TYPE_NAME).text(component.getName());
        element.element(PackageConstants.TicketTypeConstants.DESCRIPTION).text(component.getDescription());
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
        FacilioModule ticketTypeModule = moduleBean.getModule("tickettype");;
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        SelectRecordsBuilder<TicketTypeContext> builder = new SelectRecordsBuilder<TicketTypeContext>()
                .table(ticketTypeModule.getTableName())
                .select(moduleBean.getAllFields(ticketTypeModule.getName()))
                .module(ticketTypeModule)
                .beanClass(TicketTypeContext.class);
        List<TicketTypeContext> ticketTypes = builder.get();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ticketTypeElement = idVsData.getValue();
            TicketTypeContext ticketTypeContext = constructTicketTypeFromBuilder(ticketTypeElement);
            boolean containsName = false;
            Long id = -1L;
            for(TicketTypeContext ticketType : ticketTypes) {
                if (ticketType.getName().equals(ticketTypeContext.getName())) {
                    id = ticketType.getId();
                    containsName = true;
                    break;
                }
            }
            if (!containsName) {
                long ticketTypeId = addTicketType(ticketTypeContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), ticketTypeId);
            }
            else{
                ticketTypeContext.setId(id);
                updateTicketType(ticketTypeContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents, boolean isReUpdate) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long ticketTypeId = idVsData.getKey();
            XMLBuilder ticketTypeElement = idVsData.getValue();
            TicketTypeContext ticketTypeContext = constructTicketTypeFromBuilder(ticketTypeElement);
            ticketTypeContext.setId(ticketTypeId);
            updateTicketType(ticketTypeContext);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            FacilioChain deleteTicketTypeChain = FacilioChainFactory.getDeleteTicketTypeChain();
            FacilioContext context = deleteTicketTypeChain.getContext();
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(id));
            deleteTicketTypeChain.execute();
        }
    }
    public Map<Long, Long> getTicketTypeIdVsModuleId() throws Exception {
        Map<Long, Long> ticketTypeIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketTypeModule = moduleBean.getModule("tickettype");
        List<TicketTypeContext> props = (List<TicketTypeContext>)PackageBeanUtil.getModuleDataIdVsModuleId(null,ticketTypeModule, TicketTypeContext.class);
        if (CollectionUtils.isNotEmpty(props)) {
            for (TicketTypeContext prop : props) {
                ticketTypeIdVsModuleId.put( prop.getId(), prop.getModuleId());
            }
        }
        return ticketTypeIdVsModuleId;
    }
    public List<TicketTypeContext> getTicketTypeForIds(Collection<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketTypeModule = moduleBean.getModule("tickettype");
        List<TicketTypeContext> ticketTypes = (List<TicketTypeContext>) PackageBeanUtil.getModuleDataListsForIds(ids,ticketTypeModule, TicketTypeContext.class);
        return ticketTypes;
    }
    public static TicketTypeContext constructTicketTypeFromBuilder(XMLBuilder ticketTypeElement) throws Exception {
        String name = ticketTypeElement.getElement(PackageConstants.TicketTypeConstants.TICKET_TYPE_NAME).getText();
        String description = ticketTypeElement.getElement(PackageConstants.TicketTypeConstants.DESCRIPTION).getText();
        TicketTypeContext ticketTypeContext = new TicketTypeContext();
        ticketTypeContext.setName(name);
        ticketTypeContext.setDescription(description);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("tickettype");
        ticketTypeContext.setModuleId(module.getModuleId());
        return ticketTypeContext;

    }
    private long addTicketType(TicketTypeContext ticketTypeContext) throws Exception {
        TicketTypeContext ticketTyContext = new TicketTypeContext();
        ticketTyContext.setName(ticketTypeContext.getName());
        ticketTyContext.setDescription(ticketTypeContext.getDescription());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketTyContext);
        FacilioChain addTicketTypeChain = FacilioChainFactory.getAddTicketTypeChain();
        addTicketTypeChain.execute(context);
        long ticketTypeId = (long) context.get("recordId");
        return ticketTypeId;
    }
    private void updateTicketType(TicketTypeContext ticketTypeContext) throws Exception {
        TicketTypeContext ticketTyContext = new TicketTypeContext();
        ticketTyContext.setName(ticketTypeContext.getName());
        ticketTyContext.setDescription(ticketTypeContext.getDescription());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketTyContext);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(ticketTyContext.getId()));
        FacilioChain updateTicketTypeChain = FacilioChainFactory.getUpdateTicketTypeChain();
        updateTicketTypeChain.execute(context);
    }
}
