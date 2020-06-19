package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.services.factory.FacilioFactory;

public class ConnectedAppContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String name;
	private String linkName;
	private String description;
	private long logoId;
	private Boolean isActive;
	private String sandBoxBaseUrl;
	private String productionBaseUrl;
	private String startUrl;
	private Boolean showInLauncher;
	private ConnectedAppSAMLContext connectedAppSAML;
	private List<ConnectedAppWidgetContext> connectedAppWidgetsList;
	private List<VariableContext> variablesList;
	private List<ConnectedAppConnectorContext> connectedAppConnectorsList;
	
	public List<ConnectedAppConnectorContext> getConnectedAppConnectorsList() {
		return connectedAppConnectorsList;
	}

	public void setConnectedAppConnectorsList(List<ConnectedAppConnectorContext> connectedAppConnectorsList) {
		this.connectedAppConnectorsList = connectedAppConnectorsList;
	}

	public List<VariableContext> getVariablesList() {
		return variablesList;
	}

	public void setVariablesList(List<VariableContext> variablesList) {
		this.variablesList = variablesList;
	}

	public List<ConnectedAppWidgetContext> getConnectedAppWidgetsList() {
		return connectedAppWidgetsList;
	}

	public void setConnectedAppWidgetsList(List<ConnectedAppWidgetContext> connectedAppWidgetsList) {
		this.connectedAppWidgetsList = connectedAppWidgetsList;
	}

	public ConnectedAppSAMLContext getConnectedAppSAML() {
		return connectedAppSAML;
	}

	public void setConnectedAppSAML(ConnectedAppSAMLContext connectedAppSAML) {
		this.connectedAppSAML = connectedAppSAML;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getShowInLauncher() {
		return showInLauncher;
	}

	public void setShowInLauncher(Boolean showInLauncher) {
		this.showInLauncher = showInLauncher;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinkName() {
		if (this.linkName == null && this.name != null && !this.name.trim().isEmpty()) {
			this.linkName = this.name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		}
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLogoId() {
		return logoId;
	}

	public String getLogoUrl() throws Exception {
		if (this.logoId > 0) {
			return FacilioFactory.getFileStore().getPrivateUrl(this.logoId);
		}
		return null;
	}

	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}

	public String getSandBoxBaseUrl() {
		return sandBoxBaseUrl;
	}

	public void setSandBoxBaseUrl(String sandBoxBaseUrl) {
		this.sandBoxBaseUrl = sandBoxBaseUrl;
	}

	public String getProductionBaseUrl() {
		return productionBaseUrl;
	}

	public void setProductionBaseUrl(String productionBaseUrl) {
		this.productionBaseUrl = productionBaseUrl;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	private HostingType hostingType;

	public HostingType getHostingTypeEnum() {
		return hostingType;
	}

	public void setHostingType(HostingType hostingType) {
		this.hostingType = hostingType;
	}

	public int getHostingType() {
		if (hostingType != null) {
			return hostingType.getValue();
		}
		return -1;
	}

	public void setHostingType(int hostingType) {
		this.hostingType = HostingType.valueOf(hostingType);
	}

	public enum HostingType {
		INTERNAL, EXTERNAL;

		public int getValue() {
			return ordinal() + 1;
		}

		public static HostingType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

}