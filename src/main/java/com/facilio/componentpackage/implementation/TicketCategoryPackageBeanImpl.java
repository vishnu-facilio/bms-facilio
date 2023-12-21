package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TicketCategoryPackageBeanImpl implements PackageBean<TicketCategoryContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
      return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getTicketCategoryIdVsModuleId();
    }

    @Override
    public Map<Long, TicketCategoryContext> fetchComponents(List<Long> ids) throws Exception {
        List<TicketCategoryContext> ticketCategories  = getTicketCategoryForIds(ids);
        Map<Long, TicketCategoryContext> ticketCategoryIdVsTicketCategoryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketCategories)) {
            ticketCategories.forEach(ticketCategoryContext -> ticketCategoryIdVsTicketCategoryMap.put(ticketCategoryContext.getId(), ticketCategoryContext));
            PackageBeanUtil.addPickListConfForXML(FacilioConstants.ContextNames.TICKET_CATEGORY, "name", ticketCategories, TicketCategoryContext.class, false);
        }
        return ticketCategoryIdVsTicketCategoryMap;
    }

    @Override
    public void convertToXMLComponent(TicketCategoryContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.TicketCategoryConstants.TICKET_CATEGORY_NAME).text(component.getName());
        element.element(PackageConstants.TicketCategoryConstants.TICKET_DISPLAY_NAME).text(component.getDisplayName());
        element.element(PackageConstants.TicketCategoryConstants.DESCRIPTION).text(component.getDescription());
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
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketCategoryModule = moduleBean.getModule("ticketcategory");
        SelectRecordsBuilder<TicketCategoryContext> builder = new SelectRecordsBuilder<TicketCategoryContext>()
                .table(ticketCategoryModule.getTableName())
                .select(moduleBean.getAllFields(ticketCategoryModule.getName()))
                .module(ticketCategoryModule)
                .beanClass(TicketCategoryContext.class);
        List<TicketCategoryContext> ticketCategories = builder.get();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ticketCategoryElement = idVsData.getValue();
            TicketCategoryContext ticketCategoryContext = constructTicketCategoryFromBuilder(ticketCategoryElement);
            boolean containsName = false;
            Long id = -1L;
            for(TicketCategoryContext ticketCategory : ticketCategories) {
                if (ticketCategory.getName().equals(ticketCategoryContext.getName())) {
                    containsName = true;
                    id = ticketCategory.getId();
                    break;
                }
            }
            if (!containsName) {
                long ticketCategoryId = addTicketCategory(ticketCategoryContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), ticketCategoryId);
            }else{
                ticketCategoryContext.setId(id);
                updateTicketCategory(ticketCategoryContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), id);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long ticketCategoryId = idVsData.getKey();
            XMLBuilder ticketCategoryElement = idVsData.getValue();
            TicketCategoryContext ticketCategoryContext = constructTicketCategoryFromBuilder(ticketCategoryElement);
            ticketCategoryContext.setId(ticketCategoryId);
            updateTicketCategory(ticketCategoryContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("ticketcategory");
        List<TicketCategoryContext> ticketCategories  = (List<TicketCategoryContext>) PackageBeanUtil.getModuleData(null, module,TicketCategoryContext.class, false);
        List<Long> targetTicketCategoryIds = ticketCategories.stream().map(TicketCategoryContext::getId).collect(Collectors.toList());
        Map<String, Long> ticketCategoriesUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.TICKET_CATEGORY);
        if(PackageUtil.isInstallThread()) {
            PackageBeanUtil.deleteV3OldRecordFromTargetOrg(module.getName(), ticketCategoriesUIdVsIdsFromPackage,targetTicketCategoryIds);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            FacilioChain deleteTicketCategoryChain = FacilioChainFactory.getDeleteTicketCategoryChain();
            FacilioContext context = deleteTicketCategoryChain.getContext();
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(id));
            deleteTicketCategoryChain.execute();
        }
    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addPickListConfForContext(FacilioConstants.ContextNames.TICKET_CATEGORY, "name", TicketCategoryContext.class);
    }

    public Map<Long, Long> getTicketCategoryIdVsModuleId() throws Exception {
        Map<Long, Long> ticketCategoryIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketCategoryModule = moduleBean.getModule("ticketcategory");
        List<TicketCategoryContext> props = (List<TicketCategoryContext>) PackageBeanUtil.getModuleData(null,ticketCategoryModule, TicketCategoryContext.class, false);
        if (CollectionUtils.isNotEmpty(props)) {
            for (TicketCategoryContext prop : props) {
                ticketCategoryIdVsModuleId.put( prop.getId(), prop.getModuleId());
            }
        }
        return ticketCategoryIdVsModuleId;
    }
    public List<TicketCategoryContext> getTicketCategoryForIds(Collection<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketCategoryModule = moduleBean.getModule("ticketcategory");
        List<TicketCategoryContext> ticketCategories = (List<TicketCategoryContext>) PackageBeanUtil.getModuleDataListsForIds(ids,ticketCategoryModule, TicketCategoryContext.class);
        return ticketCategories;
    }
    public static TicketCategoryContext constructTicketCategoryFromBuilder(XMLBuilder ticketCategoryElement) throws Exception {
        String name = ticketCategoryElement.getElement(PackageConstants.TicketCategoryConstants.TICKET_CATEGORY_NAME).getText();
        String displayName = ticketCategoryElement.getElement(PackageConstants.TicketCategoryConstants.TICKET_DISPLAY_NAME).getText();
        String description = ticketCategoryElement.getElement(PackageConstants.TicketCategoryConstants.DESCRIPTION).getText();

        TicketCategoryContext ticketCategoryContext = new TicketCategoryContext();
        ticketCategoryContext.setName(name);
        ticketCategoryContext.setDisplayName(displayName);
        ticketCategoryContext.setDescription(description);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("ticketcategory");
        ticketCategoryContext.setModuleId(module.getModuleId());
        return ticketCategoryContext;

    }
    private long addTicketCategory(TicketCategoryContext ticketCategoryContext) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketCategoryContext);
        FacilioChain addTicketCategoryChain = FacilioChainFactory.getAddTicketCategoryChain();
        addTicketCategoryChain.execute(context);
        long ticketCategoryId = (long) context.get("recordId");
        return ticketCategoryId;
    }
    public void updateTicketCategory( TicketCategoryContext ticketCategoryContext) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketCategoryContext);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(ticketCategoryContext.getId()));
        FacilioChain updateTicketCategoryChain = FacilioChainFactory.getUpdateTicketCategoryChain();
        updateTicketCategoryChain.execute(context);
    }
}
