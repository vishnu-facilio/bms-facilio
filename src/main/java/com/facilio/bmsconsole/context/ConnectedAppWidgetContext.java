package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ConnectedAppWidgetContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long id;
	private long criteriaId;
	private long connectedAppId;
	private String widgetName;
	private long entityId;
	private String resourcePath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	private EntityType entityType;

	public EntityType getEntityTypeEnum() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public int getEntityType() {
		if (entityType != null) {
			return entityType.getValue();
		}
		return -1;
	}

	public void setEntityType(int entityType) {
		this.entityType = EntityType.valueOf(entityType);
	}

	public enum EntityType {
		WEB_TAB,
		SUMMARY_PAGE,
		DASHBOARD_WIDGET,
		DIALER;

		public int getValue() {
			return ordinal() + 1;
		}

		public static EntityType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}