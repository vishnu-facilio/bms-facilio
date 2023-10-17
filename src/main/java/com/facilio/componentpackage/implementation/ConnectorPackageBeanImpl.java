package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectionContext;
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
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ConnectorPackageBeanImpl implements PackageBean<ConnectionContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {

        return fetchConnectors();
    }

    private Map<Long, Long> fetchConnectors() throws Exception {

        LOGGER.info("###### COnnection fetch called...");

        Map<Long, Long> idVsModuleId = new HashMap<>();
        FacilioModule connectionModule = ModuleFactory.getConnectionModule();


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getConnectionFields())
                .table(connectionModule.getTableName());

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                idVsModuleId.put((Long) prop.get("id"), -1L);
            }

        }
        LOGGER.info("########### Connection moduleVsid : "+idVsModuleId);
        return idVsModuleId;
    }

    @Override
    public Map<Long, ConnectionContext> fetchComponents(List<Long> ids) throws Exception {
        List<ConnectionContext> connections  = fetchConnectionForIds(ids);
        Map<Long, ConnectionContext> idVsMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(connections)) {
            connections.forEach(connectionContext -> idVsMap.put(connectionContext.getId(), connectionContext));
        }
        return idVsMap;
    }

    private List<ConnectionContext> fetchConnectionForIds(List<Long> ids) throws Exception {

        FacilioModule connectionModule = ModuleFactory.getConnectionModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getConnectionFields())
                .table(connectionModule.getTableName());

        if (CollectionUtils.isNotEmpty(ids)) {

            selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, connectionModule));
        }

        List<Map<String, Object>> props = selectBuilder.get();

        return CollectionUtils.isNotEmpty(props) ? FieldUtil.getAsBeanListFromMapList(props, ConnectionContext.class) : Collections.EMPTY_LIST;

    }

    @Override
    public void convertToXMLComponent(ConnectionContext component, XMLBuilder element) throws Exception {

        element.element(PackageConstants.ConnectionContext.NAME).text(component.getName());
        element.element(PackageConstants.ConnectionContext.SERVICE_NAME).text(component.getServiceName());
        element.element(PackageConstants.ConnectionContext.GRANT_TYPE).text(String.valueOf(component.getGrantType()));
        element.element(PackageConstants.ConnectionContext.AUTH_TYPE).text(String.valueOf(component.getAuthType()));
        element.element(PackageConstants.ConnectionContext.AUDIENCE).text(component.getAudience());
        element.element(PackageConstants.ConnectionContext.RESOURCE).text(component.getResource());
        element.element(PackageConstants.ConnectionContext.SCOPE).text(component.getScope());
        element.element(PackageConstants.ConnectionContext.STATE).text(String.valueOf(component.getState()));

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
            XMLBuilder element = idVsData.getValue();

            ConnectionContext connectionContext = constructConnectionContextFromBuilder(element);
            addConnections(connectionContext);

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), connectionContext.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    private ConnectionContext addConnections(ConnectionContext connectionContext) throws Exception {
        LOGGER.info("#####@@@@@@@@ Connections creation called ....");
        FacilioChain chain = TransactionChainFactory.getAddConnectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);

        chain.execute();

        return connectionContext;
    }

    private ConnectionContext constructConnectionContextFromBuilder(XMLBuilder element) {

        ConnectionContext connection = new ConnectionContext();
        connection.setName(element.getElement(PackageConstants.ConnectionContext.NAME).getText());
        connection.setServiceName(element.getElement(PackageConstants.ConnectionContext.SERVICE_NAME).getText());
        connection.setScope(element.getElement(PackageConstants.ConnectionContext.SCOPE).getText());
        connection.setAudience(element.getElement(PackageConstants.ConnectionContext.AUDIENCE).getText());
        connection.setAuthType(Integer.parseInt(element.getElement(PackageConstants.ConnectionContext.AUTH_TYPE).getText()));
        connection.setResource(element.getElement(PackageConstants.ConnectionContext.RESOURCE).getText());
        connection.setState(Integer.parseInt(element.getElement(PackageConstants.ConnectionContext.STATE).getText()));
        connection.setGrantType(Integer.parseInt(element.getElement(PackageConstants.ConnectionContext.GRANT_TYPE).getText()));

        return connection;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {


        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder element = idVsData.getValue();

            ConnectionContext connectionContext = constructConnectionContextFromBuilder(element);
            FacilioChain chain = TransactionChainFactory.getUpdateConnectionChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);

            chain.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            for (Long id : ids) {

                ConnectionContext connectionContext = new ConnectionContext();
                connectionContext.setId(id);

                FacilioChain chain = TransactionChainFactory.getDeleteConnectionChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);

                chain.execute();
            }
        }
    }
}
