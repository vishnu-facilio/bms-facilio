package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetConnectedAppsListCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(GetConnectedAppsListCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppFields())
				.table(ModuleFactory.getConnectedAppsModule().getTableName())
				;
		
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ConnectedAppContext> connectedApps = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				connectedApps.add(FieldUtil.getAsBeanFromMap(prop, ConnectedAppContext.class));
			}
		}
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppSAMLFields())
				.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
				;
		props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ConnectedAppSAMLContext connectedAppSAML = FieldUtil.getAsBeanFromMap(prop, ConnectedAppSAMLContext.class);
				connectedApps.stream().filter(ca->ca.getId()==connectedAppSAML.getConnectedAppId()).findFirst().ifPresent(ca -> ca.setConnectedAppSAML(connectedAppSAML));
			}
		}
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppWidgetsFields())
				.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
				;
		props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ConnectedAppWidgetContext connectedAppWidget = FieldUtil.getAsBeanFromMap(prop, ConnectedAppWidgetContext.class);
				connectedApps.stream().filter(ca->ca.getId()==connectedAppWidget.getConnectedAppId()).findFirst().ifPresent(ca -> {
					if(ca.getConnectedAppWidgetsList() == null){
						ca.setConnectedAppWidgetsList(new ArrayList<>());
					}
					ca.getConnectedAppWidgetsList().add(connectedAppWidget);
				});
			}
		}
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVariablesFields())
				.table(ModuleFactory.getVariablesModule().getTableName())
				;
		props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				VariableContext variable = FieldUtil.getAsBeanFromMap(prop, VariableContext.class);
				connectedApps.stream().filter(ca->ca.getId()==variable.getConnectedAppId()).findFirst().ifPresent(ca -> {
					if(ca.getVariablesList() == null){
						ca.setVariablesList(new ArrayList<>());
					}
					ca.getVariablesList().add(variable);
				});
			}
		}
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppConnectorsFields())
				.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
				;
		props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ConnectedAppConnectorContext connector = FieldUtil.getAsBeanFromMap(prop, ConnectedAppConnectorContext.class);
				connectedApps.stream().filter(ca->ca.getId()==connector.getConnectedAppId()).findFirst().ifPresent(ca -> {
					if(ca.getConnectedAppConnectorsList() == null){
						ca.setConnectedAppConnectorsList(new ArrayList<>());
					}
					ca.getConnectedAppConnectorsList().add(connector);
				});
			}
		}
		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, connectedApps);
		return false;
	}
}
