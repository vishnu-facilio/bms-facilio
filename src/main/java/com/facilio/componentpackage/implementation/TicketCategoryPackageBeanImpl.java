package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

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
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder ticketCategoryElement = idVsData.getValue();
            TicketCategoryContext ticketCategoryContext = constructTicketCategoryFromBuilder(ticketCategoryElement);
            FacilioModule ticketCategoryModule = ModuleFactory.getTicketCategoryModule();
            List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
                add(FieldFactory.getStringField("name", "NAME", ticketCategoryModule));
            }};
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ticketCategoryModule.getTableName())
                    .select(selectableFields)
                    .andCondition(CriteriaAPI.getCondition("NAME", "name", ticketCategoryContext.getName(), StringOperators.IS));
            List<Map<String, Object>> props = builder.get();
            if(CollectionUtils.isEmpty(props)) {
                long ticketCategoryId = addTicketCategory(ticketCategoryContext);
                ticketCategoryContext.setId(ticketCategoryId);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), ticketCategoryId);
            }
            else{
                updateTicketCategory(ticketCategoryContext);
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
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioChain deleteTicketCategoryChain = FacilioChainFactory.getDeleteTicketCategoryChain();
        FacilioContext context = deleteTicketCategoryChain.getContext();
        for (long id : ids) {
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(id));
            deleteTicketCategoryChain.execute();
        }
    }
    public Map<Long, Long> getTicketCategoryIdVsModuleId() throws Exception {
        Map<Long, Long> ticketCategoryIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ticketCategoryModule = moduleBean.getModule("ticketcategory");
        List<TicketCategoryContext> props = (List<TicketCategoryContext>) PackageBeanUtil.getContextIdVsParentId(null,ticketCategoryModule, TicketCategoryContext.class);
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
        List<TicketCategoryContext> ticketCategories = (List<TicketCategoryContext>) PackageBeanUtil.getContextListsForIds(ids,ticketCategoryModule, TicketCategoryContext.class);
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
        TicketCategoryContext ticketCatContext = new TicketCategoryContext();
        ticketCatContext.setName(ticketCategoryContext.getName());
        ticketCatContext.setDisplayName(ticketCategoryContext.getDisplayName());
        ticketCatContext.setDescription(ticketCategoryContext.getDescription());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketCatContext);
        FacilioChain addTicketCategoryChain = FacilioChainFactory.getAddTicketCategoryChain();
        addTicketCategoryChain.execute(context);
        long ticketCategoryId = (long) context.get("recordId");
        return ticketCategoryId;
    }
    public void updateTicketCategory( TicketCategoryContext ticketCategoryContext) throws Exception {
        TicketCategoryContext ticketCatContext = new TicketCategoryContext();
        ticketCatContext.setName(ticketCategoryContext.getName());
        ticketCatContext.setDisplayName(ticketCategoryContext.getDisplayName());
        ticketCatContext.setDescription(ticketCategoryContext.getDescription());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD, ticketCatContext);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(ticketCatContext.getId()));
        FacilioChain updateTicketCategoryChain = FacilioChainFactory.getUpdateTicketCategoryChain();
        updateTicketCategoryChain.execute(context);
    }
}
