package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TicketPriorityPackageBeanImpl implements PackageBean<TicketPriorityContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getTicketPriorityIdVsModuleId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getTicketPriorityIdVsModuleId(false);
    }

    @Override
    public Map<Long, TicketPriorityContext> fetchComponents(List<Long> ids) throws Exception {
        List<TicketPriorityContext> ticketPriorities  = getTicketPriorityForIds(ids);
        Map<Long, TicketPriorityContext> ticketPriorityIdVsTicketPriorityMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketPriorities)) {
            ticketPriorities.forEach(ticketPriorityContext -> ticketPriorityIdVsTicketPriorityMap.put(ticketPriorityContext.getId(), ticketPriorityContext));
            PackageBeanUtil.addPickListConfForXML(FacilioConstants.ContextNames.TICKET_PRIORITY, "priority", ticketPriorities, TicketPriorityContext.class, false);
        }
        return ticketPriorityIdVsTicketPriorityMap;
    }

    @Override
    public void convertToXMLComponent(TicketPriorityContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.TicketPriorityConstants.PRIORITY).text(component.getPriority());
        element.element(PackageConstants.TicketPriorityConstants.SEQUENCE_NUMBER).text(String.valueOf(component.getSequenceNumber()));
        element.element(PackageConstants.TicketPriorityConstants.COLOUR).text(component.getColour());
        element.element(PackageConstants.TicketPriorityConstants.DISPLAY_NAME).text(component.getDisplayName());
        element.element(PackageConstants.TicketPriorityConstants.DESCRIPTION).text(component.getDescription());
        element.element(PackageConstants.TicketPriorityConstants.IS_DEFAULT).text(String.valueOf(component.getIsDefault()));
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
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketPriorityModule = moduleBean.getModule("ticketpriority");
        Map<String, Long> uniqueIdentifierVsTicketPriorityId = new HashMap<>();
        SelectRecordsBuilder<TicketPriorityContext> ticketPriorityBuilder = new SelectRecordsBuilder<TicketPriorityContext>()
                .select(moduleBean.getAllFields(ticketPriorityModule.getName()))
                .beanClass(TicketPriorityContext.class)
                .module(ticketPriorityModule)
                .table(ticketPriorityModule.getTableName());
        List<TicketPriorityContext> ticketPriorities = ticketPriorityBuilder.get();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder ticketPriorityElement = idVsData.getValue();
            TicketPriorityContext ticketPriorityContext = constructTicketPriorityFromBuilder(ticketPriorityElement);
            boolean isDefault = Boolean.parseBoolean(ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.IS_DEFAULT).getText());
            if (isDefault) {
                for(TicketPriorityContext ticketPriority : ticketPriorities) {
                    if (ticketPriority.getPriority().equals(ticketPriorityContext.getPriority())) {
                        uniqueIdentifierVsTicketPriorityId.put(uniqueIdentifier, ticketPriority.getId());
                        break;
                    }
                }
            }
        }
        return uniqueIdentifierVsTicketPriorityId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketPriorityModule = moduleBean.getModule("ticketpriority");
        SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
                .table(ticketPriorityModule.getTableName())
                .select(moduleBean.getAllFields(ticketPriorityModule.getName()))
                .module(ticketPriorityModule)
                .beanClass(TicketPriorityContext.class);
        List<TicketPriorityContext> ticketPriorities = builder.get();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ticketPriorityElement = idVsData.getValue();
            TicketPriorityContext ticketPriorityContext = constructTicketPriorityFromBuilder(ticketPriorityElement);
            boolean containsName = false;
            Long id = -1L;
            for(TicketPriorityContext ticketPriority : ticketPriorities) {
                if (ticketPriority.getPriority().equals(ticketPriorityContext.getPriority())) {
                    containsName = true;
                    id = ticketPriority.getId();
                    break;
                }
            }
            if (!containsName) {
                long ticketPriorityId = addTicketPriority(ticketPriorityContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), ticketPriorityId);
            }
            else{
                ticketPriorityContext.setId(id);
                updateTicketPriority(ticketPriorityContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long ticketPriorityId = idVsData.getKey();
            XMLBuilder ticketPriorityElement = idVsData.getValue();
            TicketPriorityContext ticketPriorityContext = constructTicketPriorityFromBuilder(ticketPriorityElement);
            ticketPriorityContext.setId(ticketPriorityId);
            updateTicketPriority(ticketPriorityContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("ticketpriority");
        List<TicketPriorityContext> ticketPriorities  = (List<TicketPriorityContext>) PackageBeanUtil.getModuleData(null, module,TicketPriorityContext.class, false);
        List<Long> targetTicketPriorityIds = ticketPriorities.stream().map(TicketPriorityContext::getId).collect(Collectors.toList());
        Map<String, Long> ticketPrioritiesUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.TICKET_PRIORITY);
        if(PackageUtil.isInstallThread()) {
            PackageBeanUtil.deleteV3OldRecordFromTargetOrg(module.getName(), ticketPrioritiesUIdVsIdsFromPackage,targetTicketPriorityIds);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            FacilioChain deleteTicketPriorityChain = FacilioChainFactory.getDeleteTicketPriorityChain();
            FacilioContext context = deleteTicketPriorityChain.getContext();
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(id));
            deleteTicketPriorityChain.execute();
        }
    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addPickListConfForContext(FacilioConstants.ContextNames.TICKET_PRIORITY, "priority", TicketPriorityContext.class);
    }

    public Map<Long, Long> getTicketPriorityIdVsModuleId(boolean fetchSystem) throws Exception {
        Map<Long, Long> ticketPriorityIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketPriorityModule = moduleBean.getModule("ticketpriority");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ISDEFAULT", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));
        List<TicketPriorityContext> props = (List<TicketPriorityContext>) PackageBeanUtil.getModuleData(criteria, ticketPriorityModule, TicketPriorityContext.class, false);
        if (CollectionUtils.isNotEmpty(props)) {
            for (TicketPriorityContext prop : props) {
                ticketPriorityIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return ticketPriorityIdVsModuleId;
    }
    public List<TicketPriorityContext> getTicketPriorityForIds(List<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketPriorityModule = moduleBean.getModule("ticketpriority");
        List<TicketPriorityContext> ticketPriorities = (List<TicketPriorityContext>) PackageBeanUtil.getModuleDataListsForIds(ids,ticketPriorityModule, TicketPriorityContext.class );;
        return ticketPriorities;
    }
    public static TicketPriorityContext constructTicketPriorityFromBuilder(XMLBuilder ticketPriorityElement) throws Exception {
        String priority = ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.PRIORITY).getText();
        Integer sequenceNumber = Integer.parseInt(ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.SEQUENCE_NUMBER).getText());
        String colour = ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.COLOUR).getText();
        String displayName = ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.DISPLAY_NAME).getText();
        String description = ticketPriorityElement.getElement(PackageConstants.TicketPriorityConstants.DESCRIPTION).getText();

        TicketPriorityContext ticketPriorityContext = new TicketPriorityContext();
        ticketPriorityContext.setPriority(priority);
        ticketPriorityContext.setSequenceNumber(sequenceNumber);
        ticketPriorityContext.setColour(colour);
        ticketPriorityContext.setDisplayName(displayName);
        ticketPriorityContext.setDescription(description);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("ticketpriority");
        ticketPriorityContext.setModuleId(module.getModuleId());

        return ticketPriorityContext;

    }
    private long addTicketPriority(TicketPriorityContext ticketPriorityContext) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketPriorityContext);
        FacilioChain addTicketPriorityChain = FacilioChainFactory.getAddTicketPriorityChain();
        addTicketPriorityChain.execute(context);
        long ticketPriorityId = (long) context.get("recordId");
        return ticketPriorityId;
    }
    private void updateTicketPriority(TicketPriorityContext ticketPriorityContext) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketPriorityContext);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(ticketPriorityContext.getId()));
        FacilioChain updateTicketPriorityChain = FacilioChainFactory.getUpdateTicketPriorityChain();
        updateTicketPriorityChain.execute(context);
    }
}
