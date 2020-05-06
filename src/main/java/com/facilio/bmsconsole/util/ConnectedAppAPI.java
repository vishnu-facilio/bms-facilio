package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class ConnectedAppAPI {
	
	public static ConnectedAppContext getConnectedApp(long connectedAppId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppFields())
				.table(ModuleFactory.getConnectedAppsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(connectedAppId, (ModuleFactory.getConnectedAppsModule())))
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "true", BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ConnectedAppContext connectedApp = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedAppContext.class);
			return connectedApp;
		}
		return null;
	}
	
	public static ConnectedAppWidgetContext getConnectedAppWidget(long widgetId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppWidgetsFields())
				.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
				.innerJoin("ConnectedApps")
				.on("ConnectedApp_Widgets.CONNECTEDAPP_ID=ConnectedApps.ID")
				.andCondition(CriteriaAPI.getIdCondition(widgetId, (ModuleFactory.getConnectedAppWidgetsModule())))
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "true", BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ConnectedAppWidgetContext connectedAppWidget = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedAppWidgetContext.class);
			return connectedAppWidget;
		}
		return null;
	}
	
	public static ConnectedAppSAMLContext getConnectedAppSAML(long connectedAppId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppSAMLFields())
				.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (props != null) {
			ConnectedAppSAMLContext connectedAppSAML = FieldUtil.getAsBeanFromMap(props, ConnectedAppSAMLContext.class);
			return connectedAppSAML;
		}
		return null;
	}
	
	public static List<ConnectedAppWidgetContext> getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType entityType) throws Exception {
		return getConnectedAppWidgets(entityType, null);
	}
	
	public static List<ConnectedAppWidgetContext> getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType entityType, String entityId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppWidgetsFields())
				.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
				.innerJoin("ConnectedApps")
				.on("ConnectedApp_Widgets.CONNECTEDAPP_ID=ConnectedApps.ID")
				.andCondition(CriteriaAPI.getCondition("ENTITY_TYPE", "entityType", String.valueOf(entityType.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "true", BooleanOperators.IS));
		
		if (entityId != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ENTITY_ID", "entityId", String.valueOf(entityId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ConnectedAppWidgetContext> connectedAppWidgetsList = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ConnectedAppWidgetContext connectedAppWidget = FieldUtil.getAsBeanFromMap(prop, ConnectedAppWidgetContext.class);
				if(connectedAppWidget.getCriteriaId() > 0){
					connectedAppWidget.setCriteria(CriteriaAPI.getCriteria(connectedAppWidget.getCriteriaId()));
				}
				connectedAppWidgetsList.add(connectedAppWidget);
			}
			return connectedAppWidgetsList;
		}
		return null;
	}
	
	public static List<ConnectedAppWidgetContext> getSummaryPageWidgets(Long moduleId, long recordId) throws Exception {
		List<Long> moduleIds = new ArrayList<>();
		moduleIds.add(moduleId);
		return getSummaryPageWidgets(moduleIds, recordId);
	}
	
	public static List<ConnectedAppWidgetContext> getSummaryPageWidgets(List<Long> moduleIds, long recordId) throws Exception {
		
		String entityId = StringUtils.join(moduleIds, ",");
		
		List<ConnectedAppWidgetContext> summaryPageWidgets = getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType.SUMMARY_PAGE, entityId);
		if (summaryPageWidgets != null && summaryPageWidgets.size() > 0) {
			
			List<ConnectedAppWidgetContext> filteredSummaryPageWidgets = new ArrayList<>();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for (ConnectedAppWidgetContext appWidget : summaryPageWidgets) {
				
				FacilioModule module = modBean.getModule(appWidget.getEntityId());
				
				if (appWidget.getCriteria() != null) {
					List<FacilioField> selectFields = new ArrayList<>();
					selectFields.add(FieldFactory.getIdField(module));
					
					GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
							.select(selectFields)
							.table(module.getTableName())
							.andCondition(CriteriaAPI.getIdCondition(recordId, module))
							.andCriteria(appWidget.getCriteria());
					
					List<Map<String, Object>> props = selectBuilder.get();
					if (props != null && !props.isEmpty()) {
						filteredSummaryPageWidgets.add(appWidget);
					}
				}
				else {
					filteredSummaryPageWidgets.add(appWidget);
				}
			}
			
			return filteredSummaryPageWidgets;
		}
		return null;
	}
	
	public static ConnectedAppConnectorContext getConnector(long connectedAppId, String connectionName) throws Exception {
		
		FacilioField connectedAppIdField = FieldFactory.getField("connectedAppId", "CONNECTEDAPP_ID", ModuleFactory.getConnectedAppConnectorsModule(), FieldType.NUMBER);
		FacilioField connectorNameField = FieldFactory.getField("name", "NAME", ModuleFactory.getConnectionModule(), FieldType.STRING);
		FacilioField connectedAppIsActiveField = FieldFactory.getField("isActive", "IS_ACTIVE", ModuleFactory.getConnectedAppsModule(), FieldType.BOOLEAN);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppConnectorsFields())
				.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
				.innerJoin("ConnectedApps")
				.on("ConnectedApp_Connectors.CONNECTEDAPP_ID=ConnectedApps.ID")
				.innerJoin("Connection")
				.on("ConnectedApp_Connectors.CONNECTOR_ID=Connection.ID")
				.andCondition(CriteriaAPI.getCondition(connectedAppIdField, connectedAppId + "", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(connectorNameField, connectionName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(connectedAppIsActiveField, "true", BooleanOperators.IS));
		
		
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (props != null) {
			ConnectedAppConnectorContext connectedAppConnector = FieldUtil.getAsBeanFromMap(props, ConnectedAppConnectorContext.class);
			connectedAppConnector.setConnection(ConnectionUtil.getConnection(connectedAppConnector.getConnectorId()));
			return connectedAppConnector;
		}
		return null;
	}
}
