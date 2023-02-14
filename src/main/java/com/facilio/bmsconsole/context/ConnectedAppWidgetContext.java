package com.facilio.bmsconsole.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConnectedAppWidgetContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long id;
	Criteria criteria;
	private long criteriaId;
	private long connectedAppId;
	private String widgetName;
	private String linkName;
	private long entityId;
	private String resourcePath;

	private String connectedAppLinkName;

	private String sandBoxBaseUrl;

	private String productionBaseUrl;
	private String connectedAppName;


	public String getLinkName() {
		if (this.linkName == null && this.widgetName != null && !this.widgetName.trim().isEmpty()) {
			this.linkName = this.widgetName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		}
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

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

	private EntityType entityTypeEnum;

	public void setEntityTypeEnum(EntityType entityTypeEnum) {
		this.entityTypeEnum = entityTypeEnum;
	}
	
	public EntityType getEntityTypeEnum() {
		if(entityType > 0){
			return EntityType.valueOf(entityType);
		}
		return entityTypeEnum;
	}
	
	private int entityType;

	public int getEntityType() {
		if(entityTypeEnum!=null){
			return entityTypeEnum.getValue();
		}
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public enum EntityType {
		WEB_TAB,
		SUMMARY_PAGE,
		DASHBOARD_WIDGET,
		DIALER,
		RELATED_LIST,
		CREATE_RECORD,
		EDIT_RECORD,
		CREATE_RECORD_SIDEBAR,
		CUSTOM_BUTTON,
		FORM_BACKGROUND,
		VIEW_BACKGROUND,
		TOPBAR;

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