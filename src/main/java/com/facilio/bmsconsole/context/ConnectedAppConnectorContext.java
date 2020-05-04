package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ConnectedAppConnectorContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long id;
	private long connectorId;
	private long connectedAppId;
	private ConnectionContext connection;

	public ConnectionContext getConnection() {
		return connection;
	}

	public void setConnection(ConnectionContext connection) {
		this.connection = connection;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(long connectorId) {
		this.connectorId = connectorId;
	}

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

}